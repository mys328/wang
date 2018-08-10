package com.thinkwin.goodscenter.vo;

import java.io.Serializable;

public class ProductSpecVo implements Serializable{

	private static final long serialVersionUID = -2073112451787502226L;
	/**
	 * 商品id
	 */
	private String id;

	/**
	 * 价格
	 */
	private String price;
	/**
	 * 版本
	 */
	private String version ;
	/**
	 * 折扣率
	 */
	private String discount;
	/**
	 * 折扣率中文
	 */
	private String discountTip;
	/**
	 * 赠送会议室数量
	 */
	private String room;
	/**
	 * 赠送空间数量
	 */
	private String space;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getDiscountTip() {
		return discountTip;
	}

	public void setDiscountTip(String discountTip) {
		this.discountTip = discountTip;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getSpace() {
		return space;
	}

	public void setSpace(String space) {
		this.space = space;
	}
}
