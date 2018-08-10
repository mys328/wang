package com.thinkwin.dictionary.service;

import com.thinkwin.common.model.db.CityDictionary;
import com.thinkwin.common.model.db.SysDictionary;

import java.util.List;

/**
  *  数据字典接口
  *
  *  开发人员:daipengkai
  *  创建时间:2017/6/30
  *
  */

public interface DictionaryService {

    /**
     * 添加数据字典
     *
     * @param sysDictionary 数据字典实体
     * @return
     */
    SysDictionary insertDictionary(SysDictionary sysDictionary);


    /**
     * 删除数据字典
     *
     * @param dictId 字典ID
     * @return
     */
    boolean deleteDictionary(String dictId);

    /**
     * 修改数据字典
     *
     * @param sysDictionary
     * @return
     */
    SysDictionary updateDictionary(SysDictionary sysDictionary);


    /**
     * 根据字典ID查看
     *
     * @param dicCode 字典ID
     * @return
     */
    SysDictionary selectByIdSysDictionary(String dicCode);

    /**
     * 根据字典code查看列表
     * @param dictId 字典code值
     * @return
     */
    List<SysDictionary> selectSysDictionary(String dictId);

    /**
     * 方法名：selectSysDictionaryByParentId</br>
     * 描述：根据字典父Id查询所有字典信息</br>
     * 参数：parentId 父id</br>
     * 返回值：</br>
     */
    List<SysDictionary> selectSysDictionaryByParentId(String parentId);

    /**
     * 添加城市数据字典
     *
     * @param cityDictionary 城市数据字典实体
     * @return
     */
    boolean insertCityDictionary(CityDictionary cityDictionary);

    /**
     * 方法名：selectParentCityListsByABC</br>
     * 描述：根据拼音首字母排序查询上级市列表</br>
     * 参数：[abc]</br>
     * 返回值：java.util.List<></></br>
     */
    List<CityDictionary> selectParentCityListsByABC(String abc);

    /**
     * 方法名：selectCountyListByParentCity</br>
     * 描述：根据上级市查询所辖的所有县</br>
     * 参数：[parentCity]</br>
     * 返回值：java.util.List<com.thinkwin.common.model.db.CityDictionary></br>
     */
    List<CityDictionary> selectCountyListByParentCity(String parentCity);

    /**
     * 根据地区及地区上级查询应该地区信息
     * @param city
     * @param parentCity
     * @return
     */
    CityDictionary selectCityAndParentCity(String city,String parentCity);

    /**
     * 方法名：groupSelectAllProvince</br>
     * 描述：分组查询所有的省</br>
     * 参数：[]</br>
     * 返回值：java.util.List<com.thinkwin.common.model.db.CityDictionary></br>
     */
    List<CityDictionary> groupSelectAllProvince();

    /**
     * 方法名：groupSelectAllParentCity</br>
     * 描述：根据省分组查询所有上级市</br>
     * 参数：[province]</br>
     * 返回值：java.util.List<com.thinkwin.common.model.db.CityDictionary></br>
     */
    List<CityDictionary> groupSelectAllParentCity(String province);

    /**
     * 方法名：selectCountysByParentCity</br>
     * 描述：根据上级市查询县</br>
     * 参数：[parenetCity]</br>
     * 返回值：java.util.List<com.thinkwin.common.model.db.CityDictionary></br>
     */
    List<CityDictionary> selectCountysByParentCity(String parenetCity);
}