package com.thinkwin.common.vo;

import java.io.Serializable;

/**
 * 给部门统计时环形图的数据提供方便
 * AUTHOR: yinchunlei
 * DATA: 2017/11/24.
 */
public class DepStatisticalDataVooo implements Serializable {

    private String name;//部门名称
    private String value;//会议时常数

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
