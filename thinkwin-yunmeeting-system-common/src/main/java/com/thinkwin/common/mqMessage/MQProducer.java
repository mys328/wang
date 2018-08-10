package com.thinkwin.common.mqMessage;
import org.springframework.jms.core.JmsTemplate;
  
public class MQProducer{  
    private JmsTemplate jmsTemplet = null ;  
    public void setJmsTemplet(JmsTemplate jmsTemplet) {  
        this.jmsTemplet = jmsTemplet;  
    }  
      
    public void simpleSend(MqMessageVoInterface messageMQVo) {
        this.jmsTemplet.setDeliveryPersistent(true);  
        this.jmsTemplet.convertAndSend(messageMQVo);
    }  
} 