package com.thinkwin.orders.dto;

import com.thinkwin.common.model.BasePageEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AdminOrderQuery implements Serializable {
	private BasePageEntity page;
	private String tenantId;
	private String appId;
	private java.util.List<Integer> status;
	private List<Integer> orderTypes;
	private java.util.Date startTime;
	private Date endTime;
	private String nonceStr;
	private String timeStamp;
	private String sign;

	public BasePageEntity getPage() {
		return page;
	}

	public void setPage(BasePageEntity page) {
		this.page = page;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public List<Integer> getStatus() {
		return status;
	}

	public void setStatus(List<Integer> status) {
		this.status = status;
	}

	public List<Integer> getOrderTypes() {
		return orderTypes;
	}

	public void setOrderTypes(List<Integer> orderTypes) {
		this.orderTypes = orderTypes;
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

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
}
