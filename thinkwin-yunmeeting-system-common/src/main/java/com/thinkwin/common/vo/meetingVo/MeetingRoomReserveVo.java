package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;
import java.util.Date;

/**
 * 类名: MeetingRoomReserveVo </br>
 * 描述: 会议室预定设置Vo</br>
 * 开发人员： weining </br>
 * 创建时间：  2018/6/14 </br>
 */
public class MeetingRoomReserveVo implements Serializable{
    private static final long serialVersionUID = -1379752742069812388L;

    /**
     * 主键
     */
    private String id;

    /**
     * 可预订时间开始
     */
    private Date reserveTimeStart;

    /**
     * 可预订时间结束
     */
    private Date reserveTimeEnd;

    /**
     * 单次会议最大时长
     */
    private Integer meetingMaximum;

    /**
     * 单次会议最小时长
     */
    private Integer meetingMinimum;

    /**
     * 会议室可预订周期
     */
    private Integer reserveCycle;

    /**
     * 会议可提前开始时间
     */
    private Integer mayStartEarly;

    /**
     * 会议释放设置
     */
    private String releaseSettings;

    private String minutes;

    /**
     * 二维码更新频率
     */
    private Integer qrDuration;

    private String signSet;

    public Integer getQrDuration() {
        return qrDuration;
    }

    public void setQrDuration(Integer qrDuration) {
        this.qrDuration = qrDuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getReserveTimeStart() {
        return reserveTimeStart;
    }

    public void setReserveTimeStart(Date reserveTimeStart) {
        this.reserveTimeStart = reserveTimeStart;
    }

    public Date getReserveTimeEnd() {
        return reserveTimeEnd;
    }

    public void setReserveTimeEnd(Date reserveTimeEnd) {
        this.reserveTimeEnd = reserveTimeEnd;
    }

    public Integer getMeetingMaximum() {
        return meetingMaximum;
    }

    public void setMeetingMaximum(Integer meetingMaximum) {
        this.meetingMaximum = meetingMaximum;
    }

    public Integer getMeetingMinimum() {
        return meetingMinimum;
    }

    public void setMeetingMinimum(Integer meetingMinimum) {
        this.meetingMinimum = meetingMinimum;
    }

    public Integer getReserveCycle() {
        return reserveCycle;
    }

    public void setReserveCycle(Integer reserveCycle) {
        this.reserveCycle = reserveCycle;
    }

    public Integer getMayStartEarly() {
        return mayStartEarly;
    }

    public void setMayStartEarly(Integer mayStartEarly) {
        this.mayStartEarly = mayStartEarly;
    }

    public String getReleaseSettings() {
        return releaseSettings;
    }

    public void setReleaseSettings(String releaseSettings) {
        this.releaseSettings = releaseSettings;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getSignSet() {
        return signSet;
    }

    public void setSignSet(String signSet) {
        this.signSet = signSet;
    }
}
