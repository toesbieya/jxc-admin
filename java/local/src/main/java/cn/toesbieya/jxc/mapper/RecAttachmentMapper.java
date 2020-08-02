package cn.toesbieya.jxc.mapper;

import cn.toesbieya.jxc.model.entity.RecAttachment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface RecAttachmentMapper extends BaseMapper<RecAttachment> {
    int insertBatch(List<RecAttachment> list);
}
