package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;

/**
 * 类名: MeetingDayView </br>
 * 描述:  会议日视图vo</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/8/7 </br>
 */
public class MeetingDayViewVo implements Serializable{
    private static final long serialVersionUID = 6521273261864260667L;

    private Long start;     //会议开始时间
    private Long end;       //会议结束时间
    private String meetingSubject;      //会议主题
    private String state;       //是否是组织者 0是参与者   1是组织者 2待审核
    private String isPublic;    //是否公开  0 不公开  1  公开
    private String meetingId;       //会议id
    private String resourceId;      //会议室Id
    private String location;        //会议室地点
    private String name;        //组织者姓名
    private String phone;       //组织者电话
    private String photo;       //组织者头像
    private String bigPicture; //大图
    private String inPicture; //中图
    private String smallPicture;  // 小图
    private String isSelectDeteils;    //是否可以查询详情 0 不可以  1 可以
    private String isReservationPerson;  //是否为预定人  1是  0不是


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

    public String getMeetingSubject() {
        return meetingSubject;
    }

    public void setMeetingSubject(String meetingSubject) {
        this.meetingSubject = meetingSubject;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getIsSelectDeteils() {
        return isSelectDeteils;
    }

    public void setIsSelectDeteils(String isSelectDeteils) {
        this.isSelectDeteils = isSelectDeteils;
    }

    public String getIsReservationPerson() {
        return isReservationPerson;
    }

    public void setIsReservationPerson(String isReservationPerson) {
        this.isReservationPerson = isReservationPerson;
    }

    public String getBigPicture() {
        return bigPicture;
    }

    public void setBigPicture(String bigPicture) {
        this.bigPicture = bigPicture;
    }

    public String getInPicture() {
        return inPicture;
    }

    public void setInPicture(String inPicture) {
        this.inPicture = inPicture;
    }

    public String getSmallPicture() {
        return smallPicture;
    }

    public void setSmallPicture(String smallPicture) {
        this.smallPicture = smallPicture;
    }
}
