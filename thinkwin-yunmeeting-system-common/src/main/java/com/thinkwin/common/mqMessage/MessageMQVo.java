package com.thinkwin.common.mqMessage;

/**
 * @功能：MQ信息传输VO
 * @author yangyiqian
 */
public class MessageMQVo implements MqMessageVoInterface{
	private static final long serialVersionUID = 1L;
	private String message;
	private String tenantId;
	private String queueFlag;

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String getQueueFlag() {
		return this.queueFlag;
	}

	public void setQueueFlag(String queueFlag ) {
		this.queueFlag=queueFlag;
	}


}