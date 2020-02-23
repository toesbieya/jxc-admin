package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.RecLoginHistory;
import com.toesbieya.my.model.vo.search.LoginHistorySearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RecLoginHistoryMapper {
    int insert(RecLoginHistory history);

    List<RecLoginHistory> search(LoginHistorySearch vo);

    List<RecLoginHistory> getByUid(@Param("uid") Integer uid);
}
