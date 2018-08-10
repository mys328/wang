package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`product_pack_sku_spec`")
public class ProductPackSkuSpec implements Serializable {
    private static final long serialVersionUID = -7163694599501241028L;
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`sku`")
    private String sku;

    @Column(name = "`spec_code`")
    private String specCode;

    @Column(name = "`spec_value`")
    private String specValue;

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
     * @return spec_code
     */
    public String getSpecCode() {
        return specCode;
    }

    /**
     * @param specCode
     */
    public void setSpecCode(String specCode) {
        this.specCode = specCode == null ? null : specCode.trim();
    }

    /**
     * @return spec_value
     */
    public String getSpecValue() {
        return specValue;
    }

    /**
     * @param specValue
     */
    public void setSpecValue(String specValue) {
        this.specValue = specValue == null ? null : specValue.trim();
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