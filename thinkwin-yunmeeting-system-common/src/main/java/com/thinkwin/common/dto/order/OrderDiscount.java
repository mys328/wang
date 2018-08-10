package com.thinkwin.common.dto.order;

import java.io.Serializable;
import java.util.List;

public class OrderDiscount implements Serializable {
	private static final long serialVersionUID = 7211547725318898141L;
	private List<SkuDiscountInfo> skuDiscount;
	private CouponDiscount couponDiscount;
	private Integer discount;

	public List<SkuDiscountInfo> getSkuDiscount() {
		return skuDiscount;
	}

	public void setSkuDiscount(List<SkuDiscountInfo> skuDiscount) {
		this.skuDiscount = skuDiscount;
	}

	public CouponDiscount getCouponDiscount() {
		return couponDiscount;
	}

	public void setCouponDiscount(CouponDiscount couponDiscount) {
		this.couponDiscount = couponDiscount;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
}
