package com.thinkwin.common.vo.meetingVo;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/1.
 */
public class PersonsVo implements Serializable {

    private static final long serialVersionUID = -4248114876088036418L;
    /**
     * 参会人员姓名
     */
    private String userName;
    /**
     * 参会人员id
     */
    private String userId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "PersonsVo{" +
                "userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
