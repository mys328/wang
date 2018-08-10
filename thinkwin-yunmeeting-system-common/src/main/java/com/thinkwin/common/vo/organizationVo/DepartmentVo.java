package com.thinkwin.common.vo.organizationVo;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/23 0023.
 */
public class DepartmentVo implements Serializable {
    private static final long serialVersionUID = -1098575147712400391L;
    /**
     * 部门id
     */
    private String id;
    /**
     * 部门名称
     */
    private String orgName;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
