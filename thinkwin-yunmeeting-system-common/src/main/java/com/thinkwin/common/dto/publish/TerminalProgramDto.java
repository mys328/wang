package com.thinkwin.common.dto.publish;

import java.io.Serializable;
import java.util.Date;

public class TerminalProgramDto implements Serializable {
	private static final long serialVersionUID = 3173120730143723689L;

	private String terminalId;
	private String programId;
	private String name;
	private String photoUrl;
	private String photoUrlSmall;
	private String version;
	private String status;
	private String releaseStatus;
	private Date startTime;
	private Date endTime;
	private boolean isLongPlay;
	private boolean isLeisurePlay;


	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getPhotoUrlSmall() {
		return photoUrlSmall;
	}

	public void setPhotoUrlSmall(String photoUrlSmall) {
		this.photoUrlSmall = photoUrlSmall;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReleaseStatus() {
		return releaseStatus;
	}

	public void setReleaseStatus(String releaseStatus) {
		this.releaseStatus = releaseStatus;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public boolean isLongPlay() {
		return isLongPlay;
	}

	public void setLongPlay(boolean longPlay) {
		isLongPlay = longPlay;
	}

	public boolean isLeisurePlay() {
		return isLeisurePlay;
	}

	public void setLeisurePlay(boolean leisurePlay) {
		isLeisurePlay = leisurePlay;
	}
}
