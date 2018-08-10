package com.thinkwin.common.vo.publish;

import java.io.Serializable;

public class TerminalAppUpgradeStatus implements Serializable {
	private static final long serialVersionUID = 7755715959224659683L;

	private String terminalId;
	private String tenantId;
	private String oldVersion;
	private String version;
	private Integer status;
	private Integer progress;
	private Integer result;
	private String msg;

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

	public String getOldVersion() {
		return oldVersion;
	}

	public void setOldVersion(String oldVersion) {
		this.oldVersion = oldVersion;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "TerminalAppUpgradeStatus{" +
				"terminalId='" + terminalId + '\'' +
				", tenantId='" + tenantId + '\'' +
				", version='" + version + '\'' +
				", status=" + status +
				", progress=" + progress +
				", result=" + result +
				", msg='" + msg + '\'' +
				'}';
	}

}
