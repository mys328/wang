package com.thinkwin.publish.service.impl;

import com.thinkwin.common.model.publish.PlatformTenantTerminalMiddle;
import com.thinkwin.publish.mapper.PlatformTenantTerminalMiddleMapper;
import com.thinkwin.publish.service.PlatformTenantTerminalMiddleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import java.util.Date;
import java.util.List;

/**
 * 类名: PlatformTenantTerminalMiddleServiceImpl </br>
 * 描述:租户终端中间表接口实现</br>
 * 开发人员： weining </br>
 * 创建时间：  2018/5/31 </br>
 */
@Service("platformTenantTerminalMiddleService")
public class PlatformTenantTerminalMiddleServiceImpl implements PlatformTenantTerminalMiddleService {

    @Autowired
    PlatformTenantTerminalMiddleMapper platformTenantTerminalMiddleMapper;

    @Override
    public List<PlatformTenantTerminalMiddle> selectPTenantTerminalMByTenantId(String tenantId) {
        Example example = new Example(PlatformTenantTerminalMiddle.class);
        example.createCriteria().andEqualTo("tenantId",tenantId);
        List<PlatformTenantTerminalMiddle> platformTenantTerminalMiddles = platformTenantTerminalMiddleMapper.selectByExample(example);
        if(platformTenantTerminalMiddles.size()>0){
            return platformTenantTerminalMiddles;
        }
        return null;
    }

    @Override
    public PlatformTenantTerminalMiddle selectPTenantTerminalMByHardwareId(String hardwareId) {
        Example example = new Example(PlatformTenantTerminalMiddle.class);
        example.createCriteria().andEqualTo("hardwareId",hardwareId);
        List<PlatformTenantTerminalMiddle> platformTenantTerminalMiddles = platformTenantTerminalMiddleMapper.selectByExample(example);
        if(platformTenantTerminalMiddles.size()>0){
            return platformTenantTerminalMiddles.get(0);
        }
        return null;
    }

    @Override
    public PlatformTenantTerminalMiddle selectPTenantTerminalMByTerminalId(String terminalId) {
        Example example = new Example(PlatformTenantTerminalMiddle.class);
        example.createCriteria().andEqualTo("terminalId",terminalId);
        List<PlatformTenantTerminalMiddle> platformTenantTerminalMiddles = platformTenantTerminalMiddleMapper.selectByExample(example);
        if(platformTenantTerminalMiddles.size()>0){
            return platformTenantTerminalMiddles.get(0);
        }
        return null;
    }

    @Override
    public List<PlatformTenantTerminalMiddle> selectPTenantTerminalMByEntity(PlatformTenantTerminalMiddle platformTenantTerminalMiddle) {
        List<PlatformTenantTerminalMiddle> select = platformTenantTerminalMiddleMapper.select(platformTenantTerminalMiddle);
        if(select.size()>0){
            return select;
        }
        return null;
    }

    @Override
    public boolean insertPTenantTerminalMByEntity(PlatformTenantTerminalMiddle platformTenantTerminalMiddle) {
        if(StringUtils.isNotBlank(platformTenantTerminalMiddle.getId())) {
            platformTenantTerminalMiddle.setCreateTime(new Date());
            int i = platformTenantTerminalMiddleMapper.insertSelective(platformTenantTerminalMiddle);
            if (i > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updatePTenantTerminalMByEntity(PlatformTenantTerminalMiddle platformTenantTerminalMiddle) {
        if(StringUtils.isNotBlank(platformTenantTerminalMiddle.getId())){
            platformTenantTerminalMiddle.setUpdateTime(new Date());
            int i = platformTenantTerminalMiddleMapper.updateByPrimaryKeySelective(platformTenantTerminalMiddle);
            if(i>0){
                return true;
            }
        }
        return false;
    }


    /**
     * 根据租户id删除该租户与终端的关联关系数据
     * @param tenantId
     * @return
     */
    public boolean delPlatformTenantTerminalMiddlByTenantId(String tenantId){
        if(StringUtils.isNotBlank(tenantId)){
            Example example = new Example(PlatformTenantTerminalMiddle.class);
            example.createCriteria().andEqualTo("tenantId",tenantId);
            int i = platformTenantTerminalMiddleMapper.deleteByExample(example);
            if(i >=0){
                return true;
            }
        }
        return false;
    }
}
