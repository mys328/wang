package com.thinkwin.common.vo.publish;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/17 0017.
 */
public class Rooms implements Serializable{

    private static final long serialVersionUID = -8511745709432953683L;
    /**
     * 会议室名称
     */
    private String roomName;
    /**
     * 会议室地点
     */
    private String location;
    /**
     * 会议室二维码
     */
    private String qrCodeUrl;
    /**
     * 会议室状态 1－永久关闭;2－正常开启;;3－临时关闭
     */
    private String state;


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
}
