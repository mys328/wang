package com.thinkwin.common.model.core;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "tenant_order_log")
public class TenantOrderLog implements Serializable {

	@Id
	@Column(name = "tenant_id")
	private String tenantId;

	@Column(name = "current_order")
	private String currentOrder;

	@Column(name = "order_processed")
	private Integer orderProcessed;

	@Column(name = "create_time")
	private Date createTime;

	@Column(name = "update_time")
	private Date updateTime;

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getCurrentOrder() {
		return currentOrder;
	}

	public void setCurrentOrder(String currentOrder) {
		this.currentOrder = currentOrder;
	}

	public Integer getOrderProcessed() {
		return orderProcessed;
	}

	public void setOrderProcessed(Integer orderProcessed) {
		this.orderProcessed = orderProcessed;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
