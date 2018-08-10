package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public class MeetingRoomStatisticsVo implements Serializable{

    private static final long serialVersionUID = -4728906046990141013L;
    /**
     * 会议室名称
     */
    private String name;
    /**
     * 会议室id
     */
    private String roomId;
    /**
     * 会议总数
     */
    private Integer meetingTotalNum;
    /**
     * 会议使用总时长
     */
    private long meetingUseTime;
    /**
     * 会议空闲时长
     */
    private long meetingFreeTime;
    /**
     * 会议室使用率
     */
    private String meetingRoomUtilization;
    /**
     * 会议使用率排序
     */
    private double num;

    /**
     * 审核未通过会议数量
     */
    private Integer notPassMeetingNum;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Integer getMeetingTotalNum() {
        return meetingTotalNum;
    }

    public void setMeetingTotalNum(Integer meetingTotalNum) {
        this.meetingTotalNum = meetingTotalNum;
    }

    public long getMeetingUseTime() {
        return meetingUseTime;
    }

    public void setMeetingUseTime(long meetingUseTime) {
        this.meetingUseTime = meetingUseTime;
    }

    public long getMeetingFreeTime() {
        return meetingFreeTime;
    }

    public void setMeetingFreeTime(long meetingFreeTime) {
        this.meetingFreeTime = meetingFreeTime;
    }

    public String getMeetingRoomUtilization() {
        return meetingRoomUtilization;
    }

    public void setMeetingRoomUtilization(String meetingRoomUtilization) {
        this.meetingRoomUtilization = meetingRoomUtilization;
    }

    public double getNum() {
        return num;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public Integer getNotPassMeetingNum() {
        return notPassMeetingNum;
    }

    public void setNotPassMeetingNum(Integer notPassMeetingNum) {
        this.notPassMeetingNum = notPassMeetingNum;
    }
}
