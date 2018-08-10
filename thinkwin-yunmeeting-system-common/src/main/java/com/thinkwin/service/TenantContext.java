package com.thinkwin.service;

import com.thinkwin.TenantUserVo;

public class TenantContext {
	private static ThreadLocal<String> tenantId = new ThreadLocal<String>();
	private static ThreadLocal<TenantUserVo> userInfo = new ThreadLocal<TenantUserVo>();

	public static String getTenantId() {
		return tenantId.get();
	}

	public static TenantUserVo getUserInfo() {
		return userInfo.get();
	}

	public static void clearTenantId(){
		TenantContext.tenantId.remove();
	}

	public static void clearUserInfo(){
		TenantContext.userInfo.remove();
	}

	public static void setTenantId(String id) {
		TenantContext.tenantId.set(id);
	}

	public static void setUserInfo(TenantUserVo userInfo) {
		TenantContext.userInfo.set(userInfo);
	}

	public static final String RPC_CONTEXT_TENANTID_PARAM = "$tenantId$";
	public static final String RPC_CONTEXT_USER_PARAM = "$userInfo$";
}
