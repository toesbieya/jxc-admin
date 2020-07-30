package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.RecUserAction;
import cn.toesbieya.jxc.model.vo.search.UserActionSearch;

import java.util.List;

public interface RecUserActionMapper {
    void insert(RecUserAction action);

    List<RecUserAction> search(UserActionSearch vo);
}
