package com.thinkwin.SMSsender.util;

/**
 * MQ传消息接口
 * @author yangyiqian
 *
 */
public interface MqMessageVoInterface {
	//获取渠道信息
	public String getFd_uuid();
	//获取信息
	public String getMessage();

}
