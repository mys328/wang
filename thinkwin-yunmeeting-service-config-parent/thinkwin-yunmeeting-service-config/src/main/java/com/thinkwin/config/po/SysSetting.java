package com.thinkwin.config.po;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "saas_setting")
public class SysSetting {

    @Column(name = "`id`")
    private Long settingId;

    @Column(name = "`name`")
    private String name;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "modify_time")
    private Date modifyTime;

    @Column(name = "setting_key")
    private String settingKey;

    @Column(name = "`content`")
    private String content;

    public Long getSettingId() {
        return settingId;
    }

    public void setSettingId(Long settingId) {
        this.settingId = settingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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