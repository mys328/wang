package com.thinkwin.common.dto.publish;

import java.io.Serializable;

public class AppUpgradeResponseDto implements Serializable {
	private static final long serialVersionUID = -904377454677032087L;

	private String version;
	private Integer status;
	private Integer progress;
	private String msg;

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

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
