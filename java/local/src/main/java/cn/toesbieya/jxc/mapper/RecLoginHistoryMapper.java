package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.RecLoginHistory;
import cn.toesbieya.jxc.model.vo.search.LoginHistorySearch;

import java.util.List;

public interface RecLoginHistoryMapper {
    int insert(RecLoginHistory history);

    List<RecLoginHistory> search(LoginHistorySearch vo);
}
