package com.thinkwin.common.vo.statisticalAnalysisVo;

import java.io.Serializable;
import java.util.Date;

/**
 * User: yinchunlei
 * Date: 2017/11/7.
 * Company: thinkwin
 * 组织会议数量详情实体vo类
 */
public class OrganizationalMeetingInfoVo implements Serializable {
    private String meetingId;//会议主键id
    private String meetingName;//会议名称
    private String organizerName;//组织人名称
    private String organizerId;//组织人主键id
    private Date startTime;//会议开始时间
    private Date endTime;//会议结束时间
    private Integer meetingHours;//会议时长
    private Integer numberOfParticipants;//参会人数
    private String attendanceRate;//会议签到率
    private String meetingResponseRate;//会议响应率

    public Integer getMeetingHours() {
        return meetingHours;
    }

    public void setMeetingHours(Integer meetingHours) {
        this.meetingHours = meetingHours;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(Integer numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public String getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(String attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public String getMeetingResponseRate() {
        return meetingResponseRate;
    }

    public void setMeetingResponseRate(String meetingResponseRate) {
        this.meetingResponseRate = meetingResponseRate;
    }
}
