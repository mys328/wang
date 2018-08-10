package com.thinkwin.mailsender.service.impl;

import com.thinkwin.common.ContextHolder;
import com.thinkwin.mailsender.service.YunmeetingSendMailService;
import com.thinkwin.mailsender.vo.MailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendMailTask implements Runnable {
	private static Logger LOGGER = LoggerFactory.getLogger(MailSenderService.class);

	private MailVo mailVo;

	public SendMailTask(MailVo mailVo){
		this.mailVo = mailVo;
	}

	@Override
	public void run() {
		try{
			YunmeetingSendMailService mailSenderService = (YunmeetingSendMailService)ContextHolder.getApplicationContext().getBean("mailSenderService");
			mailSenderService.sendMail(mailVo);
		} catch (Exception e){
			LOGGER.error("定时发送邮件异常: ", e);
		}
	}
}
