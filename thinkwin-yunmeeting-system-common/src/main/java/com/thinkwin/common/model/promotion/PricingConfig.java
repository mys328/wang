package com.thinkwin.common.model.promotion;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`pricing_config`")
public class PricingConfig implements Serializable{
    private static final long serialVersionUID = -3367308224730224273L;
    /**
     * 基本设置主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

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