package com.thinkwin.common.vo.publish;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PublishProgramRequest implements Serializable {
	private static final long serialVersionUID = -8375430716356662995L;

	private String programId;
	private Date start;
	private Date end;
	private Boolean standing = false;
	private Boolean idle = false;
	private List<String> terminals;


	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Boolean isStanding() {
		return standing;
	}

	public void setStanding(Boolean standing) {
		this.standing = standing;
	}

	public Boolean isIdle() {
		return idle;
	}

	public void setIdle(Boolean idle) {
		this.idle = idle;
	}

	public List<String> getTerminals() {
		return terminals;
	}

	public void setTerminals(List<String> terminals) {
		this.terminals = terminals;
	}
}
