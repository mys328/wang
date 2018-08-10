package com.thinkwin.pay.dto;

import java.io.Serializable;

public class PayNotify implements Serializable {
	String orderId;

	String appId;

	String tenantId;

	String tradeNo;

	String payChannel;

	String timestamp;

	String nonceStr;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	@Override
	public String toString() {
		return "PayNotify{" +
				"orderId='" + orderId + '\'' +
				", appId='" + appId + '\'' +
				", tenantId='" + tenantId + '\'' +
				", tradeNo='" + tradeNo + '\'' +
				", payChannel='" + payChannel + '\'' +
				", timestamp='" + timestamp + '\'' +
				", nonceStr='" + nonceStr + '\'' +
				'}';
	}
}
