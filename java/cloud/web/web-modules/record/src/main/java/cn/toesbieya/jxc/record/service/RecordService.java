package cn.toesbieya.jxc.record.service;

import cn.toesbieya.jxc.api.service.RecordApi;
import cn.toesbieya.jxc.api.vo.AttachmentOperation;
import cn.toesbieya.jxc.common.model.entity.RecAttachment;
import cn.toesbieya.jxc.common.model.entity.RecLoginHistory;
import cn.toesbieya.jxc.common.model.entity.RecUserAction;
import cn.toesbieya.jxc.record.mapper.RecAttachmentMapper;
import cn.toesbieya.jxc.record.mapper.RecLoginHistoryMapper;
import cn.toesbieya.jxc.record.mapper.RecUserActionMapper;
import cn.toesbieya.jxc.record.model.vo.LoginHistorySearch;
import cn.toesbieya.jxc.record.model.vo.UserActionSearch;
import cn.toesbieya.jxc.web.common.model.vo.PageResult;
import cn.toesbieya.jxc.web.common.utils.IpUtil;
import cn.toesbieya.jxc.web.common.utils.QiniuUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
@org.apache.dubbo.config.annotation.Service
public class RecordService implements RecordApi {
    @Resource
    private RecLoginHistoryMapper loginHistoryMapper;
    @Resource
    private RecUserActionMapper userActionMapper;
    @Resource
    private RecAttachmentMapper attachmentMapper;
    @Resource
    private QiniuUtil qiniuUtil;

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
        List<RecLoginHistory> list = loginHistoryMapper.selectList(wrapper);
        return new PageResult<>(list);
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
        List<RecUserAction> list = userActionMapper.selectList(wrapper);
        return new PageResult<>(list);
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

    @Transactional(rollbackFor = Exception.class)
    public void handleAttachment(AttachmentOperation vo) {
        List<RecAttachment> uploadImageList = vo.getUploadImageList();
        List<String> deleteImageList = vo.getDeleteImageList();

        if (uploadImageList != null && uploadImageList.size() > 0) {
            attachmentMapper.insertBatch(uploadImageList);
        }

        if (deleteImageList != null && deleteImageList.size() > 0) {
            attachmentMapper.delete(
                    Wrappers.lambdaQuery(RecAttachment.class)
                            .in(RecAttachment::getUrl, deleteImageList)
            );

            qiniuUtil.deleteBatch(deleteImageList.toArray(new String[0]));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delAttachmentByPid(String pid) {
        List<String> urls = attachmentMapper.getUrlByPid(pid);

        if (urls.size() == 0) return;

        attachmentMapper.delete(Wrappers.lambdaQuery(RecAttachment.class).eq(RecAttachment::getPid, pid));

        qiniuUtil.deleteBatch(urls.toArray(new String[0]));
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
