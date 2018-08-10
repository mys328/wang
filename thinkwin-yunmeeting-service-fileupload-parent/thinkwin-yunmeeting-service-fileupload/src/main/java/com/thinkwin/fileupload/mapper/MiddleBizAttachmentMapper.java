package com.thinkwin.fileupload.mapper;

import com.thinkwin.common.model.db.MiddleBizAttachment;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MiddleBizAttachmentMapper extends Mapper<MiddleBizAttachment> {


    List<MiddleBizAttachment> insertTimingTaskTenementFile();
}