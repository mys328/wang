package com.thinkwin.common.model.core;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`saas_user_api`")
public class SaasUserApi implements Serializable{
    private static final long serialVersionUID = 6395841304748351221L;
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 用户ID
     */
    @Column(name = "`user_id`")
    private String userId;

    /**
     * 租户Id
     */
    @Column(name = "`tenant_id`")
    private String tenantId;

    @Column(name = "`api_key`")
    private String apiKey;

    @Column(name = "`api_secret`")
    private String apiSecret;

    /**
     * 最后登录时间
     */
    @Column(name = "`last_login_time`")
    private Date lastLoginTime;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取用户ID
     *
     * @return user_id - 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    /**
     * 获取租户Id
     *
     * @return tenant_id - 租户Id
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * 设置租户Id
     *
     * @param tenantId 租户Id
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    /**
     * @return api_key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @param apiKey
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey == null ? null : apiKey.trim();
    }

    /**
     * @return api_secret
     */
    public String getApiSecret() {
        return apiSecret;
    }

    /**
     * @param apiSecret
     */
    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret == null ? null : apiSecret.trim();
    }

    /**
     * 获取最后登录时间
     *
     * @return last_login_time - 最后登录时间
     */
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * 设置最后登录时间
     *
     * @param lastLoginTime 最后登录时间
     */
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}