package com.thinkwin.pay.service.impl;

import com.thinkwin.common.ContextHolder;
import com.thinkwin.pay.service.NotifyExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotifyRetryWorker implements Runnable {
	private static Logger log = LoggerFactory.getLogger(NotifyRetryWorker.class);

	private String tradeNo;

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}


	@Override
	public void run() {
		try{
			NotifyExecutor notifyExecutor = (NotifyExecutor) ContextHolder.getApplicationContext().getBean("notifyExecutor");

			notifyExecutor.notify(this.tradeNo);
		}
		catch (Exception e){
			log.error("订单: 向订单系统推送支付结果失败，交易单号: {}, msg: {}", this.tradeNo, e);
		}
	}

}
