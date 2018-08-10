package com.thinkwin.pay.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "payment")
public class Payment {

	public Payment(){
		this.paymentLogs = new ArrayList<PaymentLog>();
		this.notifyLogs = new ArrayList<PayNotifyLog>();
	}

	@Id
	@Column(name = "order_id")
	private String orderId;

	@Column(name = "tenant_id")
	private String tenantId;


	@Column(name = "subject")
	private String subject;

	@Column(name = "body")
	private String body = "";

	@Column(name = "product_id")
	private String productId = "";

	@Column(name = "total_amount")
	private Double totalAmount;

	@Column(name = "fee_type")
	private String feeType;

	@Column(name = "seller_id")
	private String sellerId;

	@Column(name = "trade_no")
	private String tradeNo;

	@Column(name = "status")
	private Integer status;

	@Column(name = "cert_img_url")
	private String certImgUrl;

	@Column(name = "retry_times")
	private Integer retryTimes;

	@Column(name = "expire_time")
	private Date expireTime;

	@Column(name = "timer_task_id")
	private String timerTaskId;

	@Column(name = "create_time")
	private Date createTime;

	@Column(name = "update_time")
	private Date updateTime;

	@Transient
	private List<PaymentLog> paymentLogs;

	@Transient
	private List<PayNotifyLog> notifyLogs;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCertImgUrl() {
		return certImgUrl;
	}

	public void setCertImgUrl(String certImgUrl) {
		this.certImgUrl = certImgUrl;
	}

	public Integer getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(Integer retryTimes) {
		this.retryTimes = retryTimes;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public String getTimerTaskId() {
		return timerTaskId;
	}

	public void setTimerTaskId(String timerTaskId) {
		this.timerTaskId = timerTaskId;
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

	public List<PaymentLog> getPaymentLogs() {
		return paymentLogs;
	}

	public void addPaymentLog(PaymentLog log){
		if(null == log){
			return;
		}
		this.paymentLogs.add(log);
	}

	public void setPaymentLogs(List<PaymentLog> paymentLogs) {
		this.paymentLogs = paymentLogs;
	}

	public List<PayNotifyLog> getNotifyLogs() {
		return notifyLogs;
	}

	public void setNotifyLogs(List<PayNotifyLog> notifyLogs) {
		this.notifyLogs = notifyLogs;
	}

	@Override
	public String toString() {
		return "Payment{" +
				", orderId='" + orderId + '\'' +
				", tenantId='" + tenantId + '\'' +
				", subject='" + subject + '\'' +
				", totalAmount=" + totalAmount +
				", sellerId=" + sellerId +
				", feeType='" + feeType + '\'' +
				", status=" + status +
				'}';
	}
}
