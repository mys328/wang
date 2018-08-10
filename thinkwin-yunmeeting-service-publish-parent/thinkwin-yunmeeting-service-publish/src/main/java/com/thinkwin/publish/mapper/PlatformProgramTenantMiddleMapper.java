package com.thinkwin.publish.mapper;

import com.thinkwin.common.model.publish.PlatformProgramTenantMiddle;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PlatformProgramTenantMiddleMapper extends Mapper<PlatformProgramTenantMiddle> {

    List<PlatformProgramTenantMiddle> selectPlatformProgramTenantMiddle();

    /**
     * 根据租户主键id获取与该租户相关联的节目主键id集合
     * @param platformProgrameLabeId
     * @return
     */
    List<String> selectPlatformProgrameIdsByLabelType(String platformProgrameLabeId);
}