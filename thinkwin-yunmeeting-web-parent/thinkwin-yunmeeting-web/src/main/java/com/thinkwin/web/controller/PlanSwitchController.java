package com.thinkwin.web.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.publish.BootDownTaskDto;
import com.thinkwin.common.dto.publish.BootDownTaskPeriodDto;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.InfoBootDownTask;
import com.thinkwin.common.model.db.InfoTaskTerminalMiddle;
import com.thinkwin.common.model.publish.PlatformTenantTerminalMiddle;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.vo.publish.TerminalPlanSwitchVo;
import com.thinkwin.publish.service.PlatformTenantTerminalMiddleService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.InfoBootDownLogService;
import com.thinkwin.yuncm.service.InfoBootDownTaskService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * 计划开关机
 * User:wangxilei
 * Date:2018/5/3
 * Company:thinkwin
 */
@RestController
@RequestMapping(value = {"/planSwitch","/terminalClient"})
public class PlanSwitchController {

    @Resource
    private InfoBootDownTaskService infoBootDownTaskService;

    @Resource
    private InfoBootDownLogService infoBootDownLogService;

    @Resource
    private PlatformTenantTerminalMiddleService platformTenantTerminalMiddleService;

    /**
     * 获取计划开关机任务列表
     * @param word 搜索关键字
     * @param state 任务运行状态（1运行中0未启动，默认全部）
     * @return 任务列表
     */
    @RequestMapping(value = "getPlans",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseResult getPlans(String word, String state){
        Map<String,Object> map = new HashMap<>();
        List<BootDownTaskDto> tasks = infoBootDownTaskService.getAllInfoBootDownTask(word,state,null);
        map.put("plans",tasks);
        List<String> taskCount = infoBootDownTaskService.getTaskCount(word);
        int start = 0;
        int size = 0;
        if(taskCount!=null && taskCount.size()>0){
            size = taskCount.size();
            for (String s:taskCount){
                if(s!=null && s.equals("1")){
                    start+=1;
                }
            }
        }
        List<Integer> counts = new ArrayList<Integer>();
        counts.add(size);
        counts.add(start);
        counts.add(size-start);
        map.put("counts",counts);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
    }

    /**
     * 获取终端列表
     * @return 终端列表
     */
    @RequestMapping(value = "queryTerminals",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseResult queryTerminals(String taskId){
        Map<String,Object> map = new HashMap<>();
        List<TerminalPlanSwitchVo> terminals = infoBootDownTaskService.getAllTerminals(taskId);
        map.put("terminals",terminals);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
    }

    /**
     * 获取任务执行终端列表
     * @return 终端列表
     */
    @RequestMapping(value = "getRunTerminals",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseResult getRunTerminals(String taskId){
        Map<String,Object> map = new HashMap<>();
        List<TerminalPlanSwitchVo> terminals = infoBootDownTaskService.getPlanSwitchTerminals(taskId);
        map.put("terminals",terminals);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
    }

    /**
     * 创建开关机任务
     * @return boolean
     */
    @RequestMapping(value = "addPlan",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseResult addPlan(String taskName, String[] terminals, String periods, String ifOpenDown, String downStartTime, String downEndTime){
        if(StringUtils.isNotBlank(taskName)
                &&terminals!=null&&
            (StringUtils.isNotBlank(periods)||StringUtils.isNotBlank(ifOpenDown))){
            if(ifOpenDown.equals("1")){
                if(StringUtils.isBlank(downStartTime)||StringUtils.isBlank(downEndTime)){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
                }
            }

            List<BootDownTaskPeriodDto> bootDownTaskDtos = JSON.parseArray(periods, BootDownTaskPeriodDto.class);
            boolean b = infoBootDownTaskService.addPlan(taskName, terminals, bootDownTaskDtos, ifOpenDown, downStartTime, downEndTime);
            if(b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.Failure.getDescription(),BusinessExceptionStatusEnum.Failure.getCode());
            }
        }

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 获取任务详情
     * @return 任务详情
     */
    @RequestMapping(value = "getPlan",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseResult getPlan(String taskId){
        Map<String,Object> map = new HashMap<>();
        BootDownTaskDto plan = infoBootDownTaskService.getPlan(taskId);
        map.put("plan",plan);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
    }

    /**
     * 更新开关机任务
     * @return
     */
    @RequestMapping(value = "updatePlan",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseResult updatePlan(String taskId,String taskName,String[] terminals,String periods,
                                     String ifOpenDown,String downStartTime,String downEndTime,Integer option){
        if(StringUtils.isNotBlank(taskName)&&terminals.length>0&&
                (StringUtils.isNotBlank(periods)||StringUtils.isNotBlank(ifOpenDown))){
            if(ifOpenDown.equals("1")){
                if(StringUtils.isBlank(downStartTime)||StringUtils.isBlank(downEndTime)){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
                }
            }
            List<BootDownTaskPeriodDto> bootDownTaskDtos = JSON.parseArray(periods, BootDownTaskPeriodDto.class);
            Map<String, Object> map = infoBootDownTaskService.updatePlan(taskId, taskName, terminals, bootDownTaskDtos, ifOpenDown, downStartTime, downEndTime, option);
            boolean result = (boolean) map.get("result");
            return getResponseResult(map, result);
        }

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 启动
     * @param taskId
     * @return
     */
    @RequestMapping(value = "start",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseResult start(String taskId){
        if(StringUtils.isNotBlank(taskId)){
            Map<String, Object> map = infoBootDownTaskService.start(taskId);
            boolean result = (boolean) map.get("result");
            return getResponseResult(map, result);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 停止
     * @param taskId
     * @return
     */
    @RequestMapping(value = "stop",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseResult stop(String taskId){
        if(StringUtils.isNotBlank(taskId)){
            Map<String, Object> map = infoBootDownTaskService.stop(taskId);
            boolean result = (boolean) map.get("result");
            return getResponseResult(map, result);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 删除
     * @param taskId
     * @return
     */
    @RequestMapping(value = "delete",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseResult delete(String taskId,String status){
        if(StringUtils.isNotBlank(taskId)){
            Map<String, Object> map = infoBootDownTaskService.delete(taskId,status);
            boolean result = (boolean) map.get("result");
            return getResponseResult(map, result);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 重试
     * @param taskId
     * @param terminals
     * @return
     */
    @RequestMapping(value = "retry",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseResult retry(String taskId,String[] terminals){
        if(StringUtils.isNotBlank(taskId)&&terminals!=null){
            Map<String, Object> map = infoBootDownTaskService.retry(taskId, Arrays.asList(terminals));
            boolean result = (boolean) map.get("result");
            return getResponseResult(map, result);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    private ResponseResult getResponseResult(Map<String, Object> map, boolean result) {
        if(result){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),BusinessExceptionStatusEnum.Success.getCode());
        }else {
            Object errorTerminalIds = map.get("errorTerminalIds");
            Integer status = (Integer) map.get("status");
            String msg="",code="";
            if(status!=null) {
                if (status == -1) {
                    msg = BusinessExceptionStatusEnum.PlanSwitchAllFail.getDescription();
                    code = BusinessExceptionStatusEnum.PlanSwitchAllFail.getCode();
                } else if (status == -2) {
                    msg = BusinessExceptionStatusEnum.PlanSwitchPartFail.getDescription();
                    code = BusinessExceptionStatusEnum.PlanSwitchPartFail.getCode();
                } else if (status == -3) {
                    msg = BusinessExceptionStatusEnum.PlanSwitchRefresh.getDescription();
                    code = BusinessExceptionStatusEnum.PlanSwitchRefresh.getCode();
                } else if (status == -4) {
                    msg = BusinessExceptionStatusEnum.PlanSwitchDelete.getDescription();
                    code = BusinessExceptionStatusEnum.PlanSwitchRefresh.getCode();
                } else {
                    msg = BusinessExceptionStatusEnum.Failure.getDescription();
                    code = BusinessExceptionStatusEnum.Failure.getCode();
                }
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,msg,errorTerminalIds,code);
        }
    }

    /**
     * 查看日志
     * @param word 搜索关键字（任务名称/终端名称）
     * @param state 发送状态（1成功0失败，默认全部）
     * @param taskId 任务Id
     * @param startTime 搜索开始时间
     * @param endTime 搜索结束时间
     * @return
     */
    @RequestMapping(value = "getLogs",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseResult getLogs(BasePageEntity page, String word, String state, String taskId, String startTime, String endTime){
        if(StringUtils.isNotBlank(taskId)){
            if(StringUtils.isNotBlank(startTime)&&StringUtils.isNotBlank(endTime)){
//                if(startTime.equals(endTime)){
                    startTime = startTime+" 00:00:00";
                    endTime = endTime + " 23:59:59";
//                }
            }
            InfoBootDownTask taskDetail = infoBootDownTaskService.getTaskDetail(taskId);
            if(taskDetail!=null) {
                PageInfo info = infoBootDownLogService.getLogs(page, word, state, taskId, startTime, endTime);
                Map<String, Integer> counts = infoBootDownLogService.getLogsCount(page, word, state, taskId, startTime, endTime);
                Map<String, Object> map = new HashMap<>();
                map.put("counts", counts);
                if (info != null) {
                    map.put("logs", info.getList());
                    map.put("pages", info.getPages());
                    map.put("pageNum", info.getPageNum());
                    map.put("total", info.getTotal());
                } else {
                    map.put("logs", null);
                    map.put("pages", 0);
                    map.put("pageNum", null);
                    map.put("total", 0);
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.PlanSwitchRefresh.getDescription(),BusinessExceptionStatusEnum.PlanSwitchRefresh.getCode());
            }

        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 清除日志
     * @param taskId
     * @return
     */
    @RequestMapping(value = "clearLog",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseResult clearLog(String taskId){
        if(StringUtils.isNotBlank(taskId)){
            boolean b = infoBootDownLogService.clearLog(taskId);
            if(b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.Failure.getDescription(),BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 终端主动获取开关机任务列表
     * @param terminalId 终端Id
     * @return
     */
    @RequestMapping(value = "getTerminalPlan",method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseResult getTerminalPlan(String terminalId){
        if(StringUtils.isNotBlank(terminalId)) {
            PlatformTenantTerminalMiddle middle = new PlatformTenantTerminalMiddle();
            middle.setTerminalId(terminalId);
            List<PlatformTenantTerminalMiddle> middles = platformTenantTerminalMiddleService.selectPTenantTerminalMByEntity(middle);
            if(middles!=null && middles.size()>0){
                PlatformTenantTerminalMiddle middle1 = middles.get(0);
                TenantContext.setTenantId(middle1.getTenantId());
            }
            List<BootDownTaskDto> tasks = infoBootDownTaskService.getAllInfoBootDownTask(null, "1", terminalId);
            BootDownTaskDto dto = null;
            if (tasks != null && tasks.size() > 0) {
                dto = tasks.get(0);
                //运行状态置为成功
                String taskId = dto.getId();
                InfoTaskTerminalMiddle middle1 = new InfoTaskTerminalMiddle();
                middle1.setTerminalId(terminalId);
                middle1.setBootDownTaskId(taskId);
                infoBootDownTaskService.updateRunStatus(middle1);
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), dto);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }
}
