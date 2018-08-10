package com.thinkwin.log.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.TenantUserVo;
import com.thinkwin.common.dto.publish.TerminalLogDto;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.LocalIpUtil;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.InfoReleaseTerminal;
import com.thinkwin.common.model.log.SysLog;
import com.thinkwin.common.model.log.SysLogType;
import com.thinkwin.common.model.log.TerminalLog;
import com.thinkwin.common.model.publish.PlatformTenantTerminalMiddle;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.DateTypeFormat;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.log.mapper.SysLogMapper;
import com.thinkwin.log.mapper.SysLogTypeMapper;
import com.thinkwin.log.mapper.TerminalLogMapper;
import com.thinkwin.log.service.TerminalLogService;
import com.thinkwin.publish.service.PlatformTenantTerminalMiddleService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User:wangxilei
 * Date:2018/5/31
 * Company:thinkwin
 */
@Service("terminalLogService")
public class TerminalLogServiceImpl implements TerminalLogService {
    @Resource
    private TerminalLogMapper terminalLogMapper;
    @Resource
    private SysLogMapper sysLogMapper;
    @Resource
    private SysLogTypeMapper sysLogTypeMapper;
    @Resource
    private SaasTenantService saasTenantService;
    @Resource
    private PlatformTenantTerminalMiddleService platformTenantTerminalMiddleService;

    private ExecutorService executor = Executors.newWorkStealingPool();

    @Override
    public void addTerminalLog(TerminalLog log) {
        executor.execute(() -> {
            if(log!=null){
                if(log.getId()==null) {
                    log.setId(CreateUUIdUtil.Uuid());
                }
                TenantUserVo tenantUserVo = TenantContext.getUserInfo();
                if (tenantUserVo != null) {
                    log.setOperator(tenantUserVo.getUserName());
                    log.setOperateUserId(tenantUserVo.getUserId());
                    log.setIp(tenantUserVo.getIp());
                    if(log.getSource()==null) {
                        log.setSource(tenantUserVo.getDevice());
                    }
                }else {
                    if(log.getIp()==null) {
                        try {
                            String localIp = LocalIpUtil.getLocalIp();
                            log.setIp(localIp);
                        } catch (SocketException e) {
                            e.printStackTrace();
                        }
                    }
                    if(log.getSource()==null) {
                        log.setSource("PC");
                    }
                }
                if(log.getTerminalId()!=null){
                    if(log.getTerminalName()==null) {
                        PlatformTenantTerminalMiddle pt = new PlatformTenantTerminalMiddle();
                        pt.setTerminalId(log.getTerminalId());
                        List<PlatformTenantTerminalMiddle> middles = platformTenantTerminalMiddleService.selectPTenantTerminalMByEntity(pt);
                        if(middles!=null && middles.size()>0){
                            PlatformTenantTerminalMiddle middle = middles.get(0);
                            String terminalName = middle.getTerminalName();
                            if(StringUtils.isNotBlank(terminalName)){
                                log.setTerminalName(terminalName);
                            }else {
                                log.setTerminalName(middle.getHardwareId());
                            }
                        }
                    }
                }
//                String terminalName = "";
//                if(log.getTerminalName()!=null){
//                    terminalName = log.getTerminalName();
//                    terminalName = "";
//                }
                if(log.getCommand()!=null){
                    String cmd = log.getCommand();
                    if(cmd.equals("10001")||cmd.equals("10002")||cmd.equals("10003")||cmd.equals("10004")||cmd.equals("10009")||cmd.equals("10101")
                            ||cmd.equals("20010")||cmd.equals("20011")||cmd.equals("20012")||cmd.equals("20015")||cmd.equals("20016")){
                        log.setBusinesstype(BusinessType.terminalMonitorOp.toString());
                        if(cmd.equals("10001")){
                            log.setEventtype(EventType.publish_program.toString());
                            log.setContent("发送发布节目指令");
                        }
                        if(cmd.equals("10002")){
                            log.setEventtype(EventType.add_msg.toString());
                            log.setContent("发送插播消息指令");
                        }
                        if(cmd.equals("10003")){
                            log.setEventtype(EventType.delete_program.toString());
                            log.setContent("发送删除节目指令");
                        }
                        if(cmd.equals("10004")){
                            log.setEventtype(EventType.delete_msg.toString());
                            log.setContent("发送删除消息指令");
                        }
                        if(cmd.equals("10009")){
                            log.setEventtype(EventType.get_screenshot.toString());
                            log.setContent("发送截屏指令");
                        }
                        if(cmd.equals("20010")){
                            log.setEventtype(EventType.version_upgrade.toString());
                            log.setContent("发送远程更新指令");
                        }
                        if(cmd.equals("20012")){
                            log.setEventtype(EventType.update_voiceLight.toString());
                            log.setContent("发送设置音量亮度指令");
                        }
                        if(cmd.equals("20015")){
                            log.setEventtype(EventType.remote_reboot.toString());
                            log.setContent("发送远程重启指令");
                        }
                        if(cmd.equals("10101")){
                            log.setEventtype(EventType.meeting_update.toString());
                            log.setContent("发送会议变更指令");
                        }
//                        meeting_update
                    }
                    if(cmd.equals("20013")||cmd.equals("20014")){
                        log.setBusinesstype(BusinessType.planSwitchOp.toString());
                        if(cmd.equals("20013")){
                            log.setEventtype(EventType.start_task.toString());
                            log.setContent("发送启动定时开关机指令");
                        }
                        if(cmd.equals("20014")){
                            log.setEventtype(EventType.stop_task.toString());
                            log.setContent("发送取消定时开关机指令");
                        }
                    }
                    if(cmd.startsWith("300")||cmd.startsWith("400")){
                        log.setBusinesstype(BusinessType.terminalClientOp.toString());
                    }
                }
                if(log.getBusinesstype()!=null){
                    SysLogType sysLogType = new SysLogType();
                    sysLogType.setType(log.getBusinesstype());
                    sysLogType = sysLogTypeMapper.selectOne(sysLogType);
                    log.setBusinessname(sysLogType.getName());
                }
                if(log.getEventtype() != null){
                    SysLogType sysLogType = new SysLogType();
                    sysLogType.setType(log.getEventtype());
                    sysLogType = sysLogTypeMapper.selectOne(sysLogType);
                    log.setEventname(sysLogType.getName());
                }
                if(log.getStatus()!=null){
                    if(log.getStatus()>0){
                        if(StringUtils.isBlank(log.getLoglevel())){
                            log.setLoglevel("info");
                        }
                        if(StringUtils.isBlank(log.getResult())) {
                            log.setResult("成功");
                        }
                    }else {
                        if(StringUtils.isBlank(log.getLoglevel())){
                            log.setLoglevel("error");
                        }
                        if(StringUtils.isBlank(log.getResult())) {
                            log.setResult("失败");
                            if(log.getCommand()!=null){
                                log.setResult("终端离线或网络异常造成的业务失败");
                            }
                        }
                    }
                }
                log.setOperatedate(DateTypeFormat.DateToStr(new Date()));
                if(log.getTenantId()!=null){
                    if(log.getTenantName()==null) {
                        SaasTenant saasTenant = saasTenantService.selectSaasTenantServcie(log.getTenantId());
                        if (saasTenant != null) {
                            log.setTenantName(saasTenant.getTenantName());
                        }
                    }
                }

                int i = terminalLogMapper.insertSelective(log);
                if(i>0){
                    // 写入sys_log
                    SysLog sysLog = new SysLog();
                    BeanUtils.copyProperties(log,sysLog);
                    sysLogMapper.insertSelective(sysLog);
                }
            }
        });
    }

