package cn.toesbieya.jxc.controller.doc;

import cn.toesbieya.jxc.enumeration.DocStatusEnum;
import cn.toesbieya.jxc.model.entity.BizSellOutbound;
import cn.toesbieya.jxc.model.entity.BizSellOutboundSub;
import cn.toesbieya.jxc.model.vo.R;
import cn.toesbieya.jxc.model.vo.SellOutboundVo;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.model.vo.search.SellOutboundSearch;
import cn.toesbieya.jxc.model.vo.update.DocStatusUpdate;
import cn.toesbieya.jxc.service.doc.BizSellOutboundService;
import cn.toesbieya.jxc.util.SessionUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("doc/sell/outbound")
public class SellOutboundController {
    @Resource
    private BizSellOutboundService service;

    @GetMapping("getById")
    public R getById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return R.fail("参数错误");
        SellOutboundVo vo = service.getById(id);
        return vo == null ? R.fail("获取单据信息失败") : R.success(vo);
    }

    @GetMapping("getSubById")
    public R getSubById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) return R.fail("参数错误");
        return R.success(service.getSubById(id));
    }

    @PostMapping("search")
    public R search(@RequestBody SellOutboundSearch vo) {
        return R.success(service.search(vo));
    }

    @PostMapping("export")
    public void export(@RequestBody SellOutboundSearch vo, HttpServletResponse response) throws Exception {
        service.export(vo, response);
    }

    @PostMapping("add")
    public R add(@RequestBody SellOutboundVo vo) {
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
    public R update(@RequestBody SellOutboundVo vo) {
        String errMsg = validateUpdate(vo);
        if (errMsg == null) errMsg = validateSub(vo.getData());
        if (errMsg != null) return R.fail(errMsg);

        return service.update(vo);
    }

    @PostMapping("commit")
    public R commit(@RequestBody SellOutboundVo vo) {
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
        if (StringUtils.isEmpty(vo.getPid())) return R.fail("参数错误");
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
