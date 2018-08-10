package com.thinkwin.yuncm.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.thinkwin.common.dto.publish.BootDownTaskDto;
import com.thinkwin.common.dto.publish.BootDownTaskPeriodDto;
import com.thinkwin.common.dto.publish.CommandMessage;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.model.log.TerminalLog;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.DateTypeFormat;
import com.thinkwin.common.utils.DateUtil;
import com.thinkwin.common.utils.TimeUtil;
import com.thinkwin.common.vo.publish.TerminalPlanSwitchVo;
import com.thinkwin.log.service.TerminalLogService;
import com.thinkwin.publish.service.PublishService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.localservice.LocalMeetingReserveService;
import com.thinkwin.yuncm.mapper.InfoBootDownPeriodMapper;
import com.thinkwin.yuncm.mapper.InfoBootDownTaskMapper;
import com.thinkwin.yuncm.mapper.InfoReleaseTerminalMapper;
import com.thinkwin.yuncm.mapper.InfoTaskTerminalMiddleMapper;
import com.thinkwin.yuncm.service.InfoBootDownLogService;
import com.thinkwin.yuncm.service.InfoBootDownTaskService;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User:wangxilei
 * Date:2018/5/7
 * Company:thinkwin
 */
@Service("infoBootDownTaskService")
public class InfoBootDownTaskServiceImpl implements InfoBootDownTaskService {
    @Autowired
    private InfoBootDownTaskMapper infoBootDownTaskMapper;
    @Autowired
    private InfoBootDownPeriodMapper infoBootDownPeriodMapper;
    @Autowired
    private InfoReleaseTerminalMapper infoReleaseTerminalMapper;
    @Autowired
    private InfoTaskTerminalMiddleMapper infoTaskTerminalMiddleMapper;
    @Autowired
    private LocalMeetingReserveService localMeetingReserveService;
    @Resource
    private PublishService publishService;
    @Autowired
    private InfoBootDownLogService infoBootDownLogService;
    @Autowired
    private TerminalLogService terminalLogService;

    @Override
    public List<BootDownTaskDto> getAllInfoBootDownTask(String condition,String state,String terminalId) {
        List<BootDownTaskDto> tasks = infoBootDownTaskMapper.getAllInfoBootDownTask(condition, state,terminalId);
        if(tasks!=null && tasks.size()>0){
            for (BootDownTaskDto task :tasks) {
                List<BootDownTaskPeriodDto> periods = this.getPeriodByTaskId(task.getId());
                task.setPeriods(periods);
            }
        }
        return tasks;
    }

    @Override
    public List<String> getTaskCount(String condition) {
        return infoBootDownTaskMapper.getPlanCount(condition);
    }

    @Override
    public List<BootDownTaskPeriodDto> getPeriodByTaskId(String taskId) {
        List<BootDownTaskPeriodDto> list = null;
        Example ex = new Example(InfoBootDownPeriod.class);
        ex.createCriteria().andEqualTo("bootDownTaskId",taskId);
        ex.setOrderByClause("LOCATE(weekly,'1,2,3,4,5,6,0')");
        List<InfoBootDownPeriod> periods = infoBootDownPeriodMapper.selectByExample(ex);
        if(periods!=null && periods.size()>0){
            list = new ArrayList<>();
            for (InfoBootDownPeriod p:periods) {
                BootDownTaskPeriodDto dto = new BootDownTaskPeriodDto();
                dto.setId(p.getId());
                dto.setWeekly(p.getWeekly());
                List<Map<String,Object>> mapList=new ArrayList<>();
                if(p.getAmStartTime()!=null&&p.getAmEndTime()!=null){
                    Map<String,Object> map = new HashMap<>();
                    map.put("startTime", TimeUtil.getTime(p.getAmStartTime()));
                    map.put("endTime",TimeUtil.getTime(p.getAmEndTime()));
                    mapList.add(map);
                }
                if(p.getPmStartTime()!=null&&p.getPmEndTime()!=null){
                    Map<String,Object> map = new HashMap<>();
                    map.put("startTime",TimeUtil.getTime(p.getPmStartTime()));
                    map.put("endTime",TimeUtil.getTime(p.getPmEndTime()));
                    mapList.add(map);
                }
                dto.setTimes(mapList);
                list.add(dto);
            }
        }
        return list;
    }

