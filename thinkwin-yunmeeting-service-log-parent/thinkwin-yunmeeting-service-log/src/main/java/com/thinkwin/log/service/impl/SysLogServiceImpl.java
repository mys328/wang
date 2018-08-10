package com.thinkwin.log.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.TenantUserVo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.log.SysLog;
import com.thinkwin.common.model.log.SysLogType;
import com.thinkwin.common.utils.DateTypeFormat;
import com.thinkwin.common.utils.StringUtil;
import com.thinkwin.log.mapper.SysLogMapper;
import com.thinkwin.log.mapper.SysLogTypeMapper;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.service.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("sysLogService")
public class SysLogServiceImpl implements SysLogService {
    @Autowired
    private SysLogMapper sysLogMapper;

    @Autowired
    private SysLogTypeMapper sysLogTypeMapper;

    @Override
    public boolean insertSysLog(SysLog sysLog) {
        String id = sysLog.getId();
        if (!StringUtil.isEmpty(id)) {
            int num = sysLogMapper.insert(sysLog);
            if (num > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public PageInfo<SysLog> selectSysLogListByPage(BasePageEntity page, SysLog sysLog) {

        Map map = new HashMap();
        map.put("tenantId", TenantContext.getTenantId());

        if (sysLog.getBusinesstypes().size() >0 && StringUtil.isEmpty(sysLog.getEventtype())) {
            map.put("businesstypes", sysLog.getBusinesstypes());
        }
        if (!StringUtil.isEmpty(sysLog.getEventtype())) {
            map.put("eventtype", sysLog.getEventtype());
        }
        if (!StringUtil.isEmpty(sysLog.getContent())) {
            map.put("content", sysLog.getContent());
        }

        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<SysLog> sysLogList = sysLogMapper.selectSysLogListByPage(map);
        return new PageInfo<>(sysLogList);
    }

    @Override
    public SysLog selectSysLogById(String id) {
        return sysLogMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean deleteSysLogList(SysLog sysLog) {
        String tenantId = TenantContext.getTenantId();
        sysLog.setTenantId(tenantId);
        if (StringUtil.isEmpty(tenantId)) {
            int num = sysLogMapper.delete(sysLog);
            if (num > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void createLog(String businessType, String eventType, String content,String result,String loglevel) {
        try {
            TenantUserVo tenantUserVo = TenantContext.getUserInfo();
            String userName = "";
            String userId = "";
            String ip = "";
            String  source = "";
            if (tenantUserVo != null) {
                userName = tenantUserVo.getUserName();
                userId = tenantUserVo.getUserId();
                ip = tenantUserVo.getIp();
                source = tenantUserVo.getDevice();
            }

            SysLog log = new SysLog();
            log.setId(UUID.randomUUID().toString());
            log.setBusinesstype(businessType.toString());
            if(businessType.toString() != null){
                SysLogType sysLogType = new SysLogType();
                sysLogType.setType(businessType.toString());
                sysLogType = sysLogTypeMapper.selectOne(sysLogType);
                log.setBusinessname(sysLogType.getName());
            }
            log.setBusinessid("");
            log.setEventtype(eventType.toString());
            if(eventType.toString() != null){
                SysLogType sysLogType = new SysLogType();
                sysLogType.setType(eventType.toString());
                sysLogType = sysLogTypeMapper.selectOne(sysLogType);
                log.setEventname(sysLogType.getName());
            }
            log.setState(0);
            log.setContent(content);
            log.setResult(result);
            log.setOperateUserId(userId);
            log.setOperatedate(DateTypeFormat.DateToStr(new Date()));
            log.setOperator(userName);
            log.setIp(ip);
            log.setTenantId(TenantContext.getTenantId());
            log.setSource(source);
            log.setLoglevel(loglevel);

            this.insertSysLog(log);

        } catch (Exception e) {
        }
    }

    @Override
    public void createLog(String businessType, String eventType, String content,String result,String loglevel,String ip,String source) {
        try {
            SysLog log = new SysLog();
            log.setId(UUID.randomUUID().toString());
            log.setBusinesstype(businessType.toString());
            if(businessType.toString() != null){
                SysLogType sysLogType = new SysLogType();
                sysLogType.setType(businessType.toString());
                sysLogType = sysLogTypeMapper.selectOne(sysLogType);
                log.setBusinessname(sysLogType.getName());
            }
            log.setBusinessid("");
            log.setEventtype(eventType.toString());
            if(eventType.toString() != null){
                SysLogType sysLogType = new SysLogType();
                sysLogType.setType(eventType.toString());
                sysLogType = sysLogTypeMapper.selectOne(sysLogType);
                log.setEventname(sysLogType.getName());
            }
            log.setState(0);
            log.setContent(content);
            log.setResult(result);
            log.setOperatedate(DateTypeFormat.DateToStr(new Date()));
            if(content.indexOf("登录") != -1){
                content = content.substring(0,content.indexOf("登录"));
                log.setOperator(content);
            }
            if(content.indexOf("创建") != -1){
                content = content.substring(0,content.indexOf("创建"));
                log.setOperator(content);
            }
            log.setIp(ip);
            log.setTenantId(TenantContext.getTenantId());
            log.setSource(source);
            log.setLoglevel(loglevel);

            this.insertSysLog(log);

        } catch (Exception e) {
        }
    }

    @Override
    public boolean updateSysLogList(SysLog sysLog) {
        Map map = new HashMap();
        map.put("tenantId", TenantContext.getTenantId());

        if (sysLog.getBusinesstypes().size() >0 && StringUtil.isEmpty(sysLog.getEventtype())) {
            map.put("businesstypes", sysLog.getBusinesstypes());
        }
        if (!StringUtil.isEmpty(sysLog.getEventtype())) {
            map.put("eventtype", sysLog.getEventtype());
        }

        if (sysLog != null) {
            int num = sysLogMapper.updateSysLogList(map);
            if (num > 0) {
                return true;
            }
        }
        return false;
    }

}
