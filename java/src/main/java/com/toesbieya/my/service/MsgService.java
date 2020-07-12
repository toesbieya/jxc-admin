package com.toesbieya.my.service;

import com.github.pagehelper.PageHelper;
import com.toesbieya.my.constant.MsgConstant;
import com.toesbieya.my.constant.SocketConstant;
import com.toesbieya.my.mapper.MsgMapper;
import com.toesbieya.my.model.entity.Msg;
import com.toesbieya.my.model.entity.MsgState;
import com.toesbieya.my.model.vo.UserVo;
import com.toesbieya.my.model.vo.result.PageResult;
import com.toesbieya.my.model.vo.search.MsgPersonalSearch;
import com.toesbieya.my.model.vo.search.MsgSearch;
import com.toesbieya.my.module.SocketModule;
import com.toesbieya.my.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

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

    public Result add(Msg msg) {
        msg.setId(msgMapper.insert(msg));
        return Result.success("添加成功", msg);
    }

    public Result update(Msg msg) {
        int rows = msgMapper.update(msg);
        return rows > 0 ? Result.success("修改成功") : Result.fail("修改失败，请刷新重试");
    }

    public Result publish(Msg msg) {
        boolean isFirstCreate = msg.getId() == null;
        Result result = isFirstCreate ? add(msg) : update(msg);
        if (result.isSuccess()) {
            result.setMsg("提交成功");
            result.setData(msg);
            if (msg.getBroadcast().equals(MsgConstant.TO_ALL)) {
                SocketModule.broadcast(SocketConstant.EVENT_NEW_MESSAGE);
            }
            else {
                String recipient = msg.getRecipient();
                if (!StringUtils.isEmpty(recipient)) {
                    for (String id : recipient.split(",")) {
                        SocketModule.sendEvent(SocketConstant.EVENT_NEW_MESSAGE, Integer.valueOf(id));
                    }
                }
            }
        }
        else result.setMsg("发布失败，" + result.getMsg());
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
        PageResult<Msg> result = new PageResult<>(list);
        if (vo.isUnread()) {
            result.setData(msgMapper.getUnreadCountByUser(vo));
        }
        return result;
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
