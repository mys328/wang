package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/8/30.
 */
public class MeetingRoomVo implements Serializable {
    private static final long serialVersionUID = 7052445232501844224L;

    /**
     * 开始时间
     */
    private Date staDate;
    /**
     * 结束时间
     */
    private Date endDate;
    /**
     * 创建者姓名
     */
    private String personName;
    /**
     * 会议名称
     */
    private String meetingName;
    /**
     * 手机号
     */
    private String phone;

    /**
     *是否公开
     */
    private String isPublic;


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

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }
}
