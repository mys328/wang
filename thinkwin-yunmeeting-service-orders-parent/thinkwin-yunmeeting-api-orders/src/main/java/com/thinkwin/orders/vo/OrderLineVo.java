package com.thinkwin.orders.vo;

import com.thinkwin.goodscenter.vo.ProductSkuVo;

import java.io.Serializable;

public class OrderLineVo implements Serializable {

	private String productSku;

	private String productPackSku;

	private String uom;

	private Integer qty;

	private Integer free;

	private Integer giveaway;

	private String itemDesc;

	public String getProductSku() {
		return productSku;
	}

	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}

	public String getProductPackSku() {
		return productPackSku;
	}

	public void setProductPackSku(String productPackSku) {
		this.productPackSku = productPackSku;
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

	public Integer getFree() {
		return free;
	}

	public void setFree(Integer free) {
		this.free = free;
	}

	public Integer getGiveaway() {
		return giveaway;
	}

	public void setGiveaway(Integer giveaway) {
		this.giveaway = giveaway;
	}

	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}
}
