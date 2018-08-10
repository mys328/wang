package com.thinkwin.common.model.db;

import java.util.Date;
import javax.persistence.*;

@Table(name = "`product_pack_line`")
public class ProductPackLine {
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`product_pack_id`")
    private String productPackId;

    @Column(name = "`product_id`")
    private String productId;

    @Column(name = "`product_uom`")
    private String productUom;

    @Column(name = "`qty`")
    private Integer qty;

    @Column(name = "`sort_order`")
    private Integer sortOrder;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`update_time`")
    private Date updateTime;

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
     * @return product_uom
     */
    public String getProductUom() {
        return productUom;
    }

    /**
     * @param productUom
     */
    public void setProductUom(String productUom) {
        this.productUom = productUom == null ? null : productUom.trim();
    }

    /**
     * @return qty
     */
    public Integer getQty() {
        return qty;
    }

    /**
     * @param qty
     */
    public void setQty(Integer qty) {
        this.qty = qty;
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