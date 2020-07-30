package cn.toesbieya.jxc.controller;

import cn.toesbieya.jxc.model.entity.BizSellOutboundSub;
import cn.toesbieya.jxc.utils.SessionUtil;
import cn.toesbieya.jxc.enumeration.DocStatusEnum;
import cn.toesbieya.jxc.model.entity.BizSellOutbound;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.model.vo.search.SellOutboundSearch;
import cn.toesbieya.jxc.model.vo.update.DocumentStatusUpdate;
import cn.toesbieya.jxc.service.BizSellOutboundService;
import cn.toesbieya.jxc.utils.Result;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("document/sell/outbound")
public class BizSellOutboundController {
    @Resource
    private BizSellOutboundService service;

    @GetMapping("getById")
    public Result getById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        BizSellOutbound outbound = service.getById(id);
        return outbound == null ? Result.fail("获取单据信息失败") : Result.success(outbound);
    }

    @GetMapping("getSubById")
    public Result getSubById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        return Result.success(service.getSubById(id));
    }

    @PostMapping("search")
    public Result search(@RequestBody SellOutboundSearch vo) {
        return Result.success(service.search(vo));
    }

    @PostMapping("export")
    public void export(@RequestBody SellOutboundSearch vo, HttpServletResponse response) throws Exception {
        service.export(vo, response);
    }

    @PostMapping("add")
    public Result add(@RequestBody BizSellOutbound vo) {
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
    public Result update(@RequestBody BizSellOutbound vo) {
        String errMsg = validateUpdate(vo);
        if (errMsg == null) errMsg = validateSub(vo.getData());
        if (errMsg != null) return Result.fail(errMsg);

        return service.update(vo);
    }

    @PostMapping("commit")
    public Result commit(@RequestBody BizSellOutbound vo) {
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
        if (CollectionUtils.isEmpty(list)) {
            return "销售出库单必须要有出库列表";
        }
        int len = list.size();
        for (int i = 0; i < len; i++) {
            BizSellOutboundSub sub = list.get(i);
            if (sub.getSid() == null) {
                return String.format("第%d个出库商品没有选择库存", i);
            }
            if (sub.getNum() == null || sub.getNum().compareTo(BigDecimal.ZERO) <= 0) {
                return String.format("第%d个出库商品数量有误", i);
            }
            if (sub.getCid() == null || StringUtils.isEmpty(sub.getCname())) {
                return String.format("第%d个出库商品数量有误", i);
            }
        }
        return null;
    }
}
