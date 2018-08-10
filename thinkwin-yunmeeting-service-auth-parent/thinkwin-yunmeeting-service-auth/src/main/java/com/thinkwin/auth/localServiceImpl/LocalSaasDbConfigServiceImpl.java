package com.thinkwin.auth.localServiceImpl;

import com.thinkwin.auth.localService.LocalSaasDbConfigService;
import com.thinkwin.auth.mapper.core.SaasDbConfigMapper;
import com.thinkwin.common.model.core.SaasDbConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("localSaasDbConfigService")
public class LocalSaasDbConfigServiceImpl implements LocalSaasDbConfigService {
    @Autowired
    SaasDbConfigMapper saasDbConfigMapper;

    @Override
    public List<SaasDbConfig> selectAllSaasDbConfig() {
        List<SaasDbConfig> saasDbConfigList = this.saasDbConfigMapper.selectAll();
        if(null!=saasDbConfigList&&saasDbConfigList.size()>0){
            return saasDbConfigList;
        }
        return null;
    }
}
