package com.thinkwin.common.vo.mobile;

import com.thinkwin.common.model.db.SysUser;

import java.io.Serializable;

/*
 * 类说明：微信登陆返回对象
 * @author lining 2017/8/30
 * @version 1.0
 *
 */
public class MobileLoginVo implements Serializable{

    private static final long serialVersionUID = -4355534982143790658L;

    private String tenantId; //租户id
    private String openId;  //openId
    private SysUser sysUser; //用户

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public SysUser getSysUser() {
        return sysUser;
    }

    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }
}
