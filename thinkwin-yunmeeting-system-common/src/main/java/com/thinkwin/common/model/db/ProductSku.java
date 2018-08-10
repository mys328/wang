package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`product_sku`")
public class ProductSku implements Serializable {
    private static final long serialVersionUID = 8402969880254937070L;
    @Id
    @Column(name = "`sku`")
    private String sku;

    @Column(name = "`product_id`")
    private String productId;

    @Column(name = "`promotion_code`")
    private String promotionCode;

    @Column(name = "`list_price`")
    private BigDecimal listPrice;

    @Column(name = "`sale_price`")
    private BigDecimal salePrice;

    /**
     * SKU状态，0：不可用，1：可用
     */
    @Column(name = "`status`")
    private Integer status;

    @Column(name = "`sort_order`")
    private Integer sortOrder;

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
     * @return product_id
     */
    public String getProductId() {
        return productId;
    }

    /**
     * @param productId
     */
    public void setProductId(String productId) {
        this.productId = productId == null ? null : productId.trim();
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