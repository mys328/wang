package com.thinkwin.yuncm.service;

import com.thinkwin.common.dto.publish.BootDownTaskDto;
import com.thinkwin.common.dto.publish.BootDownTaskPeriodDto;
import com.thinkwin.common.model.db.InfoBootDownTask;
import com.thinkwin.common.model.db.InfoReleaseTerminal;
import com.thinkwin.common.model.db.InfoTaskTerminalMiddle;
import com.thinkwin.common.vo.publish.TerminalPlanSwitchVo;

import java.util.List;
import java.util.Map;

/**
 * 开关机任务
 * User:wangxilei
 * Date:2018/5/7
 * Company:thinkwin
 */
public interface InfoBootDownTaskService {
    /**
     * 获取计划开关机列表
     * @param condition 搜索条件
     * @return
     */
    List<BootDownTaskDto> getAllInfoBootDownTask(String condition, String state,String terminalId);

    /**
     * 获取任务数量（全部、启动中、未启动）
     * @return
     */
    List<String> getTaskCount(String condition);

    /**
     * 获取任务运行时段
     * @param taskId
     * @return
     */
    List<BootDownTaskPeriodDto> getPeriodByTaskId(String taskId);

    /**
     * 获取执行终端集合
     * @param taskId
     * @return
     */
    List<TerminalPlanSwitchVo> getPlanSwitchTerminals(String taskId);

    /**
     * 获取所有终端集合
     * @param taskId
     * @return
     */
    List<TerminalPlanSwitchVo> getAllTerminals(String taskId);

    /**
     * 获取终端对象集合
     * @param taskId
     * @return
     */
    List<InfoReleaseTerminal> getPlanSwitchTerminalsInfo(String taskId);

    /**
     * 获取终端在任务中的状态（占用/空闲）
     * @param terminalId
     * @return
     */
    Map getTerminalTaskState(String terminalId);

    /**
     * 创建开关机任务
     * @param taskName 任务名称
     * @param terminals 终端列表
     * @param periods  运行时段列表
     * @param ifOpenDown  是否开启特别关机时段(1是0否)
     * @param downStartTime 特别关机开始时间
     * @param downEndTime  特别关机结束时间
     * @return
     */
    boolean addPlan(String taskName,String[] terminals,List<BootDownTaskPeriodDto> periods,String ifOpenDown,String downStartTime,String downEndTime);

    /**
     * 获取开关机任务详情
     * @param taskId
     * @return
     */
    BootDownTaskDto getPlan(String taskId);

    /**
     * 获取任务详情
     * @param taskId
     * @return
     */
    InfoBootDownTask getTaskDetail(String taskId);

    /**
     * 修改开关机任务
     * @param taskId 任务Id
     * @param taskName 任务名称
     * @param terminals 终端列表
     * @param periods  运行时段列表
     * @param ifOpenDown  是否开启特别关机时段(1是0否)
     * @param downStartTime 特别关机开始时间
     * @param downEndTime  特别关机结束时间
     * @param option 提交类型（1保存2另存新任务）
     * @return
     */
    Map<String,Object> updatePlan(String taskId,String taskName,String[] terminals,List<BootDownTaskPeriodDto> periods,String ifOpenDown,String downStartTime,String downEndTime,Integer option);

    /**
     * 启动
     * @param taskId
     * @return
     */
    Map<String,Object> start(String taskId);

    /**
     * 停止
     * @param taskId
     * @return
     */
    Map<String,Object> stop(String taskId);

    /**
     * 重试
     * @param taskId
     * @param terminals
     * @return
     */
    Map<String,Object> retry(String taskId,List<String> terminals);

    /**
     * 删除
     * @param taskId
     * @return
     */
    Map<String,Object> delete(String taskId,String status);

    /**
     * 更新任务运行状态
     * @return
     */
    boolean updateRunStatus(InfoTaskTerminalMiddle middle);
}
