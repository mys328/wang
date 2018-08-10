package com.thinkwin.common.model.core;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`saas_user_web`")
public class SaasUserWeb implements Serializable{
    private static final long serialVersionUID = 7677023610053966886L;
    /**
     * 主键
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
     * 账号
     */
    @Column(name = "`account`")
    private String account;

    /**
     * 密码
     */
    @Column(name = "`password`")
    private String password;

    /**
     * 租户id
     */
    @Column(name = "`tenant_id`")
    private String tenantId;

    /**
     * 状态 1：正常 0：禁用 2：离职
     */
    @Column(name = "`status`")
    private Integer status;

    /**
     * 最后登录时间
     */
    @Column(name = "`last_login_time`")
    private Date lastLoginTime;

    /**
     * 上次登录时间
     */
    @Column(name = "`last_time_login`")
    private Date lastTimeLogin;

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
     * 获取账号
     *
     * @return account - 账号
     */
    public String getAccount() {
        return account;
    }

    /**
     * 设置账号
     *
     * @param account 账号
     */
    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    /**
     * 获取密码
     *
     * @return password - 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * 获取租户id
     *
     * @return tenant_id - 租户id
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * 设置租户id
     *
     * @param tenantId 租户id
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    /**
     * 获取状态 1：正常 0：禁用 2：离职
     *
     * @return status - 状态 1：正常 0：禁用 2：离职
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态 1：正常 0：禁用 2：离职
     *
     * @param status 状态 1：正常 0：禁用 2：离职
     */
    public void setStatus(Integer status) {
        this.status = status;
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

    /**
     * 获取上次登录时间
     *
     * @return last_time_login - 上次登录时间
     */
    public Date getLastTimeLogin() {
        return lastTimeLogin;
    }

    /**
     * 设置上次登录时间
     *
     * @param lastTimeLogin 上次登录时间
     */
    public void setLastTimeLogin(Date lastTimeLogin) {
        this.lastTimeLogin = lastTimeLogin;
    }
}