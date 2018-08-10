package com.thinkwin.common.model.core;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`saas_user_tenant_middle`")
public class SaasUserTenantMiddle implements Serializable{
    private static final long serialVersionUID = 8251013441967970341L;
    /**
     * 主键ID
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 用户Id
     */
    @Column(name = "`user_id`")
    private String userId;

    /**
     * 租户ID
     */
    @Column(name = "`tenant_id`")
    private String tenantId;

    /**
     * 创建人ID
     */
    @Column(name = "`create_id`")
    private String createId;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`")
    private Date createTime;

    /**
     * 获取主键ID
     *
     * @return id - 主键ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取用户Id
     *
     * @return user_id - 用户Id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户Id
     *
     * @param userId 用户Id
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    /**
     * 获取租户ID
     *
     * @return tenant_id - 租户ID
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * 设置租户ID
     *
     * @param tenantId 租户ID
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    /**
     * 获取创建人ID
     *
     * @return create_id - 创建人ID
     */
    public String getCreateId() {
        return createId;
    }

    /**
     * 设置创建人ID
     *
     * @param createId 创建人ID
     */
    public void setCreateId(String createId) {
        this.createId = createId == null ? null : createId.trim();
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}