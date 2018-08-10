package com.thinkwin.common.model;

import java.io.Serializable;

/**
 * User: yinchunlei
 * Date: 2018/5/2
 * Company: thinkwin
 */
public class AppCompayInfo implements Serializable {

    private String id;
    private String tenantId;
    private String tenantName;
    private String companyLogo;
    private String bigPicture;
    private String inPicture;
    private String smallPicture;
    private String companyType;
    private String companyAddress;
    private String companyDescription;
    private String companyInvitationCode;
    private String qrcodePath;
    private String adminId;
    private String productName;
    private String basePackageExpir;
    private String expectNumber;
    private String buyRoomNumTotal;
    private String spaceTotal;
    private String adminPhone;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    public String getTenantName() {
        return tenantName;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }
    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setBigPicture(String bigPicture) {
        this.bigPicture = bigPicture;
    }
    public String getBigPicture() {
        return bigPicture;
    }

    public void setInPicture(String inPicture) {
        this.inPicture = inPicture;
    }
    public String getInPicture() {
        return inPicture;
    }

    public void setSmallPicture(String smallPicture) {
        this.smallPicture = smallPicture;
    }
    public String getSmallPicture() {
        return smallPicture;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }
    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }
    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }
    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyInvitationCode(String companyInvitationCode) {
        this.companyInvitationCode = companyInvitationCode;
    }
    public String getCompanyInvitationCode() {
        return companyInvitationCode;
    }

    public void setQrcodePath(String qrcodePath) {
        this.qrcodePath = qrcodePath;
    }
    public String getQrcodePath() {
        return qrcodePath;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
    public String getAdminId() {
        return adminId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getProductName() {
        return productName;
    }

    public void setBasePackageExpir(String basePackageExpir) {
        this.basePackageExpir = basePackageExpir;
    }
    public String getBasePackageExpir() {
        return basePackageExpir;
    }

    public void setExpectNumber(String expectNumber) {
        this.expectNumber = expectNumber;
    }
    public String getExpectNumber() {
        return expectNumber;
    }

    public void setBuyRoomNumTotal(String buyRoomNumTotal) {
        this.buyRoomNumTotal = buyRoomNumTotal;
    }
    public String getBuyRoomNumTotal() {
        return buyRoomNumTotal;
    }

    public void setSpaceTotal(String spaceTotal) {
        this.spaceTotal = spaceTotal;
    }
    public String getSpaceTotal() {
        return spaceTotal;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }
    public String getAdminPhone() {
        return adminPhone;
    }
}
