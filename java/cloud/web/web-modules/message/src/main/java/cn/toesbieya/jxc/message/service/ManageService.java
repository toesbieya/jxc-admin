package cn.toesbieya.jxc.message.service;

import cn.toesbieya.jxc.common.constant.SocketConstant;
import cn.toesbieya.jxc.common.model.entity.Msg;
import cn.toesbieya.jxc.common.model.vo.R;
import cn.toesbieya.jxc.common.model.vo.SocketEventVo;
import cn.toesbieya.jxc.common.util.WebSocketUtil;
import cn.toesbieya.jxc.message.constant.MsgConstant;
import cn.toesbieya.jxc.message.mapper.MsgMapper;
import cn.toesbieya.jxc.message.model.vo.MsgSearch;
import cn.toesbieya.jxc.web.common.annoation.UserAction;
import cn.toesbieya.jxc.web.common.model.vo.PageResult;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManageService {
    @Resource
    private MsgMapper mapper;

    public PageResult<Msg> search(MsgSearch vo) {
        String title = vo.getTitle();
        String type = vo.getType();
        String status = vo.getStatus();

        Wrapper<Msg> wrapper =
                Wrappers.lambdaQuery(Msg.class)
                        .like(!StringUtils.isEmpty(title), Msg::getTitle, title)
                        .inSql(!StringUtils.isEmpty(type), Msg::getType, type)
                        .inSql(!StringUtils.isEmpty(status), Msg::getStatus, status)
                        .orderByDesc(Msg::getId);

        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageResult<>(mapper.selectList(wrapper));
    }

    @UserAction("'添加消息：'+#msg.title")
    public R add(Msg msg) {
        mapper.insert(msg);
        return R.success("添加成功", msg);
    }

    @UserAction("'修改消息：'+#msg.title")
    public R update(Msg msg) {
        int rows = mapper.update(
                null,
                Wrappers.lambdaUpdate(Msg.class)
                        .set(Msg::getTitle, msg.getTitle())
                        .set(Msg::getContent, msg.getContent())
                        .set(Msg::getType, msg.getType())
                        .set(Msg::getPid, msg.getPid())
                        .set(Msg::getPname, msg.getPname())
                        .set(Msg::getPtime, msg.getPtime())
                        .set(Msg::getWid, msg.getWid())
                        .set(Msg::getWname, msg.getWname())
                        .set(Msg::getWtime, msg.getWtime())
                        .set(Msg::getStatus, msg.getStatus())
                        .set(Msg::isBroadcast, msg.isBroadcast())
                        .set(Msg::getRecipient, msg.getRecipient())
                        .eq(Msg::getId, msg.getId())
        );
        return rows > 0 ? R.success("修改成功") : R.fail("修改失败，请刷新重试");
    }

    @UserAction("'发布消息：'+#msg.title")
    public R publish(Msg msg) {
        boolean isFirstCreate = msg.getId() == null;
        R result = isFirstCreate ? add(msg) : update(msg);

        if (result.isSuccess()) {
            result.setMsg("提交成功");
            result.setData(msg);

            SocketEventVo eventVo = new SocketEventVo();
            eventVo.setEvent(SocketConstant.EVENT_NEW_MESSAGE);

            //发送websocket消息
            if (msg.isBroadcast()) {
                eventVo.setType(SocketConstant.REDIS_EVENT_BROADCAST);
                WebSocketUtil.sendEvent(eventVo);
            }
            else {
                String recipient = msg.getRecipient();

                if (!StringUtils.isEmpty(recipient)) {
                    String[] ids = recipient.split(",");
                    List<Integer> to = Arrays.stream(ids).map(Integer::valueOf).collect(Collectors.toList());

                    eventVo.setType(SocketConstant.REDIS_EVENT_SPECIFIC);
                    eventVo.setTo(to);

                    WebSocketUtil.sendEvent(eventVo);
                }
            }
        }
        else result.setMsg("发布失败，请刷新重试");

        return result;
    }

    @UserAction("'撤回消息：'+#msg.title")
    public R withdraw(Msg msg) {
        int rows = mapper.update(
                null,
                Wrappers.lambdaUpdate(Msg.class)
                        .set(Msg::getStatus, MsgConstant.STATUS_WITHDREW)
                        .set(Msg::getWid, msg.getWid())
                        .set(Msg::getWname, msg.getWname())
                        .set(Msg::getWtime, msg.getWtime())
                        .eq(Msg::getId, msg.getId())
                        .eq(Msg::getStatus, MsgConstant.STATUS_PUBLISHED)
        );
        return rows > 0 ? R.success("撤回成功", msg) : R.fail("撤回失败，请刷新重试");
    }

    @UserAction("'删除消息：'+#title")
    public R del(int id, String title) {
        int rows = mapper.deleteById(id);
        return rows > 0 ? R.success("删除成功") : R.fail("删除失败，请刷新重试");
    }
}
