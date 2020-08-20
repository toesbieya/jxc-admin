package cn.toesbieya.jxc.controller.doc;

import cn.toesbieya.jxc.enumeration.DocStatusEnum;
import cn.toesbieya.jxc.model.entity.BizPurchaseOrder;
import cn.toesbieya.jxc.model.entity.BizPurchaseOrderSub;
import cn.toesbieya.jxc.model.vo.PurchaseOrderVo;
import cn.toesbieya.jxc.model.vo.R;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.model.vo.search.PurchaseOrderSearch;
import cn.toesbieya.jxc.model.vo.update.DocStatusUpdate;
import cn.toesbieya.jxc.service.doc.BizPurchaseOrderService;
import cn.toesbieya.jxc.util.SessionUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("doc/purchase/order")
public class PurchaseOrderController {
    @Resource
    private BizPurchaseOrderService service;

    @GetMapping("getById")
    public R getById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return R.fail("参数错误");
        PurchaseOrderVo vo = service.getById(id);
        return vo == null ? R.fail("获取单据信息失败") : R.success(vo);
    }

    @GetMapping("getSubById")
    public R getSubById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return R.fail("参数错误");
        return R.success(service.getSubById(id));
    }

    @PostMapping("search")
    public R search(@RequestBody PurchaseOrderSearch vo) {
        return R.success(service.search(vo));
    }

    @PostMapping("export")
    public void export(@RequestBody PurchaseOrderSearch vo, HttpServletResponse response) throws Exception {
        service.export(vo, response);
    }

    @PostMapping("add")
    public R add(@RequestBody PurchaseOrderVo vo) {
        if (vo.getSid() == null
                || StringUtils.isEmpty(vo.getSname())
                || vo.getTotal() == null) {
            return R.fail("参数错误");
        }
        String errMsg = validateSub(vo.getData());
        if (errMsg != null) return R.fail(errMsg);

        UserVo user = SessionUtil.get();
        vo.setCid(user.getId());
        vo.setCname(user.getNickName());
        vo.setCtime(System.currentTimeMillis());
        vo.setStatus(DocStatusEnum.DRAFT.getCode());

        return service.add(vo);
    }

    @PostMapping("update")
    public R update(@RequestBody PurchaseOrderVo vo) {
        String errMsg = validateUpdate(vo);
        if (errMsg == null) errMsg = validateSub(vo.getData());
        if (errMsg != null) return R.fail(errMsg);

        return service.update(vo);
    }

    @PostMapping("commit")
    public R commit(@RequestBody PurchaseOrderVo vo) {
        boolean isFirst = StringUtils.isEmpty(vo.getId());

        String errMsg = validateSub(vo.getData());
        if (!isFirst && errMsg == null) errMsg = validateUpdate(vo);
        if (errMsg != null) return R.fail(errMsg);

        vo.setStatus(DocStatusEnum.WAIT_VERIFY.getCode());

        if (isFirst) {
            UserVo user = SessionUtil.get();
            vo.setCid(user.getId());
            vo.setCname(user.getNickName());
            vo.setCtime(System.currentTimeMillis());
        }

        return service.commit(vo);
    }

    @PostMapping("withdraw")
    public R withdraw(@RequestBody DocStatusUpdate vo) {
        return service.withdraw(vo, SessionUtil.get());
    }

    @PostMapping("pass")
    public R pass(@RequestBody DocStatusUpdate vo) {
        return service.pass(vo, SessionUtil.get());
    }

    @PostMapping("reject")
    public R reject(@RequestBody DocStatusUpdate vo) {
        return service.reject(vo, SessionUtil.get());
    }

    @GetMapping("del")
    public R del(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return R.fail("参数错误");
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
