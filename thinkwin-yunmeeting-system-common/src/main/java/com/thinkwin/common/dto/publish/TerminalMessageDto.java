package com.thinkwin.common.dto.publish;

import java.io.Serializable;
import java.util.Date;

public class TerminalMessageDto implements Serializable {
	private static final long serialVersionUID = -5048109854788742023L;

	private String messageId;
	private String terminalId;
	private String content;
	/**
	 * 时长单位
	 */
	private String unit;

	/**
	 * 时长
	 */
	private String length;

	private Date sendTime;
	private Date expiryTime;
	//消息发送状态，0：正在发送；1：已送达；2：未送达
	Integer status;

	public Date getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
