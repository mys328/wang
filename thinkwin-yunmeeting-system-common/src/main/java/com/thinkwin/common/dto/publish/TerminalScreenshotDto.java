package com.thinkwin.common.dto.publish;

import java.io.Serializable;

public class TerminalScreenshotDto implements Serializable {
	private static final long serialVersionUID = 7944665693688197359L;

	private String terminalId;
	private String tenantId;
	private String requestId;
	private String ext;
	private String pictureUrl;
	private String smallPictureUrl;

	//操作状态，0：成功，1：异常
	private Integer status;
	private long timestamp;

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String id) {
		this.terminalId = id;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public String getSmallPictureUrl() {
		return smallPictureUrl;
	}

	public void setSmallPictureUrl(String smallPictureUrl) {
		this.smallPictureUrl = smallPictureUrl;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
