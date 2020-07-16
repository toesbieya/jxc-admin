package cn.toesbieya.jxc.message.mapper;

import cn.toesbieya.jxc.common.model.entity.Msg;
import cn.toesbieya.jxc.message.model.vo.MsgPersonalSearch;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface MsgMapper extends BaseMapper<Msg> {
    List<Msg> getReadByUser(MsgPersonalSearch search);

    List<Msg> getUnreadByUser(MsgPersonalSearch search);

    Integer getUnreadCountByUser(MsgPersonalSearch search);
}
