package com.thinkwin.auth.localService;

import com.thinkwin.common.model.core.SaasDbConfig;

import java.util.List;

/**
 * 域表
 */
public interface LocalSaasDbConfigService {
    //根据条件查询域
    public List<SaasDbConfig> selectAllSaasDbConfig();
}
