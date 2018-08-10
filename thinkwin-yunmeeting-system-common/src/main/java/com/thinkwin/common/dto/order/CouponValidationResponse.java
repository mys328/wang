package com.thinkwin.common.dto.order;

import java.io.Serializable;
import java.util.List;

public class CouponValidationResponse implements Serializable {

	/**
	 * 优惠券状态，1可用，0不可用
	 */
	private Integer status;

	/**
	 * 优惠券类型
	 */
	private String type;

	/**
	 * 优惠券赠送商品
	 */
	private List<CouponProduct> product;

	/**
	 * 购买时长
	 */
	private Integer serviceTerm;

	/**
	 * 订单折扣值
	 */
	private Integer discount;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<CouponProduct> getProduct() {
		return product;
	}

	public void setProduct(List<CouponProduct> product) {
		this.product = product;
	}

	public Integer getServiceTerm() {
		return serviceTerm;
	}

	public void setServiceTerm(Integer serviceTerm) {
		this.serviceTerm = serviceTerm;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}

}
