package com.thinkwin.common.dto.publish;

import java.io.Serializable;

public class TerminalUpdateLog implements Serializable {
	private static final long serialVersionUID = 8226633844952004343L;

	private String appVersion;
	private String updateDesc;


	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getUpdateDesc() {
		return updateDesc;
	}

	public void setUpdateDesc(String updateDesc) {
		this.updateDesc = updateDesc;
	}
}
