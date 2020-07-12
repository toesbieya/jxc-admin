package com.toesbieya.my.controller;

import com.toesbieya.my.enumeration.BizDocumentStatusEnum;
import com.toesbieya.my.model.entity.BizPurchaseOrder;
import com.toesbieya.my.model.entity.BizPurchaseOrderSub;
import com.toesbieya.my.model.vo.UserVo;
import com.toesbieya.my.model.vo.search.PurchaseOrderSearch;
import com.toesbieya.my.model.vo.update.DocumentStatusUpdate;
import com.toesbieya.my.service.BizPurchaseOrderService;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.SessionUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("purchase/order")
public class BizPurchaseOrderController {
    @Resource
    private BizPurchaseOrderService purchaseOrderService;

    @GetMapping("getById")
    public Result getById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        BizPurchaseOrder order = purchaseOrderService.getById(id);
        return order == null ? Result.fail("获取单据信息失败") : Result.success(order);
    }

    @GetMapping("getSubById")
    public Result getSubById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        return Result.success(purchaseOrderService.getSubById(id));
    }

    @PostMapping("search")
    public Result search(@RequestBody PurchaseOrderSearch vo) {
        return Result.success(purchaseOrderService.search(vo));
    }

    @PostMapping("export")
    public void export(@RequestBody PurchaseOrderSearch vo, HttpServletResponse response) throws Exception {
        purchaseOrderService.export(vo, response);
    }

    @PostMapping("add")
    public Result add(@RequestBody BizPurchaseOrder order) {
        if (order.getSid() == null
                || StringUtils.isEmpty(order.getSname())
                || order.getTotal() == null) {
            return Result.fail("参数错误");
        }
        String errMsg = validateSub(order.getData());
        if (errMsg != null) return Result.fail(errMsg);

        UserVo user = SessionUtil.get();
        order.setCid(user.getId());
        order.setCname(user.getName());
        order.setCtime(System.currentTimeMillis());
        order.setStatus(BizDocumentStatusEnum.DRAFT.getCode());

        return purchaseOrderService.add(order);
    }

    @PostMapping("update")
    public Result update(@RequestBody BizPurchaseOrder order) {
        String errMsg = validateUpdate(order);
        if (errMsg == null) errMsg = validateSub(order.getData());
        if (errMsg != null) return Result.fail(errMsg);

        return purchaseOrderService.update(order);
    }

    @PostMapping("commit")
    public Result commit(@RequestBody BizPurchaseOrder order) {
        boolean isFirst = StringUtils.isEmpty(order.getId());

        String errMsg = validateSub(order.getData());
        if (!isFirst && errMsg == null) errMsg = validateUpdate(order);
        if (errMsg != null) return Result.fail(errMsg);

        order.setStatus(BizDocumentStatusEnum.WAIT_VERIFY.getCode());

        if (isFirst) {
            UserVo user = SessionUtil.get();
            order.setCid(user.getId());
            order.setCname(user.getName());
            order.setCtime(System.currentTimeMillis());
        }

        return purchaseOrderService.commit(order);
    }

    @PostMapping("withdraw")
    public Result withdraw(@RequestBody DocumentStatusUpdate vo) {
        return purchaseOrderService.withdraw(vo, SessionUtil.get());
    }

    @PostMapping("pass")
    public Result pass(@RequestBody DocumentStatusUpdate vo) {
        return purchaseOrderService.pass(vo, SessionUtil.get());
    }

    @PostMapping("reject")
    public Result reject(@RequestBody DocumentStatusUpdate vo) {
        return purchaseOrderService.reject(vo, SessionUtil.get());
    }

    @GetMapping("del")
    public Result del(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        return purchaseOrderService.del(id);
    }

    private String validateUpdate(BizPurchaseOrder main) {
        if (StringUtils.isEmpty(main.getId())
                || StringUtils.isEmpty(main.getSid())
                || StringUtils.isEmpty(main.getSname())
                || StringUtils.isEmpty(main.getCid())
                || StringUtils.isEmpty(main.getCname())
                || StringUtils.isEmpty(main.getCtime())
                || StringUtils.isEmpty(main.getStatus())
                || main.getTotal() == null
        ) return "参数错误";
        return null;
    }

    private String validateSub(List<BizPurchaseOrderSub> list) {
        if (CollectionUtils.isEmpty(list)) return "采购订单必须要有采购列表";
        int i = 1;
        for (BizPurchaseOrderSub sub : list) {
            if (sub.getPrice() == null || sub.getPrice() <= 0) return "第" + i + "个采购商品价格有误";
            if (sub.getNum() == null || sub.getNum() <= 0) return "第" + i + "个采购商品数量有误";
            i++;
        }
        return null;
    }
}
