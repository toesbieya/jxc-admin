package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.BizDocumentHistory;
import cn.toesbieya.jxc.model.vo.search.DocumentHistorySearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BizDocumentHistoryMapper {
    List<BizDocumentHistory> getByPid(@Param("pid") String pid);

    List<BizDocumentHistory> search(DocumentHistorySearch vo);

    int insert(BizDocumentHistory history);
}
