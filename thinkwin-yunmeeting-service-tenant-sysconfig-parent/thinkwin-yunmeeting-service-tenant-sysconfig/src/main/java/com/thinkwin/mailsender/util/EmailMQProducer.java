package com.thinkwin.mailsender.util;  
import org.springframework.jms.core.JmsTemplate;
  
public class EmailMQProducer{  
    private JmsTemplate jmsTemplet = null ;  
    public void setJmsTemplet(JmsTemplate jmsTemplet) {  
        this.jmsTemplet = jmsTemplet;  
    }  
      
    public void simpleSend(MqMessageVoInterface emailMessageMQVo) {
        this.jmsTemplet.setDeliveryPersistent(true);  
        this.jmsTemplet.convertAndSend(emailMessageMQVo);
    }  
} 