    @Override
    public List<TerminalPlanSwitchVo> getPlanSwitchTerminals(String taskId) {
        List<InfoReleaseTerminal> terminals = this.getPlanSwitchTerminalsInfo(taskId);

        List<TerminalPlanSwitchVo> list = null;
        if(terminals!=null && terminals.size()>0){
            list = new ArrayList<>();
            for(InfoReleaseTerminal terminal:terminals){
                TerminalPlanSwitchVo vo = new TerminalPlanSwitchVo();
                vo.setId(terminal.getId());
                vo.setTerminalName(terminal.getTerminalName());
                if(StringUtils.isNotBlank(terminal.getMeetingRoomId())){
                    YuncmMeetingRoom yuncmMeetingRoom = localMeetingReserveService.selectByidYuncmMeetingRoom(terminal.getMeetingRoomId());
                    if(yuncmMeetingRoom!=null) {
                        vo.setMeetingRoomName(yuncmMeetingRoom.getName());
                    }
                }
                String status = terminal.getStatus();
                InfoTaskTerminalMiddle middle = new InfoTaskTerminalMiddle();
                middle.setBootDownTaskId(taskId);
                middle.setTerminalId(terminal.getId());
                List<InfoTaskTerminalMiddle> middles = infoTaskTerminalMiddleMapper.select(middle);
                if(middles.size()>0){
                    InfoTaskTerminalMiddle terminalMiddle = middles.get(0);
                    if(StringUtils.isBlank(terminalMiddle.getRunStatus())){
                        if(StringUtils.isNotBlank(status)){
                            if(status.equals("0")){
                                vo.setState(-2);
                            }else {
                                vo.setState(Integer.valueOf(status));
                            }
                        }else{
                            vo.setState(-2);
                        }
                    }else {
                        if(StringUtils.isNotBlank(status)){
                            if(status.equals("0")&&!terminalMiddle.getRunStatus().equals("0")){
                                vo.setState(-2);
                            }else {
                                vo.setState(Integer.valueOf(terminalMiddle.getRunStatus()));
                            }
                        }else{
                            vo.setState(Integer.valueOf(terminalMiddle.getRunStatus()));
                        }
                    }
                }else{
                    vo.setState(-2);
                }

                Map map1 = this.getTerminalTaskState(terminal.getId());
                Long state = (Long) map1.get("taskState");
                if(state!=null && state >0){
                    String taskId1 = (String)map1.get("taskId");
                    vo.setTaskName((String) map1.get("taskName"));
                    vo.setTaskId(taskId1);
                    if(taskId1!=null && !taskId1.equals(taskId)) {
                        vo.setState(-1);
                    }
                }
                list.add(vo);
            }
        }
        return list;
    }

    @Override
    public List<TerminalPlanSwitchVo> getAllTerminals(String taskId) {
        List<InfoReleaseTerminal> terminals = this.getPlanSwitchTerminalsInfo(null);

        List<TerminalPlanSwitchVo> list = null;
        if(terminals!=null && terminals.size()>0){
            list = new ArrayList<>();
            for(InfoReleaseTerminal terminal:terminals){
                TerminalPlanSwitchVo vo = new TerminalPlanSwitchVo();
                vo.setId(terminal.getId());
                vo.setTerminalName(terminal.getTerminalName());
                if(StringUtils.isNotBlank(terminal.getMeetingRoomId())){
                    YuncmMeetingRoom yuncmMeetingRoom = localMeetingReserveService.selectByidYuncmMeetingRoom(terminal.getMeetingRoomId());
                    if(yuncmMeetingRoom!=null) {
                        vo.setMeetingRoomName(yuncmMeetingRoom.getName());
                    }
                }
                String status = terminal.getStatus();
                if(StringUtils.isNotBlank(status)){
                    if(status.equals("0")){
                        vo.setState(-2);
                    }else {
                        vo.setState(Integer.valueOf(status));
                    }
                }else{
                    vo.setState(-2);
                }

                Map map1 = this.getTerminalTaskState(terminal.getId());
                Long state = (Long) map1.get("taskState");
                if(state!=null && state >0){
                    String taskId1 = (String)map1.get("taskId");
                    vo.setTaskName((String) map1.get("taskName"));
                    vo.setTaskId(taskId1);
                    if(StringUtils.isBlank(taskId)){
                        if(status!=null && status.equals("0") && taskId1.equals(taskId)){
                            vo.setState(-2);
                        }else{
                            vo.setState(-1);
                        }
                    }else {
                        if(taskId1!=null && !taskId1.equals(taskId)) {
                            vo.setState(-1);
                        }
                    }
                }
                list.add(vo);
            }
        }
        return list;
    }

    @Override
    public List<InfoReleaseTerminal> getPlanSwitchTerminalsInfo(String taskId) {
        Example ex = new Example(InfoReleaseTerminal.class);
        if(StringUtils.isNotBlank(taskId)){
            Example example = new Example(InfoTaskTerminalMiddle.class);
            example.createCriteria().andEqualTo("bootDownTaskId",taskId);
            List<InfoTaskTerminalMiddle> middles = infoTaskTerminalMiddleMapper.selectByExample(example);
            List<String> collect = middles.stream().map(InfoTaskTerminalMiddle::getTerminalId).collect(Collectors.toList());
            if(collect!=null&&collect.size()>0) {
                ex.createCriteria().andIn("id", collect);
            }else {
                return null;
            }
        }
        ex.selectProperties("id");
        ex.selectProperties("terminalName");
        ex.selectProperties("meetingRoomId");
        ex.selectProperties("status");
        List<InfoReleaseTerminal> terminals = infoReleaseTerminalMapper.selectByExample(ex);
        return terminals;
    }

