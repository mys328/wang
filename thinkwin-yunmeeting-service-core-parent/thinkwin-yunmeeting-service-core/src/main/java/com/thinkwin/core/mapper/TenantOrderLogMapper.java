package com.thinkwin.core.mapper;

import com.thinkwin.common.model.core.TenantOrderLog;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface TenantOrderLogMapper extends Mapper<TenantOrderLog> {
	void createTenantOrderLog(@Param("tenantId")String tenantId);
}
