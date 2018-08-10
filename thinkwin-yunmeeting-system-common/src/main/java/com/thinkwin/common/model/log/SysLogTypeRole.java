package com.thinkwin.common.model.log;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`sys_log_type_role`")
public class SysLogTypeRole implements Serializable {
    private static final long serialVersionUID = 2333662812551157441L;
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`log_type_id`")
    private String logTypeId;

    @Column(name = "`role_id`")
    private String roleId;

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
     * @return log_type_id
     */
    public String getLogTypeId() {
        return logTypeId;
    }

    /**
     * @param logTypeId
     */
    public void setLogTypeId(String logTypeId) {
        this.logTypeId = logTypeId == null ? null : logTypeId.trim();
    }

    /**
     * @return role_id
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * @param roleId
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId == null ? null : roleId.trim();
    }
}