package com.thinkwin.common.dto.publish;

import java.io.Serializable;

public class BroadcastCommandVo implements Serializable {
	private static final long serialVersionUID = -3580518993069688849L;

	private String terminalId;
	private String msgId;
	private String content;
	private Integer length;
	private String unit;
	private String speed;

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	@Override
	public String toString() {
		return "BroadcastCommandVo{" +
				"terminalId='" + terminalId + '\'' +
				", msgId='" + msgId + '\'' +
				", length=" + length +
				", unit='" + unit + '\'' +
				", speed='" + speed + '\'' +
				'}';
	}
}
