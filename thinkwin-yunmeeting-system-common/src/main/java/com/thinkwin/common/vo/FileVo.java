package com.thinkwin.common.vo;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/2 0002.
 */
public class FileVo implements Serializable {
    private static final long serialVersionUID = -5373304893112666573L;
    /**
     * 图片id
     */
    private String id;
    /**
     * 大图
     */
    private String big;

    /**
     * 中图
     */
    private String in;

    /**
     * 小图
     */
    private String small;

    /**
     * 大图
     */
    private String primary;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBig() {
        return big;
    }

    public void setBig(String big) {
        this.big = big;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }
}
