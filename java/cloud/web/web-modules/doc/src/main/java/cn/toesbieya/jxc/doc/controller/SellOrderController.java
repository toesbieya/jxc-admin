package cn.toesbieya.jxc.doc.controller;

import cn.toesbieya.jxc.common.model.entity.BizSellOrder;
import cn.toesbieya.jxc.common.model.entity.BizSellOrderSub;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.common.model.vo.UserVo;
import cn.toesbieya.jxc.doc.enumeration.DocStatusEnum;
import cn.toesbieya.jxc.doc.model.vo.DocStatusUpdate;
import cn.toesbieya.jxc.doc.model.vo.SellOrderSearch;
import cn.toesbieya.jxc.doc.model.vo.SellOrderVo;
import cn.toesbieya.jxc.doc.service.SellOrderService;
import cn.toesbieya.jxc.web.common.utils.SessionUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("sell/order")
public class SellOrderController {
    @Resource
    private SellOrderService service;

    @GetMapping("getById")
    public Result getById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        SellOrderVo vo = service.getById(id);
        return vo == null ? Result.fail("获取单据信息失败") : Result.success(vo);
    }

    @GetMapping("getSubById")
    public Result getSubById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        return Result.success(service.getSubById(id));
    }

    @PostMapping("search")
    public Result search(@RequestBody SellOrderSearch vo) {
        return Result.success(service.search(vo));
    }

    @PostMapping("export")
    public void export(@RequestBody SellOrderSearch vo, HttpServletResponse response) throws Exception {
        service.export(vo, response);
    }

    @PostMapping("add")
    public Result add(@RequestBody SellOrderVo vo) {
        if (vo.getCustomerId() == null
                || StringUtils.isEmpty(vo.getCustomerName())
                || vo.getTotal() == null) {
            return Result.fail("参数错误");
        }
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
    public Result update(@RequestBody SellOrderVo vo) {
        String errMsg = validateUpdate(vo);
        if (errMsg == null) errMsg = validateSub(vo.getData());
        if (errMsg != null) return Result.fail(errMsg);

        return service.update(vo);
    }

    @PostMapping("commit")
    public Result commit(@RequestBody SellOrderVo vo) {
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
    public Result withdraw(@RequestBody DocStatusUpdate vo) {
        return service.withdraw(vo, SessionUtil.get());
    }

    @PostMapping("pass")
    public Result pass(@RequestBody DocStatusUpdate vo) {
        return service.pass(vo, SessionUtil.get());
    }

    @PostMapping("reject")
    public Result reject(@RequestBody DocStatusUpdate vo) {
        return service.reject(vo, SessionUtil.get());
    }

    @GetMapping("del")
    public Result del(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        return service.del(id);
    }

    private String validateUpdate(BizSellOrder main) {
        if (StringUtils.isEmpty(main.getId())
                || StringUtils.isEmpty(main.getCustomerId())
                || StringUtils.isEmpty(main.getCustomerName())
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
        int len = list.size();
        for (int i = 0; i < len; i++) {
            BizSellOrderSub sub = list.get(i);
            if (sub.getNum() == null || sub.getNum().compareTo(BigDecimal.ZERO) <= 0) {
                return String.format("第%d个销售商品数量有误", i);
            }
            if (sub.getPrice() == null || sub.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                return String.format("第%d个销售商品价格有误", i);
            }
        }
        return null;
    }
}
