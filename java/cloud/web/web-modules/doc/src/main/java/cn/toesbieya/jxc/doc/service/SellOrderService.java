package cn.toesbieya.jxc.doc.service;

import cn.toesbieya.jxc.api.RecordApi;
import cn.toesbieya.jxc.api.StockApi;
import cn.toesbieya.jxc.common.model.entity.*;
import cn.toesbieya.jxc.common.model.vo.R;
import cn.toesbieya.jxc.common.model.vo.StockSearch;
import cn.toesbieya.jxc.common.model.vo.StockSearchResult;
import cn.toesbieya.jxc.common.model.vo.UserVo;
import cn.toesbieya.jxc.doc.enumeration.DocHistoryEnum;
import cn.toesbieya.jxc.doc.enumeration.DocStatusEnum;
import cn.toesbieya.jxc.doc.mapper.DocHistoryMapper;
import cn.toesbieya.jxc.doc.mapper.SellOrderMapper;
import cn.toesbieya.jxc.doc.mapper.SellOrderSubMapper;
import cn.toesbieya.jxc.doc.model.vo.DocStatusUpdate;
import cn.toesbieya.jxc.doc.model.vo.SellOrderExport;
import cn.toesbieya.jxc.doc.model.vo.SellOrderSearch;
import cn.toesbieya.jxc.doc.model.vo.SellOrderVo;
import cn.toesbieya.jxc.doc.util.DocUtil;
import cn.toesbieya.jxc.web.common.annoation.Lock;
import cn.toesbieya.jxc.web.common.annoation.UserAction;
import cn.toesbieya.jxc.web.common.model.vo.PageResult;
import cn.toesbieya.jxc.web.common.util.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellOrderService {
    @Resource
    private SellOrderMapper mainMapper;
    @Resource
    private SellOrderSubMapper subMapper;
    @Resource
    private DocHistoryMapper historyMapper;
    @Resource
    private StockApi stockApi;
    @Resource
    private RecordApi recordApi;

    //组装子表、附件列表的数据
    public SellOrderVo getById(String id) {
        BizSellOrder main = mainMapper.selectById(id);

        if (main == null) return null;

        SellOrderVo vo = new SellOrderVo(main);

        vo.setData(getSubById(id));
        vo.setImageList(recordApi.getAttachmentByPid(id));

        return vo;
    }

    //根据主表ID获取子表
    public List<BizSellOrderSub> getSubById(String id) {
        return subMapper.selectList(
                Wrappers.lambdaQuery(BizSellOrderSub.class)
                        .eq(BizSellOrderSub::getPid, id)
        );
    }

    public PageResult<BizSellOrder> search(SellOrderSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageResult<>(mainMapper.selectList(getSearchCondition(vo)));
    }

    public void export(SellOrderSearch vo, HttpServletResponse response) throws Exception {
        List<SellOrderExport> list = mainMapper.export(getSearchCondition(vo));
        ExcelUtil.exportSimply(list, response, "销售订单导出");
    }

    @UserAction("'添加销售订单'")
    @Transactional(rollbackFor = Exception.class)
    public R add(SellOrderVo doc) {
        return addMain(doc);
    }

    @UserAction("'修改销售订单'+#doc.id")
    @Lock("#doc.id")
    @Transactional(rollbackFor = Exception.class)
    public R update(SellOrderVo doc) {
        return updateMain(doc);
    }

    @UserAction("'提交销售订单'+#doc.id")
    @Lock("#doc.id")
    @Transactional(rollbackFor = Exception.class)
    public R commit(SellOrderVo doc) {
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

    @UserAction("'撤回销售订单'+#vo.id")
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
                        .uname(user.getName())
                        .statusBefore(DocStatusEnum.WAIT_VERIFY.getCode())
                        .statusAfter(DocStatusEnum.DRAFT.getCode())
                        .time(System.currentTimeMillis())
                        .info(info)
                        .build()
        );

        return R.success("撤回成功");
    }

    @UserAction("'通过销售订单'+#vo.id")
    @Lock("#vo.id")
    @Transactional(rollbackFor = Exception.class)
    public R pass(DocStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();
        long now = System.currentTimeMillis();

        if (0 == mainMapper.update(
                null,
                Wrappers.lambdaUpdate(BizSellOrder.class)
                        .set(BizSellOrder::getStatus, DocStatusEnum.VERIFIED.getCode())
                        .set(BizSellOrder::getVid, user.getId())
                        .set(BizSellOrder::getVname, user.getName())
                        .set(BizSellOrder::getVtime, now)
                        .eq(BizSellOrder::getId, id)
                        .eq(BizSellOrder::getStatus, DocStatusEnum.WAIT_VERIFY.getCode())
        )) {
            return R.fail("通过失败，请刷新重试");
        }

        historyMapper.insert(
                BizDocHistory.builder()
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

        return R.success("通过成功");
    }

    @UserAction("'驳回销售订单'+#vo.id")
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
                        .uname(user.getName())
                        .statusBefore(DocStatusEnum.WAIT_VERIFY.getCode())
                        .statusAfter(DocStatusEnum.DRAFT.getCode())
                        .time(System.currentTimeMillis())
                        .info(info)
                        .build()
        );

        return R.success("驳回成功");
    }

    @UserAction("'删除销售订单'+#id")
    @Lock("#id")
    @Transactional(rollbackFor = Exception.class)
    public R del(String id) {
        if (mainMapper.deleteById(id) < 1) {
            return R.fail("删除失败");
        }

        //同时删除子表和附件
        delSubByPid(id);
        recordApi.delAttachmentByPid(id);

        return R.success("删除成功");
    }

    private R addMain(SellOrderVo doc) {
        String err = checkStock(doc.getData());
        if (err != null) return R.fail(err);

        String id = DocUtil.getDocId("XSDD");

        if (StringUtils.isEmpty(id)) {
            return R.fail("获取单号失败");
        }

        doc.setId(id);

        List<BizSellOrderSub> subList = doc.getData();

        for (BizSellOrderSub sub : subList) {
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
        recordApi.handleAttachment(uploadImageList, null);

        return R.success("添加成功", id);
    }

    private R updateMain(SellOrderVo doc) {
        String docId = doc.getId();

        String err = checkUpdateStatus(docId);
        if (err == null) err = checkStock(doc.getData());
        if (err != null) return R.fail(err);

        //更新主表
        mainMapper.update(
                null,
                Wrappers.lambdaUpdate(BizSellOrder.class)
                        .set(BizSellOrder::getCustomerId, doc.getCustomerId())
                        .set(BizSellOrder::getCustomerName, doc.getCustomerName())
                        .set(BizSellOrder::getStatus, doc.getStatus())
                        .set(BizSellOrder::getTotal, doc.getTotal())
                        .set(BizSellOrder::getRemark, doc.getRemark())
                        .eq(BizSellOrder::getId, docId)
        );

        //删除旧的子表
        delSubByPid(docId);

        //插入新的子表
        List<BizSellOrderSub> subList = doc.getData();
        subList.forEach(sub -> sub.setRemainNum(sub.getNum()));
        subMapper.insertBatch(subList);

        //附件增删
        List<RecAttachment> uploadImageList = doc.getUploadImageList();
        Long time = System.currentTimeMillis();
        for (RecAttachment attachment : uploadImageList) {
            attachment.setPid(docId);
            attachment.setTime(time);
        }
        recordApi.handleAttachment(uploadImageList, doc.getDeleteImageList());

        return R.success("修改成功");
    }

    //只有拟定状态的单据才能修改
    private String checkUpdateStatus(String id) {
        BizSellOrder doc = mainMapper.selectById(id);
        if (doc == null || !doc.getStatus().equals(DocStatusEnum.DRAFT.getCode())) {
            return "单据状态已更新，请刷新后重试";
        }
        return null;
    }

    //添加、修改、提交、审核通过都必须检验库存是否足够
    private String checkStock(List<BizSellOrderSub> list) {
        List<String> cids = list.stream().map(i -> String.valueOf(i.getCid())).collect(Collectors.toList());
        StockSearch vo = new StockSearch();
        vo.setCids(String.join(",", cids));
        List<StockSearchResult> stockList = stockApi.getByCondition(vo);

        //list长度必然大于等于stockList长度
        int subListLength = list.size();
        int stockListLength = stockList.size();
        if (subListLength != stockListLength) {
            BizSellOrderSub sub = list.get(stockListLength);
            return String.format("商品【%s】库存不足", sub.getCname());
        }

        list.sort(Comparator.comparing(BizDocSub::getCid));
        stockList.sort(Comparator.comparing(StockSearchResult::getCid));
        for (int i = 0; i < subListLength; i++) {
            BizSellOrderSub sub = list.get(i);
            StockSearchResult stock = stockList.get(i);
            if (sub.getNum().compareTo(stock.getTotalNum()) > 0) {
                return String.format("商品【%s】库存不足", sub.getCname());
            }
        }

        return null;
    }

    //根据主表ID删除子表
    private void delSubByPid(String pid) {
        subMapper.delete(
                Wrappers.lambdaQuery(BizSellOrderSub.class)
                        .eq(BizSellOrderSub::getPid, pid)
        );
    }

    //驳回单据，只有等待审核单据的才能被驳回
    private int rejectById(String id) {
        return mainMapper.update(
                null,
                Wrappers.lambdaUpdate(BizSellOrder.class)
                        .set(BizSellOrder::getStatus, DocStatusEnum.DRAFT.getCode())
                        .eq(BizSellOrder::getId, id)
                        .eq(BizSellOrder::getStatus, DocStatusEnum.WAIT_VERIFY.getCode())
        );
    }

    private Wrapper<BizSellOrder> getSearchCondition(SellOrderSearch vo) {
        Integer customerId = vo.getCustomerId();
        String customerName = vo.getCustomerName();
        String finish = vo.getFinish();
        Long ftimeStart = vo.getFtimeStart();
        Long ftimeEnd = vo.getFtimeEnd();

        return DocUtil.baseCondition(BizSellOrder.class, vo)
                .eq(customerId != null, BizSellOrder::getCustomerId, customerId)
                .like(!StringUtils.isEmpty(customerName), BizSellOrder::getCustomerName, customerName)
                .inSql(!StringUtils.isEmpty(finish), BizSellOrder::getFinish, finish)
                .ge(ftimeStart != null, BizSellOrder::getCtime, ftimeStart)
                .le(ftimeEnd != null, BizSellOrder::getCtime, ftimeEnd);
    }
}
