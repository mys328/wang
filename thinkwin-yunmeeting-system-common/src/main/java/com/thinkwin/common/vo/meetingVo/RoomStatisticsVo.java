package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;
import java.util.Date;

/**
 * 会议室统计Vo
 */
public class RoomStatisticsVo implements Serializable{

    private static final long serialVersionUID = 7771996313122825243L;
    /**
     * 会议室id
     */
    private String id;

    /**
     * 会议室开始时间
     */
    private Date staDate;

    /**
     * 会议室结束时间
     */
    private Date endDate;

    /**
     * 会议室id
     */
    private String roomId;
    /**
     * 会议状态
     */
    private String state;
    /**
     * 预订人姓名
     */
    private String userName;
    /**
     * 会议名称
     */
    private String meetingName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStaDate() {
        return staDate;
    }

    public void setStaDate(Date staDate) {
        this.staDate = staDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }
}
