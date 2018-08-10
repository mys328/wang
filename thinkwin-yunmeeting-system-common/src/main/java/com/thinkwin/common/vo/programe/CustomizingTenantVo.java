package com.thinkwin.common.vo.programe;

import java.io.Serializable;

/**
 * User: yinchunlei
 * Date: 2018/7/13
 * Company: thinkwin
 */
public class CustomizingTenantVo implements Serializable {

    private String id;//租户主键id
    private String tenantName;//租户名称

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
