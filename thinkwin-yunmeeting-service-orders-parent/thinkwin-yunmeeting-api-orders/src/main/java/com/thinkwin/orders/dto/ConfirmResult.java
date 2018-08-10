package com.thinkwin.orders.dto;

import java.io.Serializable;

public class ConfirmResult implements Serializable {
	boolean success;

	String payStatus;

	String outTradeNo;

	String errorMsg;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "ConfirmResult{" +
				"success=" + success +
				", payStatus='" + payStatus + '\'' +
				", outTradeNo='" + outTradeNo + '\'' +
				", errorMsg='" + errorMsg + '\'' +
				'}';
	}
}
