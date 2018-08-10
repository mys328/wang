package com.thinkwin.common.model.core;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`short_url`")
public class ShortUrl implements Serializable {
    @Id
    @Column(name = "`id`")
    private Integer id;

    @Column(name = "`short_url`")
    private String shortUrl;

    @Column(name = "`real_url`")
    private String realUrl;

    @Column(name = "`tenant_id`")
    private String tenantId;

    @Column(name = "`create_time`")
    private Date createTime;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return short_url
     */
    public String getShortUrl() {
        return shortUrl;
    }

    /**
     * @param shortUrl
     */
    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl == null ? null : shortUrl.trim();
    }

    /**
     * @return real_url
     */
    public String getRealUrl() {
        return realUrl;
    }

    /**
     * @param realUrl
     */
    public void setRealUrl(String realUrl) {
        this.realUrl = realUrl == null ? null : realUrl.trim();
    }

    /**
     * @return tenant_id
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
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
}