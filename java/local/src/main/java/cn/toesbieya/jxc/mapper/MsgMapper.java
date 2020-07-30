package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.Msg;
import cn.toesbieya.jxc.model.entity.MsgState;
import cn.toesbieya.jxc.model.vo.search.MsgPersonalSearch;
import cn.toesbieya.jxc.model.vo.search.MsgSearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MsgMapper {
    List<Msg> search(MsgSearch search);

    int insert(Msg msg);

    int update(Msg msg);

    int withdraw(Msg msg);

    int del(@Param("id") int id);

    int insertState(MsgState state);

    boolean checkRead(@Param("mid") int mid, @Param("uid") int uid);

    List<Msg> getReadByUser(MsgPersonalSearch search);

    List<Msg> getUnreadByUser(MsgPersonalSearch search);
}
