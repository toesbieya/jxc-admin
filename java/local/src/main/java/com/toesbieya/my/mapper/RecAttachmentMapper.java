package com.toesbieya.my.mapper;

import com.toesbieya.my.model.entity.RecAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RecAttachmentMapper {
    List<RecAttachment> getByPid(@Param("pid") String pid);

    List<String> getUrlByPid(@Param("pid") String pid);

    int add(List<RecAttachment> list);

    int del(RecAttachment attachment);

    int delByPid(@Param("pid") String pid);

    int delByUrls(List<String> list);
}
