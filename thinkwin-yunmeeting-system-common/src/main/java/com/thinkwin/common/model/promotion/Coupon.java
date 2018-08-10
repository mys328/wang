package com.thinkwin.common.model.promotion;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Table(name = "`coupon`")
public class Coupon implements Serializable {
    private static final long serialVersionUID = 5428088894430126002L;
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`coupon_code`")
    private String couponCode;

    @Column(name = "`batch_id`")
    private String batchId;

    /**
     * 1：已使用，0：未使用
     */
    @Column(name = "`status`")
    private Integer status;

    @Column(name = "`order_id`")
    private String orderId;

    /**
     * 给指定的租户发放优惠券
     */
    @Column(name = "`tenant_id`")
    private String tenantId;

    /**
     * 创建该优惠券的代理商ID
     */
    @Column(name = "`sales_agent_id`")
    private String salesAgentId;

    @Column(name = "`used_by`")
    private String usedBy;

    /**
     * 领取方名称，有可能是租户也有可能是销售代理
     */
    @Column(name = "`obtained_by`")
    private String obtainedBy;

    @Column(name = "`used_at`")
    private Date usedAt;

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
     * @return batch_id
     */
    public String getBatchId() {
        return batchId;
    }

    /**
     * @param batchId
     */
    public void setBatchId(String batchId) {
        this.batchId = batchId == null ? null : batchId.trim();
    }

    /**
     * 获取1：已使用，0：未使用
     *
     * @return status - 1：已使用，0：未使用
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置1：已使用，0：未使用
     *
     * @param status 1：已使用，0：未使用
     */
    public void setStatus(Integer status) {
        this.status = status;
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
     * 获取给指定的租户发放优惠券
     *
     * @return tenant_id - 给指定的租户发放优惠券
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * 设置给指定的租户发放优惠券
     *
     * @param tenantId 给指定的租户发放优惠券
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    /**
     * 获取创建该优惠券的代理商ID
     *
     * @return sales_agent_id - 创建该优惠券的代理商ID
     */
    public String getSalesAgentId() {
        return salesAgentId;
    }

    /**
     * 设置创建该优惠券的代理商ID
     *
     * @param salesAgentId 创建该优惠券的代理商ID
     */
    public void setSalesAgentId(String salesAgentId) {
        this.salesAgentId = salesAgentId == null ? null : salesAgentId.trim();
    }

    /**
     * @return used_by
     */
    public String getUsedBy() {
        return usedBy;
    }

    /**
     * @param usedBy
     */
    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy == null ? null : usedBy.trim();
    }

    /**
     * 获取领取方名称，有可能是租户也有可能是销售代理
     *
     * @return obtained_by - 领取方名称，有可能是租户也有可能是销售代理
     */
    public String getObtainedBy() {
        return obtainedBy;
    }

    /**
     * 设置领取方名称，有可能是租户也有可能是销售代理
     *
     * @param obtainedBy 领取方名称，有可能是租户也有可能是销售代理
     */
    public void setObtainedBy(String obtainedBy) {
        this.obtainedBy = obtainedBy == null ? null : obtainedBy.trim();
    }

    /**
     * @return used_at
     */
    public Date getUsedAt() {
        return usedAt;
    }

    /**
     * @param usedAt
     */
    public void setUsedAt(Date usedAt) {
        this.usedAt = usedAt;
    }

}