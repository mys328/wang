package com.thinkwin.common.dto.order;

import java.io.Serializable;
import java.util.List;

public class CouponDiscount implements Serializable {
	private static final long serialVersionUID = -2308803602275776438L;

	private List<CouponProduct> products;
	private ServiceTermInfo serviceTermInfo;
	private Integer discount;

	public List<CouponProduct> getProducts() {
		return products;
	}

	public void setProducts(List<CouponProduct> products) {
		this.products = products;
	}

	public ServiceTermInfo getServiceTermInfo() {
		return serviceTermInfo;
	}

	public void setServiceTermInfo(ServiceTermInfo serviceTermInfo) {
		this.serviceTermInfo = serviceTermInfo;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
}
