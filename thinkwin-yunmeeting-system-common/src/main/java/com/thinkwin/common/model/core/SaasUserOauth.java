package com.thinkwin.common.model.core;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "`saas_user_oauth`")
public class SaasUserOauth implements Serializable {
    private static final long serialVersionUID = -4081133107722144431L;
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 用户Id
     */
    @Column(name = "`user_id`")
    private String userId;

    /**
     * 第三方类型  1：开发平台 2：公众平台  0：其他
     */
    @Column(name = "`oauth_type`")
    private Integer oauthType;

    /**
     * 第三方唯一openId
     */
    @Column(name = "`oauth_open_id`")
    private String oauthOpenId;

    /**
     * 第三方唯一unionId
     */
    @Column(name = "`oauth_union_id`")
    private String oauthUnionId;

    /**
     * 公众号生成临时二维码凭据
     */
    @Column(name = "`ticket`")
    private String ticket;

    /**
     * ticket过期时间
     */
    @Column(name = "`ticket_time`")
    private Date ticketTime;

    /**
     * 密码
     */
    @Column(name = "`password`")
    private String password;

    /**
     * 租户Id
     */
    @Column(name = "`tenant_id`")
    private String tenantId;

    /**
     * 第三方账户头像
     */
    @Column(name = "`oauth_photo`")
    private String oauthPhoto;

    /**
     * 第三方账户昵称
     */
    @Column(name = "`oauth_user_name`")
    private String oauthUserName;

    /**
     * 第三方access_token
     */
    @Column(name = "`oauth_access_token`")
    private String oauthAccessToken;

    /**
     * 第三方刷新token
     */
    @Column(name = "`oauth_refresh_token`")
    private String oauthRefreshToken;

    /**
     * 第三方access_token过期时间
     */
    @Column(name = "`oauth_expires`")
    private String oauthExpires;

    /**
     * 业务状态
     */
    @Column(name = "`state`")
    private Integer state;

    /**
     * 状态 1：正常 0：禁用
     */
    @Column(name = "`status`")
    private Integer status;

