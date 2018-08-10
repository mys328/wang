package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`sku_uom`")
public class SkuUom implements Serializable {
    private static final long serialVersionUID = 1808695040605549686L;
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`sku`")
    private String sku;

    @Column(name = "`uom_code`")
    private String uomCode;

    @Column(name = "`uom_value`")
    private String uomValue;

    @Column(name = "`sort_order`")
    private Integer sortOrder;

    /**
     * 1：启用
            0：已下架
     */
    @Column(name = "`status`")
    private Integer status;

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
     * @return uom_code
     */
    public String getUomCode() {
        return uomCode;
    }

    /**
     * @param uomCode
     */
    public void setUomCode(String uomCode) {
        this.uomCode = uomCode == null ? null : uomCode.trim();
    }

    /**
     * @return uom_value
     */
    public String getUomValue() {
        return uomValue;
    }

    /**
     * @param uomValue
     */
    public void setUomValue(String uomValue) {
        this.uomValue = uomValue == null ? null : uomValue.trim();
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
     * 获取1：启用
            0：已下架
     *
     * @return status - 1：启用
            0：已下架
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置1：启用
            0：已下架
     *
     * @param status 1：启用
            0：已下架
     */
    public void setStatus(Integer status) {
        this.status = status;
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