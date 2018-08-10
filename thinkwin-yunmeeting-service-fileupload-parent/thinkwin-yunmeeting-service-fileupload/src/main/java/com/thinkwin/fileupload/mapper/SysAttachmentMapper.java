package com.thinkwin.fileupload.mapper;

import com.thinkwin.common.model.db.SysAttachment;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SysAttachmentMapper extends Mapper<SysAttachment> {
    /**
     * 批量查询图片信息
     * @param map
     * @return
     */
    public List<SysAttachment> selectSysAttachmentInfo(Map map);
}