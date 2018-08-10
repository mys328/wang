package com.thinkwin.core.mapper;

import com.thinkwin.common.model.core.SaasSetting;
import tk.mybatis.mapper.common.Mapper;

public interface SaasSettingMapper extends Mapper<SaasSetting> {

    /**
     * 根据key查看配置信息
     * @param settingKey
     * @return
     */
    SaasSetting selectBySettingKeySaasSetting(String settingKey);





}