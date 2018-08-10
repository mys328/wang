package com.thinkwin.pay.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class PayNotifyLog {

	@Id
	@Column(name = "log_id")
	private Long logId;

	@Column(name = "notify_time")
	private Date notifyTime;

	@Column(name = "notify_content")
	private String notifyContent;

	@Column(name = "status")
	private Integer status;

	@Column(name = "client_response")
	private String clientResponse;

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public Date getNotifyTime() {
		return notifyTime;
	}

	public void setNotifyTime(Date notifyTime) {
		this.notifyTime = notifyTime;
	}

	public String getNotifyContent() {
		return notifyContent;
	}

	public void setNotifyContent(String notifyContent) {
		this.notifyContent = notifyContent;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getClientResponse() {
		return clientResponse;
	}

	public void setClientResponse(String clientResponse) {
		this.clientResponse = clientResponse;
	}

	@Override
	public String toString() {
		return "PayNotifyLog{" +
				"logId=" + logId +
				", notifyTime=" + notifyTime +
				", notifyContent='" + notifyContent + '\'' +
				", status=" + status +
				", clientResponse='" + clientResponse + '\'' +
				'}';
	}
}
