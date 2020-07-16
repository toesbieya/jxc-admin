package cn.toesbieya.jxc.message.service;

import cn.toesbieya.jxc.common.model.entity.Msg;
import cn.toesbieya.jxc.common.model.entity.MsgState;
import cn.toesbieya.jxc.common.model.entity.SysUser;
import cn.toesbieya.jxc.common.model.vo.Result;
import cn.toesbieya.jxc.message.mapper.MsgMapper;
import cn.toesbieya.jxc.message.mapper.MsgStateMapper;
import cn.toesbieya.jxc.message.model.vo.MsgPersonalSearch;
import cn.toesbieya.jxc.web.common.annoation.UserAction;
import cn.toesbieya.jxc.web.common.model.vo.PageResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MsgUserService {
    @Resource
    private MsgMapper msgMapper;
    @Resource
    private MsgStateMapper msgStateMapper;

    public PageResult<Msg> search(MsgPersonalSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());

        List<Msg> list = vo.isUnread() ? msgMapper.getUnreadByUser(vo) : msgMapper.getReadByUser(vo);

        PageResult<Msg> result = new PageResult<>(list);

        if (vo.isUnread()) {
            result.setData(msgMapper.getUnreadCountByUser(vo));
        }

        return result;
    }

    public Result read(SysUser user, int id) {
        //如果该消息已读，直接返回
        if (0 != msgStateMapper.selectCount(
                Wrappers.lambdaQuery(MsgState.class)
                        .eq(MsgState::getId, id)
                        .eq(MsgState::getMid, user.getId())
        )) {
            return Result.success();
        }

        MsgState state = new MsgState();
        state.setMid(id);
        state.setUid(user.getId());
        state.setTime(System.currentTimeMillis());

        return Result.success(msgStateMapper.insert(state));
    }

    @UserAction("'清空所有未读消息'")
    public Result readAll(SysUser user) {
        MsgPersonalSearch vo = new MsgPersonalSearch();
        vo.setUid(user.getId());
        vo.setCtime(user.getCtime());

        MsgState state = new MsgState();
        state.setUid(user.getId());
        state.setTime(System.currentTimeMillis());

        List<Msg> list = msgMapper.getUnreadByUser(vo);

        for (Msg msg : list) {
            state.setMid(msg.getId());
            msgStateMapper.insert(state);
        }

        return Result.success();
    }
}
