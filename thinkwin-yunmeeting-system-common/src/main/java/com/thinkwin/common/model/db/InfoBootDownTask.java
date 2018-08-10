package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`info_boot_down_task`")
public class InfoBootDownTask implements Serializable {
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 任务名称
     */
    @Column(name = "`task_name`")
    private String taskName;

    /**
     * 1是；0否
     */
    @Column(name = "`if_open_down`")
    private String ifOpenDown;

    /**
     * 特别关机开始
     */
    @Column(name = "`down_start_time`")
    private Date downStartTime;

    /**
     * 特别关机结束
     */
    @Column(name = "`down_end_time`")
    private Date downEndTime;

    /**
     * 指令内容
     */
    @Column(name = "`cmd_content`")
    private String cmdContent;

    /**
     * 指令代码
     */
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
     * 修改人ID
     */
    @Column(name = "`modifier`")
    private String modifier;

    /**
     * 修改时间
     */
    @Column(name = "`modify_time`")
    private Date modifyTime;

    /**
     * 任务状态:运行中:1;未启动0
     */
    @Column(name = "`status`")
    private String status;

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
     * 获取任务名称
     *
     * @return task_name - 任务名称
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * 设置任务名称
     *
     * @param taskName 任务名称
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName == null ? null : taskName.trim();
    }

    /**
     * 获取1是；0否
     *
     * @return if_open_down - 1是；0否
     */
    public String getIfOpenDown() {
        return ifOpenDown;
    }

    /**
     * 设置1是；0否
     *
     * @param ifOpenDown 1是；0否
     */
    public void setIfOpenDown(String ifOpenDown) {
        this.ifOpenDown = ifOpenDown == null ? null : ifOpenDown.trim();
    }

    /**
     * 获取特别关机开始
     *
     * @return down_start_time - 特别关机开始
     */
    public Date getDownStartTime() {
        return downStartTime;
    }

    /**
     * 设置特别关机开始
     *
     * @param downStartTime 特别关机开始
     */
    public void setDownStartTime(Date downStartTime) {
        this.downStartTime = downStartTime;
    }

    /**
     * 获取特别关机结束
     *
     * @return down_end_time - 特别关机结束
     */
    public Date getDownEndTime() {
        return downEndTime;
    }

    /**
     * 设置特别关机结束
     *
     * @param downEndTime 特别关机结束
     */
    public void setDownEndTime(Date downEndTime) {
        this.downEndTime = downEndTime;
    }

    /**
     * 获取指令内容
     *
     * @return cmd_content - 指令内容
     */
    public String getCmdContent() {
        return cmdContent;
    }

    /**
     * 设置指令内容
     *
     * @param cmdContent 指令内容
     */
    public void setCmdContent(String cmdContent) {
        this.cmdContent = cmdContent == null ? null : cmdContent.trim();
    }

    /**
     * 获取指令代码
     *
     * @return cmd_code - 指令代码
     */
    public String getCmdCode() {
        return cmdCode;
    }

    /**
     * 设置指令代码
     *
     * @param cmdCode 指令代码
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
     * 获取修改人ID
     *
     * @return modifier - 修改人ID
     */
    public String getModifier() {
        return modifier;
    }

    /**
     * 设置修改人ID
     *
     * @param modifier 修改人ID
     */
    public void setModifier(String modifier) {
        this.modifier = modifier == null ? null : modifier.trim();
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
     * 获取任务状态:运行中:1;未启动0
     *
     * @return status - 任务状态:运行中:1;未启动0
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置任务状态:运行中:1;未启动0
     *
     * @param status 任务状态:运行中:1;未启动0
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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