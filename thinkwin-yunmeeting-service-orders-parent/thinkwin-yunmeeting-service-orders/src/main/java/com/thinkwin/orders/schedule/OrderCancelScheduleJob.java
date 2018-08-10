package com.thinkwin.orders.schedule;

import com.thinkwin.common.ContextHolder;

public class OrderCancelScheduleJob implements Runnable {
	private String userId;
	private String orderId;

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public void run() {
		OrderServiceUtil orderUtil = (OrderServiceUtil) ContextHolder.getApplicationContext().getBean("orderUtil");

		orderUtil.cancelOrder(orderId, userId);
	}
}
