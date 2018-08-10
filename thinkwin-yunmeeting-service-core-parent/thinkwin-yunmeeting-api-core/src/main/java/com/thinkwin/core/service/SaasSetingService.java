package com.thinkwin.core.service;

import com.thinkwin.common.model.core.SaasSetting;

/**
 * 接口说明：
 * 平台全局参数配置
 * @author lining
 * @version 1.0
 * @Date 2018/5/31
 */
public interface SaasSetingService {



    public SaasSetting findByKey(String key);

    /**
     * 获取value值
     * @param key
     * @return
     */
    String get(String key);

    /**
     * 修改value值
     * @param key
     * @param value
     */
    void set(String key, String value);
}
