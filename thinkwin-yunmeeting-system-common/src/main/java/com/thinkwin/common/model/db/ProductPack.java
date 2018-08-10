package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`product_pack`")
public class ProductPack implements Serializable {
    private static final long serialVersionUID = -8660402939714263121L;
    @Id
    @Column(name = "`package_id`")
    private String packageId;

    @Column(name = "`category_code`")
    private String categoryCode;

    /**
     * 1:正常使用, 0: 已下架
     */
    @Column(name = "`status`")
    private Integer status;

    @Column(name = "`sort_order`")
    private Integer sortOrder;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`update_time`")
    private Date updateTime;

    /**
     * @return package_id
     */
    public String getPackageId() {
        return packageId;
    }

    /**
     * @param packageId
     */
    public void setPackageId(String packageId) {
        this.packageId = packageId == null ? null : packageId.trim();
    }

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
     * 获取1:正常使用, 0: 已下架
     *
     * @return status - 1:正常使用, 0: 已下架
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置1:正常使用, 0: 已下架
     *
     * @param status 1:正常使用, 0: 已下架
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