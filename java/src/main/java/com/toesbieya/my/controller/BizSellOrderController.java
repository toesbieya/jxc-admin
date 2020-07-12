package com.toesbieya.my.controller;

import com.toesbieya.my.enumeration.BizDocumentStatusEnum;
import com.toesbieya.my.model.entity.BizSellOrder;
import com.toesbieya.my.model.entity.BizSellOrderSub;
import com.toesbieya.my.model.vo.UserVo;
import com.toesbieya.my.model.vo.search.SellOrderSearch;
import com.toesbieya.my.model.vo.update.DocumentStatusUpdate;
import com.toesbieya.my.service.BizSellOrderService;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.SessionUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("sell/order")
public class BizSellOrderController {
    @Resource
    private BizSellOrderService sellOrderService;

    @GetMapping("getById")
    public Result getById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        BizSellOrder order = sellOrderService.getById(id);
        return order == null ? Result.fail("获取单据信息失败") : Result.success(order);
    }

    @GetMapping("getSubById")
    public Result getSubById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        return Result.success(sellOrderService.getSubById(id));
    }

    @PostMapping("search")
    public Result search(@RequestBody SellOrderSearch vo) {
        return Result.success(sellOrderService.search(vo));
    }

    @PostMapping("export")
    public void export(@RequestBody SellOrderSearch vo, HttpServletResponse response) throws Exception {
        sellOrderService.export(vo, response);
    }

    @PostMapping("add")
    public Result add(@RequestBody BizSellOrder order) {
        if (order.getCustomer_id() == null
                || StringUtils.isEmpty(order.getCustomer_name())
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

        return sellOrderService.add(order);
    }

    @PostMapping("update")
    public Result update(@RequestBody BizSellOrder order) {
        String errMsg = validateUpdate(order);
        if (errMsg == null) errMsg = validateSub(order.getData());
        if (errMsg != null) return Result.fail(errMsg);

        return sellOrderService.update(order);
    }

    @PostMapping("commit")
    public Result commit(@RequestBody BizSellOrder order) {
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

        return sellOrderService.commit(order);
    }

    @PostMapping("withdraw")
    public Result withdraw(@RequestBody DocumentStatusUpdate vo) {
        return sellOrderService.withdraw(vo, SessionUtil.get());
    }

    @PostMapping("pass")
    public Result pass(@RequestBody DocumentStatusUpdate vo) {
        return sellOrderService.pass(vo, SessionUtil.get());
    }

    @PostMapping("reject")
    public Result reject(@RequestBody DocumentStatusUpdate vo) {
        return sellOrderService.reject(vo, SessionUtil.get());
    }

    @GetMapping("del")
    public Result del(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        return sellOrderService.del(id);
    }

    private String validateUpdate(BizSellOrder main) {
        if (StringUtils.isEmpty(main.getId())
                || StringUtils.isEmpty(main.getCustomer_id())
                || StringUtils.isEmpty(main.getCustomer_name())
                || StringUtils.isEmpty(main.getCid())
                || StringUtils.isEmpty(main.getCname())
                || StringUtils.isEmpty(main.getCtime())
                || StringUtils.isEmpty(main.getStatus())
                || main.getTotal() == null
        ) return "参数错误";
        return null;
    }

    private String validateSub(List<BizSellOrderSub> list) {
        if (CollectionUtils.isEmpty(list)) return "销售订单必须要有销售列表";
        int i = 1;
        for (BizSellOrderSub sub : list) {
            if (sub.getPrice() == null || sub.getPrice() <= 0) return "第" + i + "个销售商品价格有误";
            if (sub.getNum() == null || sub.getNum() <= 0) return "第" + i + "个销售商品数量有误";
            i++;
        }
        return null;
    }
}
