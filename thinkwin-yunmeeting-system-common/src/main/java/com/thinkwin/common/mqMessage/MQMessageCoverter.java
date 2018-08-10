package com.thinkwin.common.mqMessage;
  
import java.util.UUID;

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
public class MQMessageCoverter implements MessageConverter{
    public Object fromMessage(Message message) throws JMSException,  
            MessageConversionException {
        ObjectMessage objMsg = (ObjectMessage)message;  
        MessageMQVo messageMQVo = new MessageMQVo();
        messageMQVo.setTenantId(objMsg.getStringProperty("tenantId"));
        messageMQVo.setMessage(objMsg.getStringProperty("message"));
        return messageMQVo;
    }
  
    public Message toMessage(Object obj, Session session) throws JMSException,  
            MessageConversionException {  
    	MessageMQVo messageMQVo = (MessageMQVo)obj;
        ObjectMessage objMsg = session.createObjectMessage();
        String messageId = UUID.randomUUID().toString();
        objMsg.setJMSCorrelationID(messageId);
        objMsg.setJMSReplyTo(objMsg.getJMSDestination());  
        objMsg.setStringProperty("tenantId",messageMQVo.getTenantId());
        objMsg.setStringProperty("message",messageMQVo.getMessage());
        return objMsg;  
    }  
} 