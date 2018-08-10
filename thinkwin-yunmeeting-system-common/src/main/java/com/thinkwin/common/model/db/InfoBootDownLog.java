package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`info_boot_down_log`")
public class InfoBootDownLog implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 开关机任务ID
     */
    @Column(name = "`boot_down_task_id`")
    private String bootDownTaskId;

    /**
     * 终端ID,每个终端一条记录
     */
    @Column(name = "`release_terminal`")
    private String releaseTerminal;

    /**
     * 终端名称
     */
    @Column(name = "`terminal_name`")
    private String terminalName;

    @Column(name = "`cmd_content`")
    private String cmdContent;

    @Column(name = "`cmd_code`")
    private String cmdCode;

    /**
     * 执行状态,发送成功:1;发送失败:2
     */
    @Column(name = "`run_status`")
    private String runStatus;

    /**
     * 执行时间
     */
    @Column(name = "`run_time`")
    private Date runTime;

    /**
     * 指令批次,用于区分每组指令执行情况
     */
    @Column(name = "`cmd_batch`")
    private String cmdBatch;

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
     * 获取开关机任务ID
     *
     * @return boot_down_task_id - 开关机任务ID
     */
    public String getBootDownTaskId() {
        return bootDownTaskId;
    }

    /**
     * 设置开关机任务ID
     *
     * @param bootDownTaskId 开关机任务ID
     */
    public void setBootDownTaskId(String bootDownTaskId) {
        this.bootDownTaskId = bootDownTaskId == null ? null : bootDownTaskId.trim();
    }

    /**
     * 获取终端ID,每个终端一条记录
     *
     * @return release_terminal - 终端ID,每个终端一条记录
     */
    public String getReleaseTerminal() {
        return releaseTerminal;
    }

    /**
     * 设置终端ID,每个终端一条记录
     *
     * @param releaseTerminal 终端ID,每个终端一条记录
     */
    public void setReleaseTerminal(String releaseTerminal) {
        this.releaseTerminal = releaseTerminal == null ? null : releaseTerminal.trim();
    }

    /**
     * 获取终端名称
     *
     * @return terminal_name - 终端名称
     */
    public String getTerminalName() {
        return terminalName;
    }

    /**
     * 设置终端名称
     *
     * @param terminalName 终端名称
     */
    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName == null ? null : terminalName.trim();
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
     * 获取执行状态,发送成功:1;发送失败:2
     *
     * @return run_status - 执行状态,发送成功:1;发送失败:2
     */
    public String getRunStatus() {
        return runStatus;
    }

    /**
     * 设置执行状态,发送成功:1;发送失败:2
     *
     * @param runStatus 执行状态,发送成功:1;发送失败:2
     */
    public void setRunStatus(String runStatus) {
        this.runStatus = runStatus == null ? null : runStatus.trim();
    }

    /**
     * 获取执行时间
     *
     * @return run_time - 执行时间
     */
    public Date getRunTime() {
        return runTime;
    }

    /**
     * 设置执行时间
     *
     * @param runTime 执行时间
     */
    public void setRunTime(Date runTime) {
        this.runTime = runTime;
    }

    /**
     * 获取指令批次,用于区分每组指令执行情况
     *
     * @return cmd_batch - 指令批次,用于区分每组指令执行情况
     */
    public String getCmdBatch() {
        return cmdBatch;
    }

    /**
     * 设置指令批次,用于区分每组指令执行情况
     *
     * @param cmdBatch 指令批次,用于区分每组指令执行情况
     */
    public void setCmdBatch(String cmdBatch) {
        this.cmdBatch = cmdBatch == null ? null : cmdBatch.trim();
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