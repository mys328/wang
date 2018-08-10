package com.thinkwin.orders.service;

import com.thinkwin.common.ContextHolder;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.schedule.service.TimerService;

import java.util.concurrent.TimeUnit;

public class UpdateTenantOrderJob implements Runnable {
	private String tenantId;
	private String orderId;

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	private Integer retryTimes = 1;

	public Integer getRetryTimes() {
		return retryTimes;
	}

	@Override
	public void run() {
		SaasTenantService saasTenantService = (SaasTenantService) ContextHolder.getApplicationContext().getBean("saasTenantService");
		boolean success = saasTenantService.updateTenantOrder(tenantId, orderId);

		if(!success){
			if(retryTimes > 5){
				return;
			}

			retryTimes++;
			TimerService timerService = (TimerService) ContextHolder.getApplicationContext().getBean("timerService");
			timerService.schedule(this, retryTimes, TimeUnit.SECONDS);
		}
	}
}
