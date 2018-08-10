package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`yunmeeting_dynamics`")
public class YunmeetingDynamics implements Serializable{
    private static final long serialVersionUID = -6709588264070564134L;
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 动态内容
     */
    @Column(name = "`content`")
    private String content;

    /**
     * 会议Id
     */
    @Column(name = "`conference_id`")
    private String conferenceId;

    /**
     * 参会人Id
     */
    @Column(name = "`participants_id`")
    private String participantsId;

    /**
     * 提前发送通知时间
     */
    @Column(name = "`inform_time`")
    private Date informTime;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`")
    private Date createTime;

    /**
     * 创建人Id
     */
    @Column(name = "`create_id`")
    private String createId;

    /**
     * 0:用户留言；1:系统推送
     */
    @Column(name = "`dynamics_type`")
    private String dynamicsType;

    /**
     * 记录状态:1:已删除；0:未删除
     */
    @Column(name = "`delete_state`")
    private String deleteState;

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
     * 获取动态内容
     *
     * @return content - 动态内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置动态内容
     *
     * @param content 动态内容
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
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
     * 获取参会人Id
     *
     * @return participants_id - 参会人Id
     */
    public String getParticipantsId() {
        return participantsId;
    }

    /**
     * 设置参会人Id
     *
     * @param participantsId 参会人Id
     */
    public void setParticipantsId(String participantsId) {
        this.participantsId = participantsId == null ? null : participantsId.trim();
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
     * 获取创建人Id
     *
     * @return createId - 创建人Id
     */
    public String getCreateId() {
        return createId;
    }

    /**
     * 设置创建人Id
     *
     * @param createId 创建人Id
     */
    public void setCreateId(String createId) {
        this.createId = createId == null ? null : createId.trim();
    }

    /**
     * 获取0:用户留言；1:系统推送
     *
     * @return dynamics_type - 0:用户留言；1:系统推送
     */
    public String getDynamicsType() {
        return dynamicsType;
    }

    /**
     * 设置0:用户留言；1:系统推送
     *
     * @param dynamicsType 0:用户留言；1:系统推送
     */
    public void setDynamicsType(String dynamicsType) {
        this.dynamicsType = dynamicsType == null ? null : dynamicsType.trim();
    }

    /**
     * 获取记录状态:1:已删除；0:未删除
     *
     * @return delete_state - 记录状态:1:已删除；0:未删除
     */
    public String getDeleteState() {
        return deleteState;
    }

    /**
     * 设置记录状态:1:已删除；0:未删除
     *
     * @param deleteState 记录状态:1:已删除；0:未删除
     */
    public void setDeleteState(String deleteState) {
        this.deleteState = deleteState == null ? null : deleteState.trim();
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