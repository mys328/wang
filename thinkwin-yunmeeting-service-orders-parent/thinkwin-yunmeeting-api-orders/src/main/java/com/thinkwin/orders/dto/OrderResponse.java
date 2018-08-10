package com.thinkwin.orders.dto;

import com.thinkwin.orders.vo.OrderVo;

import java.io.Serializable;

public class OrderResponse implements Serializable {

	private OrderVo orderVo;

	private String errorMsg;

	private String timerTaskId;

	private String couponErrMsg;

	public OrderVo getOrderVo() {
		return orderVo;
	}

	public void setOrderVo(OrderVo orderVo) {
		this.orderVo = orderVo;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getTimerTaskId() {
		return timerTaskId;
	}

	public void setTimerTaskId(String timerTaskId) {
		this.timerTaskId = timerTaskId;
	}

	public String getCouponErrMsg() {
		return couponErrMsg;
	}

	public void setCouponErrMsg(String couponErrMsg) {
		this.couponErrMsg = couponErrMsg;
	}
}
