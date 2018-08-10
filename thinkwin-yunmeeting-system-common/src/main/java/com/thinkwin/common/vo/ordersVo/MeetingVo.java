package com.thinkwin.common.vo.ordersVo;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/25.
 */
public class MeetingVo implements Serializable {
    private static final long serialVersionUID = -8623340558167255680L;
    /**
     * 总会议室
     */
    private String totalRooms;
    /**
     * 使用会议室
     */
    private String  usedRooms;
    /**
     * 剩余会议室
     */
    private String freeRooms;

    public String getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(String totalRooms) {
        this.totalRooms = totalRooms;
    }

    public String getUsedRooms() {
        return usedRooms;
    }

    public void setUsedRooms(String usedRooms) {
        this.usedRooms = usedRooms;
    }

    public String getFreeRooms() {
        return freeRooms;
    }

    public void setFreeRooms(String freeRooms) {
        this.freeRooms = freeRooms;
    }
}
