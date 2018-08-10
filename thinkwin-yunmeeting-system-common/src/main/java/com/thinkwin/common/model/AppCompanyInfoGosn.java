package com.thinkwin.common.model;

import java.io.Serializable;

/**
 * User: yinchunlei
 * Date: 2018/5/2
 * Company: thinkwin
 */
public class AppCompanyInfoGosn  implements Serializable {
    private int ifSuc;
    private String msg;
    private AppCompayInfo data;
    private String code;

    public int getIfSuc() {
        return ifSuc;
    }

    public void setIfSuc(int ifSuc) {
        this.ifSuc = ifSuc;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public AppCompayInfo getData() {
        return data;
    }

    public void setData(AppCompayInfo data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
