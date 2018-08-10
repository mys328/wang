package com.thinkwin.common;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.thinkwin.TenantUserVo;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yunmeeting.framework.datasource.dynamicdatasource.DBContextHolder;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;

public class DubboServiceContextAop {
	public void dubboContext(JoinPoint jp) {
		if(TenantContext.getTenantId() != null) {
			String tenantId = TenantContext.getTenantId();
			RpcContext.getContext().setAttachment(TenantContext.RPC_CONTEXT_TENANTID_PARAM, tenantId);
		}

		if (TenantContext.getUserInfo() != null) {
			TenantUserVo tenantUser = TenantContext.getUserInfo();
			RpcContext.getContext().setAttachment(TenantContext.RPC_CONTEXT_USER_PARAM, JSON.toJSONString(tenantUser));
		}
	}
}