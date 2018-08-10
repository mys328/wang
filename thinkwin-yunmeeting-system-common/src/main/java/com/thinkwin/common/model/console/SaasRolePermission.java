package com.thinkwin.common.model.console;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`saas_role_permission`")
public class SaasRolePermission implements Serializable{
    private static final long serialVersionUID = -8222580912043659395L;
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 角色主键ID
     */
    @Column(name = "`role_id`")
    private String roleId;

    /**
     * 权限主键ID
     */
    @Column(name = "`permission_id`")
    private String permissionId;

    @Column(name = "`creater_id`")
    private String createrId;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`modifyer_id`")
    private String modifyerId;

    @Column(name = "`modify_time`")
    private Date modifyTime;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取角色主键ID
     *
     * @return role_id - 角色主键ID
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * 设置角色主键ID
     *
     * @param roleId 角色主键ID
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId == null ? null : roleId.trim();
    }

    /**
     * 获取权限主键ID
     *
     * @return permission_id - 权限主键ID
     */
    public String getPermissionId() {
        return permissionId;
    }

    /**
     * 设置权限主键ID
     *
     * @param permissionId 权限主键ID
     */
    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId == null ? null : permissionId.trim();
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
     * @return modifyer_id
     */
    public String getModifyerId() {
        return modifyerId;
    }

    /**
     * @param modifyerId
     */
    public void setModifyerId(String modifyerId) {
        this.modifyerId = modifyerId == null ? null : modifyerId.trim();
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
}