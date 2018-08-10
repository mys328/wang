package com.thinkwin.orders.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name = "`order`")
public class Order {

	public Order(){
		this.orderLines = new ArrayList<OrderLine>();
	}

	@Id
	@Column(name = "order_id")
	private String orderId;

	@Column(name = "order_sn")
	private String orderSn;

	@Column(name = "tenant_id")
	private String tenantId;

	@Column(name = "order_subject")
	private String orderSubject;

	@Column(name = "order_source")
	private Integer orderSource;

	@Column(name = "order_type")
	private Integer orderType;

	@Transient
	List<OrderLine> orderLines;

	@Column(name = "tenant_name")
	private String tenantName;

	@Column(name = "buyer_user_id")
	private String buyerUserId;

	@Column(name = "buyer_user_name")
	private String buyerUserName;

	@Column(name = "service_term")
	private Integer serviceTerm;

	@Column(name = "uom")
	private Integer uom;

	@Column(name = "days")
	private Integer days;

	@Column(name = "total_price")
	private Double totalPrice;

	@Column(name = "pay_price")
	private Double payPrice;

	@Column(name = "client_ip")
	private String clientIp;

	@Column(name = "rent_start")
	private Date rentStart;

	@Column(name = "rent_end")
	private Date rentEnd;

	@Column(name = "status")
	private Integer status;

	@Column(name = "pay_success_time")
	private Date paySuccessTime;

	@Column(name = "pay_channel_name")
	private String payChannelName;

	@Column(name = "coupon_code")
	private String couponCode;

	@Column(name = "promotion_code")
	private String promotionCode;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "create_time")
	private Date createTime;

	@Column(name = "update_time")
	private Date updateTime;

	@Column(name = "order_discount")
	private String orderDiscount;

	@Column(name = "pricing_config_version")
	private String pricingConfigVersion;

	@Column(name = "remark")
	private String remark;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderSn() {
		return orderSn;
	}

	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getOrderSubject() {
		return orderSubject;
	}

	public void setOrderSubject(String orderSubject) {
		this.orderSubject = orderSubject;
	}

	public Integer getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(Integer orderSource) {
		this.orderSource = orderSource;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public List<OrderLine> getOrderLines() {
		return orderLines;
	}

	public void setOrderLines(List<OrderLine> orderLines) {
		this.orderLines = orderLines;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getBuyerUserId() {
		return buyerUserId;
	}

	public void setBuyerUserId(String buyerUserId) {
		this.buyerUserId = buyerUserId;
	}

	public String getBuyerUserName() {
		return buyerUserName;
	}

	public void setBuyerUserName(String buyerUserName) {
		this.buyerUserName = buyerUserName;
	}

	public Integer getServiceTerm() {
		return serviceTerm;
	}

	public void setServiceTerm(Integer serviceTerm) {
		this.serviceTerm = serviceTerm;
	}

	public Integer getUom() {
		return uom;
	}

	public void setUom(Integer uom) {
		this.uom = uom;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Double getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(Double payPrice) {
		this.payPrice = payPrice;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public Date getRentStart() {
		return rentStart;
	}

	public void setRentStart(Date rentStart) {
		this.rentStart = rentStart;
	}

	public Date getRentEnd() {
		return rentEnd;
	}

	public void setRentEnd(Date rentEnd) {
		this.rentEnd = rentEnd;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getPaySuccessTime() {
		return paySuccessTime;
	}

	public void setPaySuccessTime(Date paySuccessTime) {
		this.paySuccessTime = paySuccessTime;
	}

	public String getPayChannelName() {
		return payChannelName;
	}

	public void setPayChannelName(String payChannelName) {
		this.payChannelName = payChannelName;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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

	public String getOrderDiscount() {
		return orderDiscount;
	}

	public void setOrderDiscount(String orderDiscount) {
		this.orderDiscount = orderDiscount;
	}

	public String getPricingConfigVersion() {
		return pricingConfigVersion;
	}

	public void setPricingConfigVersion(String pricingConfigVersion) {
		this.pricingConfigVersion = pricingConfigVersion;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "Order{" +
				"orderId='" + orderId + '\'' +
				", orderSn='" + orderSn + '\'' +
				", tenantId='" + tenantId + '\'' +
				", orderSubject='" + orderSubject + '\'' +
				", orderSource=" + orderSource +
				", orderType=" + orderType +
				", tenantName='" + tenantName + '\'' +
				", buyerUserId='" + buyerUserId + '\'' +
				", buyerUserName='" + buyerUserName + '\'' +
				", totalPrice=" + totalPrice +
				", payPrice=" + payPrice +
				", clientIp='" + clientIp + '\'' +
				", status=" + status +
				", paySuccessTime=" + paySuccessTime +
				", payChannelName='" + payChannelName + '\'' +
				", createdBy='" + createdBy + '\'' +
				", createTime=" + createTime +
				'}';
	}
}
