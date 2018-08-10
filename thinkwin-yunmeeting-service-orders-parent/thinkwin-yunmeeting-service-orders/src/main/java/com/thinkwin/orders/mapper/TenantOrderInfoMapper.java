package com.thinkwin.orders.mapper;

import com.thinkwin.orders.model.TenantOrderInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface TenantOrderInfoMapper extends Mapper<TenantOrderInfo> {

	TenantOrderInfo getOrderInfoByIdLocked(@Param("tenantId") String tenantId);
}
