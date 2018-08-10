package com.thinkwin.common.model.core;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "`datasnapshot`")
public class Datasnapshot implements Serializable {
    @Id
    @Column(name = "`id`")
    private String id;

    @Column(name = "`dep_num`")
    private Integer depNum;

    @Column(name = "`total_conference_room_num`")
    private Integer totalConferenceRoomNum;

    @Column(name = "`total_conference_room_area_num`")
    private Integer totalConferenceRoomAreaNum;

    @Column(name = "`total_user_num`")
    private Integer totalUserNum;

    @Column(name = "`used_storage_space`")
    private String usedStorageSpace;

    @Column(name = "`total_conference_num`")
    private Integer totalConferenceNum;

    @Column(name = "`cancel_conference_num`")
    private Integer cancelConferenceNum;

    @Column(name = "`total_conference_length`")
    private Long totalConferenceLength;

    @Column(name = "`meeting_response_rate`")
    private String meetingResponseRate;

    @Column(name = "`total_attendance_rate`")
    private String totalAttendanceRate;

    @Column(name = "`conference_usage_rate`")
    private String conferenceUsageRate;

    @Column(name = "`tenant_id`")
    private String tenantId;

    @Column(name = "`dissolution_time`")
    private Date dissolutionTime;

    @Column(name = "terminal_total_num")
    private Integer terminalTotalNum;

    public Integer getTerminalTotalNum() {
        return terminalTotalNum;
    }

    public void setTerminalTotalNum(Integer terminalTotalNum) {
        this.terminalTotalNum = terminalTotalNum;
    }

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
     * @return dep_num
     */
    public Integer getDepNum() {
        return depNum;
    }

    /**
     * @param depNum
     */
    public void setDepNum(Integer depNum) {
        this.depNum = depNum;
    }

    /**
     * @return total_conference_room_num
     */
    public Integer getTotalConferenceRoomNum() {
        return totalConferenceRoomNum;
    }

    /**
     * @param totalConferenceRoomNum
     */
    public void setTotalConferenceRoomNum(Integer totalConferenceRoomNum) {
        this.totalConferenceRoomNum = totalConferenceRoomNum;
    }

    /**
     * @return total_conference_room_area_num
     */
    public Integer getTotalConferenceRoomAreaNum() {
        return totalConferenceRoomAreaNum;
    }

    /**
     * @param totalConferenceRoomAreaNum
     */
    public void setTotalConferenceRoomAreaNum(Integer totalConferenceRoomAreaNum) {
        this.totalConferenceRoomAreaNum = totalConferenceRoomAreaNum;
    }

    /**
     * @return total_user_num
     */
    public Integer getTotalUserNum() {
        return totalUserNum;
    }

    /**
     * @param totalUserNum
     */
    public void setTotalUserNum(Integer totalUserNum) {
        this.totalUserNum = totalUserNum;
    }

    /**
     * @return used_storage_space
     */
    public String getUsedStorageSpace() {
        return usedStorageSpace;
    }

    /**
     * @param usedStorageSpace
     */
    public void setUsedStorageSpace(String usedStorageSpace) {
        this.usedStorageSpace = usedStorageSpace == null ? null : usedStorageSpace.trim();
    }

    /**
     * @return total_conference_num
     */
    public Integer getTotalConferenceNum() {
        return totalConferenceNum;
    }

    /**
     * @param totalConferenceNum
     */
    public void setTotalConferenceNum(Integer totalConferenceNum) {
        this.totalConferenceNum = totalConferenceNum;
    }

    /**
     * @return cancel_conference_num
     */
    public Integer getCancelConferenceNum() {
        return cancelConferenceNum;
    }

    /**
     * @param cancelConferenceNum
     */
    public void setCancelConferenceNum(Integer cancelConferenceNum) {
        this.cancelConferenceNum = cancelConferenceNum;
    }

    /**
     * @return total_conference_length
     */
    public Long getTotalConferenceLength() {
        return totalConferenceLength;
    }

    /**
     * @param totalConferenceLength
     */
    public void setTotalConferenceLength(Long totalConferenceLength) {
        this.totalConferenceLength = totalConferenceLength;
    }

    /**
     * @return meeting_response_rate
     */
    public String getMeetingResponseRate() {
        return meetingResponseRate;
    }

    /**
     * @param meetingResponseRate
     */
    public void setMeetingResponseRate(String meetingResponseRate) {
        this.meetingResponseRate = meetingResponseRate == null ? null : meetingResponseRate.trim();
    }

    /**
     * @return total_attendance_rate
     */
    public String getTotalAttendanceRate() {
        return totalAttendanceRate;
    }

    /**
     * @param totalAttendanceRate
     */
    public void setTotalAttendanceRate(String totalAttendanceRate) {
        this.totalAttendanceRate = totalAttendanceRate == null ? null : totalAttendanceRate.trim();
    }

    /**
     * @return conference_usage_rate
     */
    public String getConferenceUsageRate() {
        return conferenceUsageRate;
    }

    /**
     * @param conferenceUsageRate
     */
    public void setConferenceUsageRate(String conferenceUsageRate) {
        this.conferenceUsageRate = conferenceUsageRate == null ? null : conferenceUsageRate.trim();
    }

    /**
     * @return tenant_id
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId == null ? null : tenantId.trim();
    }

    /**
     * @return dissolution_time
     */
    public Date getDissolutionTime() {
        return dissolutionTime;
    }

    /**
     * @param dissolutionTime
     */
    public void setDissolutionTime(Date dissolutionTime) {
        this.dissolutionTime = dissolutionTime;
    }
}