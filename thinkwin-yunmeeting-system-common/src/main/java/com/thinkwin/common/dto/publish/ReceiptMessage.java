package com.thinkwin.common.dto.publish;

import java.io.Serializable;

public class ReceiptMessage implements Serializable {
	private static final long serialVersionUID = 1573542909673642094L;

	//终端ID
	String terminalId;

	//租户ID
	String tenantId;

	//指令编码
	String cmd;

	//请求唯一标识，用于区分不同的操作
	String requestId;

	//指令是否已送达
	boolean arrived;


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

	public boolean isArrived() {
		return arrived;
	}

	public void setArrived(boolean arrived) {
		this.arrived = arrived;
	}

	@Override
	public String toString() {
		return "ReceiptMessage{" +
				"terminalId='" + terminalId + '\'' +
				", tenantId='" + tenantId + '\'' +
				", cmd='" + cmd + '\'' +
				", requestId='" + requestId + '\'' +
				", arrived=" + arrived +
				'}';
	}
}