    /**
     * 是否绑定1是,2否
     */
    @Column(name = "`is_bind`")
    private Integer isBind;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "`update_time`")
    private Date updateTime;

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
     * 获取第三方类型  1：开发平台 2：公众平台  0：其他
     *
     * @return oauth_type - 第三方类型  1：开发平台 2：公众平台  0：其他
     */
    public Integer getOauthType() {
        return oauthType;
    }

    /**
     * 设置第三方类型  1：开发平台 2：公众平台  0：其他
     *
     * @param oauthType 第三方类型  1：开发平台 2：公众平台  0：其他
     */
    public void setOauthType(Integer oauthType) {
        this.oauthType = oauthType;
    }

    /**
     * 获取第三方唯一openId
     *
     * @return oauth_open_id - 第三方唯一openId
     */
    public String getOauthOpenId() {
        return oauthOpenId;
    }

    /**
     * 设置第三方唯一openId
     *
     * @param oauthOpenId 第三方唯一openId
     */
    public void setOauthOpenId(String oauthOpenId) {
        this.oauthOpenId = oauthOpenId == null ? null : oauthOpenId.trim();
    }

    /**
     * 获取第三方唯一unionId
     *
     * @return oauth_union_id - 第三方唯一unionId
     */
    public String getOauthUnionId() {
        return oauthUnionId;
    }

    /**
     * 设置第三方唯一unionId
     *
     * @param oauthUnionId 第三方唯一unionId
     */
    public void setOauthUnionId(String oauthUnionId) {
        this.oauthUnionId = oauthUnionId == null ? null : oauthUnionId.trim();
    }

    /**
     * 获取公众号生成临时二维码凭据
     *
     * @return ticket - 公众号生成临时二维码凭据
     */
    public String getTicket() {
        return ticket;
    }

    /**
     * 设置公众号生成临时二维码凭据
     *
     * @param ticket 公众号生成临时二维码凭据
     */
    public void setTicket(String ticket) {
        this.ticket = ticket == null ? null : ticket.trim();
    }

    /**
     * 获取ticket过期时间
     *
     * @return ticket_time - ticket过期时间
     */
    public Date getTicketTime() {
        return ticketTime;
    }

    /**
     * 设置ticket过期时间
     *
     * @param ticketTime ticket过期时间
     */
    public void setTicketTime(Date ticketTime) {
        this.ticketTime = ticketTime;
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
     * 获取第三方账户头像
     *
     * @return oauth_photo - 第三方账户头像
     */
    public String getOauthPhoto() {
        return oauthPhoto;
    }

    /**
     * 设置第三方账户头像
     *
     * @param oauthPhoto 第三方账户头像
     */
    public void setOauthPhoto(String oauthPhoto) {
        this.oauthPhoto = oauthPhoto == null ? null : oauthPhoto.trim();
    }

    /**
     * 获取第三方账户昵称
     *
     * @return oauth_user_name - 第三方账户昵称
     */
    public String getOauthUserName() {
        return oauthUserName;
    }

    /**
     * 设置第三方账户昵称
     *
     * @param oauthUserName 第三方账户昵称
     */
    public void setOauthUserName(String oauthUserName) {
        this.oauthUserName = oauthUserName == null ? null : oauthUserName.trim();
    }

    /**
     * 获取第三方access_token
     *
     * @return oauth_access_token - 第三方access_token
     */
    public String getOauthAccessToken() {
        return oauthAccessToken;
    }

    /**
     * 设置第三方access_token
     *
     * @param oauthAccessToken 第三方access_token
     */
    public void setOauthAccessToken(String oauthAccessToken) {
        this.oauthAccessToken = oauthAccessToken == null ? null : oauthAccessToken.trim();
    }

    /**
     * 获取第三方刷新token
     *
     * @return oauth_refresh_token - 第三方刷新token
     */
    public String getOauthRefreshToken() {
        return oauthRefreshToken;
    }

    /**
     * 设置第三方刷新token
     *
     * @param oauthRefreshToken 第三方刷新token
     */
    public void setOauthRefreshToken(String oauthRefreshToken) {
        this.oauthRefreshToken = oauthRefreshToken == null ? null : oauthRefreshToken.trim();
    }

    /**
     * 获取第三方access_token过期时间
     *
     * @return oauth_expires - 第三方access_token过期时间
     */
    public String getOauthExpires() {
        return oauthExpires;
    }

    /**
     * 设置第三方access_token过期时间
     *
     * @param oauthExpires 第三方access_token过期时间
     */
    public void setOauthExpires(String oauthExpires) {
        this.oauthExpires = oauthExpires == null ? null : oauthExpires.trim();
    }

    /**
     * 获取业务状态
     *
     * @return state - 业务状态
     */
    public Integer getState() {
        return state;
    }

    /**
     * 设置业务状态
     *
     * @param state 业务状态
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 获取状态 1：正常 0：禁用
     *
     * @return status - 状态 1：正常 0：禁用
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态 1：正常 0：禁用
     *
     * @param status 状态 1：正常 0：禁用
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取是否绑定1是,2否
     *
     * @return is_bind - 是否绑定1是,2否
     */
    public Integer getIsBind() {
        return isBind;
    }

    /**
     * 设置是否绑定1是,2否
     *
     * @param isBind 是否绑定1是,2否
     */
    public void setIsBind(Integer isBind) {
        this.isBind = isBind;
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

    /**
     * 获取修改时间
     *
     * @return update_time - 修改时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置修改时间
     *
     * @param updateTime 修改时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "SaasUserOauth{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", oauthType=" + oauthType +
                ", oauthOpenId='" + oauthOpenId + '\'' +
                ", oauthUnionId='" + oauthUnionId + '\'' +
                ", ticket='" + ticket + '\'' +
                ", ticketTime=" + ticketTime +
                ", password='" + password + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", oauthPhoto='" + oauthPhoto + '\'' +
                ", oauthUserName='" + oauthUserName + '\'' +
                ", oauthAccessToken='" + oauthAccessToken + '\'' +
                ", oauthRefreshToken='" + oauthRefreshToken + '\'' +
                ", oauthExpires='" + oauthExpires + '\'' +
                ", state=" + state +
                ", status=" + status +
                ", isBind=" + isBind +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}