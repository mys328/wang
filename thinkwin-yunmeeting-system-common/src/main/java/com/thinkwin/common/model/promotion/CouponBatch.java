package com.thinkwin.common.model.promotion;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 */
@Table(name = "`coupon_batch`")
public class CouponBatch implements Serializable{

    private static final long serialVersionUID = 356021844179167728L;
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`batch_num`")
    private String batchNum;

    @Column(name = "`coupon_name`")
    private String couponName;

    /**
     * 优惠券名称拼音
     */
    @Column(name = "`coupon_name_pinyin`")
    private String couponNamePinyin;

    @Column(name = "`issue_status`")
    private Integer issueStatus;

    //特权券为T，赠送券为G，时长券为S，折扣券为Z
    @Column(name = "`coupon_type`")
    private String couponType;

    @Column(name = "`limit`")
    private Integer limit;

    @Column(name = "`effective_time`")
    private Date effectiveTime;

    @Column(name = "`expire_time`")
    private Date expireTime;

    /**
     * 1：返还，0：不返还
     */
    @Column(name = "`returnable`")
    private Integer returnable;

    @Column(name = "`total_qty`")
    private Integer totalQty;

    @Column(name = "`used_qty`")
    private Integer usedQty;

    @Column(name = "`issue_mode`")
    private Integer issueMode;  //发放状态，默认1

    /**
     * 状态:0已保存1已发布2已作废3已过期
     */
    @Column(name = "`status`")
    private Integer status;

    @Column(name = "`issued_qty`")
    private Integer issuedQty;

    @Column(name = "`created_by`")
    private String createdBy;

    @Column(name = "`create_time`")
    private Date createTime;

    /**
     * {"giveaway":{"101": 1, "102": 2}, "discount": 90, "extra_service_term": 1}
            
            giveaway: 赠送商品sku及数量配置， {"101": 1, "102": 2}
            discount：订单享受的折扣
            extra_service_term：赠送的服务时长，单位:年
     */
    @Column(name = "`discount_config`")
    private String discountConfig;

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

    /**
     * 领取方名称，有可能是租户也有可能是销售代理
     */
    @Column(name = "`obtained_by`")
    private String obtainedBy;

    @Column(name = "`used_by`")
    private String usedBy;

    @Column(name = "`update_time`")
    private Date updateTime;

    @Transient
    private String couponCode; //优惠券编号
    @Transient
    private String createName; //创建者
    @Transient
    private List<Coupon> items; //详情

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
     * @return batch_num
     */
    public String getBatchNum() {
        return batchNum;
    }

    /**
     * @param batchNum
     */
    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum == null ? null : batchNum.trim();
    }

    /**
     * @return coupon_name
     */
    public String getCouponName() {
        return couponName;
    }

    /**
     * @param couponName
     */
    public void setCouponName(String couponName) {
        this.couponName = couponName == null ? null : couponName.trim();
    }

    /**
     * @return coupon_type
     */
    public String getCouponType() {
        return couponType;
    }

    /**
     * @param couponType
     */
    public void setCouponType(String couponType) {
        this.couponType = couponType == null ? null : couponType.trim();
    }

    /**
     * @return limit
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @param limit
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * @return effective_time
     */
    public Date getEffectiveTime() {
        return effectiveTime;
    }

    /**
     * @param effectiveTime
     */
    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    /**
     * @return expire_time
     */
    public Date getExpireTime() {
        return expireTime;
    }

    /**
     * @param expireTime
     */
    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * 获取1：返还，0：不返还
     *
     * @return returnable - 1：返还，0：不返还
     */
    public Integer getReturnable() {
        return returnable;
    }

    /**
     * 设置1：返还，0：不返还
     *
     * @param returnable 1：返还，0：不返还
     */
    public void setReturnable(Integer returnable) {
        this.returnable = returnable;
    }

    /**
     * @return total_qty
     */
    public Integer getTotalQty() {
        return totalQty;
    }

    /**
     * @param totalQty
     */
    public void setTotalQty(Integer totalQty) {
        this.totalQty = totalQty;
    }

    /**
     * @return used_qty
     */
    public Integer getUsedQty() {
        return usedQty;
    }

    /**
     * @param usedQty
     */
    public void setUsedQty(Integer usedQty) {
        this.usedQty = usedQty;
    }

    /**
     * @return issue_mode
     */
    public Integer getIssueMode() {
        return issueMode;
    }

    /**
     * @param issueMode
     */
    public void setIssueMode(Integer issueMode) {
        this.issueMode = issueMode;
    }


    /**
     * @return issued_qty
     */
    public Integer getIssuedQty() {
        return issuedQty;
    }

    /**
     * @param issuedQty
     */
    public void setIssuedQty(Integer issuedQty) {
        this.issuedQty = issuedQty;
    }

    /**
     * @return created_by
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy == null ? null : createdBy.trim();
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取{"giveaway":{"101": 1, "102": 2}, "discount": 90, "extra_service_term": 1}
            
            giveaway: 赠送商品sku及数量配置， {"101": 1, "102": 2}
            discount：订单享受的折扣
            extra_service_term：赠送的服务时长，单位:年
     *
     * @return discount_config - {"giveaway":{"101": 1, "102": 2}, "discount": 90, "extra_service_term": 1}
            
            giveaway: 赠送商品sku及数量配置， {"101": 1, "102": 2}
            discount：订单享受的折扣
            extra_service_term：赠送的服务时长，单位:年
     */
    public String getDiscountConfig() {
        return discountConfig;
    }

    /**
     * 设置{"giveaway":{"101": 1, "102": 2}, "discount": 90, "extra_service_term": 1}
            
            giveaway: 赠送商品sku及数量配置， {"101": 1, "102": 2}
            discount：订单享受的折扣
            extra_service_term：赠送的服务时长，单位:年
     *
     * @param discountConfig {"giveaway":{"101": 1, "102": 2}, "discount": 90, "extra_service_term": 1}
            
            giveaway: 赠送商品sku及数量配置， {"101": 1, "102": 2}
            discount：订单享受的折扣
            extra_service_term：赠送的服务时长，单位:年
     */
    public void setDiscountConfig(String discountConfig) {
        this.discountConfig = discountConfig == null ? null : discountConfig.trim();
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
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public List<Coupon> getItems() {
        return items;
    }

    public void setItems(List<Coupon> items) {
        this.items = items;
    }

    public String getCouponNamePinyin() {
        return couponNamePinyin;
    }

    public void setCouponNamePinyin(String couponNamePinyin) {
        this.couponNamePinyin = couponNamePinyin;
    }

    public Integer getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(Integer issueStatus) {
        this.issueStatus = issueStatus;
    }
}