    @Override
    public PageInfo getAllTerminalLog(String tenantId,String word, String eventType, BasePageEntity page) {
        Example ex = new Example(TerminalLog.class);
        Example.Criteria ec = ex.createCriteria();
        if(StringUtils.isNotBlank(eventType)){
            ec.andCondition(" (eventtype ='"+eventType+"' or businesstype = '"+eventType+"')");
        }
        if(StringUtils.isNotBlank(tenantId)){
            ec.andEqualTo("tenantId",tenantId);
        }
        if(StringUtils.isNotBlank(word)){
            ec.andCondition(" (tenant_name like '%"+word+"%' or terminal_name like '%"+word+"%' or content like '%"+word+"%' " +
                    "or eventname like '%"+word+"%' or businessname like '%"+word+"%')");
        }
        ec.andEqualTo("status","0");
        ex.setOrderByClause("businesstime desc");
        ex.selectProperties("id");
        ex.selectProperties("tenantName");
        ex.selectProperties("terminalName");
        ex.selectProperties("businessname");
        ex.selectProperties("eventname");
        ex.selectProperties("businesstime");
        ex.selectProperties("content");
        ex.selectProperties("result");
        if(page!=null){
            PageHelper.startPage(page.getCurrentPage(),page.getPageSize());
        }
        List<TerminalLog> terminalLogs = terminalLogMapper.selectByExample(ex);
        PageInfo<TerminalLog> pageInfo = new PageInfo<>(terminalLogs);
        if(terminalLogs!=null&&terminalLogs.size()>0){
            List<TerminalLogDto> dtos = new ArrayList<>();
            for (TerminalLog l:terminalLogs) {
                TerminalLogDto dto = new TerminalLogDto();
                dto.setId(l.getId());
                dto.setTenantName(l.getTenantName());
                dto.setTerminalName(l.getTerminalName());
                dto.setBusinessname(l.getBusinessname());
                dto.setEventname(l.getEventname());
                dto.setBusinesstime(l.getBusinesstime());
                dto.setContent(l.getContent());
                dto.setErrorLogContent(l.getResult());
                dtos.add(dto);
            }
            PageInfo<TerminalLogDto> info = new PageInfo<>(dtos);
            info.setTotal(pageInfo.getTotal());
            info.setPages(pageInfo.getPages());
            info.setPageNum(pageInfo.getPageNum());
            return info;
        }
        return null;
    }

    @Override
    public String getTerminalLogResult(String logId) {
        Example ex = new Example(TerminalLog.class);
        ex.createCriteria().andEqualTo("id",logId);
        ex.selectProperties("id");
        if(StringUtils.isNotBlank(logId)){
            List<TerminalLog> terminalLogs = terminalLogMapper.selectByExample(ex);
            if (terminalLogs!=null && terminalLogs.size()>0){
                return terminalLogs.get(0).getResult();
            }
        }
        return null;
    }

    @Override
    public void deleteTerminalLog(TerminalLog log) {
        executor.execute(() -> {
            terminalLogMapper.delete(log);
            SysLog sysLog = new SysLog();
            BeanUtils.copyProperties(log, sysLog);
            sysLogMapper.delete(sysLog);
        });
    }
}
