package com.thinkwin.common.dto.publish;

import java.io.Serializable;
import java.util.Date;

public class BootDownTaskLogDto implements Serializable {
	private static final long serialVersionUID = 271131068029950076L;
	private String id;
	private String cmdContent;
	private String runStatus;
	private Date runTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCmdContent() {
		return cmdContent;
	}

	public void setCmdContent(String cmdContent) {
		this.cmdContent = cmdContent;
	}

	public String getRunStatus() {
		return runStatus;
	}

	public void setRunStatus(String runStatus) {
		this.runStatus = runStatus;
	}

	public Date getRunTime() {
		return runTime;
	}

	public void setRunTime(Date runTime) {
		this.runTime = runTime;
	}
}
