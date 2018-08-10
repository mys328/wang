package com.thinkwin.common.mqMessage;

/**
 * MQ传消息接口
 * @author yangyiqian
 *
 */
public interface MqMessageVoInterface {
	//获取租户信息
	public String getTenantId();
	//获取信息
	public String getMessage();
	//获取所属队列标识
	public String getQueueFlag();

}
