package com.thinkwin.common.vo.TerminalInfoVo;

import java.io.Serializable;
import java.util.List;

/**
 * 类名: AllCityVos </br>
 * 描述: web端位置返回vo</br>
 * 开发人员： weining </br>
 * 创建时间：  2018/6/11 </br>
 */
public class AllCityVos implements Serializable{
    private static final long serialVersionUID = -2253835999707565098L;

    private List<ProvinceVo> allProvince;
    private List<CityVo> allCity;
    private List<CountyVo> allCounty;

    public List<ProvinceVo> getAllProvince() {
        return allProvince;
    }

    public void setAllProvince(List<ProvinceVo> allProvince) {
        this.allProvince = allProvince;
    }

    public List<CityVo> getAllCity() {
        return allCity;
    }

    public void setAllCity(List<CityVo> allCity) {
        this.allCity = allCity;
    }

    public List<CountyVo> getAllCounty() {
        return allCounty;
    }

    public void setAllCounty(List<CountyVo> allCounty) {
        this.allCounty = allCounty;
    }
}
