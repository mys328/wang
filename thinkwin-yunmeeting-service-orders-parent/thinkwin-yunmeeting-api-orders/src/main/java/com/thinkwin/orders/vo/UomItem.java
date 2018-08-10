package com.thinkwin.orders.vo;


import java.io.Serializable;

public class UomItem implements Serializable {
	private String productId;
	private String productName;

	/**
	 * 商品单位，人、间、GB
	 */
	private String uom;

	/**
	 * 排序
	 */
	private Integer sortOrder;

	/**
	 * 前端展示名称
	 */
	private String displayName;

	/**
	 * 总数量
	 */
	private Integer total = 0;

	/**
	 * 套餐内包含的数量
	 */
	private Integer base = 0;

	/**
	 * 额外购买的数量
	 */
	private Integer extra = 0;

	/**
	 * 赠送的数量
	 */
	private Integer giveaway = 0;

	/**
	 * 永久免费的数量
	 */
	private Integer free = 0;


	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getTotal() {
		if(null == total){
			return 0;
		}

		if(base > 0){
			return base + extra;
		}
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getBase() {
		return base;
	}

	public void setBase(Integer base) {
		this.base = base;
	}

	public Integer getExtra() {
		if(null == extra){
			return 0;
		}

		return extra;
	}

	public void setExtra(Integer extra) {
		this.extra = extra;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Integer getGiveaway() {
		return giveaway;
	}

	public void setGiveaway(Integer giveaway) {
		this.giveaway = giveaway;
	}

	public Integer getFree() {
		return free;
	}

	public void setFree(Integer free) {
		this.free = free;
	}

}
