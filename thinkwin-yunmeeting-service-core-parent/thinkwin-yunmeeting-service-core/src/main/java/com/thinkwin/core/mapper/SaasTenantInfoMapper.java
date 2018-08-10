package com.thinkwin.core.mapper;

import com.thinkwin.common.model.core.SaasTenantInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SaasTenantInfoMapper extends Mapper<SaasTenantInfo> {

    /**
     * 根据租户id获取租户的详细信息
     * @param tenantId
     * @return
     */
    //public List<SaasTenantInfo> selectSaasTenantInfoByTenantId(String tenantId);
}