package com.thinkwin.common.vo.TerminalInfoVo;

import java.io.Serializable;

/**
 * 类名: ProvinceVo </br>
 * 描述: 省vo</br>
 * 开发人员： weining </br>
 * 创建时间：  2018/6/11 </br>
 */
public class ProvinceVo implements Serializable{

    private static final long serialVersionUID = 124783855356926071L;
    private String name;  //省名称
    private String id;    //省拼音
    private String sortFlag;  //排序标识

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSortFlag() {
        return sortFlag;
    }

    public void setSortFlag(String sortFlag) {
        this.sortFlag = sortFlag;
    }
}
