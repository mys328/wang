package com.thinkwin.common.model.publish;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "`platform_client_version_upgrade_recorder`")
public class PlatformClientVersionUpgradeRecorder implements Serializable {
    private static final long serialVersionUID = 6336909565186100669L;
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 租户ID
     */
    @Column(name = "`tenant_id`")
    private String tenantId;

    /**
     * 平台终端版本ID
     */
    @Column(name = "`client_version_id`")
    private String clientVersionId;

    /**
     * 终端标识
     */
    @Column(name = "`hardware_id`")
    private String hardwareId;

    /**
     * 租户名称
     */
    @Column(name = "`tenant_name`")
    private String tenantName;

    /**
     * 租户名称拼音
     */
    @Column(name = "`tenant_name_pinyin`")
    private String tenantNamePinyin;

    /**
     * 联系人
     */
    @Column(name = "`user_name`")
    private String userName;

    /**
     * 电话
     */
    @Column(name = "`phone_number`")
    private String phoneNumber;

    @Column(name = "`type`")
    private String type;

    /**
     * 上一版本
     */
    @Column(name = "`last_ver`")
    private String lastVer;

    /**
     * 当前版本
     */
    @Column(name = "`current_ver`")
    private String currentVer;

    /**
     * 状态 1成功，0失败
     */
    @Column(name = "`status`")
    private String status;

    /**
     * 升级时间
     */
    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`update_time`")
    private Date updateTime;

    /**
     * 升级日志
     */
    @Column(name = "`upgrade_log`")
    private String upgradeLog;

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
     * 获取平台终端版本ID
     *
     * @return client_version_id - 平台终端版本ID
     */
    public String getClientVersionId() {
        return clientVersionId;
    }

    /**
     * 设置平台终端版本ID
     *
     * @param clientVersionId 平台终端版本ID
     */
    public void setClientVersionId(String clientVersionId) {
        this.clientVersionId = clientVersionId == null ? null : clientVersionId.trim();
    }

    /**
     * 获取终端标识
     *
     * @return hardware_id - 终端标识
     */
    public String getHardwareId() {
        return hardwareId;
    }

    /**
     * 设置终端标识
     *
     * @param hardwareId 终端标识
     */
    public void setHardwareId(String hardwareId) {
        this.hardwareId = hardwareId == null ? null : hardwareId.trim();
    }

    /**
     * 获取租户名称
     *
     * @return tenant_name - 租户名称
     */
    public String getTenantName() {
        return tenantName;
    }

    /**
     * 设置租户名称
     *
     * @param tenantName 租户名称
     */
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName == null ? null : tenantName.trim();
    }

    /**
     * 获取租户名称拼音
     *
     * @return tenant_name_pinyin - 租户名称拼音
     */
    public String getTenantNamePinyin() {
        return tenantNamePinyin;
    }

    /**
     * 设置租户名称拼音
     *
     * @param tenantNamePinyin 租户名称拼音
     */
    public void setTenantNamePinyin(String tenantNamePinyin) {
        this.tenantNamePinyin = tenantNamePinyin == null ? null : tenantNamePinyin.trim();
    }

    /**
     * 获取联系人
     *
     * @return user_name - 联系人
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置联系人
     *
     * @param userName 联系人
     */
    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    /**
     * 获取电话
     *
     * @return phone_number - 电话
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * 设置电话
     *
     * @param phoneNumber 电话
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber == null ? null : phoneNumber.trim();
    }

    /**
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * 获取上一版本
     *
     * @return last_ver - 上一版本
     */
    public String getLastVer() {
        return lastVer= lastVer == null ? "无" : lastVer.trim();
    }

    /**
     * 设置上一版本
     *
     * @param lastVer 上一版本
     */
    public void setLastVer(String lastVer) {
        this.lastVer = lastVer == null ? null : lastVer.trim();
    }

    /**
     * 获取当前版本
     *
     * @return current_ver - 当前版本
     */
    public String getCurrentVer() {
        return currentVer;
    }

    /**
     * 设置当前版本
     *
     * @param currentVer 当前版本
     */
    public void setCurrentVer(String currentVer) {
        this.currentVer = currentVer == null ? null : currentVer.trim();
    }

    /**
     * 获取状态 1成功，0失败
     *
     * @return status - 状态 1成功，0失败
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态 1成功，0失败
     *
     * @param status 状态 1成功，0失败
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * 获取升级时间
     *
     * @return create_time - 升级时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置升级时间
     *
     * @param createTime 升级时间
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

    /**
     * 获取升级日志
     *
     * @return upgrade_log - 升级日志
     */
    public String getUpgradeLog() {
        return upgradeLog;
    }

    /**
     * 设置升级日志
     *
     * @param upgradeLog 升级日志
     */
    public void setUpgradeLog(String upgradeLog) {
        this.upgradeLog = upgradeLog == null ? null : upgradeLog.trim();
    }
}