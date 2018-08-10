package com.thinkwin.common.model.log;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`sys_log_type`")
public class SysLogType implements Serializable {

    private static final long serialVersionUID = 8001794043070193437L;
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 业务日志名称
     */
    @Column(name = "`name`")
    private String name;

    /**
     * 业务日志类型
     */
    @Column(name = "`type`")
    private String type;

    @Column(name = "`parent_id`")
    private String parentId;

    @Column(name = "`status`")
    private Integer status;

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
     * 获取业务日志名称
     *
     * @return name - 业务日志名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置业务日志名称
     *
     * @param name 业务日志名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取业务日志类型
     *
     * @return type - 业务日志类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置业务日志类型
     *
     * @param type 业务日志类型
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * @return parent_id
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * @param parentId
     */
    public void setParentId(String parentId) {
        this.parentId = parentId == null ? null : parentId.trim();
    }

    /**
     * @return status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}