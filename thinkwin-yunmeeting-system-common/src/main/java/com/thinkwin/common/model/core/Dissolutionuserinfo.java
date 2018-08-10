package com.thinkwin.common.model.core;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`dissolutionUserInfo`")
public class Dissolutionuserinfo implements Serializable {
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`user_name`")
    private String userName;

    @Column(name = "`user_name_pinyin`")
    private String userNamePinyin;

    @Column(name = "`phone_number`")
    private String phoneNumber;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`tenant_id`")
    private String tenantId;

    @Column(name = "`user_id`")
    private String userId;

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
     * @return user_name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    /**
     * @return user_name_pinyin
     */
    public String getUserNamePinyin() {
        return userNamePinyin;
    }

    /**
     * @param userNamePinyin
     */
    public void setUserNamePinyin(String userNamePinyin) {
        this.userNamePinyin = userNamePinyin == null ? null : userNamePinyin.trim();
    }

    /**
     * @return phone_number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber == null ? null : phoneNumber.trim();
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
     * @return tenant_id
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    /**
     * @return user_id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }
}