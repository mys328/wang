package com.thinkwin.common.dto.order;

import java.io.Serializable;

public class ServiceTermInfo implements Serializable {
	private Integer qty;
	private Integer uom;// 0:年, 1:月

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Integer getUom() {
		return uom;
	}

	public void setUom(Integer uom) {
		this.uom = uom;
	}
}
