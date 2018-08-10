package com.thinkwin.common.model.db;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`attr_def`")
public class AttrDef implements Serializable{
    private static final long serialVersionUID = -6555681235486228507L;
    /**
     * id
     */
    @Id
    @Column(name = "`attr_code`")
    private String attrCode;

    /**
     * 属性名称
     */
    @Column(name = "`attr_name`")
    private String attrName;

    /**
     * 描述
     */
    @Column(name = "`attr_desc`")
    private String attrDesc;

    /**
     * 获取id
     *
     * @return attr_code - id
     */
    public String getAttrCode() {
        return attrCode;
    }

    /**
     * 设置id
     *
     * @param attrCode id
     */
    public void setAttrCode(String attrCode) {
        this.attrCode = attrCode == null ? null : attrCode.trim();
    }

    /**
     * 获取属性名称
     *
     * @return attr_name - 属性名称
     */
    public String getAttrName() {
        return attrName;
    }

    /**
     * 设置属性名称
     *
     * @param attrName 属性名称
     */
    public void setAttrName(String attrName) {
        this.attrName = attrName == null ? null : attrName.trim();
    }

    /**
     * 获取描述
     *
     * @return attr_desc - 描述
     */
    public String getAttrDesc() {
        return attrDesc;
    }

    /**
     * 设置描述
     *
     * @param attrDesc 描述
     */
    public void setAttrDesc(String attrDesc) {
        this.attrDesc = attrDesc == null ? null : attrDesc.trim();
    }
}