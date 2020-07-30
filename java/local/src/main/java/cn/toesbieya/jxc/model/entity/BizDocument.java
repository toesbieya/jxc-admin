package cn.toesbieya.jxc.model.entity;

import lombok.Data;

import java.util.List;

@Data
public class BizDocument<T> {
    private String id;
    private Integer cid;
    private String cname;
    private Long ctime;
    private Integer vid;
    private String vname;
    private Long vtime;
    private Integer status;
    private String remark;
    private List<T> data;
    private List<RecAttachment> imageList;
    private List<RecAttachment> uploadImageList;
    private List<String> deleteImageList;
}
