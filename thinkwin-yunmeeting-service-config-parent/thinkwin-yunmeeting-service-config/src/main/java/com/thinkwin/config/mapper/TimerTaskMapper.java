package com.thinkwin.config.mapper;

import com.thinkwin.common.model.core.SaasTimerLog;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface TimerTaskMapper extends Mapper<SaasTimerLog> {
	String getTaskId(@Param("serviceType")Integer serviceType
			,@Param("taskType")Integer taskType
			,@Param("serviceId")String serviceId);

	SaasTimerLog getLogByTaskId(@Param("taskId")String taskId);

	void updateLogByTaskId(@Param("log")SaasTimerLog log);
}
