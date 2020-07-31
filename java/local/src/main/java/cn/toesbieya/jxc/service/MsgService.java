package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.constant.MsgConstant;
import cn.toesbieya.jxc.constant.SocketConstant;
import cn.toesbieya.jxc.model.entity.Msg;
import cn.toesbieya.jxc.model.entity.MsgState;
import cn.toesbieya.jxc.model.vo.SocketEventVo;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.model.vo.result.PageResult;
import cn.toesbieya.jxc.model.vo.search.MsgPersonalSearch;
import cn.toesbieya.jxc.model.vo.search.MsgSearch;
import cn.toesbieya.jxc.utils.WebSocketUtil;
import com.github.pagehelper.PageHelper;
import cn.toesbieya.jxc.mapper.MsgMapper;
import cn.toesbieya.jxc.model.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MsgService {
    @Resource
    private MsgMapper msgMapper;

    public PageResult<Msg> search(MsgSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        List<Msg> list = msgMapper.search(vo);
        return new PageResult<>(list);
    }

    @UserAction("'添加消息：'+#msg.title")
    public Result add(Msg msg) {
        msgMapper.insert(msg);
        return Result.success("添加成功", msg);
    }

    @UserAction("'修改消息：'+#msg.title")
    public Result update(Msg msg) {
        int rows = msgMapper.update(msg);
        return rows > 0 ? Result.success("修改成功") : Result.fail("修改失败，请刷新重试");
    }

    @UserAction("'发布消息：'+#msg.title")
    public Result publish(Msg msg) {
        boolean isFirstCreate = msg.getId() == null;
        Result result = isFirstCreate ? add(msg) : update(msg);

        if (result.isSuccess()) {
            result.setMsg("提交成功");
            result.setData(msg);

            SocketEventVo eventVo = new SocketEventVo();
            eventVo.setEvent(SocketConstant.EVENT_NEW_MESSAGE);

            if (msg.getBroadcast().equals(MsgConstant.TO_ALL)) {
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

    public Result withdraw(Msg msg) {
        int rows = msgMapper.withdraw(msg);
        return rows > 0 ? Result.success("撤回成功", msg) : Result.fail("撤回失败，请刷新重试");
    }

    public Result del(int id) {
        int rows = msgMapper.del(id);
        return rows > 0 ? Result.success("删除成功") : Result.fail("删除失败，请刷新重试");
    }

    public PageResult<Msg> searchPersonal(MsgPersonalSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        List<Msg> list = vo.isUnread() ? msgMapper.getUnreadByUser(vo) : msgMapper.getReadByUser(vo);
        return new PageResult<>(list);
    }

    public Result read(UserVo user, int id) {
        if (msgMapper.checkRead(id, user.getId())) {
            return Result.success();
        }

        MsgState state = new MsgState();

        state.setMid(id);
        state.setUid(user.getId());
        state.setTime(System.currentTimeMillis());

        return Result.success(msgMapper.insertState(state));
    }

    public Result readAll(UserVo user) {
        MsgPersonalSearch vo = new MsgPersonalSearch();
        vo.setUid(user.getId());
        vo.setCtime(user.getCtime());

        MsgState state = new MsgState();
        state.setUid(user.getId());
        state.setTime(System.currentTimeMillis());

        List<Msg> list = msgMapper.getUnreadByUser(vo);
        for (Msg msg : list) {
            state.setMid(msg.getId());
            msgMapper.insertState(state);
        }

        return Result.success();
    }
}
