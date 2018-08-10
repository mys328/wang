package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`uom_def`")
public class UomDef implements Serializable {
    private static final long serialVersionUID = 7104477289987622908L;
    @Id
    @Column(name = "`uom_code`")
    private String uomCode;

    /**
     * 计量单位分类：数量，重量，体积，长度，时间
     */
    @Column(name = "`uom_class`")
    private String uomClass;

    @Column(name = "`uom_name`")
    private String uomName;

    @Column(name = "`uom_desc`")
    private String uomDesc;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`update_time`")
    private Date updateTime;

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
     * 获取计量单位分类：数量，重量，体积，长度，时间
     *
     * @return uom_class - 计量单位分类：数量，重量，体积，长度，时间
     */
    public String getUomClass() {
        return uomClass;
    }

    /**
     * 设置计量单位分类：数量，重量，体积，长度，时间
     *
     * @param uomClass 计量单位分类：数量，重量，体积，长度，时间
     */
    public void setUomClass(String uomClass) {
        this.uomClass = uomClass == null ? null : uomClass.trim();
    }

    /**
     * @return uom_name
     */
    public String getUomName() {
        return uomName;
    }

    /**
     * @param uomName
     */
    public void setUomName(String uomName) {
        this.uomName = uomName == null ? null : uomName.trim();
    }

    /**
     * @return uom_desc
     */
    public String getUomDesc() {
        return uomDesc;
    }

    /**
     * @param uomDesc
     */
    public void setUomDesc(String uomDesc) {
        this.uomDesc = uomDesc == null ? null : uomDesc.trim();
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