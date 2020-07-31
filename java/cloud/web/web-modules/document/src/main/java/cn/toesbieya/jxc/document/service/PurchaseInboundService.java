package cn.toesbieya.jxc.document.service;

import cn.toesbieya.jxc.api.service.RecordApi;
import cn.toesbieya.jxc.api.service.StockApi;
import cn.toesbieya.jxc.api.vo.AttachmentOperation;
import cn.toesbieya.jxc.common.exception.JsonResultException;
import cn.toesbieya.jxc.common.model.entity.*;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.common.model.vo.UserVo;
import cn.toesbieya.jxc.common.utils.Util;
import cn.toesbieya.jxc.document.enumeration.DocFinishEnum;
import cn.toesbieya.jxc.document.enumeration.DocHistoryEnum;
import cn.toesbieya.jxc.document.enumeration.DocStatusEnum;
import cn.toesbieya.jxc.document.mapper.*;
import cn.toesbieya.jxc.document.model.vo.DocStatusUpdate;
import cn.toesbieya.jxc.document.model.vo.PurchaseInboundExport;
import cn.toesbieya.jxc.document.model.vo.PurchaseInboundSearch;
import cn.toesbieya.jxc.document.model.vo.PurchaseInboundVo;
import cn.toesbieya.jxc.document.util.DocUtil;
import cn.toesbieya.jxc.web.common.annoation.Lock;
import cn.toesbieya.jxc.web.common.annoation.UserAction;
import cn.toesbieya.jxc.web.common.model.vo.PageResult;
import cn.toesbieya.jxc.web.common.utils.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import org.apache.dubbo.config.annotation.Reference;
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
public class PurchaseInboundService {
    @Resource
    private PurchaseInboundMapper mainMapper;
    @Resource
    private PurchaseInboundSubMapper subMapper;
    @Resource
    private PurchaseOrderMapper orderMapper;
    @Resource
    private PurchaseOrderSubMapper orderSubMapper;
    @Resource
    private DocumentHistoryMapper historyMapper;
    @Reference
    private StockApi stockApi;
    @Reference
    private RecordApi recordApi;

    //组装子表、附件列表的数据
    public PurchaseInboundVo getById(String id) {
        BizPurchaseInbound main = mainMapper.selectById(id);

        if (main == null) return null;

        PurchaseInboundVo vo = new PurchaseInboundVo(main);

        vo.setData(this.getSubById(id));
        vo.setImageList(recordApi.getAttachmentByPid(id));

        return vo;
    }

    //根据主表ID获取子表
    public List<BizPurchaseInboundSub> getSubById(String id) {
        return subMapper.selectList(
                Wrappers.lambdaQuery(BizPurchaseInboundSub.class)
                        .eq(BizPurchaseInboundSub::getPid, id)
        );
    }

    public PageResult<BizPurchaseInbound> search(PurchaseInboundSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageResult<>(mainMapper.selectList(getSearchCondition(vo)));
    }

    public void export(PurchaseInboundSearch vo, HttpServletResponse response) throws Exception {
        List<PurchaseInboundExport> list = mainMapper.export(getSearchCondition(vo));
        ExcelUtil.exportSimply(list, response, "采购入库单导出");
    }

    @UserAction("'添加采购入库单'")
    @Transactional(rollbackFor = Exception.class)
    public Result add(PurchaseInboundVo doc) {
        return addMain(doc);
    }

    @UserAction("'修改采购入库单'+#doc.id")
    @Lock("#doc.id")
    @Transactional(rollbackFor = Exception.class)
    public Result update(PurchaseInboundVo doc) {
        return updateMain(doc);
    }

