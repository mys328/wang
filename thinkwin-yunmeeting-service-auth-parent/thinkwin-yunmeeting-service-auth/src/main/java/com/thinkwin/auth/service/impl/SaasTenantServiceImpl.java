package com.thinkwin.auth.service.impl;

import com.github.pagehelper.Page;
import com.thinkwin.auth.mapper.core.SaasTenantInfoMapper;
import com.thinkwin.auth.mapper.core.SaasTenantMapper;
import com.thinkwin.auth.service.SaasTenantService;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.core.SaasTenantInfo;
import com.thinkwin.yunmeeting.framework.datasource.dynamicdatasource.DBContextHolder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 类名: SaasTenantServiceImpl </br>
 * 描述:租户表接口实现类</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/6/8 </br>
 */
@Service("saasTenantService")
public class SaasTenantServiceImpl implements SaasTenantService {

    @Autowired
    SaasTenantMapper saasTenantMapper;
    @Autowired
    SaasTenantInfoMapper saasTenantInfoMapper;

    @Override
    public boolean saveSaasTenantServcie(SaasTenant saasTenant) {
        int i = this.saasTenantMapper.insertSelective(saasTenant);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSaasTenantService(SaasTenant saasTenant) {
        int i = this.saasTenantMapper.updateByPrimaryKeySelective(saasTenant);
        if(i>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSaasTenantServcie(String tenantId) {
        SaasTenant saasTenant = new SaasTenant();
        saasTenant.setId(tenantId);
        saasTenant.setStatus(0);
        int i = this.saasTenantMapper.updateByPrimaryKeySelective(saasTenant);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public SaasTenant selectSaasTenantServcie(SaasTenant saasTenant) {
        DBContextHolder.setDBType("0");
        List<SaasTenant> saasTenants = this.saasTenantMapper.select(saasTenant);
        if (null != saasTenants && saasTenants.size() > 0) {
            return saasTenants.get(0);
        }
        return null;
    }

    @Override
    public SaasTenant selectSaasTenantServcie(String tenantId) {
        SaasTenant saasTenant = this.saasTenantMapper.selectByPrimaryKey(tenantId);
        if(null!=saasTenant){
            return saasTenant;
        }
        return null;
    }

    @Override
    public List<SaasTenant> selectAllSaasTenantServcie(Page page) {
        return null;
    }

    @Override
    public Page selectAllSaasTenantByConditionServcie(SaasTenant saasTenant, Page page) {
        return null;
    }

    /**
     * 根据租户id获取租户详细信息
     * @param tenandId
     * @return
     */
    public SaasTenantInfo selectSaasTenantInfoByTenandId(String tenandId){
        if(StringUtils.isNotBlank(tenandId)){
            Example example = new Example(SaasTenantInfo.class);
            example.createCriteria().andEqualTo("tenantId",tenandId);
            List<SaasTenantInfo> saasTenantInfos = saasTenantInfoMapper.selectByExample(example);
            if(null != saasTenantInfos && saasTenantInfos.size() > 0){
                return saasTenantInfos.get(0);
            }
        }
        return null;
    }

}
