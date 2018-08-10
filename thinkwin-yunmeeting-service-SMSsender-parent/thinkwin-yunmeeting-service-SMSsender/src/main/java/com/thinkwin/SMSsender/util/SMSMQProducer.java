package com.thinkwin.SMSsender.util;

import org.springframework.jms.core.JmsTemplate;

/**
 * JMS传递消息有两种方式：
 * JMS template     :用于同步收发消息
 * message listener container :用于异步收发消息
 * <p>
 * JmsTemplate常用的方法有send、convertAndSend、receive和convertAndReceive
 */
public class SMSMQProducer {

    private JmsTemplate jmsTemplet = null;

    public void setJmsTemplet(JmsTemplate jmsTemplet) {
        this.jmsTemplet = jmsTemplet;
    }

    public void simpleSend(MqMessageVoInterface sMSMessageMQVo) {
        //消息持久化
        this.jmsTemplet.setDeliveryPersistent(true);
        /*
        * 使用MessageConverter的情况
        * 利用JmsTemplate的convertAndSend方法发送一个sMSMessageMQVo对象的时候
        * 就会把对应的sMSMessageMQVo对象当做参数调用我们定义好的SMSMQMessageCoverter的toMessage方法
        * */
        this.jmsTemplet.convertAndSend(sMSMessageMQVo);
    }
} 