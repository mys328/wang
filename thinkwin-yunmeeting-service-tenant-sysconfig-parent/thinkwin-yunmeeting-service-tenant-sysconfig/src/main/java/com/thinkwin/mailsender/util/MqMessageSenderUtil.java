package com.thinkwin.mailsender.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqMessageSenderUtil {
	
	
	private static final Logger log = LoggerFactory.getLogger(MqMessageSenderUtil.class);
	
	/**
	 * 功能:发送信息到MQ 
	 * @author yangyiqian
	 * @param messageMQVo TODO
	 * @param specified_fd_uuid 指定的需要回写状态的渠道 数据以后从数据库中获取
	 */
	public static void sendMessageToMQ(MqMessageVoInterface messageMQVo,String ... specified_fd_uuid) {
		// 如果创建订单成功 对平安渠道的影子订单进行订单状态回写[同步到平安订单库的好药师订单]
		if(specified_fd_uuid != null && specified_fd_uuid.length >0){
			String fd_uuid = messageMQVo.getFd_uuid();
			MqMessageSenderUtil.sendEmailMQMessage(messageMQVo);
			log.info("------------EmailSenderUtil.sendEmailMQMessage");
		}
	}



    /**
     * 功能:向Mq发送异步邮件消息
     * @author yangyiqian
     * @param orderMessageMQVo
     */
    public static void sendEmailMQMessage(MqMessageVoInterface emailMessageMQVo) {
    	EmailMQProducer emailMQProducer = (EmailMQProducer)SpringContextUtil.getBean("emailMQProducer");
    	emailMQProducer.simpleSend(emailMessageMQVo);
    }

}
