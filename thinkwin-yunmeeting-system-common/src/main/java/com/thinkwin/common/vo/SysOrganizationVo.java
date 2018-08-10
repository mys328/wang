package com.thinkwin.common.vo;

import com.thinkwin.common.model.db.SysOrganization;

import java.io.Serializable;

/**
 * 组织机构返回前端的view实体类
 * User: yinchunlei
 * Date: 2017/6/26.
 * Company: thinkwin
 */
public class SysOrganizationVo implements Serializable {

    private SysOrganization sysOrganization;
    //该字段判断是否有子级节点
    private boolean leaf;

    private Integer orgUserNum;

    public Integer getOrgUserNum() {
        return orgUserNum;
    }

    public void setOrgUserNum(Integer orgUserNum) {
        this.orgUserNum = orgUserNum;
    }

    public SysOrganization getSysOrganization() {
        return sysOrganization;
    }

    public void setSysOrganization(SysOrganization sysOrganization) {
        this.sysOrganization = sysOrganization;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }
}
