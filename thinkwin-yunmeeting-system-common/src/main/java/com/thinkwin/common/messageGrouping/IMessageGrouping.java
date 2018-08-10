package com.thinkwin.common.messageGrouping;

import com.thinkwin.common.dto.publish.CommandMessage;
import com.thinkwin.common.mqMessage.MqMessageVoInterface;

import java.util.List;

/**
 * 功能:消息分组接口 CommandMessage包含针对多个终端的指令(指令body 相同，不同终端ID存在List中)
 * @author yangyiqian
 *
 */
public interface IMessageGrouping {
	/**
	 * 处理消息分组
	 */
	 List<MqMessageVoInterface> processMessageGrouping(CommandMessage commandMessage);
	

}
