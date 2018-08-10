package com.thinkwin.common.vo.statisticalAnalysisVo;

import com.thinkwin.common.model.db.SysUser;

import java.io.Serializable;
import java.util.List;

/**
 * 部门统计数据vo类
 * User: yinchunlei
 * Date: 2017/10/29.
 * Company: thinkwin
 */
public class DepStatisticalDataVo implements Serializable {
    private String depId;//部门id
    private String depName;//部门名称
    private Integer NumberOfOrganizationalMeetings;//组织会议数
    private Integer meetingHours;//会议时长
    private Integer numberOfParticipants;//参会人数
    private Integer numberOfAbsentParticipants;//未参会人数
    private String depLengthRatioOfMeetings;//会议时长占比
    private List<SysUser> nonParticipantUserInfo;
    private int hasChild;//是否含有子级  0-没有 1-有(陈飞设定)

    public int getHasChild() {
        return hasChild;
    }

    public void setHasChild(int hasChild) {
        this.hasChild = hasChild;
    }

    public List<SysUser> getNonParticipantUserInfo() {
        return nonParticipantUserInfo;
    }

    public void setNonParticipantUserInfo(List<SysUser> nonParticipantUserInfo) {
        this.nonParticipantUserInfo = nonParticipantUserInfo;
    }

    public String getDepLengthRatioOfMeetings() {
        return depLengthRatioOfMeetings;
    }

    public void setDepLengthRatioOfMeetings(String depLengthRatioOfMeetings) {
        this.depLengthRatioOfMeetings = depLengthRatioOfMeetings;
    }

    public String getDepId() {
        return depId;
    }

    public void setDepId(String depId) {
        this.depId = depId;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public Integer getNumberOfOrganizationalMeetings() {
        return NumberOfOrganizationalMeetings;
    }

    public void setNumberOfOrganizationalMeetings(Integer numberOfOrganizationalMeetings) {
        NumberOfOrganizationalMeetings = numberOfOrganizationalMeetings;
    }

    public Integer getMeetingHours() {
        return meetingHours;
    }

    public void setMeetingHours(Integer meetingHours) {
        this.meetingHours = meetingHours;
    }

    public Integer getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(Integer numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public Integer getNumberOfAbsentParticipants() {
        return numberOfAbsentParticipants;
    }

    public void setNumberOfAbsentParticipants(Integer numberOfAbsentParticipants) {
        this.numberOfAbsentParticipants = numberOfAbsentParticipants;
    }
}
