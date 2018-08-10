package com.thinkwin.common.dto.publish;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class BootDownTaskDto implements Serializable {
	private static final long serialVersionUID = -7092121044178497422L;
	private String id;
	private String taskName;
	private String ifOpenDown;
	private Date downStartTime;
	private Date downEndTime;
	private String status;
	private Integer terminalCount;
	private Integer errorCount;
	private Object terminals;
	private List<BootDownTaskPeriodDto> periods;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getIfOpenDown() {
		return ifOpenDown;
	}

	public void setIfOpenDown(String ifOpenDown) {
		this.ifOpenDown = ifOpenDown;
	}

	public Date getDownStartTime() {
		return downStartTime;
	}

	public void setDownStartTime(Date downStartTime) {
		this.downStartTime = downStartTime;
	}

	public Date getDownEndTime() {
		return downEndTime;
	}

	public void setDownEndTime(Date downEndTime) {
		this.downEndTime = downEndTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getTerminalCount() {
		return terminalCount;
	}

	public void setTerminalCount(Integer terminalCount) {
		this.terminalCount = terminalCount;
	}

	public Integer getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}

	public Object getTerminals() {
		return terminals;
	}

	public void setTerminals(Object terminals) {
		this.terminals = terminals;
	}

	public List<BootDownTaskPeriodDto> getPeriods() {
		return periods;
	}

	public void setPeriods(List<BootDownTaskPeriodDto> periods) {
		this.periods = periods;
	}
}
