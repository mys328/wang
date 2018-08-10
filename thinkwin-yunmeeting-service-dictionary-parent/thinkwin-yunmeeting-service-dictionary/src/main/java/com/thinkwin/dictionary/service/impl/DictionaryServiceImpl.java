package com.thinkwin.dictionary.service.impl;

import com.thinkwin.common.model.db.CityDictionary;
import com.thinkwin.common.model.db.SysDictionary;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.dictionary.mapper.CityDictionaryMapper;
import com.thinkwin.dictionary.mapper.SysDictionaryMapper;
import com.thinkwin.dictionary.service.DictionaryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
  *  数据字典接口实现
  *
  *  开发人员:daipengkai
  *  创建时间:2017/6/30
  *
  */
@Service("dictionaryService")
public class DictionaryServiceImpl implements DictionaryService {


    @Autowired
    SysDictionaryMapper sysDictionaryMapper;
    @Autowired
    CityDictionaryMapper cityDictionaryMapper;


    @Override
    public SysDictionary insertDictionary(SysDictionary sysDictionary) {
        SysDictionary sys = new SysDictionary();
        int flag = 0;
        sys.setDictCode(sysDictionary.getDictCode());
        sys.setStatus(1);
        //查看当前code是否存在
        List<SysDictionary> list = this.sysDictionaryMapper.select(sys);
        //当前code不存在存入当前字典数据
        if (list.size() == 0) {
            //是否存在上一级如果存在则获取上一级存在个数进行查看排序
            if (!"0".equals(sysDictionary.getParentId())) {
                Example example = new Example(SysDictionary.class, true, true);
                example.setOrderByClause("order_num desc");
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("parentId", sysDictionary.getParentId());
                List<SysDictionary> dictionaryList = this.sysDictionaryMapper.selectByExample(example);
                if (dictionaryList.size() == 0) {
                    sysDictionary.setOrderNum(1);
                } else {
                    sysDictionary.setOrderNum(dictionaryList.get(0).getOrderNum() + 1);
                }
            } else {
                sysDictionary.setOrderNum(0);
            }
            sysDictionary.setStatus(1);
            sysDictionary.setDictId(CreateUUIdUtil.Uuid());
            sysDictionary.setCreateTime(new Date());
            flag = this.sysDictionaryMapper.insert(sysDictionary);
        }
        //添加失败返回空
        if (flag == 0) {
            sysDictionary = null;
        }
        return sysDictionary;
    }

    @Override
    public boolean deleteDictionary(String dictId) {
        boolean success = false;
        //删除字典
        int flag = 0;
        SysDictionary sysDictionary = sysDictionaryMapper.selectByPrimaryKey(dictId);
        if (sysDictionary != null) {
            //修改做逻辑删除
            sysDictionary.setStatus(0);
            flag = this.sysDictionaryMapper.updateByPrimaryKeySelective(sysDictionary);
        }
        if (flag > 0) {
            success = true;
        }
        return success;
    }

    @Override
    public SysDictionary updateDictionary(SysDictionary sysDictionary) {
        //修改字典
        SysDictionary sys = sysDictionaryMapper.selectByPrimaryKey(sysDictionary.getDictId());
        int flag = 0;
        if (sys != null) {
            sys.setDictSort(sysDictionary.getDictSort());
            sys.setTenantId(sysDictionary.getTenantId());
            sys.setDictCode(sysDictionary.getDictCode());
            sys.setDictName(sysDictionary.getDictName());
            sys.setDictValue(sysDictionary.getDictValue());
            sys.setDescript(sysDictionary.getDescript());
            sys.setPlatformId(sysDictionary.getPlatformId());
            sys.setUpdateId(sysDictionary.getUpdateId());
            sys.setUpdateTime(new Date());
            flag = this.sysDictionaryMapper.updateByPrimaryKeySelective(sys);
        }
        //修改失败返回null
        if (flag == 0) {
            sys = null;
        }
        return sys;
    }

