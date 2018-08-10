package com.thinkwin.common.vo;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * User: yinchunlei
 * Date: 2017/8/29.
 * Company: thinkwin
 */
public class SaasTenantInfoVo implements Serializable {

    private static final long serialVersionUID = -174131063344676823L;
    private String id;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 租户名称（公司名称）
     */
    private String tenantName;

    /**
     * 公司logo原图
     */
    private String companyLogo;

    private String bigPicture; //大图
    private String inPicture; //中图
    private String smallPicture;  // 小图

    /**
     * 公司类型（行业类型）
     */
    private String companyType;

    /**
     * 公司地址
     */
    private String companyAddress;

    /**
     * 公司简介
     */
    private String companyDescription;

    /**
     * 邀请码
     */
    private String companyInvitationCode;

    /**
     * 二维码路径
     */
    private String qrcodePath;

    /**
     * 主管理员Id
     */
    private String adminId;

    /**
     * 当前购买产品的名称
     */
    private String productName;

    /**
     * 套餐有效期
     */
    private Date basePackageExpir;
    /**
     * 套餐可开通用户数
     */
    private Integer expectNumber;
    /**
     * 套餐会议室总数
     */
    private Integer buyRoomNumTotal;

    private Integer spaceTotal;
    private String adminPhone;//管理员手机号

    public Integer getSpaceTotal() {
        return spaceTotal;
    }

    public void setSpaceTotal(Integer spaceTotal) {
        this.spaceTotal = spaceTotal;
    }

    public String getAdminPhone() {
        return adminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getCompanyInvitationCode() {
        return companyInvitationCode;
    }

    public void setCompanyInvitationCode(String companyInvitationCode) {
        this.companyInvitationCode = companyInvitationCode;
    }

    public String getQrcodePath() {
        return qrcodePath;
    }

    public void setQrcodePath(String qrcodePath) {
        this.qrcodePath = qrcodePath;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Date getBasePackageExpir() {
        return basePackageExpir;
    }

    public void setBasePackageExpir(Date basePackageExpir) {
        this.basePackageExpir = basePackageExpir;
    }

    public Integer getExpectNumber() {
        return expectNumber;
    }

    public void setExpectNumber(Integer expectNumber) {
        this.expectNumber = expectNumber;
    }

    public Integer getBuyRoomNumTotal() {
        return buyRoomNumTotal;
    }

    public void setBuyRoomNumTotal(Integer buyRoomNumTotal) {
        this.buyRoomNumTotal = buyRoomNumTotal;
    }

    public String getBigPicture() {
        return bigPicture;
    }

    public void setBigPicture(String bigPicture) {
        this.bigPicture = bigPicture;
    }

    public String getInPicture() {
        return inPicture;
    }

    public void setInPicture(String inPicture) {
        this.inPicture = inPicture;
    }

    public String getSmallPicture() {
        return smallPicture;
    }

    public void setSmallPicture(String smallPicture) {
        this.smallPicture = smallPicture;
    }
}
