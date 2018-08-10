package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`sys_user_role`")
public class SysUserRole implements Serializable {
    private static final long serialVersionUID = 5623596241571145996L;
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`creater_id`")
    private String createrId;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`modifyer_id`")
    private String modifyerId;

    @Column(name = "`modify_time`")
    private Date modifyTime;

    /**
     * 用户的主键ID
     */
    @Column(name = "`user_id`")
    private String userId;

    /**
     * 角色主键ID
     */
    @Column(name = "`role_id`")
    private String roleId;

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

    /**
     * 获取用户的主键ID
     *
     * @return user_id - 用户的主键ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户的主键ID
     *
     * @param userId 用户的主键ID
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
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
}