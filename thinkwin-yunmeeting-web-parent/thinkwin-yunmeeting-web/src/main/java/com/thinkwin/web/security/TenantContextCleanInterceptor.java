package com.thinkwin.web.security;

import com.thinkwin.service.TenantContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TenantContextCleanInterceptor  extends HandlerInterceptorAdapter {

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView model) throws Exception {
		TenantContext.clearTenantId();
		TenantContext.clearUserInfo();
	}
}
