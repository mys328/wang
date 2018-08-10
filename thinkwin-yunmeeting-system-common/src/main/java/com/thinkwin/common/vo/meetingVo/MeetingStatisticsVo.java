package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;

/**
 * 类名: MeetingStatisticsVo </br>
 * 描述: 会议统计Vo</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/7/21 </br>
 */
public class MeetingStatisticsVo implements Serializable {
    private static final long serialVersionUID = 5143459116649454605L;
    //会议室利用率
    private String meetingRoomUtilization;
    //个人会议占比
    private String personMeetingProportion;
    //会议总数
    private Integer meetingTotalNum = 0;
    //会议总时长
    private String meetingTotalTime;
    //个人会议总时长
    private String personMeetingTotalTime;
    //会议总人数（总参会人次）
    private Integer meetingTotalPeopleNum = 0;
    //预定会议总数
    private Integer reserveMeetingNum = 0;
    //参与会议总数
    private Integer ParticipateMeetingNum = 0;
    //所有会议使用占比
    private Double meetingUserOf = 0.0;
    //所有会议个人时长占比
    private Double meetingPersonaLength = 0.0;


    public Integer getMeetingTotalNum() {
        return meetingTotalNum;
    }

    public void setMeetingTotalNum(Integer meetingTotalNum) {
        this.meetingTotalNum = meetingTotalNum;
    }

    public String getMeetingTotalTime() {
        return meetingTotalTime;
    }

    public void setMeetingTotalTime(String meetingTotalTime) {
        this.meetingTotalTime = meetingTotalTime;
    }

    public Integer getMeetingTotalPeopleNum() {
        return meetingTotalPeopleNum;
    }

    public void setMeetingTotalPeopleNum(Integer meetingTotalPeopleNum) {
        this.meetingTotalPeopleNum = meetingTotalPeopleNum;
    }

    public Integer getReserveMeetingNum() {
        return reserveMeetingNum;
    }

    public void setReserveMeetingNum(Integer reserveMeetingNum) {
        this.reserveMeetingNum = reserveMeetingNum;
    }

    public Integer getParticipateMeetingNum() {
        return ParticipateMeetingNum;
    }

    public void setParticipateMeetingNum(Integer participateMeetingNum) {
        ParticipateMeetingNum = participateMeetingNum;
    }

    public String getMeetingRoomUtilization() {
        return meetingRoomUtilization;
    }

    public void setMeetingRoomUtilization(String meetingRoomUtilization) {
        this.meetingRoomUtilization = meetingRoomUtilization;
    }

    public String getPersonMeetingProportion() {
        return personMeetingProportion;
    }

    public void setPersonMeetingProportion(String personMeetingProportion) {
        this.personMeetingProportion = personMeetingProportion;
    }

    public String getPersonMeetingTotalTime() {
        return personMeetingTotalTime;
    }

    public void setPersonMeetingTotalTime(String personMeetingTotalTime) {
        this.personMeetingTotalTime = personMeetingTotalTime;
    }

    public Double getMeetingUserOf() {
        return meetingUserOf;
    }

    public void setMeetingUserOf(Double meetingUserOf) {
        this.meetingUserOf = meetingUserOf;
    }

    public Double getMeetingPersonaLength() {
        return meetingPersonaLength;
    }

    public void setMeetingPersonaLength(Double meetingPersonaLength) {
        this.meetingPersonaLength = meetingPersonaLength;
    }
}
