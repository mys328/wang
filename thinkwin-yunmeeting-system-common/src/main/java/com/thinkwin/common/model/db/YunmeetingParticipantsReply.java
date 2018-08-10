package com.thinkwin.common.model.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`yunmeeting_participants_reply`")
public class YunmeetingParticipantsReply implements Serializable{
    private static final long serialVersionUID = -8078506937217568324L;
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 1:接受；2:暂定；0拒绝
     */
    @Column(name = "`reply_state`")
    private String replyState;

    /**
     * 会议表ID
     */
    @Column(name = "`conference_id`")
    private String conferenceId;

    /**
     * 参会人ID
     */
    @Column(name = "`participants_id`")
    private String participantsId;

    /**
     * 回复时间
     */
    @Column(name = "`reply_time`")
    private Date replyTime;

    /**
     * 预留字段1
     */
    @Column(name = "`reserve_1`")
    private String reserve1;

    /**
     * 预留字段2
     */
    @Column(name = "`reserve_2`")
    private String reserve2;

    /**
     * 预留字段3
     */
    @Column(name = "`reserve_3`")
    private String reserve3;

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
     * 获取1:接受；2:暂定；0拒绝
     *
     * @return reply_state - 1:接受；2:暂定；0拒绝
     */
    public String getReplyState() {
        return replyState;
    }

    /**
     * 设置1:接受；2:暂定；0拒绝
     *
     * @param replyState 1:接受；2:暂定；0拒绝
     */
    public void setReplyState(String replyState) {
        this.replyState = replyState == null ? null : replyState.trim();
    }

    /**
     * 获取会议表ID
     *
     * @return conference_id - 会议表ID
     */
    public String getConferenceId() {
        return conferenceId;
    }

    /**
     * 设置会议表ID
     *
     * @param conferenceId 会议表ID
     */
    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId == null ? null : conferenceId.trim();
    }

    /**
     * 获取参会人ID
     *
     * @return participants_id - 参会人ID
     */
    public String getParticipantsId() {
        return participantsId;
    }

    /**
     * 设置参会人ID
     *
     * @param participantsId 参会人ID
     */
    public void setParticipantsId(String participantsId) {
        this.participantsId = participantsId == null ? null : participantsId.trim();
    }

    /**
     * 获取回复时间
     *
     * @return reply_time - 回复时间
     */
    public Date getReplyTime() {
        return replyTime;
    }

    /**
     * 设置回复时间
     *
     * @param replyTime 回复时间
     */
    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
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
}