package com.thinkwin.yuncm.service.impl;

import com.thinkwin.common.model.db.SysSetting;
import com.thinkwin.yuncm.mapper.SysSettingMapper;
import com.thinkwin.yuncm.service.SysSetingService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/*
 * 类说明：
 * @author lining 2018/5/15
 * @version 1.0
 *
 */
@Service("sysSetingService")
public class SysSetingServiceImpl implements SysSetingService {

    @Autowired
    SysSettingMapper sysSettingMapper;

    @Override
    public SysSetting findByKey(String key) {
        SysSetting example = new SysSetting();
        example.setSettingKey(key);
        SysSetting setting = sysSettingMapper.selectOne(example);
        return  setting;
    }

    @Override
    @Cacheable(value = "system-config", key = "T(com.thinkwin.service.TenantContext).getTenantId().concat(':').concat(#key)")
    public String get(String key) {
        SysSetting example = new SysSetting();
        example.setSettingKey(key);
        SysSetting setting = sysSettingMapper.selectOne(example);
        if(null == setting){
            return "";
        }
        return setting.getContent();
    }

    @Override
    @CachePut(value = "system-config", key = "T(com.thinkwin.service.TenantContext).getTenantId().concat(':').concat(#key)")
    public void set(String id,String key, String value) {
        SysSetting example = new SysSetting();
        example.setId(id);
        example.setSettingKey(key);
        example.setContent(value);
        sysSettingMapper.updateByPrimaryKey(example);
    }

    @Override
    public boolean add(SysSetting sysSetting) {
        if(null!=sysSetting && StringUtils.isNotBlank(sysSetting.getId())){
            int i = sysSettingMapper.insertSelective(sysSetting);
            if(i>0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(SysSetting sysSetting) {
        if(null!=sysSetting && StringUtils.isNotBlank(sysSetting.getId())){
            int i = sysSettingMapper.updateByPrimaryKeySelective(sysSetting);
            if(i>0){
                return true;
            }
        }
        return false;
    }
}
