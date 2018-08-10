package com.thinkwin.mailsender.util;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.HashMap;
import java.util.Map;


/**
 * Email MQ消息接收类
 * 
 * @author yangyiqian
 */
public class EmailMQConsumer implements MessageListener {
	
	private static final Logger log = LoggerFactory.getLogger(EmailMQConsumer.class);

	public void onMessage(Message arg0) {

		ActiveMQObjectMessage msg = (ActiveMQObjectMessage) arg0;
		try{
			if(msg != null){
				String emailStr =msg.getStringProperty("emailMessage");
				if(emailStr !=null){
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("tss", "能看到这个东东吗？是模板中来的啊");
					
					//发送邮件
					MailUtil.sendMailAndFileByTemplateStringContent("yangyiqian@thinkwin.com.cn", "测试邮件", "d://1fc0e485-f63f-430b-ba11-159c6e8af6b9.pdf", "abc.pdf", paramMap,
							"<html>您好,${tss}</html>", false);
				}
				System.out.println(111);
			}
		System.out.println(111);	
		}catch(Exception e){
			log.error(e.getMessage());
		}
	}
	
	
	
	

}