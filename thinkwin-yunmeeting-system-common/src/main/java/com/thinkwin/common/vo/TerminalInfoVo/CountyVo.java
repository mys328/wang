package com.thinkwin.common.vo.TerminalInfoVo;

import java.io.Serializable;

/**
 * 类名: CountyVo </br>
 * 描述: 县Vo</br>
 * 开发人员： weining </br>
 * 创建时间：  2018/6/11 </br>
 */
public class CountyVo implements Serializable{
    private static final long serialVersionUID = 3189999380992048706L;

    private String name;          //县名称
    private String provinceId;    //省拼音
    private String id;            //县拼音
    private String cityId;        //上级市标识

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

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
}
