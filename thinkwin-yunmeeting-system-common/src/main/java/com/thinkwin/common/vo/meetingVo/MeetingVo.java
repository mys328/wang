package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;
import java.util.List;

/**
 * 类名: MeetingVo </br>
 * 描述: 会议vo类</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/8/2 </br>
 */
public class MeetingVo implements Serializable{
    private static final long serialVersionUID = 3199134747752264213L;
    private String conferenceId;        //会议Id
    private String meetingSubject;     //会议主题
    private String clientType;      //会议来源
    private Long start;     //会议开始时间
    private Long end;     //会议结束Id
    private String meetingAddress;      //会议地点
    private String meetingOrganizer;     //会议组织者
    private String meetingHostUnit;     //会议主办单位
    private String isPublic;       //是否公开 0:不公开；1:公开
    private List<MeetingParticipantsVo> userIds;       //参会人员Id
    private String meetingContent;      //会议内容
    private String informType;     //通知类型
    private String informTime;      //通知时间
    private String resourceId;      //会议室Id
    private String cancelReason;   //取消原因

    public String getMeetingSubject() {
        return meetingSubject;
    }

    public void setMeetingSubject(String meetingSubject) {
        this.meetingSubject = meetingSubject;
    }

    public String getMeetingAddress() {
        return meetingAddress;
    }

    public void setMeetingAddress(String meetingAddress) {
        this.meetingAddress = meetingAddress;
    }

    public String getMeetingOrganizer() {
        return meetingOrganizer;
    }

    public void setMeetingOrganizer(String meetingOrganizer) {
        this.meetingOrganizer = meetingOrganizer;
    }

    public String getMeetingHostUnit() {
        return meetingHostUnit;
    }

    public void setMeetingHostUnit(String meetingHostUnit) {
        this.meetingHostUnit = meetingHostUnit;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public List<MeetingParticipantsVo> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<MeetingParticipantsVo> userIds) {
        this.userIds = userIds;
    }

    public String getMeetingContent() {
        return meetingContent;
    }

    public void setMeetingContent(String meetingContent) {
        this.meetingContent = meetingContent;
    }

    public String getInformType() {
        return informType;
    }

    public void setInformType(String informType) {
        this.informType = informType;
    }

    public String getInformTime() {
        return informTime;
    }

    public void setInformTime(String informTime) {
        this.informTime = informTime;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
