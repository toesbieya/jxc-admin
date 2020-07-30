package cn.toesbieya.jxc.controller;

import cn.toesbieya.jxc.enumeration.DocStatusEnum;
import cn.toesbieya.jxc.model.entity.BizPurchaseOrder;
import cn.toesbieya.jxc.model.entity.BizPurchaseOrderSub;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.model.vo.search.PurchaseOrderSearch;
import cn.toesbieya.jxc.model.vo.update.DocumentStatusUpdate;
import cn.toesbieya.jxc.service.BizPurchaseOrderService;
import cn.toesbieya.jxc.utils.Result;
import cn.toesbieya.jxc.utils.SessionUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("document/purchase/order")
public class BizPurchaseOrderController {
    @Resource
    private BizPurchaseOrderService service;

    @GetMapping("getById")
    public Result getById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        BizPurchaseOrder order = service.getById(id);
        return order == null ? Result.fail("获取单据信息失败") : Result.success(order);
    }

    @GetMapping("getSubById")
    public Result getSubById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return Result.fail("参数错误");
        return Result.success(service.getSubById(id));
    }

    @PostMapping("search")
    public Result search(@RequestBody PurchaseOrderSearch vo) {
        return Result.success(service.search(vo));
    }

    @PostMapping("export")
    public void export(@RequestBody PurchaseOrderSearch vo, HttpServletResponse response) throws Exception {
        service.export(vo, response);
    }

    @PostMapping("add")
    public Result add(@RequestBody BizPurchaseOrder vo) {
        if (vo.getSid() == null
                || StringUtils.isEmpty(vo.getSname())
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
    public Result update(@RequestBody BizPurchaseOrder vo) {
        String errMsg = validateUpdate(vo);
        if (errMsg == null) errMsg = validateSub(vo.getData());
        if (errMsg != null) return Result.fail(errMsg);

        return service.update(vo);
    }

    @PostMapping("commit")
    public Result commit(@RequestBody BizPurchaseOrder vo) {
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
        int len = list.size();
        for (int i = 0; i < len; i++) {
            BizPurchaseOrderSub sub = list.get(i);
            if (sub.getNum() == null || sub.getNum().compareTo(BigDecimal.ZERO) <= 0) {
                return String.format("第%d个入库商品数量有误", i);
            }
            if (sub.getPrice() == null || sub.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                return String.format("第%d个采购商品价格有误", i);
            }
        }
        return null;
    }
}