    @Override
    public SysDictionary selectByIdSysDictionary(String dicCode) {
        //根据字典code查看单条信息
        SysDictionary sysDictionary = new SysDictionary();
        sysDictionary.setDictCode(dicCode);
        sysDictionary.setStatus(1);
        List<SysDictionary> list = this.sysDictionaryMapper.select(sysDictionary);
        if (list.size() != 0) {
            sysDictionary = list.get(0);
        } else {
            sysDictionary = null;
        }
        return sysDictionary;
    }

    @Override
    public List<SysDictionary> selectSysDictionary(String dictId) {
        List<SysDictionary> list = this.sysDictionaryMapper.selectAllSysDictionary(dictId);
        return list;
    }

    @Override
    public List<SysDictionary> selectSysDictionaryByParentId(String parentId) {
        Example example = new Example(SysDictionary.class,true,true);
        example.createCriteria().andEqualTo("parentId",parentId);
        List<SysDictionary> sysDictionaries = this.sysDictionaryMapper.selectByExample(example);
        if(sysDictionaries.size()>0){
            return sysDictionaries;
        }
        return null;
    }

    @Override
    public boolean insertCityDictionary(CityDictionary cityDictionary) {
        //判断城市地区编码是否存在
        if(null != cityDictionary){
            String cityCode = cityDictionary.getCityCode();
            if(StringUtils.isNotBlank(cityCode)){
                CityDictionary cityDictionary1 = cityDictionaryMapper.selectByPrimaryKey(cityCode);
                if(null == cityDictionary1){
                    int i = cityDictionaryMapper.insertSelective(cityDictionary);
                    if(i>0){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<CityDictionary> selectParentCityListsByABC(String abc) {
        if(StringUtils.isNotBlank(abc)){
            abc = abc + "%";
            List<CityDictionary> cityDictionaries = cityDictionaryMapper.selectParentCityByABCAndGroup(abc);
            if(cityDictionaries.size()>0){
                return cityDictionaries;
            }
        }
        return null;
    }

    @Override
    public List<CityDictionary> selectCountyListByParentCity(String parentCity) {
        if(StringUtils.isNotBlank(parentCity)){
            Example example = new Example(CityDictionary.class,true,true);
            example.createCriteria().andEqualTo("parentCityChinese",parentCity);
            List<CityDictionary> cityDictionaries = cityDictionaryMapper.selectByExample(example);
            if(cityDictionaries.size()>0){
                return cityDictionaries;
            }
        }
        return null;
    }

    /**
     * 根据地区及地区上级查询应该地区信息
     *
     * @param city
     * @param parentCity
     * @return
     */
    @Override
    public CityDictionary selectCityAndParentCity(String city, String parentCity) {
        if(StringUtils.isNotBlank(city) && StringUtils.isNotBlank(parentCity)){
            Example example = new Example(CityDictionary.class,true,true);
            example.createCriteria().andEqualTo("cityChinese",city);
            example.createCriteria().andEqualTo("parentCityChinese",parentCity);
            List<CityDictionary> cityDictionaries = cityDictionaryMapper.selectByExample(example);
            if(cityDictionaries.size()>0){
                return cityDictionaries.get(0);
            }
        }
        return null;
    }

    @Override
    public List<CityDictionary> groupSelectAllProvince() {
        List<CityDictionary> cityDictionaries = cityDictionaryMapper.groupSelectAllProvince();
        if(cityDictionaries.size()>0){
            return cityDictionaries;
        }
        return null;
    }

    @Override
    public List<CityDictionary> groupSelectAllParentCity(String province) {
        List<CityDictionary> cityDictionaries = cityDictionaryMapper.groupSelectAllParentCity(province);
        if(cityDictionaries.size()>0){
            return cityDictionaries;
        }
        return null;
    }

    @Override
    public List<CityDictionary> selectCountysByParentCity(String parenetCity) {
        List<CityDictionary> cityDictionaries = cityDictionaryMapper.selectCountysByParentCity(parenetCity);
        if(cityDictionaries.size()>0){
            return cityDictionaries;
        }
        return null;
    }
}
