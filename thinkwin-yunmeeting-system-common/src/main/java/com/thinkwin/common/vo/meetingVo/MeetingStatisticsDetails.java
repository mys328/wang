package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/10/26 0026.
 */
public class MeetingStatisticsDetails implements Serializable{

    private static final long serialVersionUID = -8268317207681401708L;
    /**
     * 会议名称
     */
    private String name;
    /**
     * 会议开始时间
     */
    private Date staTime;
    /**
     * 会议结束时间
     */
    private Date endTime;
    /**
     * 会议时间长度
     */
    private String timeLength;
    /**
     * 预订人姓名
     */
    private String reserveName;
    /**
     * 会议签到率
     */
    private String SignRate;
    /**
     * 会议响应率
     */
    private String answerRate;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStaTime() {
        return staTime;
    }

    public void setStaTime(Date staTime) {
        this.staTime = staTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(String timeLength) {
        this.timeLength = timeLength;
    }

    public String getReserveName() {
        return reserveName;
    }

    public void setReserveName(String reserveName) {
        this.reserveName = reserveName;
    }

    public String getSignRate() {
        return SignRate;
    }

    public void setSignRate(String signRate) {
        SignRate = signRate;
    }

    public String getAnswerRate() {
        return answerRate;
    }

    public void setAnswerRate(String answerRate) {
        this.answerRate = answerRate;
    }
}
