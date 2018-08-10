package com.thinkwin.common.model.db;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`t_file_centre`")
public class TFileCentre implements Serializable{
    private static final long serialVersionUID = -851509351875394531L;
    @Id
    @Column(name = "`Id`")
    private String id;

    /**
     * 业务ID
     */
    @Column(name = "`biz_id`")
    private String bizId;

    /**
     * 业务类型
     */
    @Column(name = "`biz_type`")
    private String bizType;

    /**
     * 租户id
     */
    @Column(name = "`tenant_id`")
    private String tenantId;

    /**
     * 平台类型
     */
    @Column(name = "`platform_type`")
    private String platformType;

    /**
     * 附件表ID
     */
    @Column(name = "`sys_attachment_id`")
    private String sysAttachmentId;

    /**
     * @return Id
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
     * 获取业务ID
     *
     * @return biz_id - 业务ID
     */
    public String getBizId() {
        return bizId;
    }

    /**
     * 设置业务ID
     *
     * @param bizId 业务ID
     */
    public void setBizId(String bizId) {
        this.bizId = bizId == null ? null : bizId.trim();
    }

    /**
     * 获取业务类型
     *
     * @return biz_type - 业务类型
     */
    public String getBizType() {
        return bizType;
    }

    /**
     * 设置业务类型
     *
     * @param bizType 业务类型
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
     * 获取平台类型
     *
     * @return platform_type - 平台类型
     */
    public String getPlatformType() {
        return platformType;
    }

    /**
     * 设置平台类型
     *
     * @param platformType 平台类型
     */
    public void setPlatformType(String platformType) {
        this.platformType = platformType == null ? null : platformType.trim();
    }

    /**
     * 获取附件表ID
     *
     * @return sys_attachment_id - 附件表ID
     */
    public String getSysAttachmentId() {
        return sysAttachmentId;
    }

    /**
     * 设置附件表ID
     *
     * @param sysAttachmentId 附件表ID
     */
    public void setSysAttachmentId(String sysAttachmentId) {
        this.sysAttachmentId = sysAttachmentId == null ? null : sysAttachmentId.trim();
    }
}