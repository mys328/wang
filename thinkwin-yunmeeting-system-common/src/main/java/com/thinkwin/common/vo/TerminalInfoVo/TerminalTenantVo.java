package com.thinkwin.common.vo.TerminalInfoVo;

import java.io.Serializable;
import java.util.Date;

/**
 * User:wangxilei
 * Date:2018/5/17
 * Company:thinkwin
 */
public class TerminalTenantVo implements Serializable {
    private static final long serialVersionUID = 38138845223945062L;
    private String id;
    private String tenantCode;
    private String tenantName;
    private String contacts;
    private String contactsTel;
    private String basePackageType;
    private Date basePackageStart;
    private Date basePackageExpir;
    private String terminalManagerPasswd;
    private Integer author;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getContactsTel() {
        return contactsTel;
    }

    public void setContactsTel(String contactsTel) {
        this.contactsTel = contactsTel;
    }

    public String getBasePackageType() {
        return basePackageType;
    }

    public void setBasePackageType(String basePackageType) {
        this.basePackageType = basePackageType;
    }

    public Date getBasePackageStart() {
        return basePackageStart;
    }

    public void setBasePackageStart(Date basePackageStart) {
        this.basePackageStart = basePackageStart;
    }

    public Date getBasePackageExpir() {
        return basePackageExpir;
    }

    public void setBasePackageExpir(Date basePackageExpir) {
        this.basePackageExpir = basePackageExpir;
    }

    public String getTerminalManagerPasswd() {
        return terminalManagerPasswd;
    }

    public void setTerminalManagerPasswd(String terminalManagerPasswd) {
        this.terminalManagerPasswd = terminalManagerPasswd;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }
}
