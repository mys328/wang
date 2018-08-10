package com.thinkwin.common.vo.publish;

import java.io.Serializable;

public class TenantProgramVo implements Serializable {
	private static final long serialVersionUID = -1271278021859042079L;

	private String programId;
	private String name;
	private String photoUrl;
	private String photoUrlSmall;
	private String version;
	private String status;


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
}
