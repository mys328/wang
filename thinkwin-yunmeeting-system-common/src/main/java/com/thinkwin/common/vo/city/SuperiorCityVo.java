package com.thinkwin.common.vo.city;

import java.io.Serializable;
import java.util.List;

/**
 * 类名: SuperiorCityVo </br>
 * 描述: 上级市Vo</br>
 * 开发人员： weining </br>
 * 创建时间：  2018/5/18 </br>
 */
public class SuperiorCityVo implements Serializable{

    private static final long serialVersionUID = -7478760484171818233L;

    //市名称
    private String cityName;
    //市拼音
    private String cityPinYin;
    //省名称
    private String provinceName;
    //县列表
    private List<CountyCityVo> countyCityList;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityPinYin() {
        return cityPinYin;
    }

    public void setCityPinYin(String cityPinYin) {
        this.cityPinYin = cityPinYin;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public List<CountyCityVo> getCountyCityList() {
        return countyCityList;
    }

    public void setCountyCityList(List<CountyCityVo> countyCityList) {
        this.countyCityList = countyCityList;
    }
}
