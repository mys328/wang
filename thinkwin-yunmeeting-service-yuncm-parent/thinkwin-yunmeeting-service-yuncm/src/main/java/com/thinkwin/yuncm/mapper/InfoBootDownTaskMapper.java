package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.dto.publish.BootDownTaskDto;
import com.thinkwin.common.model.db.InfoBootDownTask;
import com.thinkwin.common.vo.publish.PlanCountVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface InfoBootDownTaskMapper extends Mapper<InfoBootDownTask> {
    /**
     * 获取计划开关机任务列表
     * @param condition
     * @return
     */
    List<BootDownTaskDto> getAllInfoBootDownTask(@Param("condition") String condition, @Param("state")String state, @Param("terminalId")String terminalId);

    /**
     * 获取终端在任务中的状态（占用/空闲）
     * @param terminalId
     * @return
     */
    Map getTerminalTaskState(@Param("terminalId") String terminalId);

    /**
     * 获取任务数量
     * @param condition
     * @return
     */
    List<String> getPlanCount(@Param("condition") String condition);
}