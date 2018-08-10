package com.thinkwin.SMSsender.util;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.UUID;

/**
 * 功能：消息转换器
 * 类是实现MessageConverter接口
 * 作用：
 * 1、可以把我们的非标准化Message对象转换成我们的目标Message对象，这主要是用在发送消息的时候
 * 2、可以把我们的Message对象转换成对应的目标对象，这主要是用在接收消息的时候
 */
public class SMSMQMessageCoverter implements MessageConverter {

    /*
    * 消费者接受消息 进行转换
    * fromMessage是用来把一个JMS Message转换成对应的Java对象
    * */
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        ActiveMQObjectMessage objMsg = (ActiveMQObjectMessage) message;
        SMSMessageMQVo smsMessageMQVo = new SMSMessageMQVo();
        smsMessageMQVo.setFd_uuid(objMsg.getStringProperty("fd_uuid"));
        smsMessageMQVo.setMessage(objMsg.getStringProperty("sMSMessage"));
        return smsMessageMQVo;
    }

    /*
    * 生产者发送消息进行转换
    * toMessage方法是用来把一个Java对象转换成对应的JMS Message
    *
    * */
    public Message toMessage(Object obj, Session session) throws JMSException, MessageConversionException {
        SMSMessageMQVo smsMessageMQVo = (SMSMessageMQVo) obj;
        ObjectMessage objMsg = session.createObjectMessage();
        String messageId = UUID.randomUUID().toString();
        objMsg.setJMSCorrelationID(messageId);
        objMsg.setJMSReplyTo(objMsg.getJMSDestination());
        objMsg.setStringProperty("fd_uuid", smsMessageMQVo.getFd_uuid());
        objMsg.setStringProperty("sMSMessage", smsMessageMQVo.getMessage());
        return objMsg;
    }
} 