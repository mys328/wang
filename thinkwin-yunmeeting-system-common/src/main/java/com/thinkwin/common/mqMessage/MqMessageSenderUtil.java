package com.thinkwin.common.mqMessage;

import com.thinkwin.yunmeeting.framework.util.spring.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MqMessageSenderUtil {
	
	
	private static final Logger log = LoggerFactory.getLogger(MqMessageSenderUtil.class);
	
	/**
	 * 功能:发送信息到MQ 
	 * @author yangyiqian
	 * @param messageMQVo TODO
	 */
	public static void sendMessageToMQ(MqMessageVoInterface messageMQVo) {
		String tenantId = messageMQVo.getTenantId();
		if(tenantId != null && tenantId.length() >0){
			String tid = messageMQVo.getTenantId();
			sendMQMessage(messageMQVo);
			log.info("------------MqMessageSenderUtil.sendMessageToMQ");
		}
	}


	/**
	 * 功能:发送信息到MQ
	 * @author yangyiqian

	 */
	public static void sendMessageToMQ(List<MqMessageVoInterface> messageMQVoList) {
		if(messageMQVoList !=null && messageMQVoList.size()>0){
			for (int i = 0; i <messageMQVoList.size() ; i++) {
				MqMessageVoInterface mVo = messageMQVoList.get(i);
				sendMQMessage(mVo);
				System.out.println("向MQ队列发送消息");
			}
		}
	}



    /**
     * 功能:向Mq发送异步业务消息
     * @author yangyiqian
     * @param messageVo
     */
    private static void sendMQMessage(MqMessageVoInterface messageVo) {
    	//通过
    	MQProducer bizMQProducer = (MQProducer)SpringContextUtil.getBean(messageVo.getQueueFlag());
    	bizMQProducer.simpleSend(messageVo);
    }

}
