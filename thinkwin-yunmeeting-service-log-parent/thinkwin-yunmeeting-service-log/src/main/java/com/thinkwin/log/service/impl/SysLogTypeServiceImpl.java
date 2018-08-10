package com.thinkwin.log.service.impl;

import com.thinkwin.common.model.log.SysLogType;
import com.thinkwin.log.mapper.SysLogTypeMapper;
import com.thinkwin.log.service.SysLogTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("sysLogTypeService")
public class SysLogTypeServiceImpl implements SysLogTypeService {
    @Autowired
    private SysLogTypeMapper sysLogTypeMapper;


    @Override
    public List<SysLogType> selectAllSysLogTypeList() {
        return sysLogTypeMapper.selectAll();
    }

    @Override
    public List<SysLogType> selectSysLogTypeList(SysLogType sysLogType){
        return sysLogTypeMapper.select(sysLogType);
    }

    @Override
    public List<SysLogType> selectSysLogTypeListByRoleId(String roleId) {
        return sysLogTypeMapper.selectSysLogTypeListByRoleId(roleId);
    }
}
