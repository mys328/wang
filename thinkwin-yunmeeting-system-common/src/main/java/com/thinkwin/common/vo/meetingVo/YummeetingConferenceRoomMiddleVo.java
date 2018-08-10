package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/29.
 */
public class YummeetingConferenceRoomMiddleVo implements Serializable {
    private static final long serialVersionUID = -6652955549860358296L;

    private Integer count;

    private String roomId;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
