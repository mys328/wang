package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`yunmeeting_message_inform`")
public class YunmeetingMessageInform implements Serializable{
    private static final long serialVersionUID = -8095970725628197784L;
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 会议Id
     */
    @Column(name = "`conference_id`")
    private String conferenceId;

    /**
     * 会议通知类型 1：微信  2：邮件（多个类型用逗号分隔）
     */
    @Column(name = "`inform_type`")
    private String informType;

    /**
     * 提前发送通知时间
     */
    @Column(name = "`inform_time`")
    private Date informTime;

    /**
     * 定时任务Id
     */
    @Column(name = "`timed_task_id`")
    private String timedTaskId;

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
     * 获取会议Id
     *
     * @return conference_id - 会议Id
     */
    public String getConferenceId() {
        return conferenceId;
    }

    /**
     * 设置会议Id
     *
     * @param conferenceId 会议Id
     */
    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId == null ? null : conferenceId.trim();
    }

    /**
     * 获取会议通知类型 1：微信  2：邮件（多个类型用逗号分隔）
     *
     * @return inform_type - 会议通知类型 1：微信  2：邮件（多个类型用逗号分隔）
     */
    public String getInformType() {
        return informType;
    }

    /**
     * 设置会议通知类型 1：微信  2：邮件（多个类型用逗号分隔）
     *
     * @param informType 会议通知类型 1：微信  2：邮件（多个类型用逗号分隔）
     */
    public void setInformType(String informType) {
        this.informType = informType == null ? null : informType.trim();
    }

    /**
     * 获取提前发送通知时间
     *
     * @return inform_time - 提前发送通知时间
     */
    public Date getInformTime() {
        return informTime;
    }

    /**
     * 设置提前发送通知时间
     *
     * @param informTime 提前发送通知时间
     */
    public void setInformTime(Date informTime) {
        this.informTime = informTime;
    }

    /**
     * 获取定时任务Id
     *
     * @return timed_task_id - 定时任务Id
     */
    public String getTimedTaskId() {
        return timedTaskId;
    }

    /**
     * 设置定时任务Id
     *
     * @param timedTaskId 定时任务Id
     */
    public void setTimedTaskId(String timedTaskId) {
        this.timedTaskId = timedTaskId == null ? null : timedTaskId.trim();
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