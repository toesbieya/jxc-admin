package cn.toesbieya.jxc.doc.service;

import cn.toesbieya.jxc.common.model.entity.BizDocHistory;
import cn.toesbieya.jxc.doc.mapper.DocHistoryMapper;
import cn.toesbieya.jxc.doc.model.vo.DocHistorySearch;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocHistoryService {
    @Resource
    private DocHistoryMapper mapper;

    public List<BizDocHistory> search(DocHistorySearch vo) {
        String pid = vo.getPid();
        String type = vo.getType();
        String uid = vo.getUid();
        String uname = vo.getUname();

        Wrapper<BizDocHistory> wrapper = Wrappers.lambdaQuery(BizDocHistory.class)
                .eq(!StringUtils.isEmpty(pid), BizDocHistory::getPid, pid)
                .inSql(!StringUtils.isEmpty(type), BizDocHistory::getType, type)
                .inSql(!StringUtils.isEmpty(uid), BizDocHistory::getUid, uid)
                .like(!StringUtils.isEmpty(uname), BizDocHistory::getUname, uname)
                .orderByDesc(BizDocHistory::getId);

        return mapper.selectList(wrapper);
    }
}
