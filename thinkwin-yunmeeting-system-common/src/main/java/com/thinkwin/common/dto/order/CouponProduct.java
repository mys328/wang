package com.thinkwin.common.dto.order;

import java.io.Serializable;

/**
 * 特权优惠券的赠送商品
 */
public class CouponProduct implements Serializable {
	private String sku;
	private String name;
	private String uom;
	private Integer qty;

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

}
