package com.thinkwin.core.service.impl;

import com.thinkwin.common.model.core.SaasSetting;
import com.thinkwin.core.mapper.SaasSettingMapper;
import com.thinkwin.core.service.SaasSetingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 类说明：
 *
 * @author lining
 * @version 1.0
 * @Date 2018/5/31
 */
@Service("saasSetingService")
public class SaasSetingServiceImpl implements SaasSetingService {

    @Resource
    SaasSettingMapper saasSettingMapper;

    @Override
    public SaasSetting findByKey(String key) {
        SaasSetting saasSetting=new SaasSetting();
        if(StringUtils.isNotBlank(key)){
            saasSetting.setSettingKey(key);
            saasSetting=this.saasSettingMapper.selectOne(saasSetting);
        }
        return saasSetting;
    }

    /**
     * 获取value值
     *
     * @param key
     * @return
     */
    @Override
    @Cacheable(value = "system-config", key = "T(com.thinkwin.service.TenantContext).getTenantId().concat(':').concat(#key)")
    public String get(String key) {
        SaasSetting saasSetting=this.findByKey(key);
        if(null!=saasSetting){
            return saasSetting.getContent();
        }
        return "";
    }

    /**
     * 修改value值
     *
     * @param key
     * @param value
     */
    @Override
    public void set(String key, String value) {
        SaasSetting saasSetting=this.findByKey(key);
        if(null!=saasSetting){
            saasSetting.setContent(value);
            this.saasSettingMapper.updateByPrimaryKey(saasSetting);
        }

    }
}
