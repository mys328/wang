package com.thinkwin.common.vo;

import com.thinkwin.common.model.db.SysUser;

import java.io.Serializable;

/*
 * 类说明：
 * @author lining 2017/11/14
 * @version 1.0
 *
 */
public class ChangeAdminVo implements Serializable{

    private static final long serialVersionUID = -6643533353069234147L;
    private String tenantName;
    private SysUser newAdmin;
    private SysUser oldAdmin;

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public SysUser getNewAdmin() {
        return newAdmin;
    }

    public void setNewAdmin(SysUser newAdmin) {
        this.newAdmin = newAdmin;
    }

    public SysUser getOldAdmin() {
        return oldAdmin;
    }

    public void setOldAdmin(SysUser oldAdmin) {
        this.oldAdmin = oldAdmin;
    }
}
