package com.thinkwin.web;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.thinkwin.TenantUser;
import com.thinkwin.TenantUserVo;
import com.thinkwin.service.TenantContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
public class DubboServiceContextAop {

	@Pointcut("execution(* com.thinkwin..service.*.*(..))")
	public void serviceApi() {
	}

	@Before("serviceApi()")
	public void dubboContext(JoinPoint jp) {
		if(TenantContext.getTenantId() != null){
			RpcContext.getContext().setAttachment(TenantContext.RPC_CONTEXT_TENANTID_PARAM, TenantContext.getTenantId());
		}

		if (TenantContext.getUserInfo() != null) {
			TenantUserVo tenantUser = TenantContext.getUserInfo();
			RpcContext.getContext().setAttachment(TenantContext.RPC_CONTEXT_USER_PARAM, JSON.toJSONString(tenantUser));
		}
	}
}