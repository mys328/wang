package com.thinkwin.base.service.impl;

import com.thinkwin.base.mapper.BaseMapper;
import com.thinkwin.base.service.BaseService;
import com.thinkwin.common.model.log.Base;
import com.thinkwin.common.model.log.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("baseService")
public class BaseServiceImpl implements BaseService {

    @Autowired
    private BaseMapper baseMapper;

    @Override
    public Base selectBaseById(String id) {
        return this.baseMapper.selectByPrimaryKey(id);
    }
}
