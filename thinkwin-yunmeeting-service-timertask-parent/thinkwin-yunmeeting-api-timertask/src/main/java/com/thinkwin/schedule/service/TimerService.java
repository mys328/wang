package com.thinkwin.schedule.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface TimerService {
	public String schedule(Runnable task, Date time);

	public String schedule(Runnable task, long delay, TimeUnit unit);

	public String schedule(Integer serviceType
			, Integer taskType
			, String serviceId, Runnable task, Date time);

	public String schedule(Integer serviceType
			, Integer taskType
			, String serviceId
			, Runnable task, long delay, TimeUnit unit);

	public String scheduleAtFixedRate(Integer serviceType
			, Integer taskType
			, String serviceId
			, Runnable task
			, long delay, long period, TimeUnit unit);

	public String schedule(Integer serviceType, Integer taskType
			, String serviceId, Runnable task
			, int hour, int minute);

	public String schedule(Integer serviceType, Integer taskType
			, String serviceId, Runnable task
			, int hour, int minute, Integer... daysOfWeek);

	public String schedule(Integer serviceType, Integer taskType
			, String serviceId
			, Runnable task, String cronExpression);

	public void cancelTask(Integer serviceType, Integer taskType, String serviceId);

	public void cancelTask(String taskId);
}
