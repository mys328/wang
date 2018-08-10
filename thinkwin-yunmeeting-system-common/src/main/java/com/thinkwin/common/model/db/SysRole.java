package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`sys_role`")
public class SysRole implements Serializable {
    private static final long serialVersionUID = 8805530084353939660L;
    /**
     * 角色主键id
     */
    @Id
    @Column(name = "`role_id`")
    private String roleId;

    /**
     * 角色名称
     */
    @Column(name = "`role_name`")
    private String roleName;

    @Column(name = "`org_code`")
    private String orgCode;

    /**
     * 配置名称
     */
    @Column(name = "`org_name`")
    private String orgName;

    @Column(name = "`descript`")
    private String descript;

    /**
     * 排序号
     */
    @Column(name = "`sort_num`")
    private Integer sortNum;

    /**
     * 是否为系统默认角色, 1:是; 2:否
     */
    @Column(name = "`is_system`")
    private Integer isSystem;

    @Column(name = "`creater_id`")
    private String createrId;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`modifyer`")
    private String modifyer;

    @Column(name = "`modify_time`")
    private Date modifyTime;

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

    /**
     * 获取角色主键id
     *
     * @return role_id - 角色主键id
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * 设置角色主键id
     *
     * @param roleId 角色主键id
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId == null ? null : roleId.trim();
    }

    /**
     * 获取角色名称
     *
     * @return role_name - 角色名称
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * 设置角色名称
     *
     * @param roleName 角色名称
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
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
     * @return descript
     */
    public String getDescript() {
        return descript;
    }

    /**
     * @param descript
     */
    public void setDescript(String descript) {
        this.descript = descript == null ? null : descript.trim();
    }

    /**
     * 获取排序号
     *
     * @return sort_num - 排序号
     */
    public Integer getSortNum() {
        return sortNum;
    }

    /**
     * 设置排序号
     *
     * @param sortNum 排序号
     */
    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    /**
     * 获取是否为系统默认角色, 1:是; 2:否
     *
     * @return is_system - 是否为系统默认角色, 1:是; 2:否
     */
    public Integer getIsSystem() {
        return isSystem;
    }

    /**
     * 设置是否为系统默认角色, 1:是; 2:否
     *
     * @param isSystem 是否为系统默认角色, 1:是; 2:否
     */
    public void setIsSystem(Integer isSystem) {
        this.isSystem = isSystem;
    }

    /**
     * @return creater_id
     */
    public String getCreaterId() {
        return createrId;
    }

    /**
     * @param createrId
     */
    public void setCreaterId(String createrId) {
        this.createrId = createrId == null ? null : createrId.trim();
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
     * @return modifyer
     */
    public String getModifyer() {
        return modifyer;
    }

    /**
     * @param modifyer
     */
    public void setModifyer(String modifyer) {
        this.modifyer = modifyer == null ? null : modifyer.trim();
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
}