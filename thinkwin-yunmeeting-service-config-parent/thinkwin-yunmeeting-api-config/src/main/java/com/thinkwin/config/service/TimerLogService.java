package com.thinkwin.config.service;

import com.thinkwin.common.model.core.SaasTimerLog;

public interface TimerLogService {
	void insertLog(SaasTimerLog log);

//	void updateLogByTaskId(SaasTimerLog log);

	String getTaskId(Integer serviceType, Integer taskType, String serviceId);

	SaasTimerLog getLogByTaskId(String taskId);

	void deleteLogByTaskId(String taskId);
}
