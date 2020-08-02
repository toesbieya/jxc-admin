package cn.toesbieya.jxc.service.doc;

import cn.toesbieya.jxc.mapper.BizDocHistoryMapper;
import cn.toesbieya.jxc.model.entity.BizDocHistory;
import cn.toesbieya.jxc.model.vo.result.PageResult;
import cn.toesbieya.jxc.model.vo.search.DocHistorySearch;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Service
public class BizDocHistoryService {
    @Resource
    private BizDocHistoryMapper mapper;

    public PageResult<BizDocHistory> search(DocHistorySearch vo) {
        String pid=vo.getPid();
        String type = vo.getType();
        String uid = vo.getUid();
        String uname = vo.getUname();

        Wrapper<BizDocHistory> wrapper = Wrappers.lambdaQuery(BizDocHistory.class)
                .eq(!StringUtils.isEmpty(pid), BizDocHistory::getPid, pid)
                .inSql(!StringUtils.isEmpty(type), BizDocHistory::getType, type)
                .inSql(!StringUtils.isEmpty(uid), BizDocHistory::getUid, uid)
                .like(!StringUtils.isEmpty(uname), BizDocHistory::getUname, uname)
                .orderByDesc(BizDocHistory::getId);

        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageResult<>(mapper.selectList(wrapper));
    }
}
