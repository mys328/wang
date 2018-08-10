package com.thinkwin.dictionary.mapper;

import com.thinkwin.common.model.db.CityDictionary;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CityDictionaryMapper extends Mapper<CityDictionary> {

    /**
     * 方法名：selectParentCityByABCAndGroup</br>
     * 描述：查询上级市根据首字母并且分组</br>
     * 参数：[ABC]</br>
     * 返回值：java.util.List<com.thinkwin.common.model.db.CityDictionary></br>
     */
    public List<CityDictionary> selectParentCityByABCAndGroup(String ABC);

    /**
     * 方法名：groupSelectAllProvince</br>
     * 描述：查询所有的省</br>
     * 参数：[]</br>
     * 返回值：java.util.List<com.thinkwin.common.model.db.CityDictionary></br>
     */
    public List<CityDictionary> groupSelectAllProvince();

    /**
     * 方法名：groupSelectAllParentCity</br>
     * 描述：根据条件查询所有城市</br>
     * 参数：[province]</br>
     * 返回值：java.util.List<com.thinkwin.common.model.db.CityDictionary></br>
     */
    public List<CityDictionary> groupSelectAllParentCity(String province);

    /**
     * 方法名：selectCountysByParentCity</br>
     * 描述：根据上级市查询县</br>
     * 参数：[parenetCity]</br>
     * 返回值：java.util.List<com.thinkwin.common.model.db.CityDictionary></br>
     */
    public List<CityDictionary> selectCountysByParentCity(String parenetCity);
}