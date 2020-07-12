package com.toesbieya.my.service;

import com.github.pagehelper.PageHelper;
import com.toesbieya.my.annoation.Lock;
import com.toesbieya.my.annoation.Tx;
import com.toesbieya.my.annoation.UserAction;
import com.toesbieya.my.enumeration.BizDocumentFinishEnum;
import com.toesbieya.my.enumeration.BizDocumentHistoryEnum;
import com.toesbieya.my.enumeration.BizDocumentStatusEnum;
import com.toesbieya.my.exception.JsonResultException;
import com.toesbieya.my.mapper.BizDocumentHistoryMapper;
import com.toesbieya.my.mapper.BizSellOrderMapper;
import com.toesbieya.my.mapper.BizSellOutboundMapper;
import com.toesbieya.my.mapper.BizStockMapper;
import com.toesbieya.my.model.entity.*;
import com.toesbieya.my.model.vo.UserVo;
import com.toesbieya.my.model.vo.export.SellOutboundExport;
import com.toesbieya.my.model.vo.result.PageResult;
import com.toesbieya.my.model.vo.search.SellOutboundSearch;
import com.toesbieya.my.model.vo.update.DocumentStatusUpdate;
import com.toesbieya.my.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BizSellOutboundService {
    @Resource
    private BizSellOutboundMapper sellOutboundMapper;
    @Resource
    private BizSellOrderMapper sellOrderMapper;
    @Resource
    private BizDocumentHistoryMapper documentHistoryMapper;
    @Resource
    private BizStockMapper stockMapper;
    @Resource
    private RecService recService;
    @Resource
    private BizStockService stockService;

    public BizSellOutbound getById(String id) {
        BizSellOutbound outbound = sellOutboundMapper.getById(id);
        if (outbound == null) return null;
        List<BizSellOutboundSub> list = sellOutboundMapper.getSubById(id);
        outbound.setData(list);
        List<RecAttachment> imageList = recService.getAttachmentByPid(id);
        outbound.setImageList(imageList);
        return outbound;
    }

    public List<BizSellOutboundSub> getSubById(String pid) {
        return sellOutboundMapper.getSubById(pid);
    }

    public PageResult<BizSellOutbound> search(SellOutboundSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        List<BizSellOutbound> list = sellOutboundMapper.search(vo);
        return new PageResult<>(list);
    }

    public void export(SellOutboundSearch vo, HttpServletResponse response) throws Exception {
        List<SellOutboundExport> list = sellOutboundMapper.export(vo);
        ExcelUtil.exportSimply(list, response, "销售出库单导出");
    }

    @UserAction("'添加销售出库单'")
    @Tx
    public Result add(BizSellOutbound doc) {
        return addOutbound(doc);
    }

    @UserAction("'修改销售出库单'+#doc.id")
    @Lock("#doc.id")
    @Tx
    public Result update(BizSellOutbound doc) {
        return updateOutbound(doc);
    }

    @UserAction("'提交销售出库单'+#doc.id")
    @Lock("#doc.id")
    @Tx
    public Result commit(BizSellOutbound doc) {
        boolean isFirstCreate = StringUtils.isEmpty(doc.getId());
        Result result = isFirstCreate ? addOutbound(doc) : updateOutbound(doc);

        documentHistoryMapper.add(
                BizDocumentHistory.builder()
                        .pid(doc.getId())
                        .type(BizDocumentHistoryEnum.COMMIT.getCode())
                        .uid(doc.getCid())
                        .uname(doc.getCname())
                        .status_before(BizDocumentStatusEnum.DRAFT.getCode())
                        .status_after(BizDocumentStatusEnum.WAIT_VERIFY.getCode())
                        .time(System.currentTimeMillis())
                        .build()
        );

        result.setMsg(result.isSuccess() ? "提交成功" : "提交失败，" + result.getMsg());
        return result;
    }

    @UserAction("'撤回销售出库单'+#vo.id")
    @Lock("#vo.id")
    @Tx
    public Result withdraw(DocumentStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();

        if (sellOutboundMapper.reject(id) < 1) {
            return Result.fail("撤回失败，请刷新重试");
        }

        documentHistoryMapper.add(
                BizDocumentHistory.builder()
                        .pid(id)
                        .type(BizDocumentHistoryEnum.WITHDRAW.getCode())
                        .uid(user.getId())
                        .uname(user.getName())
                        .status_before(BizDocumentStatusEnum.WAIT_VERIFY.getCode())
                        .status_after(BizDocumentStatusEnum.DRAFT.getCode())
                        .time(System.currentTimeMillis())
                        .info(info)
                        .build()
        );

        return Result.success("撤回成功");
    }

    @UserAction("'通过销售出库单'+#vo.id")
    @Lock({"#vo.pid", "#vo.id"})
    @Tx
    public Result pass(DocumentStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();
        String pid = vo.getPid();
        long now = System.currentTimeMillis();

        List<BizSellOutboundSub> outboundSubList = sellOutboundMapper.getSubById(vo.getId());
        String err = check(vo.getPid(), outboundSubList);
        if (err != null) return Result.fail("通过失败，" + err);

        if (sellOutboundMapper.pass(id, user.getId(), user.getName(), now) < 1) {
            return Result.fail("通过失败，请刷新重试");
        }

        //获取销售订单子表
        List<BizSellOrderSub> orderSubList = sellOrderMapper.getSubById(pid);

        //按分类分组统计出库数量，并出库
        Map<Integer, Double> outboundCount = new HashMap<>();
        for (BizSellOutboundSub outboundSub : outboundSubList) {
            int rows = stockMapper.outbound(outboundSub.getSid(), outboundSub.getNum());
            if (rows == 0) throw new JsonResultException("通过失败，商品" + outboundSub.getCname() + "库存不足");

            Integer cid = outboundSub.getCid();
            Double num = outboundCount.getOrDefault(cid, 0D);
            outboundCount.put(cid, num + outboundSub.getNum());
        }

        //更新销售订单子表的剩余未出库数量，记录销售订单的完成情况
        BizDocumentFinishEnum finish = BizDocumentFinishEnum.FINISHED;
        for (BizSellOrderSub orderSub : orderSubList) {
            if (orderSub.getRemain_num().equals(0D)) continue;
            Double outboundNum = outboundCount.get(orderSub.getCid());
            if (outboundNum == null) continue;

            double gap = orderSub.getRemain_num() - outboundNum;
            if (gap > 0) finish = BizDocumentFinishEnum.UNDERWAY;

            sellOrderMapper.updateSubRemainNum(orderSub.getId(), gap);
        }

        //更新销售订单完成情况
        sellOrderMapper.updateFinish(pid, finish.getCode(), finish == BizDocumentFinishEnum.FINISHED ? now : null);

        documentHistoryMapper.add(
                BizDocumentHistory.builder()
                        .pid(id)
                        .type(BizDocumentHistoryEnum.PASS.getCode())
                        .uid(user.getId())
                        .uname(user.getName())
                        .status_before(BizDocumentStatusEnum.WAIT_VERIFY.getCode())
                        .status_after(BizDocumentStatusEnum.VERIFIED.getCode())
                        .time(now)
                        .info(info)
                        .build()
        );

        return Result.success("通过成功");
    }

    @UserAction("'驳回销售出库单'+#vo.id")
    @Lock("#vo.id")
    @Tx
    public Result reject(DocumentStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();

        if (sellOutboundMapper.reject(id) < 1) {
            return Result.fail("驳回失败，请刷新重试");
        }

        documentHistoryMapper.add(
                BizDocumentHistory.builder()
                        .pid(id)
                        .type(BizDocumentHistoryEnum.REJECT.getCode())
                        .uid(user.getId())
                        .uname(user.getName())
                        .status_before(BizDocumentStatusEnum.WAIT_VERIFY.getCode())
                        .status_after(BizDocumentStatusEnum.DRAFT.getCode())
                        .time(System.currentTimeMillis())
                        .info(info)
                        .build()
        );

        return Result.success("驳回成功");
    }

    @UserAction("'删除销售出库单'+#id")
    @Lock("#id")
    @Tx
    public Result del(String id) {
        if (sellOutboundMapper.del(id) < 1) return Result.fail("删除失败");
        sellOutboundMapper.delSubByPid(id);
        recService.delAttachmentByPid(id);
        return Result.success("删除成功");
    }

    private Result addOutbound(BizSellOutbound doc) {
        String err = check(doc.getPid(), doc.getData());
        if (err != null) return Result.fail(err);

        String id = DocumentUtil.getDocumentID("XSCK");
        if (StringUtils.isEmpty(id)) return Result.fail("获取单号失败");

        doc.setId(id);
        for (BizSellOutboundSub sub : doc.getData()) {
            sub.setPid(id);
        }

        sellOutboundMapper.add(doc);
        sellOutboundMapper.addSub(doc.getData());

        List<RecAttachment> uploadImageList = doc.getUploadImageList();
        Long time = System.currentTimeMillis();
        for (RecAttachment attachment : uploadImageList) {
            attachment.setPid(id);
            attachment.setTime(time);
        }
        recService.handleAttachment(uploadImageList, null);

        return Result.success("添加成功", id);
    }

    private Result updateOutbound(BizSellOutbound doc) {
        String err = checkUpdateStatus(doc.getId());
        if (err == null) err = check(doc.getPid(), doc.getData());
        if (err != null) return Result.fail(err);

        sellOutboundMapper.delSubByPid(doc.getId());
        sellOutboundMapper.update(doc);
        sellOutboundMapper.addSub(doc.getData());

        List<RecAttachment> uploadImageList = doc.getUploadImageList();
        Long time = System.currentTimeMillis();
        for (RecAttachment attachment : uploadImageList) {
            attachment.setPid(doc.getId());
            attachment.setTime(time);
        }
        recService.handleAttachment(uploadImageList, doc.getDeleteImageList());

        return Result.success("修改成功");
    }

    //只有拟定状态的单据才能修改
    private String checkUpdateStatus(String id) {
        BizSellOutbound doc = sellOutboundMapper.getById(id);
        if (doc == null || !doc.getStatus().equals(0)) return "单据状态已更新，请刷新后重试";
        return null;
    }

    //添加、修改、通过前都需要检查
    private String check(String pid, List<BizSellOutboundSub> docSubList) {
        BizSellOrder order = sellOrderMapper.getById(pid);

        //检查销售订单是否已审核、未完成
        if (order == null || order.getStatus() != 2 || order.getFinish() == 2) {
            return "销售订单状态异常";
        }

        List<BizSellOrderSub> orderSubList = sellOrderMapper.getSubById(pid);
        if (orderSubList.size() <= 0) return "没有找到销售订单的数据";

        //按分类统计商品的出库数量
        Map<Integer, Double> outboundCount =
                docSubList
                        .stream()
                        .collect(
                                Collectors.groupingBy(
                                        BizSellOutboundSub::getCid,
                                        Collectors.summingDouble(BizSellOutboundSub::getNum)
                                )
                        );

        String[] cids = new String[outboundCount.size()];

        int index = 0;

        //检查出库商品是否符合销售订单
        for (Integer cid : outboundCount.keySet()) {
            BizSellOrderSub orderSub = Util.find(orderSubList, t -> t.getCid().equals(cid));
            if (orderSub == null) {
                return "未在销售订单中找到对应的出库商品";
            }
            if (orderSub.getRemain_num().equals(0D)) {
                return "出库商品【" + orderSub.getCname() + "】已全部出库";
            }
            if (orderSub.getRemain_num() < outboundCount.get(cid)) {
                return "出库商品【" + orderSub.getCname() + "】的数量超出订单数量";
            }
            cids[index] = String.valueOf(cid);
            index++;
        }

        //获取当前库存，判断库存是否足够
        List<BizStock> stockList = stockService.getDetail(String.join(",", cids));
        for (BizSellOutboundSub sub : docSubList) {
            BizStock stock = Util.find(stockList, i -> i.getId().equals(sub.getSid()));
            if (stock == null || stock.getNum() < sub.getNum()) {
                return "出库商品【" + sub.getCname() + "】(采购入库单：" + sub.getPid() + ")库存不足";
            }
        }

        return null;
    }
}
