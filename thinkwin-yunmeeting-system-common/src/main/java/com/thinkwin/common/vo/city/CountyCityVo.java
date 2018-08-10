package com.thinkwin.common.vo.city;

import java.io.Serializable;

/**
 * 类名: CountyCityVo </br>
 * 描述: 县级城市Vo</br>
 * 开发人员： weining </br>
 * 创建时间：  2018/5/18 </br>
 */
public class CountyCityVo implements Serializable{

    private static final long serialVersionUID = -3808528459940485923L;
    private String cityName;
    private String cityPinYin;

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
}
