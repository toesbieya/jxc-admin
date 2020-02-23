package com.toesbieya.my.controller;

import com.toesbieya.my.enumeration.BizDocumentStatusEnum;
import com.toesbieya.my.model.entity.BizSellOutbound;
import com.toesbieya.my.model.entity.BizSellOutboundSub;
import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.model.vo.search.SellOutboundSearch;
import com.toesbieya.my.model.vo.update.DocumentStatusUpdate;
import com.toesbieya.my.service.BizSellOutboundService;
import com.toesbieya.my.utils.Result;
import com.toesbieya.my.utils.Util;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("sell/outbound")
public class BizSellOutboundController {
    @Resource
    private BizSellOutboundService sellOutboundService;
    @Resource
    private HttpSession session;

    @GetMapping("getById")
    public Result getById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        BizSellOutbound outbound = sellOutboundService.getById(id);
        return outbound == null ? Result.fail("获取单据信息失败") : Result.success(outbound);
    }

    @GetMapping("getSubById")
    public Result getSubById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        return Result.success(sellOutboundService.getSubById(id));
    }

    @PostMapping("search")
    public Result search(@RequestBody SellOutboundSearch vo) {
        return Result.success(sellOutboundService.search(vo));
    }

    @PostMapping("export")
    public void export(@RequestBody SellOutboundSearch vo, HttpServletResponse response) throws Exception {
        sellOutboundService.export(vo, response);
    }

    @PostMapping("add")
    public Result add(@RequestBody BizSellOutbound outbound) {
        String errMsg = validateSub(outbound.getData());
        if (errMsg != null) return Result.fail(errMsg);

        SysUser sysUser = Util.getUser(session);
        assert sysUser != null;
        outbound.setCid(sysUser.getId());
        outbound.setCname(sysUser.getName());
        outbound.setCtime(System.currentTimeMillis());
        outbound.setStatus(BizDocumentStatusEnum.DRAFT.getCode());

        return sellOutboundService.add(outbound);
    }

    @PostMapping("update")
    public Result update(@RequestBody BizSellOutbound outbound) {
        String errMsg = validateUpdate(outbound);
        if (errMsg == null) errMsg = validateSub(outbound.getData());
        if (errMsg != null) return Result.fail(errMsg);

        return sellOutboundService.update(outbound);
    }

    @PostMapping("commit")
    public Result commit(@RequestBody BizSellOutbound outbound) {
        boolean isFirst = StringUtils.isEmpty(outbound.getId());

        String errMsg = validateSub(outbound.getData());
        if (!isFirst && errMsg == null) errMsg = validateUpdate(outbound);
        if (errMsg != null) return Result.fail(errMsg);

        outbound.setStatus(BizDocumentStatusEnum.WAIT_VERIFY.getCode());
        if (isFirst) {
            SysUser sysUser = Util.getUser(session);
            assert sysUser != null;
            outbound.setCid(sysUser.getId());
            outbound.setCname(sysUser.getName());
            outbound.setCtime(System.currentTimeMillis());
        }
        return sellOutboundService.commit(outbound);
    }

    @PostMapping("withdraw")
    public Result withdraw(@RequestBody DocumentStatusUpdate vo) {
        return sellOutboundService.withdraw(vo, Util.getUser(session));
    }

    @PostMapping("pass")
    public Result pass(@RequestBody DocumentStatusUpdate vo) {
        if (StringUtils.isEmpty(vo.getPid())) return Result.fail("参数错误");
        return sellOutboundService.pass(vo, Util.getUser(session));
    }

    @PostMapping("reject")
    public Result reject(@RequestBody DocumentStatusUpdate vo) {
        return sellOutboundService.reject(vo, Util.getUser(session));
    }

    @GetMapping("del")
    public Result del(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        return sellOutboundService.del(id);
    }

    private String validateUpdate(BizSellOutbound main) {
        if (StringUtils.isEmpty(main.getId())
                || StringUtils.isEmpty(main.getPid())
                || StringUtils.isEmpty(main.getCid())
                || StringUtils.isEmpty(main.getCname())
                || StringUtils.isEmpty(main.getCtime())
                || StringUtils.isEmpty(main.getStatus())
        ) return "参数错误";
        return null;
    }

    private String validateSub(List<BizSellOutboundSub> list) {
        if (CollectionUtils.isEmpty(list)) return "销售出库单必须要有出库列表";
        int i = 1;
        for (BizSellOutboundSub sub : list) {
            if (sub.getSid() == null) return "第" + i + "个出库商品没有选择库存";
            if (sub.getNum() == null || sub.getNum() <= 0) return "第" + i + "个出库商品数量有误";
            if (sub.getCid() == null || StringUtils.isEmpty(sub.getCname())) return "第" + i + "个出库商品数量有误";
            i++;
        }
        return null;
    }
}
