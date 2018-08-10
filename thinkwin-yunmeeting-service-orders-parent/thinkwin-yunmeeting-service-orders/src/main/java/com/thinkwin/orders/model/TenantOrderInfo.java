package com.thinkwin.orders.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tenant_order_info")
public class TenantOrderInfo {

	@Id
	@Column(name = "tenant_id")
	private String tenantId;

	@Column(name = "current_order")
	private String currentOrder;

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
