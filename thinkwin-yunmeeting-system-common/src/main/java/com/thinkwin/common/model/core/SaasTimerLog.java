package com.thinkwin.common.model.core;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "`saas_timer`")
public class SaasTimerLog implements Serializable {
	private static final long serialVersionUID = 125244385940619675L;

	@Id
	@Column(name = "`id`")
	private String id;

	@Column(name = "`service_type`")
	private Integer serviceType;

	@Column(name = "`task_type`")
	private Integer taskType;

	@Column(name = "`service_id`")
	private String serviceId;

	@Column(name = "`task_id`")
	private String taskId;

	@Column(name = "`tenant_id`")
	private String tenantId;

	@Column(name = "`task_status`")
	private Integer taskStatus;

	@Column(name = "`create_time`")
	private Date createTime;

	@Column(name = "`cancel_time`")
	private Date cancelTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getServiceType() {
		return serviceType;
	}

	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}

	public Integer getTaskType() {
		return taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(Integer taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime;
	}
}
