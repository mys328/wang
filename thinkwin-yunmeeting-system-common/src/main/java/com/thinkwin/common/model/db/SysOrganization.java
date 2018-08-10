package com.thinkwin.common.model.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name = "`sys_organization`")
public class SysOrganization implements Serializable {
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`org_code`")
    private String orgCode;

    /**
     * 配置名称
     */
    @Column(name = "`org_name`")
    private String orgName;

    /**
     * 组织机构拼音
     */
    @Column(name = "`org_name_pinyin`")
    private String orgNamePinyin;

    /**
     * 配置名称
     */
    @Column(name = "`org_nick_name`")
    private String orgNickName;

    /**
     * 如果是没有父级该值默认为“0”
     */
    @Column(name = "`parent_id`")
    private String parentId;

    /**
     * 1:机构;2:部门;3:工作组
     */
    @Column(name = "`org_type`")
    private Integer orgType;

    /**
     * 适用于机构[1:行政组织;2:账务组织;3:法人组织;4:职能组织]
     */
    @Column(name = "`org_latitude`")
    private Integer orgLatitude;

    /**
     * 有效：1；无效：0
     */
    @Column(name = "`status`")
    private Integer status;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`modify_time`")
    private Date modifyTime;

    /**
     * 排序号
     */
    @Column(name = "`compositor`")
    private Integer compositor;

    /**
     * 预留字段1
     */
    @Column(name = "`reserve_1`")
    private String reserve1;

    /**
     * 预留字段2
     */
    @Column(name = "`reserve_2`")
    private String reserve2;

    /**
     * 预留字段3
     */
    @Column(name = "`reserve_3`")
    private String reserve3;

    //子节点
    @Transient
    private List<SysOrganization> children = new ArrayList<SysOrganization>();

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * @return org_code
     */
    public String getOrgCode() {
        return orgCode;
    }

    /**
     * @param orgCode
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode == null ? null : orgCode.trim();
    }

    /**
     * 获取配置名称
     *
     * @return org_name - 配置名称
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * 设置配置名称
     *
     * @param orgName 配置名称
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName == null ? null : orgName.trim();
    }

    /**
     * 获取组织机构拼音
     *
     * @return org_name_pinyin - 组织机构拼音
     */
    public String getOrgNamePinyin() {
        return orgNamePinyin;
    }

    /**
     * 设置组织机构拼音
     *
     * @param orgNamePinyin 组织机构拼音
     */
    public void setOrgNamePinyin(String orgNamePinyin) {
        this.orgNamePinyin = orgNamePinyin == null ? null : orgNamePinyin.trim();
    }

    /**
     * 获取配置名称
     *
     * @return org_nick_name - 配置名称
     */
    public String getOrgNickName() {
        return orgNickName;
    }

    /**
     * 设置配置名称
     *
     * @param orgNickName 配置名称
     */
    public void setOrgNickName(String orgNickName) {
        this.orgNickName = orgNickName == null ? null : orgNickName.trim();
    }

    /**
     * 获取如果是没有父级该值默认为“0”
     *
     * @return parent_id - 如果是没有父级该值默认为“0”
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * 设置如果是没有父级该值默认为“0”
     *
     * @param parentId 如果是没有父级该值默认为“0”
     */
    public void setParentId(String parentId) {
        this.parentId = parentId == null ? null : parentId.trim();
    }

    /**
     * 获取1:机构;2:部门;3:工作组
     *
     * @return org_type - 1:机构;2:部门;3:工作组
     */
    public Integer getOrgType() {
        return orgType;
    }

    /**
     * 设置1:机构;2:部门;3:工作组
     *
     * @param orgType 1:机构;2:部门;3:工作组
     */
    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
    }

    /**
     * 获取适用于机构[1:行政组织;2:账务组织;3:法人组织;4:职能组织]
     *
     * @return org_latitude - 适用于机构[1:行政组织;2:账务组织;3:法人组织;4:职能组织]
     */
    public Integer getOrgLatitude() {
        return orgLatitude;
    }

    /**
     * 设置适用于机构[1:行政组织;2:账务组织;3:法人组织;4:职能组织]
     *
     * @param orgLatitude 适用于机构[1:行政组织;2:账务组织;3:法人组织;4:职能组织]
     */
    public void setOrgLatitude(Integer orgLatitude) {
        this.orgLatitude = orgLatitude;
    }

    /**
     * 获取有效：1；无效：0
     *
     * @return status - 有效：1；无效：0
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置有效：1；无效：0
     *
     * @param status 有效：1；无效：0
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return modify_time
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * @param modifyTime
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * 获取排序号
     *
     * @return compositor - 排序号
     */
    public Integer getCompositor() {
        return compositor;
    }

    /**
     * 设置排序号
     *
     * @param compositor 排序号
     */
    public void setCompositor(Integer compositor) {
        this.compositor = compositor;
    }

    /**
     * 获取预留字段1
     *
     * @return reserve_1 - 预留字段1
     */
    public String getReserve1() {
        return reserve1;
    }

    /**
     * 设置预留字段1
     *
     * @param reserve1 预留字段1
     */
    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1 == null ? null : reserve1.trim();
    }

    /**
     * 获取预留字段2
     *
     * @return reserve_2 - 预留字段2
     */
    public String getReserve2() {
        return reserve2;
    }

    /**
     * 设置预留字段2
     *
     * @param reserve2 预留字段2
     */
    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2 == null ? null : reserve2.trim();
    }

    /**
     * 获取预留字段3
     *
     * @return reserve_3 - 预留字段3
     */
    public String getReserve3() {
        return reserve3;
    }

    /**
     * 设置预留字段3
     *
     * @param reserve3 预留字段3
     */
    public void setReserve3(String reserve3) {
        this.reserve3 = reserve3 == null ? null : reserve3.trim();
    }

    public List<SysOrganization> getChildren() {
        return children;
    }

    public void setChildren(List<SysOrganization> children) {
        this.children = children;
    }
}