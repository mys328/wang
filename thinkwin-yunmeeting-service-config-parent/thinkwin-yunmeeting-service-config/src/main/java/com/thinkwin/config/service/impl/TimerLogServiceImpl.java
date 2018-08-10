package com.thinkwin.config.service.impl;

import com.thinkwin.common.model.core.SaasTimerLog;
import com.thinkwin.config.mapper.TimerTaskMapper;
import com.thinkwin.config.service.TimerLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service(value = "timerLogService")
public class TimerLogServiceImpl implements TimerLogService {

	@Autowired
	TimerTaskMapper timerTaskMapper;

	@Override
	public void insertLog(SaasTimerLog log) {
		timerTaskMapper.insert(log);
	}

	public void updateLogByTaskId(SaasTimerLog log) {
		timerTaskMapper.updateLogByTaskId(log);
	}

	@Override
	public String getTaskId(Integer serviceType, Integer taskType, String serviceId) {
		String taskId = timerTaskMapper.getTaskId(serviceType, taskType, serviceId);
		return taskId;
	}

	@Override
	public SaasTimerLog getLogByTaskId(String taskId) {
		return timerTaskMapper.getLogByTaskId(taskId);
	}

	@Override
	public void deleteLogByTaskId(String taskId) {
		Example example = new Example(SaasTimerLog.class);
		example.createCriteria()
				.andEqualTo("taskId", taskId);
		timerTaskMapper.deleteByExample(example);
	}
}
