package com.thinkwin.common.vo.mobile;

import java.io.Serializable;

/*
 * 类说明：
 * @author lining 2017/9/5
 * @version 1.0
 *
 */
public class MobileRoomScreenVo implements Serializable{
    private static final long serialVersionUID = -3124695843519663766L;

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
