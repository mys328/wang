package com.thinkwin.promotion.model;

import org.apache.camel.URIField;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Table(name = "`coupon`")
public class Coupon {

	@Id
	@Column(name = "coupon_code")
	private String couponCode;

	@Column(name = "tenant_id")
	private String tenantId;

	@Column(name = "promotion_code")
	private String promotionCode;

	@Column(name = "order_id")
	private String orderId;

	@Column(name = "`namme`")
	private String name;

	@Column(name = "effective_time")
	private Date effectiveTime;

	@Column(name = "expire_time")
	private Date expireTime;

	@Transient
	private List<PromotionRule> rules;

	@Column(name = "create_time")
	private Date createTime;

	@Column(name = "update_time")
	private Date updateTime;

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Date effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public List<PromotionRule> getRules() {
		return rules;
	}

	public void setRules(List<PromotionRule> rules) {
		this.rules = rules;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
