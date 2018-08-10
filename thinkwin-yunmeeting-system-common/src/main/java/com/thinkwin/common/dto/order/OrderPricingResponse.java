package com.thinkwin.common.dto.order;

import com.thinkwin.common.dto.promotion.CouponInfo;

import java.io.Serializable;
import java.util.List;

public class OrderPricingResponse implements Serializable {
	private String payPrice;
	private String listPrcie;
	private String expression1;
	private String expression2;
	private Long expireTime;
	private CouponInfo couponInfo;
	private boolean couponValid = false;
	private String msg;

	public String getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(String payPrice) {
		this.payPrice = payPrice;
	}

	public String getListPrcie() {
		return listPrcie;
	}

	public void setListPrcie(String listPrcie) {
		this.listPrcie = listPrcie;
	}

	public String getExpression1() {
		return expression1;
	}

	public void setExpression1(String expression1) {
		this.expression1 = expression1;
	}

	public String getExpression2() {
		return expression2;
	}

	public void setExpression2(String expression2) {
		this.expression2 = expression2;
	}

	public Long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}

	public CouponInfo getCouponInfo() {
		return couponInfo;
	}

	public void setCouponInfo(CouponInfo couponInfo) {
		this.couponInfo = couponInfo;
	}

	public boolean isCouponValid() {
		return couponValid;
	}

	public void setCouponValid(boolean couponValid) {
		this.couponValid = couponValid;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
