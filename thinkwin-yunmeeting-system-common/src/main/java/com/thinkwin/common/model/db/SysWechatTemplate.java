package com.thinkwin.common.model.db;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`sys_wechat_template`")
public class SysWechatTemplate implements Serializable{
    private static final long serialVersionUID = -6983667328515184839L;
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 模板Id
     */
    @Column(name = "`template_id`")
    private String templateId;

    /**
     * 标题
     */
    @Column(name = "`title`")
    private String title;

    /**
     * 主营行业
     */
    @Column(name = "`primary_industry`")
    private String primaryIndustry;

    /**
     * 副营行业
     */
    @Column(name = "`deputy_industry`")
    private String deputyIndustry;

    /**
     * 内容
     */
    @Column(name = "`content`")
    private String content;

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
     * 获取模板Id
     *
     * @return template_id - 模板Id
     */
    public String getTemplateId() {
        return templateId;
    }

    /**
     * 设置模板Id
     *
     * @param templateId 模板Id
     */
    public void setTemplateId(String templateId) {
        this.templateId = templateId == null ? null : templateId.trim();
    }

    /**
     * 获取标题
     *
     * @return title - 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    /**
     * 获取主营行业
     *
     * @return primary_industry - 主营行业
     */
    public String getPrimaryIndustry() {
        return primaryIndustry;
    }

    /**
     * 设置主营行业
     *
     * @param primaryIndustry 主营行业
     */
    public void setPrimaryIndustry(String primaryIndustry) {
        this.primaryIndustry = primaryIndustry == null ? null : primaryIndustry.trim();
    }

    /**
     * 获取副营行业
     *
     * @return deputy_industry - 副营行业
     */
    public String getDeputyIndustry() {
        return deputyIndustry;
    }

    /**
     * 设置副营行业
     *
     * @param deputyIndustry 副营行业
     */
    public void setDeputyIndustry(String deputyIndustry) {
        this.deputyIndustry = deputyIndustry == null ? null : deputyIndustry.trim();
    }

    /**
     * 获取内容
     *
     * @return content - 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}