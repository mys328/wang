package com.thinkwin.mailsender.util;

/**
 * @功能：订单MQ信息传输VO
 * @author yangyiqian
 */
public class EmailMessageMQVo implements MqMessageVoInterface{
	private static final long serialVersionUID = 1L;
	private String message;
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