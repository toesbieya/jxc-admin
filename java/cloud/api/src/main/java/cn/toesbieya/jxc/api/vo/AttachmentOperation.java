package cn.toesbieya.jxc.api.vo;

import cn.toesbieya.jxc.common.model.entity.RecAttachment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentOperation implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<RecAttachment> uploadImageList;
    private List<String> deleteImageList;
}
