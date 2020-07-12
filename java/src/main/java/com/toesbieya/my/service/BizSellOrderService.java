package com.toesbieya.my.service;

import com.github.pagehelper.PageHelper;
import com.toesbieya.my.annoation.Lock;
import com.toesbieya.my.annoation.Tx;
import com.toesbieya.my.annoation.UserAction;
import com.toesbieya.my.enumeration.BizDocumentHistoryEnum;
import com.toesbieya.my.enumeration.BizDocumentStatusEnum;
import com.toesbieya.my.mapper.BizDocumentHistoryMapper;
import com.toesbieya.my.mapper.BizSellOrderMapper;
import com.toesbieya.my.mapper.BizStockMapper;
import com.toesbieya.my.model.entity.*;
import com.toesbieya.my.model.vo.UserVo;
import com.toesbieya.my.model.vo.export.SellOrderExport;
import com.toesbieya.my.model.vo.result.PageResult;
import com.toesbieya.my.model.vo.result.StockSearchResult;
import com.toesbieya.my.model.vo.search.SellOrderSearch;
import com.toesbieya.my.model.vo.search.StockSearch;
import com.toesbieya.my.model.vo.update.DocumentStatusUpdate;
import com.toesbieya.my.utils.DocumentUtil;
import com.toesbieya.my.utils.ExcelUtil;
import com.toesbieya.my.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BizSellOrderService {
    @Resource
    private BizSellOrderMapper sellOrderMapper;
    @Resource
    private BizStockMapper stockMapper;
    @Resource
    private BizDocumentHistoryMapper documentHistoryMapper;
    @Resource
    private RecService recService;

    public BizSellOrder getById(String id) {
        BizSellOrder order = sellOrderMapper.getById(id);
        if (order == null) return null;

        List<BizSellOrderSub> list = sellOrderMapper.getSubById(id);
        order.setData(list);
        List<RecAttachment> imageList = recService.getAttachmentByPid(id);
        order.setImageList(imageList);

        return order;
    }

    public List<BizSellOrderSub> getSubById(String id) {
        return sellOrderMapper.getSubById(id);
    }

    public PageResult<BizSellOrder> search(SellOrderSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageResult<>(sellOrderMapper.search(vo));
    }

    public void export(SellOrderSearch vo, HttpServletResponse response) throws Exception {
        List<SellOrderExport> list = sellOrderMapper.export(vo);

        ExcelUtil.exportSimply(list, response, "销售订单导出");
    }

    @UserAction("'添加销售订单'")
    @Tx
    public Result add(BizSellOrder doc) {
        return addOrder(doc);
    }

    @UserAction("'修改销售订单'+#doc.id")
    @Lock("#doc.id")
    @Tx
    public Result update(BizSellOrder doc) {
        return updateOrder(doc);
    }

    @UserAction("'提交销售订单'+#doc.id")
    @Lock("#doc.id")
    @Tx
    public Result commit(BizSellOrder doc) {
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

    @UserAction("'撤回销售订单'+#vo.id")
    @Lock("#vo.id")
    @Tx
    public Result withdraw(DocumentStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();

        if (sellOrderMapper.reject(id) < 1) {
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

    @UserAction("'通过销售订单'+#vo.id")
    @Lock("#vo.id")
    @Tx
    public Result pass(DocumentStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();
        long now = System.currentTimeMillis();

        if (sellOrderMapper.pass(id, user.getId(), user.getName(), now) < 1) {
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

    @UserAction("'驳回销售订单'+#vo.id")
    @Lock("#vo.id")
    @Tx
    public Result reject(DocumentStatusUpdate vo, UserVo user) {
        String id = vo.getId();
        String info = vo.getInfo();

        if (sellOrderMapper.reject(id) < 1) {
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

    @UserAction("'删除销售订单'+#id")
    @Lock("#id")
    @Tx
    public Result del(String id) {
        if (sellOrderMapper.del(id) < 1) return Result.fail("删除失败");
        sellOrderMapper.delSubByPid(id);
        recService.delAttachmentByPid(id);
        return Result.success("删除成功");
    }

    private Result addOrder(BizSellOrder doc) {
        String err = checkStock(doc.getData());
        if (err != null) return Result.fail(err);

        String id = DocumentUtil.getDocumentID("XSDD");
        if (StringUtils.isEmpty(id)) return Result.fail("获取单号失败");

        doc.setId(id);
        for (BizSellOrderSub sub : doc.getData()) {
            sub.setPid(id);
            sub.setRemain_num(sub.getNum());
        }

        sellOrderMapper.add(doc);
        sellOrderMapper.addSub(doc.getData());

        List<RecAttachment> uploadImageList = doc.getUploadImageList();
        Long time = System.currentTimeMillis();
        for (RecAttachment attachment : uploadImageList) {
            attachment.setPid(id);
            attachment.setTime(time);
        }
        recService.handleAttachment(uploadImageList, null);

        return Result.success("添加成功", id);
    }

    private Result updateOrder(BizSellOrder doc) {
        String err = checkUpdateStatus(doc.getId());
        if (err == null) err = checkStock(doc.getData());
        if (err != null) return Result.fail(err);

        sellOrderMapper.delSubByPid(doc.getId());
        sellOrderMapper.update(doc);

        List<BizSellOrderSub> subList = doc.getData();
        for (BizSellOrderSub sub : subList) {
            sub.setRemain_num(sub.getNum());
        }
        sellOrderMapper.addSub(subList);

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
        BizSellOrder doc = sellOrderMapper.getById(id);
        if (doc == null || !doc.getStatus().equals(0)) return "单据状态已更新，请刷新后重试";
        return null;
    }

    //添加、修改、提交、审核通过都必须检验库存是否足够
    private String checkStock(List<BizSellOrderSub> list) {
        List<String> cids = list.stream().map(i -> String.valueOf(i.getCid())).collect(Collectors.toList());
        StockSearch vo = new StockSearch();
        vo.setCids(String.join(",", cids));
        List<StockSearchResult> stockList = stockMapper.search(vo);

        //list长度必然大于等于stockList长度
        int subListLength = list.size();
        int stockListLength = stockList.size();
        if (subListLength != stockListLength) {
            BizSellOrderSub sub = list.get(stockListLength);
            return "商品【" + sub.getCname() + "】库存不足";
        }

        list.sort(Comparator.comparing(BizDocumentSub::getCid));
        stockList.sort(Comparator.comparing(StockSearchResult::getCid));
        for (int i = 0; i < subListLength; i++) {
            BizSellOrderSub sub = list.get(i);
            StockSearchResult stock = stockList.get(i);
            if (sub.getNum() > stock.getTotal_num()) {
                return "商品【" + sub.getCname() + "】库存不足";
            }
        }

        return null;
    }
}
