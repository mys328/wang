package com.thinkwin.common.dto.publish;

import java.io.Serializable;
import java.util.List;

public class Command implements Serializable {
	private static final long serialVersionUID = 8667764621600941379L;

	private String terminalId;
	private String tenantId;
	private String requestId;
	private String cmd;
	private Object data;
	private long timestamp;


	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Command{" +
				"terminalId='" + terminalId + '\'' +
				", tenantId='" + tenantId + '\'' +
				", requestId='" + requestId + '\'' +
				", cmd='" + cmd + '\'' +
				", data=" + data +
				", timestamp=" + timestamp +
				'}';
	}
}
