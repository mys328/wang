package com.thinkwin.log.service;


import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.log.SysLog;

public interface SysLogService {

    /**
     * 添加日志
     */
    boolean insertSysLog(SysLog sysLog);

    /*
    * 带参数 分页查询
    * */
    PageInfo<SysLog> selectSysLogListByPage(BasePageEntity page, SysLog sysLog);

    /*
    * 根据id查询
    * */
    SysLog selectSysLogById(String id);

    /*
    * 清理日志（真删除）
    * */
    boolean deleteSysLogList(SysLog sysLog);

    /*
   * 清理日志（删除）
   * */
    boolean updateSysLogList(SysLog sysLog);

    /*
    * 日志统一接口
    * businessType：业务类型 枚举
    * eventType：操作类型  枚举
    * content：日志详细内容
    * result：日志结果集  异常集
    * loglevel:日志级别 枚举
    * */
    public void createLog(String businessType, String eventType, String content,String result,String loglevel);

    /*
    * 此接口重载
    * 只用于登录和注册
    * */
    public void createLog(String businessType, String eventType, String content,String result,String loglevel,String ip,String source);

}