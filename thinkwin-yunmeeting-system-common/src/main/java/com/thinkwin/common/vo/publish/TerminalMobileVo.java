package com.thinkwin.common.vo.publish;

import java.io.Serializable;

/**
 * User:wangxilei
 * Date:2018/5/9
 * Company:thinkwin
 */
public class TerminalMobileVo implements Serializable {
    private static final long serialVersionUID = -5264111989229239790L;
    private String id;
    private String name;
    private String backgroundUrl;
    private Integer status;

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

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
