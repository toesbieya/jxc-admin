package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.model.entity.BizDocumentHistory;
import cn.toesbieya.jxc.model.vo.search.DocumentHistorySearch;
import cn.toesbieya.jxc.mapper.BizDocumentHistoryMapper;
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
