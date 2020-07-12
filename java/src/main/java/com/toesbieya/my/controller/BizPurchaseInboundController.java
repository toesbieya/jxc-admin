package com.toesbieya.my.controller;

import com.toesbieya.my.enumeration.BizDocumentStatusEnum;
import com.toesbieya.my.model.entity.BizPurchaseInbound;
import com.toesbieya.my.model.entity.BizPurchaseInboundSub;
import com.toesbieya.my.model.vo.UserVo;
import com.toesbieya.my.model.vo.search.PurchaseInboundSearch;
import com.toesbieya.my.model.vo.update.DocumentStatusUpdate;
import com.toesbieya.my.service.BizPurchaseInboundService;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.SessionUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("purchase/inbound")
public class BizPurchaseInboundController {
    @Resource
    private BizPurchaseInboundService purchaseInboundService;

    @GetMapping("getById")
    public Result getById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        BizPurchaseInbound inbound = purchaseInboundService.getById(id);
        return inbound == null ? Result.fail("获取单据信息失败") : Result.success(inbound);
    }

    @GetMapping("getSubById")
    public Result getSubById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        return Result.success(purchaseInboundService.getSubById(id));
    }

    @PostMapping("search")
    public Result search(@RequestBody PurchaseInboundSearch vo) {
        return Result.success(purchaseInboundService.search(vo));
    }

    @PostMapping("export")
    public void export(@RequestBody PurchaseInboundSearch vo, HttpServletResponse response) throws Exception {
        purchaseInboundService.export(vo, response);
    }

    @PostMapping("add")
    public Result add(@RequestBody BizPurchaseInbound inbound) {
        String errMsg = validateSub(inbound.getData());
        if (errMsg != null) return Result.fail(errMsg);

        UserVo user = SessionUtil.get();
        inbound.setCid(user.getId());
        inbound.setCname(user.getName());
        inbound.setCtime(System.currentTimeMillis());
        inbound.setStatus(BizDocumentStatusEnum.DRAFT.getCode());

        return purchaseInboundService.add(inbound);
    }

    @PostMapping("update")
    public Result update(@RequestBody BizPurchaseInbound inbound) {
        String errMsg = validateUpdate(inbound);
        if (errMsg == null) errMsg = validateSub(inbound.getData());
        if (errMsg != null) return Result.fail(errMsg);

        return purchaseInboundService.update(inbound);
    }

    @PostMapping("commit")
    public Result commit(@RequestBody BizPurchaseInbound inbound) {
        boolean isFirst = StringUtils.isEmpty(inbound.getId());

        String errMsg = validateSub(inbound.getData());
        if (!isFirst && errMsg == null) errMsg = validateUpdate(inbound);
        if (errMsg != null) return Result.fail(errMsg);

        inbound.setStatus(BizDocumentStatusEnum.WAIT_VERIFY.getCode());
        if (isFirst) {
            UserVo user = SessionUtil.get();
            inbound.setCid(user.getId());
            inbound.setCname(user.getName());
            inbound.setCtime(System.currentTimeMillis());
        }
        return purchaseInboundService.commit(inbound);
    }

    @PostMapping("withdraw")
    public Result withdraw(@RequestBody DocumentStatusUpdate vo) {
        return purchaseInboundService.withdraw(vo, SessionUtil.get());
    }

    @PostMapping("pass")
    public Result pass(@RequestBody DocumentStatusUpdate vo) {
        if (StringUtils.isEmpty(vo.getPid())) return Result.fail("参数错误");
        return purchaseInboundService.pass(vo, SessionUtil.get());
    }

    @PostMapping("reject")
    public Result reject(@RequestBody DocumentStatusUpdate vo) {
        return purchaseInboundService.reject(vo, SessionUtil.get());
    }

    @GetMapping("del")
    public Result del(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        return purchaseInboundService.del(id);
    }

    private String validateUpdate(BizPurchaseInbound main) {
        if (StringUtils.isEmpty(main.getId())
                || StringUtils.isEmpty(main.getPid())
                || StringUtils.isEmpty(main.getCid())
                || StringUtils.isEmpty(main.getCname())
                || StringUtils.isEmpty(main.getCtime())
                || StringUtils.isEmpty(main.getStatus())
        ) return "参数错误";
        return null;
    }

    private String validateSub(List<BizPurchaseInboundSub> list) {
        if (CollectionUtils.isEmpty(list)) return "采购入库单必须要有入库列表";
        int i = 1;
        for (BizPurchaseInboundSub sub : list) {
            if (sub.getNum() == null || sub.getNum() <= 0) return "第" + i + "个入库商品数量有误";
            if (sub.getCid() == null || StringUtils.isEmpty(sub.getCname())) return "第" + i + "个入库商品数量有误";
            i++;
        }
        return null;
    }
}