    @Override
    public Map getTerminalTaskState(String terminalId) {
        return infoBootDownTaskMapper.getTerminalTaskState(terminalId);
//        Map map = infoBootDownTaskMapper.getTerminalTaskState(terminalId);
//        Integer state = (Integer) map.get("taskState");
//        if(state!=null && state ==0){
//            return true;
//        }else {
//            return false;
//        }
    }

    @Transactional
    @Override
    public boolean addPlan(String taskName, String[] terminals, List<BootDownTaskPeriodDto> periods, String ifOpenDown, String downStartTime, String downEndTime) {
        InfoBootDownTask task = new InfoBootDownTask();
        String uuid = CreateUUIdUtil.Uuid();
        task.setId(uuid);
        task.setStatus("0");
        task.setCreatTime(new Date());
        task.setCreater(TenantContext.getTenantId());
        task.setTaskName(taskName);
        if(StringUtils.isNotBlank(ifOpenDown)) {
            task.setIfOpenDown(ifOpenDown);
        }
        if(StringUtils.isNotBlank(downStartTime)) {
            task.setDownStartTime(DateUtil.getDate(downStartTime));
        }
        if(StringUtils.isNotBlank(downEndTime)) {
            task.setDownEndTime(DateUtil.getDate(downEndTime));
        }
        int i = infoBootDownTaskMapper.insertSelective(task);
        if(i>0){
            //判断终端列表
            if(terminals!=null) {
                if (!processTerminals(uuid, terminals)) return false;
            }
            //判断运行时段
            if(periods!=null&& periods.size()>0) {
                if (!processPeriods(uuid, periods)) return false;
            }
            return true;
        }else {
            return false;
        }
    }

    @Override
    public BootDownTaskDto getPlan(String taskId) {
        InfoBootDownTask task = infoBootDownTaskMapper.selectByPrimaryKey(taskId);
        BootDownTaskDto dto=new BootDownTaskDto();
        if(task!=null){
            dto.setId(task.getId());
            dto.setTaskName(task.getTaskName());
            dto.setStatus(task.getStatus());
            dto.setIfOpenDown(task.getIfOpenDown());
            dto.setDownStartTime(task.getDownStartTime());
            dto.setDownEndTime(task.getDownEndTime());
            InfoTaskTerminalMiddle middle = new InfoTaskTerminalMiddle();
            middle.setBootDownTaskId(taskId);
            List<InfoTaskTerminalMiddle> middles = infoTaskTerminalMiddleMapper.select(middle);

            List<Map> list = new ArrayList<>();
            if(null!=middles&&middles.size()>0){
                for (InfoTaskTerminalMiddle m:middles){
                    Map<String,String> map = new HashMap<>();
                    map.put("id",m.getTerminalId());
                    map.put("terminalName",infoReleaseTerminalMapper.selectByPrimaryKey(m.getTerminalId()).getTerminalName());
                    list.add(map);
                }
            }
            dto.setTerminals(list);

            List<BootDownTaskPeriodDto> periods = this.getPeriodByTaskId(taskId);
            dto.setPeriods(periods);
        }
        return dto;
    }

    @Override
    public InfoBootDownTask getTaskDetail(String taskId) {
        return infoBootDownTaskMapper.selectByPrimaryKey(taskId);
    }

