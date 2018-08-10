package com.thinkwin.SMSsender.util;

public class SMSMessageMQVo implements MqMessageVoInterface{
	private static final long serialVersionUID = 1L;

	//消息体
	private String message;
	//类型
	private String fd_uuid;

	public String getFd_uuid() {
		return fd_uuid;
	}

	public void setFd_uuid(String fd_uuid) {
		this.fd_uuid = fd_uuid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


}