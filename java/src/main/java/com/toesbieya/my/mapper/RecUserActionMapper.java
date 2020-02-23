package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.RecUserAction;
import com.toesbieya.my.model.vo.search.UserActionSearch;

import java.util.List;

public interface RecUserActionMapper {
    void insert(RecUserAction action);

    List<RecUserAction> search(UserActionSearch vo);
}
