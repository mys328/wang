package com.thinkwin.mailsender.util;  
  
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
  
/**功能：邮件消息转换
 * @author yangyiqian
 *
 */
public class EmailMQMessageCoverter implements MessageConverter{  
    public Object fromMessage(Message message) throws JMSException,  
            MessageConversionException {  
        ObjectMessage objMsg = (ObjectMessage)message;  
        EmailMessageMQVo mailMessageMQVo = new EmailMessageMQVo();
        mailMessageMQVo.setFd_uuid(objMsg.getStringProperty("fd_uuid"));
        mailMessageMQVo.setMessage(objMsg.getStringProperty("message"));
        return mailMessageMQVo;  
    }  
  
    public Message toMessage(Object obj, Session session) throws JMSException,  
            MessageConversionException {  
    	EmailMessageMQVo mailMessageMQVo = (EmailMessageMQVo)obj;  
        ObjectMessage objMsg = session.createObjectMessage();  
        objMsg.setJMSCorrelationID("12365478");  
        objMsg.setJMSReplyTo(objMsg.getJMSDestination());  
        objMsg.setStringProperty("fd_uuid",mailMessageMQVo.getFd_uuid());  
        objMsg.setStringProperty("emailMessage",mailMessageMQVo.getMessage());  
        return objMsg;  
    }  
} 