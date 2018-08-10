package com.thinkwin.orders.dto;

import java.io.Serializable;

public class PayResult implements Serializable {
	private String resultCode;

	private String payStatus;

	private String tradeNo;

	private String outTradeNo;

	private String buyerUserId;

	private String errorMsg;

	private String totalAmount;

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getBuyerUserId() {
		return buyerUserId;
	}

	public void setBuyerUserId(String buyerUserId) {
		this.buyerUserId = buyerUserId;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public String toString() {
		return "PayResult{" +
				"resultCode='" + resultCode + '\'' +
				", payStatus='" + payStatus + '\'' +
				", tradeNo='" + tradeNo + '\'' +
				", outTradeNo='" + outTradeNo + '\'' +
				", buyerUserId='" + buyerUserId + '\'' +
				", errorMsg='" + errorMsg + '\'' +
				", totalAmount='" + totalAmount + '\'' +
				'}';
	}
}
