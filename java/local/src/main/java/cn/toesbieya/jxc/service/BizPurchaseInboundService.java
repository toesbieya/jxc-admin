package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.annoation.Lock;
import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.enumeration.DocFinishEnum;
import cn.toesbieya.jxc.enumeration.DocHistoryEnum;
import cn.toesbieya.jxc.enumeration.DocStatusEnum;
import cn.toesbieya.jxc.exception.JsonResultException;
import cn.toesbieya.jxc.mapper.BizDocumentHistoryMapper;
import cn.toesbieya.jxc.mapper.BizPurchaseInboundMapper;
import cn.toesbieya.jxc.mapper.BizPurchaseOrderMapper;
import cn.toesbieya.jxc.mapper.BizStockMapper;
import cn.toesbieya.jxc.model.entity.*;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.model.vo.export.PurchaseInboundExport;
import cn.toesbieya.jxc.model.vo.result.PageResult;
import cn.toesbieya.jxc.model.vo.search.PurchaseInboundSearch;
import cn.toesbieya.jxc.model.vo.update.DocumentStatusUpdate;
import cn.toesbieya.jxc.utils.DocumentUtil;
import cn.toesbieya.jxc.utils.ExcelUtil;
import cn.toesbieya.jxc.model.vo.Result;
import cn.toesbieya.jxc.utils.Util;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BizPurchaseInboundService {
    @Resource
    private BizPurchaseInboundMapper mainMapper;
    @Resource
    private BizPurchaseOrderMapper orderMapper;
    @Resource
    private BizDocumentHistoryMapper historyMapper;
    @Resource
    private BizStockMapper stockMapper;
    @Resource
    private RecService recService;

    public BizPurchaseInbound getById(String id) {
        BizPurchaseInbound main = mainMapper.getById(id);

        if (main == null) return null;

        main.setData(this.getSubById(id));
        main.setImageList(recService.getAttachmentByPid(id));

        return main;
    }

    public List<BizPurchaseInboundSub> getSubById(String id) {
        return mainMapper.getSubById(id);
    }

    public PageResult<BizPurchaseInbound> search(PurchaseInboundSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageResult<>(mainMapper.search(vo));
    }

    public void export(PurchaseInboundSearch vo, HttpServletResponse response) throws Exception {
        List<PurchaseInboundExport> list = mainMapper.export(vo);
        ExcelUtil.exportSimply(list, response, "采购入库单导出");
    }

    @UserAction("'添加采购入库单'")
    @Transactional(rollbackFor = Exception.class)
    public Result add(BizPurchaseInbound doc) {
        return addMain(doc);
    }

    @UserAction("'修改采购入库单'+#doc.id")
    @Lock("#doc.id")
    @Transactional(rollbackFor = Exception.class)
    public Result update(BizPurchaseInbound doc) {
        return updateMain(doc);
    }

    @UserAction("'提交采购入库单'+#doc.id")
    @Lock("#doc.id")
    @Transactional(rollbackFor = Exception.class)
    public Result commit(BizPurchaseInbound doc) {
        boolean isFirstCreate = StringUtils.isEmpty(doc.getId());
        Result result = isFirstCreate ? addMain(doc) : updateMain(doc);

        historyMapper.insert(
                BizDocumentHistory.builder()
                        .pid(doc.getId())
                        .type(DocHistoryEnum.COMMIT.getCode())
                        .uid(doc.getCid())
                        .uname(doc.getCname())
                        .statusBefore(DocStatusEnum.DRAFT.getCode())
                        .statusAfter(DocStatusEnum.WAIT_VERIFY.getCode())
                        .time(System.currentTimeMillis())
                        .build()
        );

        result.setMsg(result.isSuccess() ? "提交成功" : "提交失败，" + result.getMsg());
        return result;
    }

    @UserAction("'撤回采购入库单'+#vo.id")
    @Lock("#vo.id")
    @Transactional(rollbackFor = Exception.class)
    public Result withdraw(DocumentStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();

        if (mainMapper.reject(id) < 1) {
            return Result.fail("撤回失败，请刷新重试");
        }

        historyMapper.insert(
                BizDocumentHistory.builder()
                        .pid(id)
                        .type(DocHistoryEnum.WITHDRAW.getCode())
                        .uid(user.getId())
                        .uname(user.getName())
                        .statusBefore(DocStatusEnum.WAIT_VERIFY.getCode())
                        .statusAfter(DocStatusEnum.DRAFT.getCode())
                        .time(System.currentTimeMillis())
                        .info(info)
                        .build()
        );

        return Result.success("撤回成功");
    }

    @UserAction("'通过采购入库单'+#vo.id")
    @Lock({"#vo.pid", "#vo.id"})
    @Transactional(rollbackFor = Exception.class)
    public Result pass(DocumentStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();
        String pid = vo.getPid();
        long now = System.currentTimeMillis();

        //检查采购订单状态、入库商品是否符合订单要求
        List<BizPurchaseInboundSub> inboundSubList = mainMapper.getSubById(vo.getId());
        String err = checkOrder(vo.getPid(), inboundSubList);
        if (err != null) return Result.fail("通过失败，" + err);

        if (mainMapper.pass(id, user.getId(), user.getName(), now) < 1) {
            return Result.fail("通过失败，请刷新重试");
        }

        //修改采购订单完成情况、子表的未入库数量
        List<BizStock> stockList = new ArrayList<>();
        DocFinishEnum finish = DocFinishEnum.FINISHED;

        //使用一级缓存
        List<BizPurchaseOrderSub> orderSubList = orderMapper.getSubById(pid);

        //生成需要入库的商品列表
        for (BizPurchaseOrderSub orderSub : orderSubList) {
            if (orderSub.getRemainNum().equals(BigDecimal.ZERO)) {
                continue;
            }

            BizPurchaseInboundSub inboundSub = Util.find(inboundSubList, i -> i.getCid().equals(orderSub.getCid()));

            if (inboundSub == null) continue;

            stockList.add(
                    BizStock.builder()
                            .cid(inboundSub.getCid()).cname(inboundSub.getCname()).num(inboundSub.getNum())
                            .price(orderSub.getPrice()).ctime(now).cgddid(pid).cgrkid(id)
                            .build()
            );

            BigDecimal gap = orderSub.getRemainNum().subtract(inboundSub.getNum());

            //如果有任意一个采购商品的remainNum大于采购商品的num，则完成情况为进行中，否则为已完成
            if (gap.compareTo(BigDecimal.ZERO) > 0) {
                finish = DocFinishEnum.UNDERWAY;
            }

            //更新采购订单子表的remainNum
            orderMapper.updateSubRemainNum(orderSub.getId(), gap);
        }

        //更新采购订单完成情况
        if (1 > orderMapper.updateFinish(pid, finish.getCode(), finish == DocFinishEnum.FINISHED ? now : null)) {
            throw new JsonResultException("通过失败，采购订单状态有误，请刷新重试");
        }

        //插入库存
        if (stockList.size() == 0) throw new JsonResultException("通过失败，入库异常");
        stockMapper.batchInsert(stockList);

        historyMapper.insert(
                BizDocumentHistory.builder()
                        .pid(id)
                        .type(DocHistoryEnum.PASS.getCode())
                        .uid(user.getId())
                        .uname(user.getName())
                        .statusBefore(DocStatusEnum.WAIT_VERIFY.getCode())
                        .statusAfter(DocStatusEnum.VERIFIED.getCode())
                        .time(now)
                        .info(info)
                        .build()
        );

        return Result.success("通过成功");
    }

    @UserAction("'驳回采购入库单'+#vo.id")
    @Lock("#vo.id")
    @Transactional(rollbackFor = Exception.class)
    public Result reject(DocumentStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();

        if (mainMapper.reject(id) < 1) {
            return Result.fail("驳回失败，请刷新重试");
        }

        historyMapper.insert(
                BizDocumentHistory.builder()
                        .pid(id)
                        .type(DocHistoryEnum.REJECT.getCode())
                        .uid(user.getId())
                        .uname(user.getName())
                        .statusBefore(DocStatusEnum.WAIT_VERIFY.getCode())
                        .statusAfter(DocStatusEnum.DRAFT.getCode())
                        .time(System.currentTimeMillis())
                        .info(info)
                        .build()
        );

        return Result.success("驳回成功");
    }

    @UserAction("'删除采购入库单'+#id")
    @Lock("#id")
    @Transactional(rollbackFor = Exception.class)
    public Result del(String id) {
        if (mainMapper.del(id) < 1) {
            return Result.fail("删除失败");
        }

        //同时删除子表和附件
        mainMapper.delSubByPid(id);
        recService.delAttachmentByPid(id);

        return Result.success("删除成功");
    }

    private Result addMain(BizPurchaseInbound doc) {
        List<BizPurchaseInboundSub> subList = doc.getData();

        String err = checkOrder(doc.getPid(), subList);
        if (err != null) return Result.fail(err);

        String id = DocumentUtil.getDocumentID("CGRK");

        if (StringUtils.isEmpty(id)) {
            return Result.fail("获取单号失败");
        }

        doc.setId(id);

        //设置子表的pid
        subList.forEach(sub -> sub.setPid(id));

        mainMapper.insert(doc);
        mainMapper.addSub(subList);

        //插入附件
        List<RecAttachment> uploadImageList = doc.getUploadImageList();
        Long time = System.currentTimeMillis();
        for (RecAttachment attachment : uploadImageList) {
            attachment.setPid(id);
            attachment.setTime(time);
        }
        recService.handleAttachment(uploadImageList, null);

        return Result.success("添加成功", id);
    }

    private Result updateMain(BizPurchaseInbound doc) {
        String docId = doc.getId();

        String err = checkUpdateStatus(docId);
        if (err == null) err = checkOrder(doc.getPid(), doc.getData());
        if (err != null) return Result.fail(err);

        //更新主表
        mainMapper.update(doc);

        //删除旧的子表
        mainMapper.delSubByPid(docId);

        //插入新的子表
        mainMapper.addSub(doc.getData());

        //附件增删
        List<RecAttachment> uploadImageList = doc.getUploadImageList();
        Long time = System.currentTimeMillis();
        for (RecAttachment attachment : uploadImageList) {
            attachment.setPid(docId);
            attachment.setTime(time);
        }
        recService.handleAttachment(uploadImageList, doc.getDeleteImageList());

        return Result.success("修改成功");
    }

    //只有拟定状态的单据才能修改
    private String checkUpdateStatus(String id) {
        BizPurchaseInbound doc = mainMapper.getById(id);
        if (doc == null || !doc.getStatus().equals(DocStatusEnum.DRAFT.getCode())) {
            return "单据状态已更新，请刷新后重试";
        }
        return null;
    }

    //检查采购订单是否已审核、未完成、还有未入库的商品
    private String checkOrder(String pid, List<BizPurchaseInboundSub> docSubList) {
        BizPurchaseOrder order = orderMapper.getById(pid);

        if (order == null
                || !order.getStatus().equals(DocStatusEnum.VERIFIED.getCode())
                || order.getFinish().equals(DocFinishEnum.FINISHED.getCode())
        ) {
            return "采购订单状态异常";
        }

        List<BizPurchaseOrderSub> orderSubList = orderMapper.getSubById(pid);

        if (CollectionUtils.isEmpty(orderSubList)) return "没有找到采购订单的数据";

        //判断入库商品是否合法
        for (BizPurchaseInboundSub inboundSub : docSubList) {
            BizPurchaseOrderSub orderSub = Util.find(orderSubList, t -> t.getCid().equals(inboundSub.getCid()));

            if (orderSub == null) {
                return String.format("入库商品【%s】不在采购订单中", inboundSub.getCname());
            }
            if (orderSub.getRemainNum().compareTo(BigDecimal.ZERO) <= 0) {
                return String.format("入库商品【%s】已全部入库", inboundSub.getCname());
            }
            if (orderSub.getRemainNum().compareTo(inboundSub.getNum()) < 0) {
                return String.format("入库商品【%s】的数量超出订单数量", inboundSub.getCname());
            }
        }
        return null;
    }
}
