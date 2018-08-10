package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`spec_def`")
public class SpecDef implements Serializable {
    private static final long serialVersionUID = 8512322024632568716L;
    @Id
    @Column(name = "`spec_code`")
    private String specCode;

    @Column(name = "`uom_code`")
    private String uomCode;

    @Column(name = "`spec_name`")
    private String specName;

    @Column(name = "`spec_desc`")
    private String specDesc;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`update_time`")
    private Date updateTime;

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
     * @return spec_name
     */
    public String getSpecName() {
        return specName;
    }

    /**
     * @param specName
     */
    public void setSpecName(String specName) {
        this.specName = specName == null ? null : specName.trim();
    }

    /**
     * @return spec_desc
     */
    public String getSpecDesc() {
        return specDesc;
    }

    /**
     * @param specDesc
     */
    public void setSpecDesc(String specDesc) {
        this.specDesc = specDesc == null ? null : specDesc.trim();
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