    @Transactional
    @Override
    public Map<String,Object> updatePlan(String taskId, String taskName, String[] terminals, List<BootDownTaskPeriodDto> periods, String ifOpenDown, String downStartTime, String downEndTime, Integer option) {
        InfoBootDownTask task = infoBootDownTaskMapper.selectByPrimaryKey(taskId);
        Map<String,Object> map = new HashMap<>();
        int status = 1;
        boolean result = true;
        List<String> errorTerminalIds=null; //(指令发送)异常终端名称列表

        //若修改任务为启动状态时发送修改指令
        boolean flag = true;
        if(option==1 && task!=null) {
            if (task.getStatus().equals("1")) {
                if (terminals.length > 0) {
                    errorTerminalIds = new ArrayList<>();
                    //校验任务下的终端是否有运行其他任务的，有则修改失败
                    for (String tid : terminals) {
                        Map map1 = this.getTerminalTaskState(tid);
                        Long state = (Long) map1.get("taskState");
                        if(state!=null && state >0){
                            String taskId1 = (String)map1.get("taskId");
                            if(taskId1!=null && !taskId1.equals(taskId)) {
                                flag = false;
                                InfoReleaseTerminal terminal = infoReleaseTerminalMapper.selectByPrimaryKey(tid);
                                if (terminal != null) {
                                    errorTerminalIds.add(terminal.getTerminalName() == null ? terminal.getHardwareId() : terminal.getTerminalName());
                                } else {
                                    errorTerminalIds.add(tid);
                                }
                            }
                        }
                    }
                    if(errorTerminalIds.size()>0){
//                        status=-2; //部分失败
//                        if(errorTerminalIds.size()==terminals.length){
//                            status=-1; //全部失败
//                        }
                        status=-1; //全部失败
                    }
                    if(!flag){
                        result = false;
                    }
                }
            }
        }
        if((task!=null&&option==1) || option==2) {
            if(option==2 && task==null){
                task = new InfoBootDownTask();
            }
            task.setTaskName(taskName);
            if (StringUtils.isNotBlank(ifOpenDown)) {
                task.setIfOpenDown(ifOpenDown);
            }
            if (StringUtils.isNotBlank(downStartTime)) {
                task.setDownStartTime(DateUtil.getDate(downStartTime));
            }
            if (StringUtils.isNotBlank(downEndTime)) {
                task.setDownEndTime(DateUtil.getDate(downEndTime));
            }
            int i = 0;
            if(option==1){ //更新
                task.setModifyTime(new Date());
                task.setModifier(TenantContext.getTenantId());
                if(StringUtils.isNotBlank(ifOpenDown)){
                    if(ifOpenDown.equals("0")){
                        task.setDownStartTime(null);
                        task.setDownEndTime(null);
                    }
                }
                i = infoBootDownTaskMapper.updateByPrimaryKey(task);
            }
            if(option==2){  //另存新任务
                taskId = CreateUUIdUtil.Uuid();
                task.setId(taskId);
                task.setStatus("0");
                task.setCreatTime(new Date());
                task.setCreater(TenantContext.getTenantId());
                i = infoBootDownTaskMapper.insertSelective(task);
            }
            if (i > 0) {
                //判断终端列表
                InfoTaskTerminalMiddle middle = new InfoTaskTerminalMiddle();
                middle.setBootDownTaskId(taskId);
                List<InfoTaskTerminalMiddle> select = infoTaskTerminalMiddleMapper.select(middle);

                if (terminals.length > 0) {
                    if(option==1&&result) {
                        InfoTaskTerminalMiddle tm = new InfoTaskTerminalMiddle();
                        tm.setBootDownTaskId(taskId);
                        List<InfoTaskTerminalMiddle> terminalMiddles = infoTaskTerminalMiddleMapper.select(tm);
                        if(terminalMiddles!=null && terminalMiddles.size()>0){
                            List<String> old = terminalMiddles.stream().map(InfoTaskTerminalMiddle::getTerminalId).collect(Collectors.toList());

                            List<String> newT = Arrays.asList(terminals);
                            //需要删除的
                            for (String terminalId:old){
                                if (!newT.contains(terminalId)) {
                                    InfoTaskTerminalMiddle m = new InfoTaskTerminalMiddle();
                                    m.setTerminalId(terminalId);
                                    m.setBootDownTaskId(taskId);
                                    infoTaskTerminalMiddleMapper.delete(m);
                                }
                            }

                            //需要添加的
                            for (String terminalId:newT) {
                                if (!old.contains(terminalId)) {
                                    InfoTaskTerminalMiddle m = new InfoTaskTerminalMiddle();
                                    m.setId(CreateUUIdUtil.Uuid());
                                    m.setTerminalId(terminalId);
                                    m.setBootDownTaskId(taskId);
                                    infoTaskTerminalMiddleMapper.insertSelective(m);
                                }
                            }
                        }
                    }
                    if (result && option==2) {
                        processTerminals(taskId, terminals);
                    }
                }
                //判断运行时段
                if (periods!=null && periods.size()>0) {
                    if (option == 1) {
                        if (task.getStatus().equals("1")) {
                            if (result) {
                                InfoBootDownPeriod bootDownPeriod = new InfoBootDownPeriod();
                                bootDownPeriod.setBootDownTaskId(taskId);
                                infoBootDownPeriodMapper.delete(bootDownPeriod);
                            }
                        }else {
                            InfoBootDownPeriod bootDownPeriod = new InfoBootDownPeriod();
                            bootDownPeriod.setBootDownTaskId(taskId);
                            infoBootDownPeriodMapper.delete(bootDownPeriod);
                        }
                    }
                    if (result){
                        processPeriods(taskId, periods);
                    }
                }else{
                    if (option == 1&result) {
                        InfoBootDownPeriod bootDownPeriod = new InfoBootDownPeriod();
                        bootDownPeriod.setBootDownTaskId(taskId);
                        infoBootDownPeriodMapper.delete(bootDownPeriod);
                    }
                }
                if(option==1) {
                    if (task.getStatus().equals("1")) {
                        if (terminals.length > 0) {
                            if (result) {
                                if(select!=null&&select.size()>0){
                                    //判断需要发送停止指令的终端
                                    List<String> collect = select.stream().map(InfoTaskTerminalMiddle::getTerminalId).collect(Collectors.toList());
                                    if(collect!=null&&collect.size()>0){
                                        List<String> asList = Arrays.asList(terminals);
                                        List<String> deleteList = new ArrayList<>();
                                        for (String tid:collect){
                                            if (!asList.contains(tid)){
                                                deleteList.add(tid);
                                            }
                                        }
                                        if(deleteList.size()>0) {
                                            List<String> sendList = new ArrayList<>();
                                            errorTerminalIds = new ArrayList<>();
                                            updateStateAndPush(taskId, task.getTaskName(), "20014", errorTerminalIds, deleteList, sendList);
                                        }
                                    }
                                }
                                List<String> sendList = new ArrayList<>();
                                errorTerminalIds = new ArrayList<>();
                                updateStateAndPush(taskId, task.getTaskName(), "20013", errorTerminalIds, Arrays.asList(terminals), sendList);
                            }
                        }
                    }
                }
            } else {
                result = false;
            }
        }else {
            result = false;
            status = -4;//操作冲突，刷新状态
        }
        map.put("status",status);
        map.put("result",result);
        map.put("errorTerminalIds",errorTerminalIds);
        return map;
    }

