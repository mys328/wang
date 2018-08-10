package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`info_task_terminal_middle`")
public class InfoTaskTerminalMiddle implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 任务ID
     */
    @Column(name = "`boot_down_task_id`")
    private String bootDownTaskId;

    /**
     * 终端ID
     */
    @Column(name = "`terminal_id`")
    private String terminalId;

    /**
     * 排序
     */
    @Column(name = "`sort`")
    private Integer sort;

    /**
     * 执行状态 成功:1;失败0
     */
    @Column(name = "`run_status`")
    private String runStatus;

    /**
     * 执行时间
     */
    @Column(name = "`run_time`")
    private Date runTime;

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
     * 获取任务ID
     *
     * @return boot_down_task_id - 任务ID
     */
    public String getBootDownTaskId() {
        return bootDownTaskId;
    }

    /**
     * 设置任务ID
     *
     * @param bootDownTaskId 任务ID
     */
    public void setBootDownTaskId(String bootDownTaskId) {
        this.bootDownTaskId = bootDownTaskId == null ? null : bootDownTaskId.trim();
    }

    /**
     * 获取终端ID
     *
     * @return terminal_id - 终端ID
     */
    public String getTerminalId() {
        return terminalId;
    }

    /**
     * 设置终端ID
     *
     * @param terminalId 终端ID
     */
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId == null ? null : terminalId.trim();
    }

    /**
     * 获取排序
     *
     * @return sort - 排序
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 设置排序
     *
     * @param sort 排序
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 获取执行状态 成功:1;失败0
     *
     * @return run_status - 执行状态 成功:1;失败0
     */
    public String getRunStatus() {
        return runStatus;
    }

    /**
     * 设置执行状态 成功:1;失败0
     *
     * @param runStatus 执行状态 成功:1;失败0
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
}