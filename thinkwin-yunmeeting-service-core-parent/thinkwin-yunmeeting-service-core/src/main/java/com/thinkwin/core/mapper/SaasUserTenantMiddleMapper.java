package com.thinkwin.core.mapper;

import com.thinkwin.common.model.core.SaasUserTenantMiddle;
import tk.mybatis.mapper.common.Mapper;

public interface SaasUserTenantMiddleMapper extends Mapper<SaasUserTenantMiddle> {
    /**
     * 根据租户id修改saas_user_tenant_middle租户id信息
     * @param tenantId
     * @return
     */
    public int delSaasUserTenantMiddleByTenantId(String tenantId);
}