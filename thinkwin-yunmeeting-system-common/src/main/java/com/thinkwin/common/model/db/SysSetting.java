package com.thinkwin.common.model.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "sys_setting")
public class SysSetting implements Serializable{


    private static final long serialVersionUID = 5375762546873230074L;
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "setting_key")
    private String settingKey;

    @Column(name = "`content`")
    private String content;
    public String getId() {
        return id;
    }

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "modify_time")
    private Date modifyTime;

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

}