package com.thinkwin.web.controller;

import com.thinkwin.TenantUser;
import com.thinkwin.service.TenantContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class BaseController {

	protected String getTenantId(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth instanceof AnonymousAuthenticationToken) {
			return null;
		}
		TenantUser tenantUser = (TenantUser) auth.getPrincipal();
		TenantContext.setTenantId(tenantUser.getTenantId());
		return TenantContext.getTenantId();
	}

	protected TenantUser getUser(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth instanceof AnonymousAuthenticationToken) {
			return null;
		}
		TenantUser tenantUser = (TenantUser) auth.getPrincipal();
		return tenantUser;
	}

}
