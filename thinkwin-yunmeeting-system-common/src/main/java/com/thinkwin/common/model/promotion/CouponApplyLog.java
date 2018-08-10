package com.thinkwin.common.model.promotion;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "`coupon_apply_log`")
public class CouponApplyLog implements Serializable{

    private static final long serialVersionUID = 5634965353209375714L;
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`coupon_code`")
    private String couponCode;

    @Column(name = "`order_id`")
    private String orderId;

    @Column(name = "`tenant_id`")
    private String tenantId;

    /**
     * 使用或是返还优惠券
     */
    @Column(name = "`operation`")
    private Integer operation;

    /**
     * 订单取消后返还给租户的时候
     */
    @Column(name = "`log_time`")
    private Date logTime;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * @return coupon_code
     */
    public String getCouponCode() {
        return couponCode;
    }

    /**
     * @param couponCode
     */
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode == null ? null : couponCode.trim();
    }

    /**
     * @return order_id
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * @param orderId
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    /**
     * @return tenant_id
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    /**
     * 获取使用或是返还优惠券
     *
     * @return operation - 使用或是返还优惠券
     */
    public Integer getOperation() {
        return operation;
    }

    /**
     * 设置使用或是返还优惠券
     *
     * @param operation 使用或是返还优惠券
     */
    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    /**
     * 获取订单取消后返还给租户的时候
     *
     * @return log_time - 订单取消后返还给租户的时候
     */
    public Date getLogTime() {
        return logTime;
    }

    /**
     * 设置订单取消后返还给租户的时候
     *
     * @param logTime 订单取消后返还给租户的时候
     */
    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }
}