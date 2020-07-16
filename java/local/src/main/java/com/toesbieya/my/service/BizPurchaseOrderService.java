package com.toesbieya.my.service;

import com.github.pagehelper.PageHelper;
import com.toesbieya.my.annoation.Lock;
import com.toesbieya.my.annoation.UserAction;
import com.toesbieya.my.enumeration.DocHistoryEnum;
import com.toesbieya.my.enumeration.DocStatusEnum;
import com.toesbieya.my.mapper.BizDocumentHistoryMapper;
import com.toesbieya.my.mapper.BizPurchaseOrderMapper;
import com.toesbieya.my.model.entity.BizDocumentHistory;
import com.toesbieya.my.model.entity.BizPurchaseOrder;
import com.toesbieya.my.model.entity.BizPurchaseOrderSub;
import com.toesbieya.my.model.entity.RecAttachment;
import com.toesbieya.my.model.vo.UserVo;
import com.toesbieya.my.model.vo.export.PurchaseOrderExport;
import com.toesbieya.my.model.vo.result.PageResult;
import com.toesbieya.my.model.vo.search.PurchaseOrderSearch;
import com.toesbieya.my.model.vo.update.DocumentStatusUpdate;
import com.toesbieya.my.utils.DocumentUtil;
import com.toesbieya.my.utils.ExcelUtil;
import com.toesbieya.my.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
@Slf4j
public class BizPurchaseOrderService {
    @Resource
    private BizPurchaseOrderMapper mainMapper;
    @Resource
    private BizDocumentHistoryMapper historyMapper;
    @Resource
    private RecService recService;

    public BizPurchaseOrder getById(String id) {
        BizPurchaseOrder main = mainMapper.getById(id);

        if (main == null) return null;

        main.setData(this.getSubById(id));
        main.setImageList(recService.getAttachmentByPid(id));

        return main;
    }

    public List<BizPurchaseOrderSub> getSubById(String id) {
        return mainMapper.getSubById(id);
    }

    public PageResult<BizPurchaseOrder> search(PurchaseOrderSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageResult<>(mainMapper.search(vo));
    }

    public void export(PurchaseOrderSearch vo, HttpServletResponse response) throws Exception {
        List<PurchaseOrderExport> list = mainMapper.export(vo);
        ExcelUtil.exportSimply(list, response, "采购订单导出");
    }

    @UserAction("'添加采购订单'")
    @Transactional(rollbackFor = Exception.class)
    public Result add(BizPurchaseOrder doc) {
        return addMain(doc);
    }

    @UserAction("'修改采购订单'+#doc.id")
    @Lock("#doc.id")
    @Transactional(rollbackFor = Exception.class)
    public Result update(BizPurchaseOrder doc) {
        return updateMain(doc);
    }

    @UserAction("'提交采购订单'+#doc.id")
    @Lock("#doc.id")
    @Transactional(rollbackFor = Exception.class)
    public Result commit(BizPurchaseOrder doc) {
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

    @UserAction("'撤回采购订单'+#vo.id")
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

    @UserAction("'通过采购订单'+#vo.id")
    @Lock("#vo.id")
    @Transactional(rollbackFor = Exception.class)
    public Result pass(DocumentStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();
        long now = System.currentTimeMillis();

        if (mainMapper.pass(id, user.getId(), user.getName(), now) < 1) {
            return Result.fail("通过失败，请刷新重试");
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

    @UserAction("'驳回采购订单'+#vo.id")
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

    @UserAction("'删除采购订单'+#id")
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

    private Result addMain(BizPurchaseOrder doc) {
        String id = DocumentUtil.getDocumentID("CGDD");

        if (StringUtils.isEmpty(id)) {
            return Result.fail("获取单号失败");
        }

        doc.setId(id);

        List<BizPurchaseOrderSub> subList = doc.getData();

        //设置子表的pid、剩余未出库数量
        for (BizPurchaseOrderSub sub : subList) {
            sub.setPid(id);
            sub.setRemain_num(sub.getNum());
        }

        //插入主表和子表
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

    private Result updateMain(BizPurchaseOrder doc) {
        String docId = doc.getId();

        String err = checkUpdateStatus(docId);
        if (err != null) return Result.fail(err);

        //更新主表
        mainMapper.update(doc);

        //删除旧的子表
        mainMapper.delSubByPid(docId);

        //插入新的子表
        List<BizPurchaseOrderSub> subList = doc.getData();
        subList.forEach(sub -> sub.setRemain_num(sub.getNum()));
        mainMapper.addSub(subList);

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
        BizPurchaseOrder doc = mainMapper.getById(id);
        if (doc == null || !doc.getStatus().equals(DocStatusEnum.DRAFT.getCode())) {
            return "单据状态已更新，请刷新后重试";
        }
        return null;
    }
}
