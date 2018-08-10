package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`info_remove_restart_log`")
public class InfoRemoveRestartLog implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 发送时间
     */
    @Column(name = "`send_time`")
    private Date sendTime;

    /**
     * 重启批次 ,每次重启一个批次号
     */
    @Column(name = "`restart_batch`")
    private String restartBatch;

    @Column(name = "`cmd_content`")
    private String cmdContent;

    @Column(name = "`cmd_code`")
    private String cmdCode;

    /**
     * 创建人ID
     */
    @Column(name = "`creater`")
    private String creater;

    /**
     * 创建时间
     */
    @Column(name = "`creat_time`")
    private Date creatTime;

    /**
     * 预留字段1
     */
    @Column(name = "`reserve_1`")
    private String reserve1;

    /**
     * 预留字段3
     */
    @Column(name = "`reserve_3`")
    private String reserve3;

    /**
     * 预留字段2
     */
    @Column(name = "`reserve_2`")
    private String reserve2;

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
     * 获取发送时间
     *
     * @return send_time - 发送时间
     */
    public Date getSendTime() {
        return sendTime;
    }

    /**
     * 设置发送时间
     *
     * @param sendTime 发送时间
     */
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    /**
     * 获取重启批次 ,每次重启一个批次号
     *
     * @return restart_batch - 重启批次 ,每次重启一个批次号
     */
    public String getRestartBatch() {
        return restartBatch;
    }

    /**
     * 设置重启批次 ,每次重启一个批次号
     *
     * @param restartBatch 重启批次 ,每次重启一个批次号
     */
    public void setRestartBatch(String restartBatch) {
        this.restartBatch = restartBatch == null ? null : restartBatch.trim();
    }

    /**
     * @return cmd_content
     */
    public String getCmdContent() {
        return cmdContent;
    }

    /**
     * @param cmdContent
     */
    public void setCmdContent(String cmdContent) {
        this.cmdContent = cmdContent == null ? null : cmdContent.trim();
    }

    /**
     * @return cmd_code
     */
    public String getCmdCode() {
        return cmdCode;
    }

    /**
     * @param cmdCode
     */
    public void setCmdCode(String cmdCode) {
        this.cmdCode = cmdCode == null ? null : cmdCode.trim();
    }

    /**
     * 获取创建人ID
     *
     * @return creater - 创建人ID
     */
    public String getCreater() {
        return creater;
    }

    /**
     * 设置创建人ID
     *
     * @param creater 创建人ID
     */
    public void setCreater(String creater) {
        this.creater = creater == null ? null : creater.trim();
    }

    /**
     * 获取创建时间
     *
     * @return creat_time - 创建时间
     */
    public Date getCreatTime() {
        return creatTime;
    }

    /**
     * 设置创建时间
     *
     * @param creatTime 创建时间
     */
    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
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
     * 获取预留字段3
     *
     * @return reserve_3 - 预留字段3
     */
    public String getReserve3() {
        return reserve3;
    }

    /**
     * 设置预留字段3
     *
     * @param reserve3 预留字段3
     */
    public void setReserve3(String reserve3) {
        this.reserve3 = reserve3 == null ? null : reserve3.trim();
    }

    /**
     * 获取预留字段2
     *
     * @return reserve_2 - 预留字段2
     */
    public String getReserve2() {
        return reserve2;
    }

    /**
     * 设置预留字段2
     *
     * @param reserve2 预留字段2
     */
    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2 == null ? null : reserve2.trim();
    }
}