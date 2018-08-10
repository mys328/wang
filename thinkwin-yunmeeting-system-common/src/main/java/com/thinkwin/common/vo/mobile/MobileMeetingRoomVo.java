package com.thinkwin.common.vo.mobile;

import java.io.Serializable;
import java.util.List;

/*
 * 类说明：
 * @author lining 2017/8/15
 * @version 1.0
 *
 */
public class MobileMeetingRoomVo implements Serializable {

    private static final long serialVersionUID = 3179947849538825123L;
    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 会议室Id
     */
    private String meetingRoomId;

    /**
     * 会议室名称
     */
    private String meetingRoomName;

    /**
     * 会议室状态
     */
    private String meetingRoomStatus;

    /**
     * 会议室物理位置
     */
    private String location;

    /**
     * 容量
     */
    private Integer capacity;

    /**
     * 会议室图片
     */
    private String imgPath;
    /**
     * 是否审批
     */
    private String wfdef;
    /**
     * 是否有扩声
     */
    private Integer hasMicrophone = 0;

    /**
     * 是否有显示
     */
    private Integer hasDisplay = 0;
    /**
     * 是否有视频会议
     */
    private Integer hasVedioMeeting = 0;

    /**
     * 是否白板
     */
    private Integer hasWhiteboard=0;

    private List<Meetings> meetingList;


    public static class Meetings implements Serializable {

        private static final long serialVersionUID = 5324999612857711581L;

        private String beginTime;
        private String endTime;
        private String durasion;
        private String createUser;
        private Integer seq;

        public static long getSerialVersionUID() {
            return serialVersionUID;
        }


        public String getDurasion() {
            return durasion;
        }

        public String getCreateUser() {
            return createUser;
        }

        public Integer getSeq() {
            return seq;
        }

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public void setDurasion(String durasion) {
            this.durasion = durasion;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public void setSeq(Integer seq) {
            this.seq = seq;
        }
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getMeetingRoomId() {
        return meetingRoomId;
    }

    public String getMeetingRoomName() {
        return meetingRoomName;
    }

    public String getMeetingRoomStatus() {
        return meetingRoomStatus;
    }

    public String getLocation() {
        return location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getWfdef() {
        return wfdef;
    }

    public Integer getHasMicrophone() {
        return hasMicrophone;
    }

    public Integer getHasDisplay() {
        return hasDisplay;
    }

    public Integer getHasVedioMeeting() {
        return hasVedioMeeting;
    }

    public List<Meetings> getMeetingList() {
        return meetingList;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setMeetingRoomId(String meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
    }

    public void setMeetingRoomName(String meetingRoomName) {
        this.meetingRoomName = meetingRoomName;
    }

    public void setMeetingRoomStatus(String meetingRoomStatus) {
        this.meetingRoomStatus = meetingRoomStatus;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setWfdef(String wfdef) {
        this.wfdef = wfdef;
    }

    public void setHasMicrophone(Integer hasMicrophone) {
        this.hasMicrophone = hasMicrophone;
    }

    public void setHasDisplay(Integer hasDisplay) {
        this.hasDisplay = hasDisplay;
    }

    public void setHasVedioMeeting(Integer hasVedioMeeting) {
        this.hasVedioMeeting = hasVedioMeeting;
    }

    public void setMeetingRoomsList(List<Meetings> meetingList) {
        this.meetingList = meetingList;
    }

    public Integer getHasWhiteboard() {
        return hasWhiteboard;
    }

    public void setHasWhiteboard(Integer hasWhiteboard) {
        this.hasWhiteboard = hasWhiteboard;
    }
}
