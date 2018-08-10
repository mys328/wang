package com.thinkwin.common.model.db;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by yinchunlei on 2017/12/26.
 */
@Table(name = "`biz_image_recorder`")
public class BizImageRecorder implements Serializable {
    /**
     * id
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 业务ID
     */
    @Column(name = "`biz_id`")
    private String bizId;

    /**
     * 图片ID
     */
    @Column(name = "`image_id`")
    private String imageId;
    /**
     * 图片类型
     */
    @Column(name = "`image_type`")
    private String imageType;
    /**
     * 图片URL
     */
    @Column(name = "`image_url`")
    private String imageUrl;

    /**
     * 文件类型 1:图片;2:文件;3节目图片
     */
    @Column(name = "`type`")
    private String type;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`")
    private Date createTime;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
