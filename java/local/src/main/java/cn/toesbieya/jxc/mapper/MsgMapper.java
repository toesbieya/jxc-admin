package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.Msg;
import cn.toesbieya.jxc.model.vo.search.MsgPersonalSearch;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface MsgMapper extends BaseMapper<Msg> {
    List<Msg> getReadByUser(MsgPersonalSearch search);

    List<Msg> getUnreadByUser(MsgPersonalSearch search);
}
