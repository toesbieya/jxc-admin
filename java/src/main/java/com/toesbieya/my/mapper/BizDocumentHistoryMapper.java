package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.BizDocumentHistory;
import com.toesbieya.my.model.vo.search.DocumentHistorySearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BizDocumentHistoryMapper {
    List<BizDocumentHistory> getByPid(@Param("pid") String pid);

    List<BizDocumentHistory> search(DocumentHistorySearch vo);

    int add(BizDocumentHistory history);
}
