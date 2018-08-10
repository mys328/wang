package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`product_pack_category`")
public class ProductPackCategory implements Serializable {
    private static final long serialVersionUID = -8423683159302899825L;
    @Id
    @Column(name = "`category_code`")
    private String categoryCode;

    @Column(name = "`category_name`")
    private String categoryName;

    @Column(name = "`category_desc`")
    private String categoryDesc;

    @Column(name = "`sort_order`")
    private Integer sortOrder;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`update_time`")
    private Date updateTime;

    /**
     * @return category_code
     */
    public String getCategoryCode() {
        return categoryCode;
    }

    /**
     * @param categoryCode
     */
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode == null ? null : categoryCode.trim();
    }

    /**
     * @return category_name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName == null ? null : categoryName.trim();
    }

    /**
     * @return category_desc
     */
    public String getCategoryDesc() {
        return categoryDesc;
    }

    /**
     * @param categoryDesc
     */
    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc == null ? null : categoryDesc.trim();
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