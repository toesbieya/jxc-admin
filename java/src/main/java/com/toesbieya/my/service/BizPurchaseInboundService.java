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
import com.toesbieya.my.mapper.BizPurchaseInboundMapper;
import com.toesbieya.my.mapper.BizPurchaseOrderMapper;
import com.toesbieya.my.mapper.BizStockMapper;
import com.toesbieya.my.model.entity.*;
import com.toesbieya.my.model.vo.export.PurchaseInboundExport;
import com.toesbieya.my.model.vo.result.PageResult;
import com.toesbieya.my.model.vo.search.PurchaseInboundSearch;
import com.toesbieya.my.model.vo.update.DocumentStatusUpdate;
import com.toesbieya.my.utils.ExcelUtil;
import com.toesbieya.my.utils.RedisUtil;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BizPurchaseInboundService {
    @Resource
    private BizPurchaseInboundMapper purchaseInboundMapper;
    @Resource
    private BizPurchaseOrderMapper purchaseOrderMapper;
    @Resource
    private BizDocumentHistoryMapper documentHistoryMapper;
    @Resource
    private BizStockMapper stockMapper;
    @Resource
    private RecService recService;

    public BizPurchaseInbound getById(String id) {
        BizPurchaseInbound inbound = purchaseInboundMapper.getById(id);
        if (inbound == null) return null;
        List<BizPurchaseInboundSub> list = purchaseInboundMapper.getSubById(id);
        inbound.setData(list);
        List<RecAttachment> imageList = recService.getAttachmentByPid(id);
        inbound.setImageList(imageList);
        return inbound;
    }

    public List<BizPurchaseInboundSub> getSubById(String id) {
        return purchaseInboundMapper.getSubById(id);
    }

    public PageResult<BizPurchaseInbound> search(PurchaseInboundSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        List<BizPurchaseInbound> list = purchaseInboundMapper.search(vo);
        return new PageResult<>(list);
    }

    public void export(PurchaseInboundSearch vo, HttpServletResponse response) throws Exception {
        List<PurchaseInboundExport> list = purchaseInboundMapper.export(vo);
        ExcelUtil.exportSimply(list, response, "采购入库单导出");
    }

    @UserAction("'添加采购入库单'")
    @Tx
    public Result add(BizPurchaseInbound doc) {
        return addInbound(doc);
    }

    @UserAction("'修改采购入库单'+#doc.id")
    @Lock("#doc.id")
    @Tx
    public Result update(BizPurchaseInbound doc) {
        return updateInbound(doc);
    }

    @UserAction("'提交采购入库单'+#doc.id")
    @Lock("#doc.id")
    @Tx
    public Result commit(BizPurchaseInbound doc) {
        boolean isFirstCreate = StringUtils.isEmpty(doc.getId());
        Result result = isFirstCreate ? addInbound(doc) : updateInbound(doc);

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

    @UserAction("'撤回采购入库单'+#vo.id")
    @Lock("#vo.id")
    @Tx
    public Result withdraw(DocumentStatusUpdate vo, SysUser user) {
        String id = vo.getId();
        String info = vo.getInfo();

        if (purchaseInboundMapper.reject(id) < 1) {
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

    @UserAction("'通过采购入库单'+#vo.id")
    @Lock({"#vo.pid", "#vo.id"})
    @Tx
    public Result pass(DocumentStatusUpdate vo, SysUser user) {
        String id = vo.getId();
        String info = vo.getInfo();
        String pid = vo.getPid();
        long now = System.currentTimeMillis();

        /*检查采购订单状态、入库商品是否符合订单要求*/
        List<BizPurchaseInboundSub> inboundSubList = purchaseInboundMapper.getSubById(vo.getId());
        String err = checkOrder(vo.getPid(), inboundSubList);
        if (err != null) return Result.fail("通过失败，" + err);

        if (purchaseInboundMapper.pass(id, user.getId(), user.getName(), now) < 1) {
            return Result.fail("通过失败，请刷新重试");
        }

        /*修改采购订单完成情况、子表的未入库数量*/
        List<BizStock> stockList = new ArrayList<>();
        BizDocumentFinishEnum finish = BizDocumentFinishEnum.FINISHED;
        //使用一级缓存
        List<BizPurchaseOrderSub> orderSubList = purchaseOrderMapper.getSubById(pid);
        //如果有任意一个采购商品的remain_num大于采购商品的num，则完成情况为进行中，否则为已完成
        for (BizPurchaseOrderSub orderSub : orderSubList) {
            if (orderSub.getRemain_num().equals(0D)) continue;
            BizPurchaseInboundSub inboundSub = Util.find(inboundSubList, i -> i.getCid().equals(orderSub.getCid()));
            if (inboundSub == null) continue;
            stockList.add(
                    BizStock.builder()
                            .cid(inboundSub.getCid())
                            .cname(inboundSub.getCname())
                            .num(inboundSub.getNum())
                            .price(orderSub.getPrice())
                            .ctime(now)
                            .cgddid(pid)
                            .cgrkid(id)
                            .build()
            );
            double gap = orderSub.getRemain_num() - inboundSub.getNum();
            if (gap > 0) finish = BizDocumentFinishEnum.UNDERWAY;
            //更新采购订单子表
            purchaseOrderMapper.updateSubRemainNum(orderSub.getId(), gap);
        }
        //更新采购订单完成情况
        purchaseOrderMapper.updateFinish(pid, finish.getCode(), finish == BizDocumentFinishEnum.FINISHED ? now : null);

        //插入库存
        if (stockList.size() == 0) throw new JsonResultException("通过失败，入库异常");
        stockMapper.batchInsert(stockList);

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

    @UserAction("'驳回采购入库单'+#vo.id")
    @Lock("#vo.id")
    @Tx
    public Result reject(DocumentStatusUpdate vo, SysUser user) {
        String id = vo.getId();
        String info = vo.getInfo();

        if (purchaseInboundMapper.reject(id) < 1) {
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

    @UserAction("'删除采购入库单'+#id")
    @Lock("#id")
    @Tx
    public Result del(String id) {
        if (purchaseInboundMapper.del(id) < 1) return Result.fail("删除失败");
        purchaseInboundMapper.delSubByPid(id);
        recService.delAttachmentByPid(id);
        return Result.success("删除成功");
    }

    private Result addInbound(BizPurchaseInbound doc) {
        String err = checkOrder(doc.getPid(), doc.getData());
        if (err != null) return Result.fail(err);

        String id = RedisUtil.getDocumentID("CGRK");
        if (StringUtils.isEmpty(id)) return Result.fail("获取单号失败");

        doc.setId(id);
        for (BizPurchaseInboundSub sub : doc.getData()) {
            sub.setPid(id);
        }

        purchaseInboundMapper.add(doc);
        purchaseInboundMapper.addSub(doc.getData());

        List<RecAttachment> uploadImageList = doc.getUploadImageList();
        Long time = System.currentTimeMillis();
        for (RecAttachment attachment : uploadImageList) {
            attachment.setPid(id);
            attachment.setTime(time);
        }
        recService.handleAttachment(uploadImageList, null);

        return Result.success("添加成功", id);
    }

    private Result updateInbound(BizPurchaseInbound doc) {
        String err = checkUpdateStatus(doc.getId());
        if (err == null) err = checkOrder(doc.getPid(), doc.getData());
        if (err != null) return Result.fail(err);

        purchaseInboundMapper.delSubByPid(doc.getId());
        purchaseInboundMapper.update(doc);
        purchaseInboundMapper.addSub(doc.getData());

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
        BizPurchaseInbound doc = purchaseInboundMapper.getById(id);
        if (doc == null || !doc.getStatus().equals(0)) return "单据状态已更新，请刷新后重试";
        return null;
    }

    //检查采购订单是否已审核、未完成、还有未入库的商品
    private String checkOrder(String pid, List<BizPurchaseInboundSub> docSubList) {
        BizPurchaseOrder order = purchaseOrderMapper.getById(pid);
        if (order == null || order.getStatus() != 2 || order.getFinish() == 2) {
            return "采购订单状态异常";
        }
        List<BizPurchaseOrderSub> orderSubList = purchaseOrderMapper.getSubById(pid);
        if (orderSubList.size() <= 0) return "没有找到采购订单的数据";
        for (BizPurchaseInboundSub inboundSub : docSubList) {
            BizPurchaseOrderSub orderSub = Util.find(orderSubList, t -> t.getCid().equals(inboundSub.getCid()));
            if (orderSub == null) {
                return "入库商品【" + inboundSub.getCname() + "】不在采购订单中";
            }
            if (orderSub.getRemain_num().equals(0D)) {
                return "入库商品【" + inboundSub.getCname() + "】已全部入库";
            }
            if (orderSub.getRemain_num() < inboundSub.getNum()) {
                return "入库商品【" + inboundSub.getCname() + "】的数量超出订单数量";
            }
        }
        return null;
    }
}
