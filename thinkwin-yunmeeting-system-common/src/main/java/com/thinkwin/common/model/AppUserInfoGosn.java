package com.thinkwin.common.model;

import java.io.Serializable;

/**
 * User: yinchunlei
 * Date: 2018/5/2
 * Company: thinkwin
 */
public class AppUserInfoGosn  implements Serializable {
    private int ifSuc;
    private String msg;
    private AppUserInfo data;
    private String code;
    public void setIfSuc(int ifSuc) {
        this.ifSuc = ifSuc;
    }
    public int getIfSuc() {
        return ifSuc;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setData(AppUserInfo data) {
        this.data = data;
    }
    public AppUserInfo getData() {
        return data;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }
}
