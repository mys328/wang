package com.thinkwin.common.model.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "`middle_biz_attachment`")
public class MiddleBizAttachment implements Serializable{
    private static final long serialVersionUID = 5218402408487905730L;
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`biz_id`")
    private String bizId;

    /**
     * 业务类型ID,.来自于数据字典
     */
    @Column(name = "`biz_type`")
    private String bizType;

    /**
     * 租户id
     */
    @Column(name = "`tenant_id`")
    private String tenantId;

    /**
     * CM:1；乐会:2;企云会:3     (平台类型)
     */
    @Column(name = "`platform_type`")
    private String platformType;

    /**
     * 系统附件表ID
     */
    @Column(name = "`sys_attachment`")
    private String sysAttachment;

    /**
     * 预留字段1
     */
    @Column(name = "`reserve_1`")
    private String reserve1;

    /**
     * 预留字段2
     */
    @Column(name = "`reserve_2`")
    private String reserve2;

    /**
     * 预留字段3
     */
    @Column(name = "`reserve_3`")
    private String reserve3;

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
     * @return biz_id
     */
    public String getBizId() {
        return bizId;
    }

    /**
     * @param bizId
     */
    public void setBizId(String bizId) {
        this.bizId = bizId == null ? null : bizId.trim();
    }

    /**
     * 获取业务类型ID,.来自于数据字典
     *
     * @return biz_type - 业务类型ID,.来自于数据字典
     */
    public String getBizType() {
        return bizType;
    }

    /**
     * 设置业务类型ID,.来自于数据字典
     *
     * @param bizType 业务类型ID,.来自于数据字典
     */
    public void setBizType(String bizType) {
        this.bizType = bizType == null ? null : bizType.trim();
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
     * 获取CM:1；乐会:2;企云会:3     (平台类型)
     *
     * @return platform_type - CM:1；乐会:2;企云会:3     (平台类型)
     */
    public String getPlatformType() {
        return platformType;
    }

    /**
     * 设置CM:1；乐会:2;企云会:3     (平台类型)
     *
     * @param platformType CM:1；乐会:2;企云会:3     (平台类型)
     */
    public void setPlatformType(String platformType) {
        this.platformType = platformType == null ? null : platformType.trim();
    }

    /**
     * 获取系统附件表ID
     *
     * @return sys_attachment - 系统附件表ID
     */
    public String getSysAttachment() {
        return sysAttachment;
    }

    /**
     * 设置系统附件表ID
     *
     * @param sysAttachment 系统附件表ID
     */
    public void setSysAttachment(String sysAttachment) {
        this.sysAttachment = sysAttachment == null ? null : sysAttachment.trim();
    }

    /**
     * 获取预留字段1
     *
     * @return reserve_1 - 预留字段1
     */
    public String getReserve1() {
        return reserve1;
    }

    /**
     * 设置预留字段1
     *
     * @param reserve1 预留字段1
     */
    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1 == null ? null : reserve1.trim();
    }

    /**
     * 获取预留字段2
     *
     * @return reserve_2 - 预留字段2
     */
    public String getReserve2() {
        return reserve2;
    }

    /**
     * 设置预留字段2
     *
     * @param reserve2 预留字段2
     */
    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2 == null ? null : reserve2.trim();
    }

    /**
     * 获取预留字段3
     *
     * @return reserve_3 - 预留字段3
     */
    public String getReserve3() {
        return reserve3;
    }

    /**
     * 设置预留字段3
     *
     * @param reserve3 预留字段3
     */
    public void setReserve3(String reserve3) {
        this.reserve3 = reserve3 == null ? null : reserve3.trim();
    }
}