    private boolean processPeriods(String taskId, List<BootDownTaskPeriodDto> periods) {
        boolean result = true;
        for (BootDownTaskPeriodDto t : periods) {
            if (t != null) {
                InfoBootDownPeriod period = new InfoBootDownPeriod();
                period.setId(CreateUUIdUtil.Uuid());
                period.setBootDownTaskId(taskId);
                period.setWeekly(t.getWeekly());
                if (t.getTimes() != null && t.getTimes().size() > 0) {
                    if (t.getTimes().size() == 1) {
                        period.setAmStartTime(DateUtil.getDate((String) t.getTimes().get(0).get("startTime"),DateUtil.TYPE_TIME));
                        period.setAmEndTime(DateUtil.getDate((String) t.getTimes().get(0).get("endTime"),DateUtil.TYPE_TIME));
                    }
                    if (t.getTimes().size() == 2) {
                        period.setAmStartTime(DateUtil.getDate((String) t.getTimes().get(0).get("startTime"),DateUtil.TYPE_TIME));
                        period.setAmEndTime(DateUtil.getDate((String) t.getTimes().get(0).get("endTime"),DateUtil.TYPE_TIME));
                        period.setPmStartTime(DateUtil.getDate((String) t.getTimes().get(1).get("startTime"),DateUtil.TYPE_TIME));
                        period.setPmEndTime(DateUtil.getDate((String) t.getTimes().get(1).get("endTime"),DateUtil.TYPE_TIME));
                    }
                }
                int i1 = infoBootDownPeriodMapper.insertSelective(period);
                if (i1 <= 0) {
                    result = false;
                }
            }
        }
        return result;
    }

