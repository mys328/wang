package com.thinkwin.schedule.service.impl;

import com.thinkwin.common.model.core.SaasTimerLog;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.TimeUtil;
import com.thinkwin.config.service.TimerLogService;
import com.thinkwin.schedule.service.ServiceType;
import com.thinkwin.schedule.service.TimerService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.CronSchedule;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RScheduledFuture;
import org.redisson.api.RedissonClient;
import org.redisson.config.*;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RedissonTimerService implements TimerService {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(RedissonTimerService.class);

	private Config config;
	private EnumSet<ServiceType> serviceType;
	private RedissonClient redisson;
	private RScheduledExecutorService scheduleService;
	private String workerGroupName = "";
	private int wokersCount = 2;

	private RedissonNode node;

	private TimerLogService timerLogService;

	public void setTimerLogService(TimerLogService timerLogService) {
		this.timerLogService = timerLogService;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public EnumSet<ServiceType> getServiceType() {
		return serviceType;
	}

	public void setServiceType(EnumSet<ServiceType> serviceType) {
		this.serviceType = serviceType;
	}

	public String getWorkerGroupName() {
		return workerGroupName;
	}

	public void setWorkerGroupName(String workerGroupName) {
		this.workerGroupName = workerGroupName;
	}

	/**
	 * 创建定时服务
	 * @param config 配置选项, https://dzone.com/articles/distributed-tasks-execution-and-scheduling-in-java
	 * @param serviceType 选择服务角色, NODE可理解为redis节点, EXECUTOR可视为redis客户端, 可同时设置两种角色, 也可以分别部署，只要保证使用相同的redis配置
	 * @param workerGroupName 定时服务分组名称，要保证至少有一个NODE和一个EXECUTOR属于同一分组
	 */
	public RedissonTimerService(Config config, EnumSet<ServiceType> serviceType, String workerGroupName){
		newServer(config, serviceType, workerGroupName);
	}

	private void newServer(Config config, EnumSet<ServiceType> serviceType, String workerGroupName){
		if(CollectionUtils.isEmpty(serviceType)){
			throw new IllegalArgumentException("service type must not be empty");
		}

		this.serviceType = serviceType;
		this.config = config;
		this.workerGroupName = workerGroupName;
//		config.setCodec(new KryoCodec());

		if(this.serviceType.contains(ServiceType.NODE)){
			RedissonNodeConfig nodeConfig = new RedissonNodeConfig(config);
			nodeConfig.setExecutorServiceWorkers(Collections.singletonMap(this.workerGroupName, this.wokersCount));
			this.node = RedissonNode.create(nodeConfig);
			this.node.start();
		}

		if(this.serviceType.contains(ServiceType.EXECUTOR)){
			this.redisson = Redisson.create(config);
			this.scheduleService = redisson.getExecutorService(this.workerGroupName);
		}

	}

	public RedissonTimerService(SingleServerConfig config, EnumSet<ServiceType> serviceType, String workerGroupName){
		Config _config = new Config();
//		String addr = config.getAddress().getHost() + ":" + config.getAddress().getPort();
//		_config.useSingleServer().setAddress("tcp://" + addr);
		_config.useSingleServer().setAddress(config.getAddress().toString());
//		_config.useSingleServer().setAddress(config.getAddress().toString());
		if(StringUtils.isNotBlank(config.getPassword())){
			_config.useSingleServer().setPassword(config.getPassword());
		}
//		_config.useSingleServer().setConnectionPoolSize(config.getConnectionPoolSize());
		newServer(_config, serviceType, workerGroupName);
	}

	public RedissonTimerService(SentinelServersConfig config, EnumSet<ServiceType> serviceType, String workerGroupName){
		Config _config = new Config();
		SentinelServersConfig singleServerConfig = _config.useSentinelServers();
		config.getSentinelAddresses().forEach(e -> {
			String addr = e.getHost() + ":" + e.getPort();

			singleServerConfig.addSentinelAddress (addr);
		});
		newServer(_config, serviceType, workerGroupName);
	}

	public RedissonTimerService(MasterSlaveServersConfig config, EnumSet<ServiceType> serviceType, String workerGroupName){
		Config _config = new Config();
		MasterSlaveServersConfig masterSlaveServersConfig = _config.useMasterSlaveServers();
		String masterAddr = config.getMasterAddress().getHost() + ":" + config.getMasterAddress().getPort();
		masterSlaveServersConfig.setMasterAddress(masterAddr);
		config.getSlaveAddresses().forEach(e->{
			masterSlaveServersConfig.addSlaveAddress(e);
		});
		newServer(_config, serviceType, workerGroupName);
	}


	public void shutDownNode(){
		this.node.shutdown();
	}

	public void shutDownExecutor(){
		this.scheduleService.shutdown();
	}

	@Override
	public String schedule(Runnable task, Date time) {
		DateTime now = DateTime.now();
		if(time.before(now.toDate())){
			return null;
		}

		DateTime fireTime = new DateTime(time);
		//计算时间差
		long delay = Seconds.secondsBetween(now, fireTime).getSeconds();
		RScheduledFuture<?> future = this.scheduleService.schedule(task, delay, TimeUnit.SECONDS);

		return future.getTaskId();
	}

	@Override
	public String schedule(Runnable task, long delay, TimeUnit unit){
		RScheduledFuture<?> future = this.scheduleService.schedule(task, delay, unit);

		return future.getTaskId();
	}

	@Override
	public String schedule(Integer serviceType, Integer taskType
			, String serviceId, Runnable task, Date time) {
		DateTime now = DateTime.now();
		if(time.before(now.toDate())){
			return null;
		}

		DateTime fireTime = new DateTime(time);
		//计算时间差
		long delay = Seconds.secondsBetween(now, fireTime).getSeconds();
		RScheduledFuture<?> future = this.scheduleService.schedule(task, delay, TimeUnit.SECONDS);

		if(TimeUtil.getDateDiff(now.toDate(), time, TimeUnit.HOURS) >= 8){
			insertLog(serviceType, taskType, serviceId, future.getTaskId());
		}
		return future.getTaskId();
	}

	@Override
	public String schedule(Integer serviceType, Integer taskType
			, String serviceId, Runnable task, long delay, TimeUnit unit){
		RScheduledFuture<?> future = this.scheduleService.schedule(task, delay, unit);

		Date now = new Date();
		Date time = TimeUtil.addTimes(now, delay, unit);
		if(TimeUtil.getDateDiff(now, time, TimeUnit.HOURS) >= 8){
			insertLog(serviceType, taskType, serviceId, future.getTaskId());
		}
		return future.getTaskId();
	}

	@Override
	public String scheduleAtFixedRate(Integer serviceType, Integer taskType
			, String serviceId, Runnable task
			, long delay, long period, TimeUnit unit) {
		RScheduledFuture<?> future = this.scheduleService.scheduleAtFixedRate(task, delay, period, unit);
		insertLog(serviceType, taskType, serviceId, future.getTaskId());
		return future.getTaskId();
	}

	@Override
	public String schedule(Integer serviceType, Integer taskType
			, String serviceId, Runnable task, int hour, int minute) {
		RScheduledFuture<?> future = (RScheduledFuture)this.scheduleService.schedule(task, CronSchedule.dailyAtHourAndMinute(hour, minute));

		insertLog(serviceType, taskType, serviceId, future.getTaskId());

		return future.getTaskId();
	}

	@Override
	public String schedule(Integer serviceType, Integer taskType
			, String serviceId, Runnable task
			, int hour, int minute, Integer... daysOfWeek) {
		RScheduledFuture<?> future = (RScheduledFuture)this.scheduleService.schedule(task, CronSchedule.weeklyOnDayAndHourAndMinute(hour, minute, daysOfWeek));

		insertLog(serviceType, taskType, serviceId, future.getTaskId());

		return future.getTaskId();
	}

	@Override
	public String schedule(Integer serviceType, Integer taskType
			, String serviceId, Runnable task, String cronExpression) {
		CronSchedule cronSchedule = CronSchedule.of(cronExpression);
		RScheduledFuture<?> future = (RScheduledFuture)this.scheduleService.schedule(task, cronSchedule);
		insertLog(serviceType, taskType, serviceId, future.getTaskId());
		return future.getTaskId();
	}

	@Override
	public void cancelTask(Integer serviceType, Integer taskType, String serviceId) {
		String taskId = timerLogService.getTaskId(serviceType, taskType, serviceId);
		if(StringUtils.isBlank(taskId)){
			logger.error("定时: taskId 为空.");
			return;
		}

		deleteTimerLog(taskId);
		this.scheduleService.cancelScheduledTask(taskId);
	}

	@Override
	public void cancelTask(String taskId) {
//		deleteTimerLog(taskId);
		this.scheduleService.cancelScheduledTask(taskId);
	}

	private void deleteTimerLog(String taskId){
		if(null == timerLogService){
			throw new NullPointerException("定时: timerLogService 为空.");
		}

		timerLogService.deleteLogByTaskId(taskId);
	}

	private void insertLog(Integer serviceType, Integer taskType, String serviceId, String taskId){
//		SaasTimerLog saasTimerLog = new SaasTimerLog();
//		saasTimerLog.setId(CreateUUIdUtil.Uuid());
//		saasTimerLog.setTenantId(TenantContext.getTenantId());
//		saasTimerLog.setServiceType(serviceType);
//		saasTimerLog.setTaskType(taskType);
//		saasTimerLog.setServiceId(serviceId);
//		saasTimerLog.setTaskId(taskId);
//		saasTimerLog.setTaskStatus(0);
//		saasTimerLog.setCreateTime(new Date());
//		timerLogService.insertLog(saasTimerLog);
	}
}
