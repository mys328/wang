package com.thinkwin.common.vo.statisticalAnalysisVo;

import java.io.Serializable;

/**
 * User: yinchunlei
 * Date: 2017/10/25.
 * Company: thinkwin
 * 人员统计数据Vo类
 */
public class UserStatisticalDataVo implements Serializable {

    private String userName;//员工姓名
    private Integer meetingNum;//会议数量
    private String organizationalMeetingNum;//组织会议数
    private String participationConferenceNum;//参与会议数
    private Integer meetingHours;//会议时长
    private Integer conferenceMessageNumber;//会议留言数
    private String individualAttendanceRate;//个人签到率
    private Double individualAttendanceRateNum;//个人签到率(数字)
    private String individualCorrespondingRate;//个人响应率
    private Double individualCorrespondingRateNum;//个人响应率(数字)
    private String userId;//员工主键id

    public Double getIndividualAttendanceRateNum() {
        return individualAttendanceRateNum;
    }

    public void setIndividualAttendanceRateNum(Double individualAttendanceRateNum) {
        this.individualAttendanceRateNum = individualAttendanceRateNum;
    }

    public Double getIndividualCorrespondingRateNum() {
        return individualCorrespondingRateNum;
    }

    public void setIndividualCorrespondingRateNum(Double individualCorrespondingRateNum) {
        this.individualCorrespondingRateNum = individualCorrespondingRateNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getMeetingNum() {
        return meetingNum;
    }

    public void setMeetingNum(Integer meetingNum) {
        this.meetingNum = meetingNum;
    }

    public Integer getMeetingHours() {
        return meetingHours;
    }

    public void setMeetingHours(Integer meetingHours) {
        this.meetingHours = meetingHours;
    }

    public Integer getConferenceMessageNumber() {
        return conferenceMessageNumber;
    }

    public void setConferenceMessageNumber(Integer conferenceMessageNumber) {
        this.conferenceMessageNumber = conferenceMessageNumber;
    }

    public String getIndividualAttendanceRate() {
        return individualAttendanceRate;
    }

    public void setIndividualAttendanceRate(String individualAttendanceRate) {
        this.individualAttendanceRate = individualAttendanceRate;
    }

    public String getIndividualCorrespondingRate() {
        return individualCorrespondingRate;
    }

    public void setIndividualCorrespondingRate(String individualCorrespondingRate) {
        this.individualCorrespondingRate = individualCorrespondingRate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrganizationalMeetingNum() {
        return organizationalMeetingNum;
    }

    public void setOrganizationalMeetingNum(String organizationalMeetingNum) {
        this.organizationalMeetingNum = organizationalMeetingNum;
    }

    public String getParticipationConferenceNum() {
        return participationConferenceNum;
    }

    public void setParticipationConferenceNum(String participationConferenceNum) {
        this.participationConferenceNum = participationConferenceNum;
    }
}
