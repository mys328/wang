package com.thinkwin.orders.dto;

public class OrderCancelRequest {
	String orderId;
	String appId;
	String nonceStr;
	String timestamp;
	String sign;

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

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public String toString() {
		return "OrderCancelRequest{" +
				"orderId='" + orderId + '\'' +
				", appId='" + appId + '\'' +
				", nonceStr='" + nonceStr + '\'' +
				", timestamp='" + timestamp + '\'' +
				", sign='" + sign + '\'' +
				'}';
	}
}
