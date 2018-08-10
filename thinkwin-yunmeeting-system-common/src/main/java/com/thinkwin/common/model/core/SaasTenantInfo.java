package com.thinkwin.common.model.core;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`saas_tenant_info`")
public class SaasTenantInfo implements Serializable{
    private static final long serialVersionUID = -1776770221267304633L;
    @Id
    @Column(name = "`Id`")
    private String id;

    /**
     * 租户Id
     */
    @Column(name = "`tenant_id`")
    private String tenantId;

    /**
     * 租户名称（公司名称）
     */
    @Column(name = "`tenant_name`")
    private String tenantName;

    /**
     * 公司logo
     */
    @Column(name = "`company_logo`")
    private String companyLogo;

    /**
     * 公司类型（行业类型）
     */
    @Column(name = "`company_type`")
    private String companyType;

    /**
     * 公司地址
     */
    @Column(name = "`company_address`")
    private String companyAddress;

    /**
     * 公司简介
     */
    @Column(name = "`company_description`")
    private String companyDescription;

    /**
     * 邀请码
     */
    @Column(name = "`company_invitation_code`")
    private String companyInvitationCode;

    /**
     * 二维码路径
     */
    @Column(name = "`qrcode_path`")
    private String qrcodePath;

    /**
     * 主管理员Id
     */
    @Column(name = "`admin_id`")
    private String adminId;

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
     * 获取租户名称（公司名称）
     *
     * @return tenant_name - 租户名称（公司名称）
     */
    public String getTenantName() {
        return tenantName;
    }

    /**
     * 设置租户名称（公司名称）
     *
     * @param tenantName 租户名称（公司名称）
     */
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName == null ? null : tenantName.trim();
    }

    /**
     * 获取公司logo
     *
     * @return company_logo - 公司logo
     */
    public String getCompanyLogo() {
        return companyLogo;
    }

    /**
     * 设置公司logo
     *
     * @param companyLogo 公司logo
     */
    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo == null ? null : companyLogo.trim();
    }

    /**
     * 获取公司类型（行业类型）
     *
     * @return company_type - 公司类型（行业类型）
     */
    public String getCompanyType() {
        return companyType;
    }

    /**
     * 设置公司类型（行业类型）
     *
     * @param companyType 公司类型（行业类型）
     */
    public void setCompanyType(String companyType) {
        this.companyType = companyType == null ? null : companyType.trim();
    }

    /**
     * 获取公司地址
     *
     * @return company_address - 公司地址
     */
    public String getCompanyAddress() {
        return companyAddress;
    }

    /**
     * 设置公司地址
     *
     * @param companyAddress 公司地址
     */
    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress == null ? null : companyAddress.trim();
    }

    /**
     * 获取公司简介
     *
     * @return company_description - 公司简介
     */
    public String getCompanyDescription() {
        return companyDescription;
    }

    /**
     * 设置公司简介
     *
     * @param companyDescription 公司简介
     */
    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription == null ? null : companyDescription.trim();
    }

    /**
     * 获取邀请码
     *
     * @return company_invitation_code - 邀请码
     */
    public String getCompanyInvitationCode() {
        return companyInvitationCode;
    }

    /**
     * 设置邀请码
     *
     * @param companyInvitationCode 邀请码
     */
    public void setCompanyInvitationCode(String companyInvitationCode) {
        this.companyInvitationCode = companyInvitationCode == null ? null : companyInvitationCode.trim();
    }

    /**
     * 获取二维码路径
     *
     * @return qrcode_path - 二维码路径
     */
    public String getQrcodePath() {
        return qrcodePath;
    }

    /**
     * 设置二维码路径
     *
     * @param qrcodePath 二维码路径
     */
    public void setQrcodePath(String qrcodePath) {
        this.qrcodePath = qrcodePath == null ? null : qrcodePath.trim();
    }

    /**
     * 获取主管理员Id
     *
     * @return admin_id - 主管理员Id
     */
    public String getAdminId() {
        return adminId;
    }

    /**
     * 设置主管理员Id
     *
     * @param adminId 主管理员Id
     */
    public void setAdminId(String adminId) {
        this.adminId = adminId == null ? null : adminId.trim();
    }
}