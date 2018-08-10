package com.thinkwin.common.dto.publish;

import java.io.Serializable;
import java.util.Map;

public class CommandResponse implements Serializable {
	private static final long serialVersionUID = 1048201834210524749L;

	private String terminalId;
	private String tenantId;
	private String cmd;
	private String requestId;
	private Map<String, Object> data;
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

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
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
		return "CommandResponse{" +
				"terminalId='" + terminalId + '\'' +
				", tenantId='" + tenantId + '\'' +
				", cmd='" + cmd + '\'' +
				", requestId='" + requestId + '\'' +
				", data=" + data +
				", timestamp=" + timestamp +
				'}';
	}
}
