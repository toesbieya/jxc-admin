package cn.toesbieya.jxc.service.doc;

import cn.toesbieya.jxc.annoation.Lock;
import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.enumeration.DocHistoryEnum;
import cn.toesbieya.jxc.enumeration.DocStatusEnum;
import cn.toesbieya.jxc.mapper.BizDocHistoryMapper;
import cn.toesbieya.jxc.mapper.BizPurchaseOrderMapper;
import cn.toesbieya.jxc.mapper.BizPurchaseOrderSubMapper;
import cn.toesbieya.jxc.model.entity.BizDocHistory;
import cn.toesbieya.jxc.model.entity.BizPurchaseOrder;
import cn.toesbieya.jxc.model.entity.BizPurchaseOrderSub;
import cn.toesbieya.jxc.model.entity.RecAttachment;
import cn.toesbieya.jxc.model.vo.PurchaseOrderVo;
import cn.toesbieya.jxc.model.vo.R;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.model.vo.export.PurchaseOrderExport;
import cn.toesbieya.jxc.model.vo.result.PageResult;
import cn.toesbieya.jxc.model.vo.search.PurchaseOrderSearch;
import cn.toesbieya.jxc.model.vo.update.DocStatusUpdate;
import cn.toesbieya.jxc.service.RecService;
import cn.toesbieya.jxc.util.DocUtil;
import cn.toesbieya.jxc.util.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
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
    private BizPurchaseOrderSubMapper subMapper;
    @Resource
    private BizDocHistoryMapper historyMapper;
    @Resource
    private RecService recService;

    //组装子表、附件列表的数据
    public PurchaseOrderVo getById(String id) {
        BizPurchaseOrder main = mainMapper.selectById(id);

        if (main == null) return null;

        PurchaseOrderVo vo = new PurchaseOrderVo(main);

        vo.setData(getSubById(id));
        vo.setImageList(recService.getAttachmentByPid(id));

        return vo;
    }

    //根据主表ID获取子表
    public List<BizPurchaseOrderSub> getSubById(String id) {
        return subMapper.selectList(
                Wrappers.lambdaQuery(BizPurchaseOrderSub.class)
                        .eq(BizPurchaseOrderSub::getPid, id)
        );
    }

    public PageResult<BizPurchaseOrder> search(PurchaseOrderSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageResult<>(mainMapper.selectList(getSearchCondition(vo)));
    }

    public void export(PurchaseOrderSearch vo, HttpServletResponse response) throws Exception {
        List<PurchaseOrderExport> list = mainMapper.export(getSearchCondition(vo));
        ExcelUtil.exportSimply(list, response, "采购订单导出");
    }

    @UserAction("'添加采购订单'")
    @Transactional(rollbackFor = Exception.class)
    public R add(PurchaseOrderVo doc) {
        return addMain(doc);
    }

    @UserAction("'修改采购订单'+#doc.id")
    @Lock("#doc.id")
    @Transactional(rollbackFor = Exception.class)
    public R update(PurchaseOrderVo doc) {
        return updateMain(doc);
    }

    @UserAction("'提交采购订单'+#doc.id")
    @Lock("#doc.id")
    @Transactional(rollbackFor = Exception.class)
    public R commit(PurchaseOrderVo doc) {
        boolean isFirstCreate = StringUtils.isEmpty(doc.getId());
        R result = isFirstCreate ? addMain(doc) : updateMain(doc);

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

    @UserAction("'撤回采购订单'+#vo.id")
    @Lock("#vo.id")
    @Transactional(rollbackFor = Exception.class)
    public R withdraw(DocStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();

        if (rejectById(id) < 1) {
            return R.fail("撤回失败，请刷新重试");
        }

        historyMapper.insert(
                BizDocHistory.builder()
                        .pid(id)
                        .type(DocHistoryEnum.WITHDRAW.getCode())
                        .uid(user.getId())
                        .uname(user.getNickName())
                        .statusBefore(DocStatusEnum.WAIT_VERIFY.getCode())
                        .statusAfter(DocStatusEnum.DRAFT.getCode())
                        .time(System.currentTimeMillis())
                        .info(info)
                        .build()
        );

        return R.success("撤回成功");
    }

    @UserAction("'通过采购订单'+#vo.id")
    @Lock("#vo.id")
    @Transactional(rollbackFor = Exception.class)
    public R pass(DocStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();
        long now = System.currentTimeMillis();

        if (0 == mainMapper.update(
                null,
                Wrappers.lambdaUpdate(BizPurchaseOrder.class)
                        .set(BizPurchaseOrder::getStatus, DocStatusEnum.VERIFIED.getCode())
                        .set(BizPurchaseOrder::getVid, user.getId())
                        .set(BizPurchaseOrder::getVname, user.getNickName())
                        .set(BizPurchaseOrder::getVtime, now)
                        .eq(BizPurchaseOrder::getId, id)
                        .eq(BizPurchaseOrder::getStatus, DocStatusEnum.WAIT_VERIFY.getCode())
        )) {
            return R.fail("通过失败，请刷新重试");
        }

        historyMapper.insert(
                BizDocHistory.builder()
                        .pid(id)
                        .type(DocHistoryEnum.PASS.getCode())
                        .uid(user.getId())
                        .uname(user.getNickName())
                        .statusBefore(DocStatusEnum.WAIT_VERIFY.getCode())
                        .statusAfter(DocStatusEnum.VERIFIED.getCode())
                        .time(now)
                        .info(info)
                        .build()
        );

        return R.success("通过成功");
    }

    @UserAction("'驳回采购订单'+#vo.id")
    @Lock("#vo.id")
    @Transactional(rollbackFor = Exception.class)
    public R reject(DocStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();

        if (rejectById(id) < 1) {
            return R.fail("驳回失败，请刷新重试");
        }

        historyMapper.insert(
                BizDocHistory.builder()
                        .pid(id)
                        .type(DocHistoryEnum.REJECT.getCode())
                        .uid(user.getId())
                        .uname(user.getNickName())
                        .statusBefore(DocStatusEnum.WAIT_VERIFY.getCode())
                        .statusAfter(DocStatusEnum.DRAFT.getCode())
                        .time(System.currentTimeMillis())
                        .info(info)
                        .build()
        );

        return R.success("驳回成功");
    }

    @UserAction("'删除采购订单'+#id")
    @Lock("#id")
    @Transactional(rollbackFor = Exception.class)
    public R del(String id) {
        if (mainMapper.deleteById(id) < 1) {
            return R.fail("删除失败");
        }

        //同时删除子表和附件
        delSubByPid(id);
        recService.delAttachmentByPid(id);

        return R.success("删除成功");
    }

    private R addMain(PurchaseOrderVo doc) {
        String id = DocUtil.getDocId("CGDD");

        if (StringUtils.isEmpty(id)) {
            return R.fail("获取单号失败");
        }

        doc.setId(id);

        List<BizPurchaseOrderSub> subList = doc.getData();

        //设置子表的pid、剩余未出库数量
        for (BizPurchaseOrderSub sub : subList) {
            sub.setPid(id);
            sub.setRemainNum(sub.getNum());
        }

        //插入主表和子表
        mainMapper.insert(doc);
        subMapper.insertBatch(subList);

        //插入附件
        List<RecAttachment> uploadImageList = doc.getUploadImageList();
        Long time = System.currentTimeMillis();
        for (RecAttachment attachment : uploadImageList) {
            attachment.setPid(id);
            attachment.setTime(time);
        }
        recService.handleAttachment(uploadImageList, null);

        return R.success("添加成功", id);
    }

    private R updateMain(PurchaseOrderVo doc) {
        String docId = doc.getId();

        String err = checkUpdateStatus(docId);
        if (err != null) return R.fail(err);

        //更新主表
        mainMapper.update(
                null,
                Wrappers.lambdaUpdate(BizPurchaseOrder.class)
                        .set(BizPurchaseOrder::getSid, doc.getSid())
                        .set(BizPurchaseOrder::getSname, doc.getSname())
                        .set(BizPurchaseOrder::getStatus, doc.getStatus())
                        .set(BizPurchaseOrder::getTotal, doc.getTotal())
                        .set(BizPurchaseOrder::getRemark, doc.getRemark())
                        .eq(BizPurchaseOrder::getId, docId)
        );

        //删除旧的子表
        delSubByPid(docId);

        //插入新的子表
        List<BizPurchaseOrderSub> subList = doc.getData();
        subList.forEach(sub -> sub.setRemainNum(sub.getNum()));
        subMapper.insertBatch(subList);

        //附件增删
        List<RecAttachment> uploadImageList = doc.getUploadImageList();
        Long time = System.currentTimeMillis();
        for (RecAttachment attachment : uploadImageList) {
            attachment.setPid(docId);
            attachment.setTime(time);
        }
        recService.handleAttachment(uploadImageList, doc.getDeleteImageList());

        return R.success("修改成功");
    }

    //只有拟定状态的单据才能修改
    private String checkUpdateStatus(String id) {
        BizPurchaseOrder doc = mainMapper.selectById(id);
        if (doc == null || !doc.getStatus().equals(DocStatusEnum.DRAFT.getCode())) {
            return "单据状态已更新，请刷新后重试";
        }
        return null;
    }

    //根据主表ID删除子表
    private void delSubByPid(String pid) {
        subMapper.delete(
                Wrappers.lambdaQuery(BizPurchaseOrderSub.class)
                        .eq(BizPurchaseOrderSub::getPid, pid)
        );
    }

    //驳回单据，只有等待审核单据的才能被驳回
    private int rejectById(String id) {
        return mainMapper.update(
                null,
                Wrappers.lambdaUpdate(BizPurchaseOrder.class)
                        .set(BizPurchaseOrder::getStatus, DocStatusEnum.DRAFT.getCode())
                        .eq(BizPurchaseOrder::getId, id)
                        .eq(BizPurchaseOrder::getStatus, DocStatusEnum.WAIT_VERIFY.getCode())
        );
    }

    private Wrapper<BizPurchaseOrder> getSearchCondition(PurchaseOrderSearch vo) {
        Integer sid = vo.getSid();
        String sname = vo.getSname();
        String finish = vo.getFinish();
        Long ftimeStart = vo.getFtimeStart();
        Long ftimeEnd = vo.getFtimeEnd();

        return DocUtil.baseCondition(BizPurchaseOrder.class, vo)
                .eq(sid != null, BizPurchaseOrder::getSid, sid)
                .like(!StringUtils.isEmpty(sname), BizPurchaseOrder::getSname, sname)
                .inSql(!StringUtils.isEmpty(finish), BizPurchaseOrder::getFinish, finish)
                .ge(ftimeStart != null, BizPurchaseOrder::getCtime, ftimeStart)
                .le(ftimeEnd != null, BizPurchaseOrder::getCtime, ftimeEnd);
    }
}
