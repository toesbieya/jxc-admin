package cn.toesbieya.jxc.api;

import cn.toesbieya.jxc.common.model.entity.RecAttachment;
import cn.toesbieya.jxc.common.model.entity.RecLoginHistory;
import cn.toesbieya.jxc.common.model.entity.RecUserAction;

import java.util.List;

public interface RecordApi {
    List<RecAttachment> getAttachmentByPid(String pid);

    void handleAttachment(List<RecAttachment> upload, List<String> delete);

    void delAttachmentByPid(String pid);

    void insertLoginHistory(RecLoginHistory vo);

    void insertUserAction(RecUserAction vo);
}
