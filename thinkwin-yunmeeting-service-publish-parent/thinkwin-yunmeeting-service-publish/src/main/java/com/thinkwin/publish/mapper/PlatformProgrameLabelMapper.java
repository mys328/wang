package com.thinkwin.publish.mapper;

import com.thinkwin.common.model.publish.PlatformProgrameLabel;
import tk.mybatis.mapper.common.Mapper;

import java.util.Map;

public interface PlatformProgrameLabelMapper extends Mapper<PlatformProgrameLabel> {

    /**
     * 标签状态修改功能
     * @param map
     * @return
     */
    int updateLabelStatusByLabelStatus(Map map);
}