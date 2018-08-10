package com.thinkwin.service;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.fastjson.JSON;
import com.thinkwin.TenantUser;
import com.thinkwin.TenantUserVo;
import com.thinkwin.yunmeeting.framework.datasource.dynamicdatasource.DBContextHolder;
import org.apache.commons.lang.StringUtils;

public class DubboContextFilter implements Filter {
	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		if(RpcContext.getContext().getAttachment(TenantContext.RPC_CONTEXT_TENANTID_PARAM) != null){
			String tenantId = RpcContext.getContext().getAttachment(TenantContext.RPC_CONTEXT_TENANTID_PARAM).toString();
			TenantContext.setTenantId(tenantId);
			DBContextHolder.setDBType(tenantId);
		}else{
			String dbType = DBContextHolder.getDBType();
			if(StringUtils.isBlank(dbType)){
				DBContextHolder.setDBType("0");//访问核心库
			}
		}

		if(RpcContext.getContext().getAttachment(TenantContext.RPC_CONTEXT_USER_PARAM) != null) {
			TenantUserVo userInfo = JSON.parseObject(RpcContext.getContext().getAttachment(TenantContext.RPC_CONTEXT_USER_PARAM), TenantUserVo.class);
			TenantContext.setUserInfo(userInfo);
		}

		Result result;
		try{
			RpcContext.getContext().getAttachments().remove(Constants.ASYNC_KEY);
			result = invoker.invoke(invocation);
		}
		finally {
			RpcContext.getContext().removeAttachment(TenantContext.RPC_CONTEXT_TENANTID_PARAM);
			TenantContext.clearTenantId();

			RpcContext.getContext().removeAttachment(TenantContext.RPC_CONTEXT_USER_PARAM);
			TenantContext.clearUserInfo();
		}
		return result;
	}
}
