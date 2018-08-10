package com.thinkwin;


import java.util.Collection;

import org.springframework.security.core.userdetails.User;

public class TenantUser extends User {
	private static final long serialVersionUID = -2853562359998660273L;

	private String tenantId;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private String email;

	private String ip;


	private String device;

	public TenantUser(String username, String password, boolean enabled,
	                boolean accountNonExpired, boolean credentialsNonExpired,
	                boolean accountNonLocked,
	                Collection authorities, String tenantId) {

		super(username, password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, authorities);

		this.tenantId = tenantId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getTenantId() {
		return tenantId;
	}

	private  String userName;

	private String userId;

	public String getUserName() {
		return userName;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}
}
