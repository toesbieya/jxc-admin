package com.toesbieya.my.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageSerializable;
import com.toesbieya.my.enumeration.RecLoginHistoryEnum;
import com.toesbieya.my.enumeration.UserActionEnum;
import com.toesbieya.my.mapper.RecAttachmentMapper;
import com.toesbieya.my.mapper.RecLoginHistoryMapper;
import com.toesbieya.my.mapper.RecUserActionMapper;
import com.toesbieya.my.model.entity.RecAttachment;
import com.toesbieya.my.model.entity.RecLoginHistory;
import com.toesbieya.my.model.entity.RecUserAction;
import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.model.vo.search.LoginHistorySearch;
import com.toesbieya.my.model.vo.search.UserActionSearch;
import com.toesbieya.my.utils.IpUtil;
import com.toesbieya.my.utils.QiniuUtil;
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

    public PageSerializable<RecLoginHistory> searchLoginHistory(LoginHistorySearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        List<RecLoginHistory> list = loginHistoryMapper.search(vo);
        return new PageSerializable<>(list);
    }

    public PageSerializable<RecUserAction> searchUserAction(UserActionSearch vo) {
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        List<RecUserAction> list = userActionMapper.search(vo);
        return new PageSerializable<>(list);
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
    public void insertLoginHistory(SysUser user, String ip, RecLoginHistoryEnum historyEnum) {
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
