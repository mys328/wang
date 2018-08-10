package com.thinkwin.common.dto.publish;

import java.io.Serializable;

public class TerminalProgramCommandDto implements Serializable {
	private static final long serialVersionUID = -1021467755189466732L;

	private String id;
	private String programName;
	private String photoUrl;
	private String previewUrl;
	private String attachmentUrl;
	private Integer programStatus;
	private String start;
	private String end;
	private boolean standing;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getPreviewUrl() {
		return previewUrl;
	}

	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	public Integer getProgramStatus() {
		return programStatus;
	}

	public void setProgramStatus(Integer programStatus) {
		this.programStatus = programStatus;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public boolean isStanding() {
		return standing;
	}

	public void setStanding(boolean standing) {
		this.standing = standing;
	}
}
