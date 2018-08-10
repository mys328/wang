package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/24 0024.
 */
public class StatisticsRoomVo implements Serializable{

    private static final long serialVersionUID = 3632134153206702622L;
    private String name;

    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
