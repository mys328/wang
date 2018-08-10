package com.thinkwin.common.vo.ordersVo;

import com.thinkwin.common.dto.order.OrderDiscount;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单管理vo类
 * User: yinchunlei
 * Date: 2017/8/31.
 * Company: thinkwin
 */
public class OrderManagementVo implements Serializable {

    private String orderId;//订单主键id
    private String orderNum;//订单号
    private String tenantName;//公司名称
    private String skuName;//购买服务名称
    private String payStatus;//支付状态
    private Date payTime;//支付时间
    private String discount;//优惠折扣
    private String payType;//支付方式
    private double payPrice;//支付金额
    private double profitAmount;//利润金额
    private String certificatePath;//凭证存放路径
    private String status;//支付状态code
    private String channel;//支付方式code
    private String remark;
    //订单优惠信息
    private OrderDiscount orderDiscount;

    //优惠券编码
    private String couponCode;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public double getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(double payPrice) {
        this.payPrice = payPrice;
    }

    public double getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(double profitAmount) {
        this.profitAmount = profitAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public OrderDiscount getOrderDiscount() {
        return orderDiscount;
    }

    public void setOrderDiscount(OrderDiscount orderDiscount) {
        this.orderDiscount = orderDiscount;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
