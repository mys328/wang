package com.thinkwin.common.model.console;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`saas_user_permission`")
public class SaasUserPermission implements Serializable{
    private static final long serialVersionUID = -8789156789800893761L;
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 用户的主键ID
     */
    @Column(name = "`user_id`")
    private String userId;

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