package com.thinkwin.common.vo.statisticalAnalysisVo;

import java.io.Serializable;
import java.util.Date;

/**
 * 根据用户的会议数量查询数据的vo类
 * AUTHOR: yinchunlei
 * DATA: 2017/11/27.
 */
public class ConferenceNumInfoVo implements Serializable {
    private String meetingName;//会议名称
    private Date meetingStartTime;//会议开始时间
    private Date meetingEndTime;//会议结束时间
    private String organizerName;//组织者名称
    private String participantsNum;//参会人数

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

    public String getParticipantsNum() {
        return participantsNum;
    }

    public void setParticipantsNum(String participantsNum) {
        this.participantsNum = participantsNum;
    }

    public Date getMeetingStartTime() {
        return meetingStartTime;
    }

    public void setMeetingStartTime(Date meetingStartTime) {
        this.meetingStartTime = meetingStartTime;
    }

    public Date getMeetingEndTime() {
        return meetingEndTime;
    }

    public void setMeetingEndTime(Date meetingEndTime) {
        this.meetingEndTime = meetingEndTime;
    }
}
