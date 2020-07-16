package cn.toesbieya.jxc.record.mapper;

import cn.toesbieya.jxc.common.model.entity.RecAttachment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RecAttachmentMapper extends BaseMapper<RecAttachment> {
    int insertBatch(List<RecAttachment> list);

    List<String> getUrlByPid(@Param("pid") String pid);
}
