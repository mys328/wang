package com.thinkwin.log.service;


import com.thinkwin.common.model.log.SysLogType;

import java.util.List;

public interface SysLogTypeService {
    /*
    * 查询所有日志类型
    * */
    List<SysLogType> selectAllSysLogTypeList();

    /*
    * 根据条件查询
    * */
    List<SysLogType> selectSysLogTypeList(SysLogType sysLogType);

    /*
    * 根据role条件查询
    * */
    List<SysLogType> selectSysLogTypeListByRoleId(String roleId);
}