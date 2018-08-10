package com.thinkwin.common.dto.order;

import java.io.Serializable;

public class SkuDiscountInfo implements Serializable {
	private static final long serialVersionUID = 8624883705090026640L;

	private String sku;
	private String name;
	private String uom;
	private Integer unit;
	private Integer discount;

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

	public Integer getUnit() {
		return unit;
	}

	public void setUnit(Integer unit) {
		this.unit = unit;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
}
