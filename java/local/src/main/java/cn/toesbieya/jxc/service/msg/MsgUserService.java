package cn.toesbieya.jxc.service.msg;

import cn.toesbieya.jxc.annoation.UserAction;
import cn.toesbieya.jxc.mapper.MsgMapper;
import cn.toesbieya.jxc.mapper.MsgStateMapper;
import cn.toesbieya.jxc.model.entity.Msg;
import cn.toesbieya.jxc.model.entity.MsgState;
import cn.toesbieya.jxc.model.entity.SysUser;
import cn.toesbieya.jxc.model.vo.R;
import cn.toesbieya.jxc.model.vo.result.PageResult;
import cn.toesbieya.jxc.model.vo.search.MsgPersonalSearch;
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
        return new PageResult<>(list);
    }

    public R read(SysUser user, int id) {
        //如果该消息已读，直接返回
        if (!msgStateMapper
                .selectCount(
                        Wrappers.lambdaQuery(MsgState.class)
                                .eq(MsgState::getId, id)
                                .eq(MsgState::getMid, user.getId())
                )
                .equals(0)) {
            return R.success();
        }

        MsgState state = new MsgState();
        state.setMid(id);
        state.setUid(user.getId());
        state.setTime(System.currentTimeMillis());

        return R.success(msgStateMapper.insert(state));
    }

    @UserAction("'清空所有未读消息'")
    public R readAll(SysUser user) {
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

        return R.success();
    }
}
