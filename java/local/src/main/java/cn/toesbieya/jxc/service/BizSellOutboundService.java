package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.annoation.Lock;
import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.enumeration.DocFinishEnum;
import cn.toesbieya.jxc.enumeration.DocHistoryEnum;
import cn.toesbieya.jxc.enumeration.DocStatusEnum;
import cn.toesbieya.jxc.exception.JsonResultException;
import cn.toesbieya.jxc.mapper.BizDocumentHistoryMapper;
import cn.toesbieya.jxc.mapper.BizSellOrderMapper;
import cn.toesbieya.jxc.mapper.BizSellOutboundMapper;
import cn.toesbieya.jxc.mapper.BizStockMapper;
import cn.toesbieya.jxc.model.entity.*;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.model.vo.export.SellOutboundExport;
import cn.toesbieya.jxc.model.vo.result.PageResult;
import cn.toesbieya.jxc.model.vo.search.SellOutboundSearch;
import cn.toesbieya.jxc.model.vo.update.DocumentStatusUpdate;
import cn.toesbieya.jxc.utils.DocumentUtil;
import cn.toesbieya.jxc.utils.ExcelUtil;
import cn.toesbieya.jxc.utils.Result;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BizSellOutboundService {
    @Resource
    private BizSellOutboundMapper mainMapper;
    @Resource
    private BizSellOrderMapper orderMapper;
    @Resource
    private BizDocumentHistoryMapper historyMapper;
    @Resource
    private BizStockMapper stockMapper;
    @Resource
    private RecService recService;
    @Resource
    private BizStockService stockService;

    public BizSellOutbound getById(String id) {
        BizSellOutbound main = mainMapper.getById(id);

        if (main == null) return null;

        main.setData(this.getSubById(id));
        main.setImageList(recService.getAttachmentByPid(id));

        return main;
    }

    public List<BizSellOutboundSub> getSubById(String pid) {
        return mainMapper.getSubById(pid);
    }

    public PageResult<BizSellOutbound> search(SellOutboundSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageResult<>(mainMapper.search(vo));
    }

    public void export(SellOutboundSearch vo, HttpServletResponse response) throws Exception {
        List<SellOutboundExport> list = mainMapper.export(vo);
        ExcelUtil.exportSimply(list, response, "销售出库单导出");
    }

    @UserAction("'添加销售出库单'")
    @Transactional(rollbackFor = Exception.class)
    public Result add(BizSellOutbound doc) {
        return addMain(doc);
    }

    @UserAction("'修改销售出库单'+#doc.id")
    @Lock("#doc.id")
    @Transactional(rollbackFor = Exception.class)
    public Result update(BizSellOutbound doc) {
        return updateMain(doc);
    }

    @UserAction("'提交销售出库单'+#doc.id")
    @Lock("#doc.id")
    @Transactional(rollbackFor = Exception.class)
    public Result commit(BizSellOutbound doc) {
        boolean isFirstCreate = StringUtils.isEmpty(doc.getId());
        Result result = isFirstCreate ? addMain(doc) : updateMain(doc);

        historyMapper.insert(
                BizDocumentHistory.builder()
                        .pid(doc.getId())
                        .type(DocHistoryEnum.COMMIT.getCode())
                        .uid(doc.getCid())
                        .uname(doc.getCname())
                        .status_before(DocStatusEnum.DRAFT.getCode())
                        .status_after(DocStatusEnum.WAIT_VERIFY.getCode())
                        .time(System.currentTimeMillis())
                        .build()
        );

        result.setMsg(result.isSuccess() ? "提交成功" : "提交失败，" + result.getMsg());
        return result;
    }

    @UserAction("'撤回销售出库单'+#vo.id")
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
                        .status_before(DocStatusEnum.WAIT_VERIFY.getCode())
                        .status_after(DocStatusEnum.DRAFT.getCode())
                        .time(System.currentTimeMillis())
                        .info(info)
                        .build()
        );

        return Result.success("撤回成功");
    }

    @UserAction("'通过销售出库单'+#vo.id")
    @Lock({"#vo.pid", "#vo.id"})
    @Transactional(rollbackFor = Exception.class)
    public Result pass(DocumentStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();
        String pid = vo.getPid();
        long now = System.currentTimeMillis();

        List<BizSellOutboundSub> subList = this.getSubById(vo.getId());
        String err = check(vo.getPid(), subList);
        if (err != null) return Result.fail("通过失败，" + err);

        if (mainMapper.pass(id, user.getId(), user.getName(), now) < 1) {
            return Result.fail("通过失败，请刷新重试");
        }

        //按分类分组统计出库数量，并出库
        Map<Integer, BigDecimal> outboundCount = new HashMap<>();
        for (BizSellOutboundSub outboundSub : subList) {
            int rows = stockMapper.outbound(outboundSub.getSid(), outboundSub.getNum());
            if (rows == 0) {
                throw new JsonResultException(String.format("通过失败，商品%s库存不足", outboundSub.getCname()));
            }

            Integer cid = outboundSub.getCid();
            BigDecimal num = outboundCount.getOrDefault(cid, BigDecimal.ZERO);
            outboundCount.put(cid, num.add(outboundSub.getNum()));
        }

        //更新销售订单子表的剩余未出库数量，记录销售订单的完成情况
        DocFinishEnum finish = DocFinishEnum.FINISHED;
        List<BizSellOrderSub> orderSubList = orderMapper.getSubById(pid);

        for (BizSellOrderSub orderSub : orderSubList) {
            if (orderSub.getRemain_num().equals(BigDecimal.ZERO)) {
                continue;
            }

            BigDecimal outboundNum = outboundCount.get(orderSub.getCid());

            if (outboundNum == null) continue;

            BigDecimal gap = orderSub.getRemain_num().subtract(outboundNum);

            //如果有任意一个采购商品的remain_num大于采购商品的num，则完成情况为进行中，否则为已完成
            if (gap.compareTo(BigDecimal.ZERO) > 0) {
                finish = DocFinishEnum.UNDERWAY;
            }

            orderMapper.updateSubRemainNum(orderSub.getId(), gap);
        }

        //更新销售订单完成情况
        if (1 > orderMapper.updateFinish(pid, finish.getCode(), finish == DocFinishEnum.FINISHED ? now : null)) {
            throw new JsonResultException("通过失败，销售订单状态有误，请刷新重试");
        }

        historyMapper.insert(
                BizDocumentHistory.builder()
                        .pid(id)
                        .type(DocHistoryEnum.PASS.getCode())
                        .uid(user.getId())
                        .uname(user.getName())
                        .status_before(DocStatusEnum.WAIT_VERIFY.getCode())
                        .status_after(DocStatusEnum.VERIFIED.getCode())
                        .time(now)
                        .info(info)
                        .build()
        );

        return Result.success("通过成功");
    }

    @UserAction("'驳回销售出库单'+#vo.id")
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
                        .status_before(DocStatusEnum.WAIT_VERIFY.getCode())
                        .status_after(DocStatusEnum.DRAFT.getCode())
                        .time(System.currentTimeMillis())
                        .info(info)
                        .build()
        );

        return Result.success("驳回成功");
    }

    @UserAction("'删除销售出库单'+#id")
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

    private Result addMain(BizSellOutbound doc) {
        List<BizSellOutboundSub> subList = doc.getData();

        String err = check(doc.getPid(), subList);
        if (err != null) return Result.fail(err);

        String id = DocumentUtil.getDocumentID("XSCK");

        if (StringUtils.isEmpty(id)) {
            return Result.fail("获取单号失败");
        }

        doc.setId(id);

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

    private Result updateMain(BizSellOutbound doc) {
        String docId = doc.getId();

        String err = checkUpdateStatus(docId);
        if (err == null) err = check(doc.getPid(), doc.getData());
        if (err != null) return Result.fail(err);

        mainMapper.update(doc);
        mainMapper.delSubByPid(docId);
        mainMapper.addSub(doc.getData());

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
        BizSellOutbound doc = mainMapper.getById(id);
        if (doc == null || !doc.getStatus().equals(DocStatusEnum.DRAFT.getCode())) {
            return "单据状态已更新，请刷新后重试";
        }
        return null;
    }

    //添加、修改、通过前都需要检查
    private String check(String pid, List<BizSellOutboundSub> docSubList) {
        BizSellOrder order = orderMapper.getById(pid);

        //检查销售订单是否已审核、未完成
        if (order == null
                || !order.getStatus().equals(DocStatusEnum.VERIFIED.getCode())
                || order.getFinish().equals(DocFinishEnum.FINISHED.getCode())
        ) {
            return "销售订单状态异常";
        }

        List<BizSellOrderSub> orderSubList = orderMapper.getSubById(pid);

        if (CollectionUtils.isEmpty(orderSubList)) {
            return "没有找到销售订单的数据";
        }

        //按分类统计商品的出库数量
        Map<Integer, BigDecimal> outboundCount = this.getOutboundCount(docSubList);

        String[] cids = new String[outboundCount.size()];

        int index = 0;

        //检查出库商品是否符合销售订单
        for (Integer cid : outboundCount.keySet()) {
            BizSellOrderSub orderSub = Util.find(orderSubList, t -> t.getCid().equals(cid));
            if (orderSub == null) {
                return "未在销售订单中找到对应的出库商品";
            }
            if (orderSub.getRemain_num().equals(BigDecimal.ZERO)) {
                return String.format("出库商品【%s】已全部出库", orderSub.getCname());
            }
            if (orderSub.getRemain_num().compareTo(outboundCount.get(cid)) < 0) {
                return String.format("出库商品【%s】的数量超出订单数量", orderSub.getCname());
            }
            cids[index] = String.valueOf(cid);
            index++;
        }

        //获取当前库存，判断库存是否足够
        List<BizStock> stockList = stockService.getDetail(String.join(",", cids));
        for (BizSellOutboundSub sub : docSubList) {
            BizStock stock = Util.find(stockList, i -> i.getId().equals(sub.getSid()));
            if (stock == null || stock.getNum().compareTo(sub.getNum()) < 0) {
                return String.format("出库商品【%s】(采购入库单：%s)库存不足", sub.getCname(), sub.getPid());
            }
        }

        return null;
    }

    private Map<Integer, BigDecimal> getOutboundCount(List<BizSellOutboundSub> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        return list
                .stream()
                .collect(
                        Collectors.groupingBy(
                                BizSellOutboundSub::getCid,
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        BizSellOutboundSub::getNum,
                                        BigDecimal::add
                                )
                        )
                );
    }
}
