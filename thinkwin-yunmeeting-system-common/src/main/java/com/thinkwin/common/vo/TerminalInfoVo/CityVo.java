package com.thinkwin.common.vo.TerminalInfoVo;

import java.io.Serializable;

/**
 * 类名: CityVo </br>
 * 描述: 上级市Vo</br>
 * 开发人员： weining </br>
 * 创建时间：  2018/6/11 </br>
 */
public class CityVo implements Serializable{

    private static final long serialVersionUID = 2311811893350617425L;
    private String name;   //上级市名称
    private String provinceId;   //上级市所属省拼音
    private String id;        //上级市标识

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
