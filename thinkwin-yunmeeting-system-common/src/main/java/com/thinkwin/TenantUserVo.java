package com.thinkwin;

public class TenantUserVo {
	private String tenantId;

	private String email;

	private String ip;

	private String device;

	private String userId;

	private  String userName;

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getUserName() {
		return userName;
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

	@Override
	public String toString() {
		return "TenantUserVo{" +
				"tenantId='" + tenantId + '\'' +
				", email='" + email + '\'' +
				", ip='" + ip + '\'' +
				", device='" + device + '\'' +
				", userId='" + userId + '\'' +
				", userName='" + userName + '\'' +
				'}';
	}
}
