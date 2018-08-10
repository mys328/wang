package com.thinkwin.config.service.impl;

import com.thinkwin.config.mapper.ConfigMapper;
import com.thinkwin.config.po.SysSetting;
import com.thinkwin.config.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service(value = "sysConfigService")
public class RedisConfigService implements ConfigService {

	@Autowired
	ConfigMapper configMapper;

	@Override
	@Cacheable(value = "system-config", key = "T(com.thinkwin.service.TenantContext).getTenantId().concat(':').concat(#key)")
	public String get(String key) {
		SysSetting example = new SysSetting();
		example.setSettingKey(key);
		SysSetting setting = configMapper.selectOne(example);
		if(null == setting){
			return "";
		}
		return setting.getContent();
	}

	@Override
	@CachePut(value = "system-config", key = "T(com.thinkwin.service.TenantContext).getTenantId().concat(':').concat(#key)")
	public void set(String key, String value) {
		SysSetting example = new SysSetting();
		example.setSettingKey(key);
		example.setContent(value);
		configMapper.updateByPrimaryKey(example);
	}
}
