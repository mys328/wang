package com.thinkwin.pay.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

@Entity
public class PaymentLog {
	@Id
	@Column(name = "trade_no")
	private String tradeNo;

	@Column(name = "order_id")
	private String orderId;

	@Column(name = "out_trade_no")
	private String outTradeNo;

	@Column(name = "buyer_user_id")
	private String buyerUserId = "";

	@Column(name = "buyer_user_name")
	private String buyerUserName;

	@Column(name = "pay_channel")
	private Integer payChannel;

	@Column(name = "status")
	private Integer status = PayStatus.PREPAY.getValue();

	@Column(name = "expire_time")
	private Date expireTime;

	@Column(name = "client_ip")
	private String clientIp;

	@Column(name = "create_time")
	private Date createTime;

	@Column(name = "update_time")
	private Date updateTime;

	@Column(name = "pay_info")
	private String payInfo;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
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

	public Integer getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(Integer payChannel) {
		this.payChannel = payChannel;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
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

	public String getPayInfo() {
		return payInfo;
	}

	public void setPayInfo(String payInfo) {
		this.payInfo = payInfo;
	}

	@Override
	public String toString() {
		return "PaymentLog{" +
				"tradeNo='" + tradeNo + '\'' +
				", outTradeNo='" + outTradeNo + '\'' +
				", buyerUserId='" + buyerUserId + '\'' +
				", status=" + status +
				", clientIp='" + clientIp + '\'' +
				'}';
	}
}
