package com.thinkwin.common.model.db;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`sku_attr`")
public class SkuAttr implements Serializable {
    private static final long serialVersionUID = 7931938311996689951L;
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`attr_code`")
    private String attrCode;

    @Column(name = "`sku`")
    private String sku;

    @Column(name = "`attr_value`")
    private String attrValue;

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
     * @return attr_code
     */
    public String getAttrCode() {
        return attrCode;
    }

    /**
     * @param attrCode
     */
    public void setAttrCode(String attrCode) {
        this.attrCode = attrCode == null ? null : attrCode.trim();
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
     * @return attr_value
     */
    public String getAttrValue() {
        return attrValue;
    }

    /**
     * @param attrValue
     */
    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue == null ? null : attrValue.trim();
    }
}