package com.thinkwin.common.vo;

import java.io.Serializable;

/**
 * @author Administrator
 */ /*
 * 类说明：
 * @author lining 2018/5/9
 * @version 1.0
 *
 */
public class SyncProVo implements Serializable {

    private static final long serialVersionUID = 6520162315090979709L;

    public int code;
    public String describe;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
