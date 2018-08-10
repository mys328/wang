package com.thinkwin.web;

import com.alibaba.dubbo.rpc.*;
import com.thinkwin.service.TenantContext;

public class TenantFilter implements Filter {
	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		Result result;
		try{
			result = invoker.invoke(invocation);
		}
		finally {
			RpcContext.getContext().remove(TenantContext.RPC_CONTEXT_TENANTID_PARAM);
		}
		return result;
	}
}
