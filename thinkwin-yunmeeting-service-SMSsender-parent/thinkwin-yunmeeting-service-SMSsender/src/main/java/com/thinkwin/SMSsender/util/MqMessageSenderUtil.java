package com.thinkwin.SMSsender.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqMessageSenderUtil {


    private static final Logger log = LoggerFactory.getLogger(MqMessageSenderUtil.class);

    /**
     * 功能:发送信息到MQ
     */
    public static void sendMessageToMQ(MqMessageVoInterface messageMQVo, String... specified_fd_uuid) {

        if (specified_fd_uuid != null && specified_fd_uuid.length > 0) {
            sendEmailMQMessage(messageMQVo);
            log.info("------------MqMessageSenderUtil.sendEmailMQMessage");
        }
    }

    /**
     * 功能:向Mq发送异步消息
     */
    public static void sendEmailMQMessage(MqMessageVoInterface emailMessageMQVo) {
        SMSMQProducer smsmqProducer = (SMSMQProducer) SpringContextUtil.getBean("sMSMQProducer");
        smsmqProducer.simpleSend(emailMessageMQVo);
    }
}