    private boolean processTerminals(String taskId, String[] terminals) {
        boolean result = true;
        for (String t : terminals) {
            InfoTaskTerminalMiddle middle = new InfoTaskTerminalMiddle();
            middle.setId(CreateUUIdUtil.Uuid());
            middle.setBootDownTaskId(taskId);
            middle.setTerminalId(t);

//            middle.setRunStatus("1");
            int i1 = infoTaskTerminalMiddleMapper.insertSelective(middle);
            if (i1 <= 0) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public Map<String,Object> start(String taskId) {
        Map<String,Object> map = new HashMap<>();
        boolean result = false;
        int status = 1;
        List<String> errorTerminalIds=null; //(占用/指令发送)异常终端名称列表
        InfoBootDownTask task = infoBootDownTaskMapper.selectByPrimaryKey(taskId);
        if(task!=null) {
            if(task.getStatus().equals("0")) { //未启动状态时
                errorTerminalIds = new ArrayList<>();
                boolean flag = true;
                //校验任务下的终端是否有运行其他任务的，有则启动失败
                List<InfoReleaseTerminal> terminals = this.getPlanSwitchTerminalsInfo(taskId);
                for (InfoReleaseTerminal terminal : terminals) {
                    Map map1 = this.getTerminalTaskState(terminal.getId());
                    Long state = (Long) map1.get("taskState");
                    if(state!=null && state >0){
                        flag = false;
                        errorTerminalIds.add(terminal.getTerminalName());
                    }
                }
                if(errorTerminalIds.size()>0){
//                    status=-2; //部分失败
//                    if(errorTerminalIds.size()==terminals.size()){
//                        status=-1; //全部失败
//                    }
                    status=-1; //全部失败
                }
                if (flag) {
                    task.setStatus("1");
                    int i = infoBootDownTaskMapper.updateByPrimaryKeySelective(task);
                    if (i > 0) {
                        List<String> collect = terminals.stream().map(InfoReleaseTerminal::getId).collect(Collectors.toList());
                        //处理发送异常终端
                        List<String> sendList = new ArrayList<>();
                        errorTerminalIds = new ArrayList<>();
                        updateStateAndPush(taskId,task.getTaskName(),"20013", errorTerminalIds, collect, sendList);
                        result = true;
                    }
                }else {
                    result = false;
                }
            }else {
                status = -3;//操作冲突，刷新状态
            }
        }else {
            status = -3;//操作冲突，刷新状态
        }
        map.put("status",status);
        map.put("result",result);
        map.put("errorTerminalIds",errorTerminalIds);
        return map;
    }


    @Override
    public Map<String, Object> stop(String taskId) {
        Map<String,Object> map = new HashMap<>();
        boolean result = false;
        int status = 1;
        List<String> errorTerminalIds=null; //指令发送异常终端名称列表
        InfoBootDownTask task = infoBootDownTaskMapper.selectByPrimaryKey(taskId);
        if(task!=null) {
            if(task.getStatus().equals("1")) { //启动状态时
                errorTerminalIds = new ArrayList<>();
                task.setStatus("0");
                int i = infoBootDownTaskMapper.updateByPrimaryKeySelective(task);
                if (i > 0) {
                    List<InfoReleaseTerminal> terminals = this.getPlanSwitchTerminalsInfo(taskId);
                    List<String> collect = terminals.stream().map(InfoReleaseTerminal::getId).collect(Collectors.toList());
                    //处理发送异常终端
                    List<String> sendList = new ArrayList<>();
                    updateStateAndPush(taskId,task.getTaskName(),"20014", errorTerminalIds, collect, sendList);
                }
            }else {
                status = -3;//操作冲突，刷新状态
            }
        }else {
            status = -3;//操作冲突，刷新状态
        }
        if(status>0){
            result = true;
        }
        map.put("status",status);
        map.put("result",result);
        map.put("errorTerminalIds",errorTerminalIds);
        return map;
    }

    @Override
    public Map<String, Object> retry(String taskId, List<String> terminals) {
        Map<String, Object> map = new HashMap<>();
        boolean result = false;
        int status = 1;
        List<String> errorTerminalIds=null; //指令发送异常终端名称列表
        if (StringUtils.isNotBlank(taskId)&&terminals.size()>0) {
            InfoBootDownTask task = infoBootDownTaskMapper.selectByPrimaryKey(taskId);
            if(task!=null) {
                errorTerminalIds = new ArrayList<>();
                for (String id : terminals) {
                    InfoTaskTerminalMiddle middle = new InfoTaskTerminalMiddle();
                    middle.setBootDownTaskId(taskId);
                    middle.setTerminalId(id);
                    InfoTaskTerminalMiddle one = infoTaskTerminalMiddleMapper.selectOne(middle);
                    if (null != one) {
                    if (null == one.getRunStatus() || one.getRunStatus().equals("0")) {
                        List<String> sendList = new ArrayList<>();
                        InfoReleaseTerminal terminal = infoReleaseTerminalMapper.selectByPrimaryKey(id);
                        //如果该终端在停止任务中需要重试 且 在其他任务中处于运行状态，
                        // 则点击该重试 不发送指令，只刷新该终端状态为成功
                        String code = "20013";
                        if (task.getStatus() != null && task.getStatus().equals("0")) {
                            code = "20014";
                            Map map1 = this.getTerminalTaskState(terminal.getId());
                            Long state = (Long) map1.get("taskState");
                            if (state != null && state > 0) {
                                one.setRunStatus("1");
                                infoTaskTerminalMiddleMapper.updateByPrimaryKeySelective(one);
                                insertLog(taskId, id, task.getTaskName(), code, "1");
                                continue;
                            }
                        }
                        //判断占用终端
                        Map map1 = this.getTerminalTaskState(terminal.getId());
                        Long state = (Long) map1.get("taskState");
                        if (state != null && state > 0) {
                            String taskId1 = (String) map1.get("taskId");
                            if (taskId1 != null && !taskId1.equals(taskId)) {
                                errorTerminalIds.add(terminal.getTerminalName() == null ? id : terminal.getTerminalName());
                                status = -1; //全部失败
                            }
                        }

                        if (status != -1) {
                            //判断离线终端
                            if (terminal == null || terminal.getStatus() == null || terminal.getStatus().equals("0")) {
                                errorTerminalIds.add(terminal.getTerminalName() == null ? id : terminal.getTerminalName());
                                status = -2; //部分失败
                            } else {
                                sendList.add(id);
                            }
                            String ss = "0";
                            if (terminal.getStatus() != null) {
                                ss = terminal.getStatus();
                            }
                            one.setRunStatus(ss);
                            infoTaskTerminalMiddleMapper.updateByPrimaryKeySelective(one);
                            //写入日志
                            insertLog(taskId, id, task.getTaskName(), code, ss);
                            //发送重试指令
                            pushCommand(taskId, sendList, code);
                        }
                    }
                }else{
                        status = -3;
                    }
                }
//                if (errorTerminalIds.size() > 0) {
//                    status = -2; //部分失败
//                    if (errorTerminalIds.size() == terminals.size()) {
//                        status = -1; //全部失败
//                    }
//                }
            }else {
                status = -3;//操作冲突，刷新状态
            }
        }
        if(status>0){
            result = true;
        }
        map.put("status",status);
        map.put("result",result);
        map.put("errorTerminalIds",errorTerminalIds);
        return map;
    }

    @Override
    public Map<String, Object> delete(String taskId,String statusOld) {
        Map<String, Object> map = new HashMap<>();
        boolean result = true;
        int status = 1;
        List<String> errorTerminalIds=null; //指令发送异常终端名称列表
        InfoBootDownTask task = infoBootDownTaskMapper.selectByPrimaryKey(taskId);
        if(task!=null){
            if(task.getStatus().equals(statusOld)) {
                int i = infoBootDownTaskMapper.deleteByPrimaryKey(task);
                if (i > 0) {
                    List<InfoReleaseTerminal> terminals = this.getPlanSwitchTerminalsInfo(taskId);
                    List<String> collect = terminals.stream().map(InfoReleaseTerminal::getId).collect(Collectors.toList());
                    //处理发送异常终端
                    List<String> sendList = new ArrayList<>();
                    errorTerminalIds = new ArrayList<>();
                    updateStateAndPush(taskId, task.getTaskName(), "20014", errorTerminalIds, collect, sendList);

                    //级联删除终端
                    InfoTaskTerminalMiddle middle = new InfoTaskTerminalMiddle();
                    middle.setBootDownTaskId(taskId);
                    int delete = infoTaskTerminalMiddleMapper.delete(middle);
                    if (delete < 0) {
                        result = false;
                    }
                    //级联删除运行时段
                    InfoBootDownPeriod period = new InfoBootDownPeriod();
                    period.setBootDownTaskId(taskId);
                    infoBootDownPeriodMapper.delete(period);
                    //级联删除日志
                    infoBootDownLogService.clearLog(taskId);
                } else {
                    result = false;
                }
            }else {
                result = false;
                status = -3;//操作冲突，刷新状态
            }
        }else {
            result = false;
            status = -3;//操作冲突，刷新状态
        }
        map.put("status",status);
        map.put("result",result);
        map.put("errorTerminalIds",errorTerminalIds);
        return map;
    }

    @Override
    public boolean updateRunStatus(InfoTaskTerminalMiddle middle) {
        if(middle!=null){
            List<InfoTaskTerminalMiddle> middles = infoTaskTerminalMiddleMapper.select(middle);
            if(middles!=null && middles.size()>0){
                InfoTaskTerminalMiddle middle1 = middles.get(0);
                middle1.setRunStatus("1");
                int i = infoTaskTerminalMiddleMapper.updateByPrimaryKeySelective(middle1);
                if(i>0){
                    return true;
                }
            }
        }
        return false;
    }

    //插入日志
    private void insertLog(String taskId, Object terminals, String taskName, String cmd,String runStatus) {
        if (terminals != null) {
            if(terminals instanceof List) {
                List<InfoReleaseTerminal> t = (List<InfoReleaseTerminal>) terminals;
                for (InfoReleaseTerminal terminal : t) {
                    InfoBootDownLog log = new InfoBootDownLog();
                    log.setBootDownTaskId(taskId);
                    log.setCmdCode(cmd);
                    log.setReleaseTerminal(terminal.getId());
                    String o = "启动";
                    if (cmd.equals("20014")) {
                        o = "停止";
                    }
                    taskName = "开关机";
//                    log.setCmdContent("向终端\"" + terminal.getTerminalName() + "\"发送" + o +taskName+"指令");
                    log.setCmdContent("向终端\"" + terminal.getTerminalName() + "\"发送" + o +taskName+"指令");
                    log.setRunStatus(runStatus);
                    log.setTerminalName(terminal.getTerminalName());
                    log.setRunTime(new Date());
                    infoBootDownLogService.insertLog(log);
                    insertTerminalLog(cmd, runStatus, terminal.getId());
                }
            }
            if(terminals instanceof String[]) {
                String[] ids = (String[]) terminals;
                for (String id :ids){
                    InfoBootDownLog log = new InfoBootDownLog();
                    log.setBootDownTaskId(taskId);
                    log.setCmdCode(cmd);
                    log.setReleaseTerminal(id);
                    String o = "启动";
                    if (cmd.equals("20014")) {
                        o = "停止";
                    }
                    Example ex =  new Example(InfoReleaseTerminal.class);
                    ex.createCriteria().andEqualTo("id",id);
                    ex.selectProperties("terminalName");
                    List<InfoReleaseTerminal> t = infoReleaseTerminalMapper.selectByExample(ex);
                    String name ="";
                    if(t!=null){
                        name = t.get(0).getTerminalName();
                    }
                    taskName = "开关机";
                    log.setCmdContent("向终端\"" + name + "\"发送" + o +taskName+"指令");
                    log.setRunStatus(runStatus);
                    log.setTerminalName(name);
                    log.setRunTime(new Date());
                    infoBootDownLogService.insertLog(log);
                    insertTerminalLog(cmd, runStatus, id);
                }
            }
            if(terminals instanceof String) {
                String id = (String) terminals;
                InfoBootDownLog log = new InfoBootDownLog();
                log.setBootDownTaskId(taskId);
                log.setCmdCode(cmd);
                log.setReleaseTerminal(id);
                String o = "启动";
                if (cmd.equals("20014")) {
                    o = "停止";
                }
                Example ex =  new Example(InfoReleaseTerminal.class);
                ex.createCriteria().andEqualTo("id",id);
                ex.selectProperties("terminalName");
                List<InfoReleaseTerminal> t = infoReleaseTerminalMapper.selectByExample(ex);
                String name ="";
                if(t!=null&&t.size()>0){
                    name = t.get(0).getTerminalName();
                }
                taskName = "开关机";
                log.setCmdContent("向终端\"" + name + "\"发送" + o +taskName+"指令");
                log.setRunStatus(runStatus);
                log.setTerminalName(name);
                log.setRunTime(new Date());
                infoBootDownLogService.insertLog(log);
                insertTerminalLog(cmd, runStatus, id);
            }
        }
    }

    //记录终端日志
    private void insertTerminalLog(String cmd, String runStatus, String id) {
        if (runStatus.equals("0")) {
            TerminalLog terminalLog = new TerminalLog();
            terminalLog.setTenantId(TenantContext.getTenantId());
            terminalLog.setTerminalId(id);
            terminalLog.setMethodname(Thread.currentThread().getStackTrace()[1].getMethodName());
            terminalLog.setClassname(this.getClass().getName());
            terminalLog.setBusinesstime(new Date());
            terminalLog.setCommand(cmd);
            terminalLog.setStatus(0);
            terminalLogService.addTerminalLog(terminalLog);
        }
    }

    //更新关联表状态,插入日志,推送指令
    private void updateStateAndPush(String taskId,String taskName,String code, List<String> errorTerminalIds, List<String> collect, List<String> sendList) {
        if(collect!=null&&collect.size()>0) {
            for (String tid : collect) {
                String ss = "0";
                InfoReleaseTerminal terminal = infoReleaseTerminalMapper.selectByPrimaryKey(tid);
                if (terminal == null || terminal.getStatus() == null || terminal.getStatus().equals("0")) {
                    errorTerminalIds.add(terminal.getTerminalName()==null?tid:terminal.getTerminalName());
                } else {
                    ss = "1";
                    sendList.add(tid);
                }
                //更新关联表发送执行状态
                InfoTaskTerminalMiddle middle = new InfoTaskTerminalMiddle();
                middle.setBootDownTaskId(taskId);
                middle.setTerminalId(tid);
                InfoTaskTerminalMiddle one = infoTaskTerminalMiddleMapper.selectOne(middle);
                if(null!=one) {
                    if(terminal.getStatus()!=null){
                        ss = terminal.getStatus();
                    }
                    one.setRunStatus(ss);
                    infoTaskTerminalMiddleMapper.updateByPrimaryKeySelective(one);
                }
                // 写入日志
                insertLog(taskId, tid, taskName, code,ss);
            }
            pushCommand(taskId, sendList, code);
        }
    }

    //发送指令
    private void pushCommand(String taskId, List<String> sendList, String code) {
        if(sendList.size()>0) {
            CommandMessage cmd = new CommandMessage();
            cmd.setCmd(code);
            cmd.setTenantId(TenantContext.getTenantId());
            cmd.setTerminals(sendList);
            cmd.setData(this.getPlan(taskId));
            cmd.setTimestamp(new Date().getTime());
            cmd.setRequestId(CreateUUIdUtil.Uuid());
            publishService.pushCommand(cmd);
        }
    }
}
