package com.thinkwin.common.model.core;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`saas_setting`")
public class SaasSetting implements Serializable  {
    private static final long serialVersionUID = 125244385940619675L;
    /**
     * 基本设置主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 配置名称
     */
    @Column(name = "`name`")
    private String name;

    /**
     * 配置标识
     */
    @Column(name = "`setting_key`")
    private String settingKey;

    /**
     * 创建人ID
     */
    @Column(name = "`creater_id`")
    private String createrId;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`")
    private Date createTime;

    /**
     * 修改人ID
     */
    @Column(name = "`modifyer_id`")
    private String modifyerId;

    /**
     * 修改时间
     */
    @Column(name = "`modify_time`")
    private Date modifyTime;

    /**
     * 有效:1 ；无效:0
     */
    @Column(name = "`status`")
    private Integer status;

    /**
     * 预留字段1
     */
    @Column(name = "`reserve_1`")
    private String reserve1;

    /**
     * 配置内容
     */
    @Column(name = "`content`")
    private String content;

    /**
     * 获取基本设置主键
     *
     * @return id - 基本设置主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置基本设置主键
     *
     * @param id 基本设置主键
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取配置名称
     *
     * @return name - 配置名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置配置名称
     *
     * @param name 配置名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取配置标识
     *
     * @return setting_key - 配置标识
     */
    public String getSettingKey() {
        return settingKey;
    }

    /**
     * 设置配置标识
     *
     * @param settingKey 配置标识
     */
    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey == null ? null : settingKey.trim();
    }

    /**
     * 获取创建人ID
     *
     * @return creater_id - 创建人ID
     */
    public String getCreaterId() {
        return createrId;
    }

    /**
     * 设置创建人ID
     *
     * @param createrId 创建人ID
     */
    public void setCreaterId(String createrId) {
        this.createrId = createrId == null ? null : createrId.trim();
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取修改人ID
     *
     * @return modifyer_id - 修改人ID
     */
    public String getModifyerId() {
        return modifyerId;
    }

    /**
     * 设置修改人ID
     *
     * @param modifyerId 修改人ID
     */
    public void setModifyerId(String modifyerId) {
        this.modifyerId = modifyerId == null ? null : modifyerId.trim();
    }

    /**
     * 获取修改时间
     *
     * @return modify_time - 修改时间
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * 设置修改时间
     *
     * @param modifyTime 修改时间
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * 获取有效:1 ；无效:0
     *
     * @return status - 有效:1 ；无效:0
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置有效:1 ；无效:0
     *
     * @param status 有效:1 ；无效:0
     */
    public void setStatus(Integer status) {
        this.status = status;
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
     * 获取配置内容
     *
     * @return content - 配置内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置配置内容
     *
     * @param content 配置内容
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}