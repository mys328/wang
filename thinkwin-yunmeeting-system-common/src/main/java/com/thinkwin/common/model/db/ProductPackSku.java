package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`product_pack_sku`")
public class ProductPackSku implements Serializable {
    private static final long serialVersionUID = 1107443428528770570L;
    @Id
    @Column(name = "`sku`")
    private String sku;

    @Column(name = "`product_pack_id`")
    private String productPackId;

    @Column(name = "`list_price`")
    private BigDecimal listPrice;

    @Column(name = "`sale_price`")
    private BigDecimal salePrice;

    //基础价格，一年的价格
    @Column(name = "`unit_price`")
    private Double unitPrice;

    /**
     * 折扣率
     */
    @Column(name = "`discount`")
    private String discount;

    /**
     * 折扣提示
     */
    @Column(name = "`discount_tip`")
    private String discountTip;

    /**
     * SKU状态，0：不可用，1：可用
     */
    @Column(name = "`status`")
    private Integer status;

    @Column(name = "`sort_order`")
    private Integer sortOrder;

    @Column(name = "`promotion_code`")
    private String promotionCode;

    @Column(name = "`sku_desc`")
    private String skuDesc;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`update_time`")
    private Date updateTime;

    /**
     * @return sku
     */
    public String getSku() {
        return sku;
    }

    /**
     * @param sku
     */
    public void setSku(String sku) {
        this.sku = sku == null ? null : sku.trim();
    }

    /**
     * @return product_pack_id
     */
    public String getProductPackId() {
        return productPackId;
    }

    /**
     * @param productPackId
     */
    public void setProductPackId(String productPackId) {
        this.productPackId = productPackId == null ? null : productPackId.trim();
    }

    /**
     * @return list_price
     */
    public BigDecimal getListPrice() {
        return listPrice;
    }

    /**
     * @param listPrice
     */
    public void setListPrice(BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    /**
     * @return sale_price
     */
    public BigDecimal getSalePrice() {
        return salePrice;
    }

    /**
     * @param salePrice
     */
    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * 获取折扣率
     *
     * @return discount - 折扣率
     */
    public String getDiscount() {
        return discount;
    }

    /**
     * 设置折扣率
     *
     * @param discount 折扣率
     */
    public void setDiscount(String discount) {
        this.discount = discount == null ? null : discount.trim();
    }

    /**
     * 获取折扣提示
     *
     * @return discount_tip - 折扣提示
     */
    public String getDiscountTip() {
        return discountTip;
    }

    /**
     * 设置折扣提示
     *
     * @param discountTip 折扣提示
     */
    public void setDiscountTip(String discountTip) {
        this.discountTip = discountTip == null ? null : discountTip.trim();
    }

    /**
     * 获取SKU状态，0：不可用，1：可用
     *
     * @return status - SKU状态，0：不可用，1：可用
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置SKU状态，0：不可用，1：可用
     *
     * @param status SKU状态，0：不可用，1：可用
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return sort_order
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return promotion_code
     */
    public String getPromotionCode() {
        return promotionCode;
    }

    /**
     * @param promotionCode
     */
    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode == null ? null : promotionCode.trim();
    }

    /**
     * @return sku_desc
     */
    public String getSkuDesc() {
        return skuDesc;
    }

    /**
     * @param skuDesc
     */
    public void setSkuDesc(String skuDesc) {
        this.skuDesc = skuDesc == null ? null : skuDesc.trim();
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
}