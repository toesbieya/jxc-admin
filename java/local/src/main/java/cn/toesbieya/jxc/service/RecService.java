package cn.toesbieya.jxc.service;

import cn.toesbieya.jxc.enumeration.RecLoginHistoryEnum;
import cn.toesbieya.jxc.enumeration.UserActionEnum;
import cn.toesbieya.jxc.model.entity.RecAttachment;
import cn.toesbieya.jxc.model.entity.RecLoginHistory;
import cn.toesbieya.jxc.model.entity.RecUserAction;
import cn.toesbieya.jxc.model.vo.UserVo;
import cn.toesbieya.jxc.model.vo.result.PageResult;
import cn.toesbieya.jxc.model.vo.search.LoginHistorySearch;
import cn.toesbieya.jxc.model.vo.search.UserActionSearch;
import cn.toesbieya.jxc.utils.QiniuUtil;
import com.github.pagehelper.PageHelper;
import cn.toesbieya.jxc.mapper.RecAttachmentMapper;
import cn.toesbieya.jxc.mapper.RecLoginHistoryMapper;
import cn.toesbieya.jxc.mapper.RecUserActionMapper;
import cn.toesbieya.jxc.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        List<RecLoginHistory> list = loginHistoryMapper.search(vo);
        return new PageResult<>(list);
    }

    public PageResult<RecUserAction> searchUserAction(UserActionSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        List<RecUserAction> list = userActionMapper.search(vo);
        return new PageResult<>(list);
    }

    public List<RecAttachment> getAttachmentByPid(String pid) {
        return attachmentMapper.getByPid(pid);
    }

    public void handleAttachment(List<RecAttachment> uploadImageList, List<String> deleteImageList) {
        if (uploadImageList != null && uploadImageList.size() > 0) {
            attachmentMapper.add(uploadImageList);
        }
        if (deleteImageList != null && deleteImageList.size() > 0) {
            attachmentMapper.delByUrls(deleteImageList);
            QiniuUtil.deleteBatch(deleteImageList.toArray(new String[0]));
        }
    }

    public void delAttachmentByPid(String pid) {
        List<String> urls = attachmentMapper.getUrlByPid(pid);
        if (urls.size() == 0) return;
        attachmentMapper.delByPid(pid);
        QiniuUtil.deleteBatch(urls.toArray(new String[0]));
    }

    @Async("dbInsertExecutor")
    public void insertLoginHistory(UserVo user, String ip, RecLoginHistoryEnum historyEnum) {
        RecLoginHistory history = RecLoginHistory.builder()
                .uid(user.getId())
                .uname(user.getName())
                .ip(ip)
                .type(historyEnum.getCode())
                .time(System.currentTimeMillis())
                .build();
        history.setAddress(IpUtil.getIpAddress(ip));
        loginHistoryMapper.insert(history);
    }

    @Async("dbInsertExecutor")
    public void insertUserAction(RecUserAction action, UserActionEnum type) {
        action.setTime(System.currentTimeMillis());
        action.setType(type.getCode());
        userActionMapper.insert(action);
    }
}
