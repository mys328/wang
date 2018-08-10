package com.thinkwin.mailsender.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.thinkwin.common.utils.MailSender;
import com.thinkwin.config.service.ConfigService;
import com.thinkwin.mailsender.service.YunmeetingSendMailService;
import com.thinkwin.mailsender.util.TemplateFactory;
import com.thinkwin.mailsender.vo.MailVo;
import com.thinkwin.schedule.service.TimerService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service(value = "mailSenderService")
public class MailSenderService implements YunmeetingSendMailService {
	static Logger LOGGER = LoggerFactory.getLogger(MailSenderService.class);

	MailSender sender;
	JSONObject jsonObject;

	@Autowired
	ConfigService configService;

	@Autowired
	TimerService timerService;

	@Value("${pool.size}")
	private Integer poolSize = 30;

	private static ExecutorService executorService;

	@PostConstruct
	public void start(){
		executorService = Executors.newFixedThreadPool(poolSize);
	}

	@Override
	public void sendMail(MailVo mailVo) {
		try {
			MailSender sender = this.getMailSender();
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					sendMail(sender, mailVo);
				}
			});
		} catch (Exception e) {
			LOGGER.error("立即发送邮件异常: ", e);
		}
	}

	@Override
	public void sendMail(MailVo mailVo, Date sendTime) {
		try {
			SendMailTask task = new SendMailTask(mailVo);
			timerService.schedule(task, sendTime);
		} catch (Exception e) {
			LOGGER.error("创建定时发送邮件任务异常: ", e);
		}
	}

	@Override
	public void sendMail(MailVo mailVo, Long time, TimeUnit timeUnit) {
		try {
			SendMailTask task = new SendMailTask(mailVo);
			timerService.schedule(task, time, timeUnit);
		} catch (Exception e) {
			LOGGER.error("创建定时发送邮件任务异常: ", e);
		}
	}

	private MailSender getMailSender(){
		if(StringUtils.isBlank(TenantContext.getTenantId())){
			TenantContext.setTenantId("0");
		}

		String smtpConfig = configService.get("smtp.config");
		JSONObject jsonObject = JSONObject.parseObject(smtpConfig);
		if(!jsonObject.equals(this.jsonObject)){
			this.sender = setSenderWithJson(jsonObject);
		}
		return this.sender;
	}

	private MailSender setSenderWithJson(JSONObject jsonObject){
		String smtpServer = jsonObject.getString("server");
		String port = jsonObject.getString("port");
		String userName = jsonObject.getString("username");
		String passWord = jsonObject.getString("password");

		String senderEmail = jsonObject.getString("sender");
		if(StringUtils.isBlank(senderEmail)){
			senderEmail = userName;
		}

		String nickName = jsonObject.getString("nickname");
		Boolean ssl = jsonObject.getBooleanValue("ssl");
		MailSender mailSender = new MailSender(smtpServer, port);
		try {
			mailSender.setNeedAuth(true);
			mailSender.setNamePass(userName, passWord, nickName);
			mailSender.setSender(senderEmail);
			mailSender.setIsSSL(ssl);
		} catch (Exception e) {
			LOGGER.error("创建MailSender失败", e);
			return null;
		}

		return mailSender;
	}

	private void sendMail(MailSender sender, MailVo mailVo) {
		if(MapUtils.isEmpty(mailVo.getRecipientsMap())){
			LOGGER.error("邮件: 收件人列表为空, 邮件主题: {}", mailVo.getSubject());
			return;
		}

		validateMailVo(mailVo);

		if(MapUtils.isEmpty(mailVo.getRecipientsMap())){
			LOGGER.error("邮件: 收件人列表为空, 邮件主题: {}", mailVo.getSubject());
			return;
		}

		if(StringUtils.isBlank(TenantContext.getTenantId())){
			TenantContext.setTenantId("0");
		}

		try {
			sender.setSubject(mailVo.getSubject());
			String maiBody = mailVo.getBody();
			if(StringUtils.isNotBlank(mailVo.getTemplateName())){
				String templateContent = this.configService.get(mailVo.getTemplateName());
				byte[] bytesEncoded = Base64.decodeBase64(templateContent.getBytes());
				templateContent = new String(bytesEncoded);

				Map<String, String> mailbodyParamMap = mailVo.getTemplateParamMap();
				maiBody = TemplateFactory.generateHtmlFromFtlContent(templateContent, mailbodyParamMap);
			}

			// recipients
			StringBuilder receiveSb =new StringBuilder();
			for (Map.Entry<String, String> recipient : mailVo.getRecipientsMap().entrySet()) {
				receiveSb.append(recipient.getKey());
				receiveSb.append(",");
			}
			String receiveString = receiveSb.toString();
			receiveString = receiveString.substring(0, receiveString.length() -1);
			sender.setReceiver(receiveString);

			// cc
			Map<String, String> ccMap = mailVo.getCcMap();
			LinkedHashMap<String, String> attachmentParam = mailVo.getAttachmentParam();
			if(MapUtils.isNotEmpty(ccMap)){
				StringBuilder ccSb =new  StringBuilder();
				for (Map.Entry<String, String> entry : ccMap.entrySet()) {
					ccSb.append(entry.getKey());
					ccSb.append(",");
				}
				String ccString = ccSb.toString();
				ccString = ccString.substring(0,ccString.length() -1);
				sender.setCopyTo(ccString);
			}

			// attachment
			if(MapUtils.isNotEmpty(attachmentParam)){
				for (Map.Entry<String, String> entry : attachmentParam.entrySet()) {
					sender.addFileAffix(entry.getKey(), entry.getValue());
				}
			}

			sender.setBody(maiBody);
			sender.sendout();
		}
		catch (Exception ex){
			LOGGER.error("sendMail", ex);
		}
	}

	private static boolean validateMailVo(MailVo vo){
		if(vo.getRecipientsMap().size() > 100){
			return true;
		}

		Map<String, String> recipients = new HashMap<>();
		for(String email : vo.getRecipientsMap().keySet()){
			String recipient = vo.getRecipientsMap().get(email);
			if(StringUtils.isBlank(email)){
				LOGGER.error("邮件: 收件人邮箱为空, 邮件主题: {}, 收件人名称: {}", vo.getSubject(), recipient);
				continue;
			}

			if(!isValidEmailAddress(email)){
				LOGGER.error("邮件: 错误的收件人邮箱地址: {}, 邮件主题: {}, 收件人名称: {}", email, vo.getSubject(), recipient);
			} else{
				recipients.put(email, recipient);
			}
		}

		if(MapUtils.isNotEmpty(vo.getCcMap())){
			Iterator<Map.Entry<String,String>> iter = vo.getCcMap().entrySet().iterator();
			while (iter.hasNext()) {
				String email = iter.next().getKey();
				String cc = vo.getCcMap().get(email);
				if(StringUtils.isBlank(email)){
					iter.remove();
					LOGGER.error("邮件: cc邮箱为空, 邮件主题: {}, cc名称: {}", vo.getSubject(), cc);
					continue;
				}

				if(!isValidEmailAddress(email)){
					iter.remove();
					LOGGER.error("邮件: 错误的cc邮箱地址: {}, 邮件主题: {}, cc名称: {}", email, vo.getSubject(), cc);
				}
			}
		}

		vo.setRecipientsMap(recipients);
		return true;
	}

	private static boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}
}
