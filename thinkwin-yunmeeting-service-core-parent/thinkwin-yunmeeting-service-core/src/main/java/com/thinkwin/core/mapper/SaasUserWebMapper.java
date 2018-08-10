package com.thinkwin.core.mapper;

import com.thinkwin.common.model.core.SaasUserWeb;
import tk.mybatis.mapper.common.Mapper;

public interface SaasUserWebMapper extends Mapper<SaasUserWeb> {

    public boolean updateUserTenantIdByTenantId(String tenantId);
}