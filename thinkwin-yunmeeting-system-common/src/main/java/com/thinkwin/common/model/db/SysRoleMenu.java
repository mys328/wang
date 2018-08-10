package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`sys_role_menu`")
public class SysRoleMenu implements Serializable {
    private static final long serialVersionUID = -2133513144842631884L;
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 菜单主键ID
     */
    @Column(name = "`menu_id`")
    private String menuId;

    /**
     * 角色主键ID
     */
    @Column(name = "`role_id`")
    private String roleId;

    @Column(name = "`creater`")
    private String creater;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`modifyer`")
    private String modifyer;

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
     * 获取菜单主键ID
     *
     * @return menu_id - 菜单主键ID
     */
    public String getMenuId() {
        return menuId;
    }

    /**
     * 设置菜单主键ID
     *
     * @param menuId 菜单主键ID
     */
    public void setMenuId(String menuId) {
        this.menuId = menuId == null ? null : menuId.trim();
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
     * @return creater
     */
    public String getCreater() {
        return creater;
    }

    /**
     * @param creater
     */
    public void setCreater(String creater) {
        this.creater = creater == null ? null : creater.trim();
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
}