    @UserAction("'提交采购入库单'+#doc.id")
    @Lock("#doc.id")
    @Transactional(rollbackFor = Exception.class)
    public Result commit(PurchaseInboundVo doc) {
        boolean isFirstCreate = StringUtils.isEmpty(doc.getId());
        Result result = isFirstCreate ? addMain(doc) : updateMain(doc);

        historyMapper.insert(
                BizDocHistory.builder()
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
    public Result withdraw(DocStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();

        if (this.rejectById(id) < 1) {
            return Result.fail("撤回失败，请刷新重试");
        }

        historyMapper.insert(
                BizDocHistory.builder()
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
    public Result pass(DocStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();
        String pid = vo.getPid();
        long now = System.currentTimeMillis();

        //检查采购订单状态、入库商品是否符合订单要求
        List<BizPurchaseInboundSub> subList = this.getSubById(vo.getId());
        String err = checkOrder(pid, subList);
        if (err != null) return Result.fail("通过失败，" + err);

        if (1 > mainMapper.update(
                null,
                Wrappers.lambdaUpdate(BizPurchaseInbound.class)
                        .set(BizPurchaseInbound::getStatus, DocStatusEnum.VERIFIED.getCode())
                        .set(BizPurchaseInbound::getVid, user.getId())
                        .set(BizPurchaseInbound::getVname, user.getName())
                        .set(BizPurchaseInbound::getVtime, now)
                        .eq(BizPurchaseInbound::getId, id)
                        .eq(BizPurchaseInbound::getStatus, DocStatusEnum.WAIT_VERIFY.getCode())
        )) {
            return Result.fail("通过失败，请刷新重试");
        }

        //修改采购订单完成情况、子表的未入库数量
        List<BizStock> stockList = new ArrayList<>();
        DocFinishEnum finish = DocFinishEnum.FINISHED;

        List<BizPurchaseOrderSub> orderSubList = this.getOrderSubListByPid(pid);

        //生成需要入库的商品列表
        for (BizPurchaseOrderSub orderSub : orderSubList) {
            if (orderSub.getRemainNum().equals(BigDecimal.ZERO)) {
                continue;
            }

            BizPurchaseInboundSub inboundSub = Util.find(subList, i -> i.getCid().equals(orderSub.getCid()));

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
            orderSubMapper.update(
                    null,
                    Wrappers.lambdaUpdate(BizPurchaseOrderSub.class)
                            .set(BizPurchaseOrderSub::getRemainNum, gap)
                            .eq(BizPurchaseOrderSub::getId, orderSub.getId())
            );
        }

        //更新采购订单完成情况
        if (1 > orderMapper.update(
                null,
                Wrappers.lambdaUpdate(BizPurchaseOrder.class)
                        .set(BizPurchaseOrder::getFinish, finish.getCode())
                        .set(BizPurchaseOrder::getFtime, finish == DocFinishEnum.FINISHED ? now : null)
                        .eq(BizPurchaseOrder::getId, pid)
                        .eq(BizPurchaseOrder::getStatus, DocStatusEnum.VERIFIED.getCode())
        )) {
            throw new JsonResultException("通过失败，采购订单状态有误，请刷新重试");
        }

        //插入库存
        stockApi.inbound(stockList);

        historyMapper.insert(
                BizDocHistory
                        .builder()
                        .pid(id).type(DocHistoryEnum.PASS.getCode())
                        .uid(user.getId()).uname(user.getName())
                        .statusBefore(DocStatusEnum.WAIT_VERIFY.getCode())
                        .statusAfter(DocStatusEnum.VERIFIED.getCode())
                        .time(now).info(info)
                        .build()
        );

        return Result.success("通过成功");
    }

    @UserAction("'驳回采购入库单'+#vo.id")
    @Lock("#vo.id")
    @Transactional(rollbackFor = Exception.class)
    public Result reject(DocStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();

        if (this.rejectById(id) < 1) {
            return Result.fail("驳回失败，请刷新重试");
        }

        historyMapper.insert(
                BizDocHistory.builder()
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
        if (mainMapper.deleteById(id) < 1) {
            return Result.fail("删除失败");
        }

        //同时删除子表和附件
        this.delSubByPid(id);
        recordApi.delAttachmentByPid(id);

        return Result.success("删除成功");
    }

    private Result addMain(PurchaseInboundVo doc) {
        List<BizPurchaseInboundSub> subList = doc.getData();

        String err = checkOrder(doc.getPid(), subList);
        if (err != null) return Result.fail(err);

        String id = DocUtil.getDocumentID("CGRK");

        if (StringUtils.isEmpty(id)) {
            return Result.fail("获取单号失败");
        }

        doc.setId(id);

        //设置子表的pid
        subList.forEach(sub -> sub.setPid(id));

        mainMapper.insert(doc);
        subMapper.insertBatch(subList);

        //插入附件
        List<RecAttachment> uploadImageList = doc.getUploadImageList();
        Long time = System.currentTimeMillis();
        for (RecAttachment attachment : uploadImageList) {
            attachment.setPid(id);
            attachment.setTime(time);
        }
        recordApi.handleAttachment(new AttachmentOperation(uploadImageList, null));

        return Result.success("添加成功", id);
    }

    private Result updateMain(PurchaseInboundVo doc) {
        String docId = doc.getId();

        String err = checkUpdateStatus(docId);
        if (err == null) err = checkOrder(doc.getPid(), doc.getData());
        if (err != null) return Result.fail(err);

        //更新主表
        mainMapper.update(
                null,
                Wrappers.lambdaUpdate(BizPurchaseInbound.class)
                        .set(BizPurchaseInbound::getPid, doc.getPid())
                        .set(BizPurchaseInbound::getStatus, doc.getStatus())
                        .set(BizPurchaseInbound::getRemark, doc.getRemark())
                        .eq(BizPurchaseInbound::getId, docId)
        );

        //删除旧的子表
        this.delSubByPid(docId);

        //插入新的子表
        subMapper.insertBatch(doc.getData());

        //附件增删
        List<RecAttachment> uploadImageList = doc.getUploadImageList();
        Long time = System.currentTimeMillis();
        for (RecAttachment attachment : uploadImageList) {
            attachment.setPid(docId);
            attachment.setTime(time);
        }
        recordApi.handleAttachment(new AttachmentOperation(uploadImageList, doc.getDeleteImageList()));

        return Result.success("修改成功");
    }

    //只有拟定状态的单据才能修改
    private String checkUpdateStatus(String id) {
        BizPurchaseInbound doc = mainMapper.selectById(id);
        if (doc == null || !doc.getStatus().equals(DocStatusEnum.DRAFT.getCode())) {
            return "单据状态已更新，请刷新后重试";
        }
        return null;
    }

    //检查采购订单是否已审核、未完成、还有未入库的商品
    private String checkOrder(String pid, List<BizPurchaseInboundSub> docSubList) {
        BizPurchaseOrder order = orderMapper.selectById(pid);

        if (order == null
                || !order.getStatus().equals(DocStatusEnum.VERIFIED.getCode())
                || order.getFinish().equals(DocFinishEnum.FINISHED.getCode())
        ) {
            return "采购订单状态异常";
        }

        List<BizPurchaseOrderSub> orderSubList = this.getOrderSubListByPid(pid);

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

    //根据主表ID删除子表
    private void delSubByPid(String pid) {
        subMapper.delete(
                Wrappers.lambdaQuery(BizPurchaseInboundSub.class)
                        .eq(BizPurchaseInboundSub::getPid, pid)
        );
    }

    //驳回单据，只有等待审核单据的才能被驳回
    private int rejectById(String id) {
        return mainMapper.update(
                null,
                Wrappers.lambdaUpdate(BizPurchaseInbound.class)
                        .set(BizPurchaseInbound::getStatus, DocStatusEnum.DRAFT.getCode())
                        .eq(BizPurchaseInbound::getId, id)
                        .eq(BizPurchaseInbound::getStatus, DocStatusEnum.WAIT_VERIFY.getCode())
        );
    }

    private List<BizPurchaseOrderSub> getOrderSubListByPid(String pid) {
        return orderSubMapper.selectList(
                Wrappers.lambdaQuery(BizPurchaseOrderSub.class)
                        .eq(BizPurchaseOrderSub::getPid, pid)
        );
    }

    private Wrapper<BizPurchaseInbound> getSearchCondition(PurchaseInboundSearch vo) {
        String pid = vo.getPid();
        String pidFuzzy = vo.getPidFuzzy();

        return DocUtil.baseCondition(BizPurchaseInbound.class, vo)
                .eq(pid != null, BizPurchaseInbound::getPid, pid)
                .like(!StringUtils.isEmpty(pidFuzzy), BizPurchaseInbound::getPid, pidFuzzy);
    }
}
