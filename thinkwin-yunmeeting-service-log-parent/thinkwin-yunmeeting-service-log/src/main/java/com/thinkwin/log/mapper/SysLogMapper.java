package com.thinkwin.log.mapper;

import com.thinkwin.common.model.log.SysLog;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SysLogMapper extends Mapper<SysLog> {

    int updateSysLogList(Map map);

    List<SysLog>  selectSysLogListByPage(Map map);
}