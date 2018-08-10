package com.thinkwin.common.vo.organizationVo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/3/23 0023.
 */
public class OrganizationsVo implements Serializable{

    private static final long serialVersionUID = -3254025244589284850L;
    /**
     * 部门id
     */
    private String id;
    /**
     * 部门名称
     */
    private String orgName;
    /**
     * 是否有下级部门
     */
    private boolean leaf ;


    private List<DepartmentVo> departmentLevel;


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

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public List<DepartmentVo> getDepartmentLevel() {
        return departmentLevel;
    }

    public void setDepartmentLevel(List<DepartmentVo> departmentLevel) {
        this.departmentLevel = departmentLevel;
    }
}
