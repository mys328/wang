package com.thinkwin.common.vo.mobile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * 类说明：
 * @author lining 2017/8/30
 * @version 1.0
 *
 */
public class MobileOrgaVo implements Serializable {
    private static final long serialVersionUID = -2412356897361820078L;

    //机构id
    private String orgaId;
    //机构名称
    private String orgaName;
    //是否为叶子组织机构
    private boolean leaf;
    //节点数量
    private Integer childCount;
    //子节点
    private List<MobileOrgaVo> children = new ArrayList<MobileOrgaVo>();

    public String getOrgaId() {
        return orgaId;
    }

    public void setOrgaId(String orgaId) {
        this.orgaId = orgaId;
    }

    public String getOrgaName() {
        return orgaName;
    }

    public void setOrgaName(String orgaName) {
        this.orgaName = orgaName;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public List<MobileOrgaVo> getChildren() {
        return children;
    }

    public void setChildren(List<MobileOrgaVo> children) {
        this.children = children;
    }
}
