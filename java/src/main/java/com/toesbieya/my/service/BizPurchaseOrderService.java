package com.toesbieya.my.service;

import com.github.pagehelper.PageHelper;
import com.toesbieya.my.annoation.Lock;
import com.toesbieya.my.annoation.Tx;
import com.toesbieya.my.annoation.UserAction;
import com.toesbieya.my.enumeration.BizDocumentHistoryEnum;
import com.toesbieya.my.enumeration.BizDocumentStatusEnum;
import com.toesbieya.my.mapper.BizDocumentHistoryMapper;
import com.toesbieya.my.mapper.BizPurchaseOrderMapper;
import com.toesbieya.my.model.entity.*;
import com.toesbieya.my.model.vo.export.PurchaseOrderExport;
import com.toesbieya.my.model.vo.result.PageResult;
import com.toesbieya.my.model.vo.search.PurchaseOrderSearch;
import com.toesbieya.my.model.vo.update.DocumentStatusUpdate;
import com.toesbieya.my.utils.ExcelUtil;
import com.toesbieya.my.utils.RedisUtil;
import com.toesbieya.my.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
@Slf4j
public class BizPurchaseOrderService {
    @Resource
    private BizPurchaseOrderMapper purchaseOrderMapper;
    @Resource
    private BizDocumentHistoryMapper documentHistoryMapper;
    @Resource
    private RecService recService;

    public BizPurchaseOrder getById(String id) {
        BizPurchaseOrder order = purchaseOrderMapper.getById(id);
        if (order == null) return null;

        List<BizPurchaseOrderSub> list = purchaseOrderMapper.getSubById(id);
        order.setData(list);
        List<RecAttachment> imageList = recService.getAttachmentByPid(id);
        order.setImageList(imageList);

        return order;
    }

    public List<BizPurchaseOrderSub> getSubById(String id) {
        return purchaseOrderMapper.getSubById(id);
    }

    public PageResult<BizPurchaseOrder> search(PurchaseOrderSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageResult<>(purchaseOrderMapper.search(vo));
    }

    public void export(PurchaseOrderSearch vo, HttpServletResponse response) throws Exception {
        List<PurchaseOrderExport> list = purchaseOrderMapper.export(vo);
        ExcelUtil.exportSimply(list, response, "采购订单导出");
    }

    @UserAction("'添加采购订单'")
    @Tx
    public Result add(BizPurchaseOrder doc) {
        return addOrder(doc);
    }

    @UserAction("'修改采购订单'+#doc.id")
    @Lock("#doc.id")
    @Tx
    public Result update(BizPurchaseOrder doc) {
        return updateOrder(doc);
    }

    @UserAction("'提交采购订单'+#doc.id")
    @Lock("#doc.id")
    @Tx
    public Result commit(BizPurchaseOrder doc) {
        boolean isFirstCreate = StringUtils.isEmpty(doc.getId());
        Result result = isFirstCreate ? addOrder(doc) : updateOrder(doc);

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

    @UserAction("'撤回采购订单'+#vo.id")
    @Lock("#vo.id")
    @Tx
    public Result withdraw(DocumentStatusUpdate vo, SysUser user) {
        String id = vo.getId();
        String info = vo.getInfo();

        if (purchaseOrderMapper.reject(id) < 1) {
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

    @UserAction("'通过采购订单'+#vo.id")
    @Lock("#vo.id")
    @Tx
    public Result pass(DocumentStatusUpdate vo, SysUser user) {
        String id = vo.getId();
        String info = vo.getInfo();
        long now = System.currentTimeMillis();

        if (purchaseOrderMapper.pass(id, user.getId(), user.getName(), now) < 1) {
            return Result.fail("通过失败，请刷新重试");
        }

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

    @UserAction("'驳回采购订单'+#vo.id")
    @Lock("#vo.id")
    @Tx
    public Result reject(DocumentStatusUpdate vo, SysUser user) {
        String id = vo.getId();
        String info = vo.getInfo();

        if (purchaseOrderMapper.reject(id) < 1) {
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

    @UserAction("'删除采购订单'+#id")
    @Lock("#id")
    @Tx
    public Result del(String id) {
        if (purchaseOrderMapper.del(id) < 1) return Result.fail("删除失败");
        purchaseOrderMapper.delSubByPid(id);
        recService.delAttachmentByPid(id);
        return Result.success("删除成功");
    }

    private Result addOrder(BizPurchaseOrder doc) {
        String id = RedisUtil.getDocumentID("CGDD");
        if (StringUtils.isEmpty(id)) return Result.fail("获取单号失败");

        doc.setId(id);
        for (BizPurchaseOrderSub sub : doc.getData()) {
            sub.setPid(id);
            sub.setRemain_num(sub.getNum());
        }

        purchaseOrderMapper.add(doc);
        purchaseOrderMapper.addSub(doc.getData());

        List<RecAttachment> uploadImageList = doc.getUploadImageList();
        Long time = System.currentTimeMillis();
        for (RecAttachment attachment : uploadImageList) {
            attachment.setPid(id);
            attachment.setTime(time);
        }
        recService.handleAttachment(uploadImageList, null);

        return Result.success("添加成功", id);
    }

    private Result updateOrder(BizPurchaseOrder doc) {
        String err = checkUpdateStatus(doc.getId());
        if (err != null) return Result.fail(err);

        purchaseOrderMapper.delSubByPid(doc.getId());
        purchaseOrderMapper.update(doc);

        List<BizPurchaseOrderSub> subList = doc.getData();
        for (BizPurchaseOrderSub sub : subList) {
            sub.setRemain_num(sub.getNum());
        }
        purchaseOrderMapper.addSub(subList);

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
        BizPurchaseOrder doc = purchaseOrderMapper.getById(id);
        if (doc == null || !doc.getStatus().equals(0)) return "单据状态已更新，请刷新后重试";
        return null;
    }
}
