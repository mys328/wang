package com.thinkwin.orders.vo;

import com.thinkwin.common.dto.order.OrderDiscount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderVo implements Serializable {

	public OrderVo(){
		this.orderLines = new ArrayList<OrderLineVo>();
		this.items = new ArrayList<UomItem>();
	}

	private String orderId;

	private Integer orderType;

	private String orderSn;

	private String tenantId;

	private String orderSubject;

	private Integer orderSource;

	private Integer serviceTerm;

	private Integer uom; //0: 年, 1: 月

	private String productId;

	private List<OrderLineVo> orderLines;

	private List<UomItem> items;

	private String tenantName;

	private String buyerUserId;

	private String buyerUserName;

	private Integer licenseQty;

	private String totalPrice;

	private String payPrice;

	private Integer discount;

	private String discountTip;

	private Integer payChannel;

	private String channelName;

	private String clientIp;

	private Integer status;

	private String statusName;

	private Date paySuccessTime;

	private Date expireTime;

	private String certImageUrl;

	private String couponCode;

	private String promotionCode;

	private String specName;

	private String specValue;

	private String uomName;

	private String uomClass;

	private Date rentStart;

	private Date rentEnd;

	private String createdBy;

	private Date createTime;

	private Date updateTime;

	private OrderDiscount orderDiscount;
	private String pricingConfigVersion;

	private String remark;

	private boolean couponValid = false;

	private Integer days;

	//定价配置ID
	private String configId;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
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

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public List<OrderLineVo> getOrderLines() {
		return orderLines;
	}

	public void setOrderLines(List<OrderLineVo> orderLines) {
		this.orderLines = orderLines;
	}

	public List<UomItem> getItems() {
		return items;
	}

	public void setItems(List<UomItem> items) {
		this.items = items;
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

	public Integer getLicenseQty() {
		return licenseQty;
	}

	public void setLicenseQty(Integer licenseQty) {
		this.licenseQty = licenseQty;
	}

	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(String payPrice) {
		this.payPrice = payPrice;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}

	public String getDiscountTip() {
		return discountTip;
	}

	public void setDiscountTip(String discountTip) {
		this.discountTip = discountTip;
	}

	public Integer getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(Integer payChannel) {
		this.payChannel = payChannel;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public Date getPaySuccessTime() {
		return paySuccessTime;
	}

	public void setPaySuccessTime(Date paySuccessTime) {
		this.paySuccessTime = paySuccessTime;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public String getCertImageUrl() {
		return certImageUrl;
	}

	public void setCertImageUrl(String certImageUrl) {
		this.certImageUrl = certImageUrl;
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

	public String getSpecName() {
		return specName;
	}

	public void setSpecName(String specName) {
		this.specName = specName;
	}

	public String getSpecValue() {
		return specValue;
	}

	public void setSpecValue(String specValue) {
		this.specValue = specValue;
	}

	public String getUomName() {
		return uomName;
	}

	public void setUomName(String uomName) {
		this.uomName = uomName;
	}

	public String getUomClass() {
		return uomClass;
	}

	public void setUomClass(String uomClass) {
		this.uomClass = uomClass;
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

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public OrderDiscount getOrderDiscount() {
		return orderDiscount;
	}

	public void setOrderDiscount(OrderDiscount orderDiscount) {
		this.orderDiscount = orderDiscount;
	}

	public String getPricingConfigVersion() {
		return pricingConfigVersion;
	}

	public void setPricingConfigVersion(String pricingConfigVersion) {
		this.pricingConfigVersion = pricingConfigVersion;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public boolean isCouponValid() {
		return couponValid;
	}

	public void setCouponValid(boolean couponValid) {
		this.couponValid = couponValid;
	}

	@Override
	public String toString() {
		return "OrderVo{" +
				"orderId='" + orderId + '\'' +
				", orderType=" + orderType +
				", orderSn='" + orderSn + '\'' +
				", tenantId='" + tenantId + '\'' +
				", orderSubject='" + orderSubject + '\'' +
				", orderSource=" + orderSource +
				", tenantName='" + tenantName + '\'' +
				", buyerUserId='" + buyerUserId + '\'' +
				", buyerUserName='" + buyerUserName + '\'' +
				", licenseQty=" + licenseQty +
				", totalPrice='" + totalPrice + '\'' +
				", payPrice='" + payPrice + '\'' +
				", payChannel=" + payChannel +
				", channelName='" + channelName + '\'' +
				", clientIp='" + clientIp + '\'' +
				", status=" + status +
				", createTime=" + createTime +
				", createdBy='" + createdBy + '\'' +
				'}';
	}
}
