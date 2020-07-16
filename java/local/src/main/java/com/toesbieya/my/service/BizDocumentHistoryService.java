package com.toesbieya.my.service;

import com.toesbieya.my.mapper.BizDocumentHistoryMapper;
import com.toesbieya.my.model.entity.BizDocumentHistory;
import com.toesbieya.my.model.vo.search.DocumentHistorySearch;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BizDocumentHistoryService {
    @Resource
    private BizDocumentHistoryMapper historyMapper;

    public List<BizDocumentHistory> search(DocumentHistorySearch vo) {
        return historyMapper.search(vo);
    }

    public List<BizDocumentHistory> getByPid(String pid) {
        return historyMapper.getByPid(pid);
    }
}
