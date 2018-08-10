package com.thinkwin.auth.service.impl;

import com.thinkwin.auth.mapper.core.SaasTenantInfoMapper;
import com.thinkwin.auth.service.SaasTenantInfoService;
import com.thinkwin.common.model.core.SaasTenantInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类名: SaasTenantInfoServiceImpl </br>
 * 描述:</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/6/19 </br>
 */
@Service("saasTenantInfoService")
public class SaasTenantInfoServiceImpl implements SaasTenantInfoService{
    @Autowired
    SaasTenantInfoMapper saasTenantInfoMapper;

    @Override
    public boolean saveSaasTenantInfo(SaasTenantInfo saasTenantInfo) {
        int i = this.saasTenantInfoMapper.insertSelective(saasTenantInfo);
        if(i>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSaasTenantInfo(SaasTenantInfo saasTenantInfo) {
        int i = this.saasTenantInfoMapper.updateByPrimaryKeySelective(saasTenantInfo);
        if(i>0){
            return true;
        }
        return false;
    }

    @Override
    public SaasTenantInfo selectSaasTenantInfo(SaasTenantInfo saasTenantInfo) {
        List<SaasTenantInfo> select = this.saasTenantInfoMapper.select(saasTenantInfo);
        if(null!=select&&select.size()>0){
            return select.get(0);
        }
        return null;
    }

    @Override
    public SaasTenantInfo selectSaasTenantInfo(String tenantId) {
        if(StringUtils.isNotBlank(tenantId)){
            SaasTenantInfo saasTenantInfo = new SaasTenantInfo();
            saasTenantInfo.setTenantId(tenantId);
            return selectSaasTenantInfo(saasTenantInfo);
        }
        return null;
    }
}
