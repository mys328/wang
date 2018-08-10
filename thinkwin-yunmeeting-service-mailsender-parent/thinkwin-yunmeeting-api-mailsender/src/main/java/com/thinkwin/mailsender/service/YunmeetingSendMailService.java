package com.thinkwin.mailsender.service;

import com.thinkwin.mailsender.vo.MailVo;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface YunmeetingSendMailService {
	void sendMail(MailVo mailVo);

	void sendMail(MailVo mailVo, Date sendTime);

	void sendMail(MailVo mailVo, Long time, TimeUnit timeUnit);
}