package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`info_boot_down_period`")
public class InfoBootDownPeriod implements Serializable{
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
     * 每周几以逗号隔开（例：1,2,3）
     */
    @Column(name = "`weekly`")
    private String weekly;

    /**
     * 上午开始时间
     */
    @Column(name = "`am_start_time`")
    private Date amStartTime;

    /**
     * 上午结束时间
     */
    @Column(name = "`am_end_time`")
    private Date amEndTime;

    /**
     * 下午开始时间
     */
    @Column(name = "`pm_start_time`")
    private Date pmStartTime;

    /**
     * 下午结束时间
     */
    @Column(name = "`pm_end_time`")
    private Date pmEndTime;

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
     * 获取每周几以逗号隔开（例：1,2,3）
     *
     * @return weekly - 每周几以逗号隔开（例：1,2,3）
     */
    public String getWeekly() {
        return weekly;
    }

    /**
     * 设置每周几以逗号隔开（例：1,2,3）
     *
     * @param weekly 每周几以逗号隔开（例：1,2,3）
     */
    public void setWeekly(String weekly) {
        this.weekly = weekly == null ? null : weekly.trim();
    }

    /**
     * 获取上午开始时间
     *
     * @return am_start_time - 上午开始时间
     */
    public Date getAmStartTime() {
        return amStartTime;
    }

    /**
     * 设置上午开始时间
     *
     * @param amStartTime 上午开始时间
     */
    public void setAmStartTime(Date amStartTime) {
        this.amStartTime = amStartTime;
    }

    /**
     * 获取上午结束时间
     *
     * @return am_end_time - 上午结束时间
     */
    public Date getAmEndTime() {
        return amEndTime;
    }

    /**
     * 设置上午结束时间
     *
     * @param amEndTime 上午结束时间
     */
    public void setAmEndTime(Date amEndTime) {
        this.amEndTime = amEndTime;
    }

    /**
     * 获取下午开始时间
     *
     * @return pm_start_time - 下午开始时间
     */
    public Date getPmStartTime() {
        return pmStartTime;
    }

    /**
     * 设置下午开始时间
     *
     * @param pmStartTime 下午开始时间
     */
    public void setPmStartTime(Date pmStartTime) {
        this.pmStartTime = pmStartTime;
    }

    /**
     * 获取下午结束时间
     *
     * @return pm_end_time - 下午结束时间
     */
    public Date getPmEndTime() {
        return pmEndTime;
    }

    /**
     * 设置下午结束时间
     *
     * @param pmEndTime 下午结束时间
     */
    public void setPmEndTime(Date pmEndTime) {
        this.pmEndTime = pmEndTime;
    }
}