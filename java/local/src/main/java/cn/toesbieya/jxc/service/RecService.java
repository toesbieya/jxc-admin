package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.mapper.RecAttachmentMapper;
import cn.toesbieya.jxc.mapper.RecLoginHistoryMapper;
import cn.toesbieya.jxc.mapper.RecUserActionMapper;
import cn.toesbieya.jxc.model.entity.RecAttachment;
import cn.toesbieya.jxc.model.entity.RecLoginHistory;
import cn.toesbieya.jxc.model.entity.RecUserAction;
import cn.toesbieya.jxc.model.vo.result.PageResult;
import cn.toesbieya.jxc.model.vo.search.LoginHistorySearch;
import cn.toesbieya.jxc.model.vo.search.UserActionSearch;
import cn.toesbieya.jxc.utils.IpUtil;
import cn.toesbieya.jxc.utils.QiniuUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecService {
    @Resource
    private RecLoginHistoryMapper loginHistoryMapper;
    @Resource
    private RecUserActionMapper userActionMapper;
    @Resource
    private RecAttachmentMapper attachmentMapper;

    public PageResult<RecLoginHistory> searchLoginHistory(LoginHistorySearch vo) {
        Integer uid = vo.getUid();
        String uname = vo.getUname();
        Integer type = vo.getType();
        String ip = vo.getIp();
        String address = vo.getAddress();
        Long startTime = vo.getStartTime();
        Long endTime = vo.getEndTime();

        Wrapper<RecLoginHistory> wrapper =
                Wrappers.lambdaQuery(RecLoginHistory.class)
                        .eq(uid != null, RecLoginHistory::getUid, uid)
                        .like(!StringUtils.isEmpty(uname), RecLoginHistory::getUname, vo.getUname())
                        .eq(type != null, RecLoginHistory::getType, type)
                        .eq(!StringUtils.isEmpty(ip), RecLoginHistory::getIp, vo.getIp())
                        .like(!StringUtils.isEmpty(address), RecLoginHistory::getAddress, vo.getAddress())
                        .ge(startTime != null, RecLoginHistory::getTime, startTime)
                        .le(endTime != null, RecLoginHistory::getTime, endTime)
                        .orderByDesc(RecLoginHistory::getTime);

        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageResult<>(loginHistoryMapper.selectList(wrapper));
    }

    public PageResult<RecUserAction> searchUserAction(UserActionSearch vo) {
        Integer uid = vo.getUid();
        String uname = vo.getUname();
        String type = vo.getType();
        String ip = vo.getIp();
        String url = vo.getUrl();
        Long startTime = vo.getStartTime();
        Long endTime = vo.getEndTime();

        Wrapper<RecUserAction> wrapper =
                Wrappers.lambdaQuery(RecUserAction.class)
                        .eq(uid != null, RecUserAction::getUid, uid)
                        .like(!StringUtils.isEmpty(uname), RecUserAction::getUname, uname)
                        .inSql(!StringUtils.isEmpty(type), RecUserAction::getType, type)
                        .eq(!StringUtils.isEmpty(ip), RecUserAction::getIp, ip)
                        .eq(!StringUtils.isEmpty(url), RecUserAction::getUrl, url)
                        .ge(startTime != null, RecUserAction::getTime, startTime)
                        .le(endTime != null, RecUserAction::getTime, endTime)
                        .orderByDesc(RecUserAction::getTime);

        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        return new PageResult<>(userActionMapper.selectList(wrapper));
    }

    public List<RecAttachment> getAttachmentByPid(String pid) {
        if (StringUtils.isEmpty(pid)) {
            return Collections.emptyList();
        }

        return attachmentMapper.selectList(
                Wrappers.lambdaQuery(RecAttachment.class)
                        .eq(RecAttachment::getPid, pid)
                        .orderByAsc(RecAttachment::getSort)
        );
    }

    public void handleAttachment(List<RecAttachment> uploadImageList, List<String> deleteImageList) {
        if (!CollectionUtils.isEmpty(uploadImageList)) {
            attachmentMapper.insertBatch(uploadImageList);
        }
        if (!CollectionUtils.isEmpty(deleteImageList)) {
            attachmentMapper.delete(
                    Wrappers.lambdaQuery(RecAttachment.class)
                            .in(RecAttachment::getUrl, deleteImageList)
            );
            QiniuUtil.deleteBatch(deleteImageList);
        }
    }

    public void delAttachmentByPid(String pid) {
        List<RecAttachment> list = attachmentMapper.selectList(
                Wrappers.lambdaQuery(RecAttachment.class)
                        .eq(RecAttachment::getPid, pid)
        );

        if (CollectionUtils.isEmpty(list)) return;

        attachmentMapper.delete(Wrappers.lambdaQuery(RecAttachment.class).eq(RecAttachment::getPid, pid));

        List<String> urls = list.stream().map(RecAttachment::getUrl).collect(Collectors.toList());
        QiniuUtil.deleteBatch(urls);
    }

    @Async("dbInsertExecutor")
    public void insertLoginHistory(RecLoginHistory history) {
        if (StringUtils.isEmpty(history.getAddress())) {
            history.setAddress(IpUtil.getIpAddress(history.getIp()));
        }
        history.setId(null);
        loginHistoryMapper.insert(history);
    }

    @Async("dbInsertExecutor")
    public void insertUserAction(RecUserAction action) {
        action.setId(null);
        userActionMapper.insert(action);
    }
}
