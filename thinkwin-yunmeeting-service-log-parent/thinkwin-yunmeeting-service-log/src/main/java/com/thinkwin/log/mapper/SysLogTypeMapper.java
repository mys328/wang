package com.thinkwin.log.mapper;

import com.thinkwin.common.model.log.SysLogType;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SysLogTypeMapper extends Mapper<SysLogType> {
    
    List<SysLogType> selectSysLogTypeListByRoleId(String roleId);
}