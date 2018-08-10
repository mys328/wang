package com.thinkwin.common.vo.publish;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2018/5/17 0017.
 */
public class MeetingInfo implements Serializable {


    private static final long serialVersionUID = 6739966601892187917L;
    /**
     * 会议名称
     */
    private String conferenceName;
    /**
     * 会议开始时间
     */
    private Date startTime;
    /**
     * 会议结束时间
     */
    private Date EndTime;

    /**
     * 创建人
     */
    private String creater;
    /**
     * 主办单位部门
     */
    private String hostUnit;
    /**
     * 会议签到二维码
     */
    private String qrCodeUrl;
    /**
     * 会议提前开始时间
     */
    private String frontTime;
    /**
     * 会议室id
     */
    private String id;
     /**
     * 终端id
     *//*
    private String terminalId;
    *//**
     * 租户id
     *//*
    private String tenantId;
    *//**
     * 二维码刷新频率
     *//*
    private String timeStep;*/


    public String getConferenceName() {
        return conferenceName;
    }

    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }



    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getHostUnit() {
        return hostUnit;
    }

    public void setHostUnit(String hostUnit) {
        this.hostUnit = hostUnit;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return EndTime;
    }

    public void setEndTime(Date endTime) {
        EndTime = endTime;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getFrontTime() {
        return frontTime;
    }

    public void setFrontTime(String frontTime) {
        this.frontTime = frontTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
