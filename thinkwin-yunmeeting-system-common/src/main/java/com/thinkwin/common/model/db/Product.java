package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`product`")
public class Product implements Serializable {
    private static final long serialVersionUID = -6363700126450750713L;
    @Id
    @Column(name = "`product_id`")
    private String productId;

    @Column(name = "`class_code`")
    private String classCode;

    @Column(name = "`product_name`")
    private String productName;

    @Column(name = "`product_desc`")
    private String productDesc;

    /**
     * 0：已下架
            1：上架状态
            
     */
    @Column(name = "`status`")
    private Integer status;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`update_time`")
    private Date updateTime;

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
     * @return class_code
     */
    public String getClassCode() {
        return classCode;
    }

    /**
     * @param classCode
     */
    public void setClassCode(String classCode) {
        this.classCode = classCode == null ? null : classCode.trim();
    }

    /**
     * @return product_name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName
     */
    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    /**
     * @return product_desc
     */
    public String getProductDesc() {
        return productDesc;
    }

    /**
     * @param productDesc
     */
    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc == null ? null : productDesc.trim();
    }

    /**
     * 获取0：已下架
            1：上架状态
            
     *
     * @return status - 0：已下架
            1：上架状态
            
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置0：已下架
            1：上架状态
            
     *
     * @param status 0：已下架
            1：上架状态
            
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