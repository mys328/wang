package com.thinkwin.common.dto.promotion;

import com.thinkwin.common.dto.order.CouponProduct;
import com.thinkwin.common.dto.order.ServiceTermInfo;

import java.io.Serializable;
import java.util.List;

public class CouponInfo implements Serializable {
	private Integer status;
	private String couponType;
	private ServiceTermInfo serviceTermInfo;
	private Integer discount;
	private List<CouponProduct> products;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCouponType() {
		return couponType;
	}

	public void setCouponType(String couponType) {
		this.couponType = couponType;
	}

	public ServiceTermInfo getServiceTerm() {
		return serviceTermInfo;
	}

	public void setServiceTerm(ServiceTermInfo serviceTerm) {
		this.serviceTermInfo = serviceTerm;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}

	public List<CouponProduct> getProducts() {
		return products;
	}

	public void setProducts(List<CouponProduct> products) {
		this.products = products;
	}
}
