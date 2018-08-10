package com.thinkwin.common.model.db;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`yunmc_room_device_service`")
public class YunmcRoomDeviceService implements Serializable{
    private static final long serialVersionUID = -207232989949441699L;
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 会议室ID
     */
    @Column(name = "`meeting_root_id`")
    private String meetingRootId;

    /**
     * 会议设备服务ID
     */
    @Column(name = "`device_service_id`")
    private String deviceServiceId;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取会议室ID
     *
     * @return meeting_root_id - 会议室ID
     */
    public String getMeetingRootId() {
        return meetingRootId;
    }

    /**
     * 设置会议室ID
     *
     * @param meetingRootId 会议室ID
     */
    public void setMeetingRootId(String meetingRootId) {
        this.meetingRootId = meetingRootId == null ? null : meetingRootId.trim();
    }

    /**
     * 获取会议设备服务ID
     *
     * @return device_service_id - 会议设备服务ID
     */
    public String getDeviceServiceId() {
        return deviceServiceId;
    }

    /**
     * 设置会议设备服务ID
     *
     * @param deviceServiceId 会议设备服务ID
     */
    public void setDeviceServiceId(String deviceServiceId) {
        this.deviceServiceId = deviceServiceId == null ? null : deviceServiceId.trim();
    }
}