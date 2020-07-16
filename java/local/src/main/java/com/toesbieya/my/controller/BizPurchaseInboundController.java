package com.toesbieya.my.controller;

import com.toesbieya.my.enumeration.DocStatusEnum;
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
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("document/purchase/inbound")
public class BizPurchaseInboundController {
    @Resource
    private BizPurchaseInboundService service;

    @GetMapping("getById")
    public Result getById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        BizPurchaseInbound inbound = service.getById(id);
        return inbound == null ? Result.fail("获取单据信息失败") : Result.success(inbound);
    }

    @GetMapping("getSubById")
    public Result getSubById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        return Result.success(service.getSubById(id));
    }

    @PostMapping("search")
    public Result search(@RequestBody PurchaseInboundSearch vo) {
        return Result.success(service.search(vo));
    }

    @PostMapping("export")
    public void export(@RequestBody PurchaseInboundSearch vo, HttpServletResponse response) throws Exception {
        service.export(vo, response);
    }

    @PostMapping("add")
    public Result add(@RequestBody BizPurchaseInbound vo) {
        String errMsg = validateSub(vo.getData());
        if (errMsg != null) return Result.fail(errMsg);

        UserVo user = SessionUtil.get();
        vo.setCid(user.getId());
        vo.setCname(user.getName());
        vo.setCtime(System.currentTimeMillis());
        vo.setStatus(DocStatusEnum.DRAFT.getCode());

        return service.add(vo);
    }

    @PostMapping("update")
    public Result update(@RequestBody BizPurchaseInbound vo) {
        String errMsg = validateUpdate(vo);
        if (errMsg == null) errMsg = validateSub(vo.getData());
        if (errMsg != null) return Result.fail(errMsg);

        return service.update(vo);
    }

    @PostMapping("commit")
    public Result commit(@RequestBody BizPurchaseInbound vo) {
        boolean isFirst = StringUtils.isEmpty(vo.getId());

        String errMsg = validateSub(vo.getData());
        if (!isFirst && errMsg == null) errMsg = validateUpdate(vo);
        if (errMsg != null) return Result.fail(errMsg);

        vo.setStatus(DocStatusEnum.WAIT_VERIFY.getCode());
        if (isFirst) {
            UserVo user = SessionUtil.get();
            vo.setCid(user.getId());
            vo.setCname(user.getName());
            vo.setCtime(System.currentTimeMillis());
        }
        return service.commit(vo);
    }

    @PostMapping("withdraw")
    public Result withdraw(@RequestBody DocumentStatusUpdate vo) {
        return service.withdraw(vo, SessionUtil.get());
    }

    @PostMapping("pass")
    public Result pass(@RequestBody DocumentStatusUpdate vo) {
        if (StringUtils.isEmpty(vo.getPid())) return Result.fail("参数错误");
        return service.pass(vo, SessionUtil.get());
    }

    @PostMapping("reject")
    public Result reject(@RequestBody DocumentStatusUpdate vo) {
        return service.reject(vo, SessionUtil.get());
    }

    @GetMapping("del")
    public Result del(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        return service.del(id);
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
        int len = list.size();
        for (int i = 0; i < len; i++) {
            BizPurchaseInboundSub sub = list.get(i);
            if (sub.getNum() == null || sub.getNum().compareTo(BigDecimal.ZERO) <= 0) {
                return String.format("第%d个入库商品数量有误", i);
            }
            if (sub.getCid() == null || StringUtils.isEmpty(sub.getCname())) {
                return String.format("第%d个入库商品分类有误", i);
            }
        }
        return null;
    }
}
