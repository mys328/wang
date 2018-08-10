package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;

/**
 * 类名: RecentMeetingVo </br>
 * 描述:</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/8/18 </br>
 */
public class RecentMeetingVo implements Serializable{
    private static final long serialVersionUID = 5302412503234655697L;

    private Long start;  //开始时间
    private Long end;    //结束时间
    private String title;       //会议主题
    private String conferenceId;        //会议Id
    private String location;        //会议是地点
    private String state;      //会议状态
    private String organizer;       //1 我组织的  0我参与的


    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }
}
