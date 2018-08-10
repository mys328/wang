package com.thinkwin.mailsender.vo;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class MailVo implements Serializable {
	private static final long serialVersionUID = -2569333770778370076L;

	/**
	 * 邮件主题
	 */
	String subject;

	/**
	 * 邮件正文
	 */
	String body;

	/**
	 * 收件人列表，<收件人邮箱，收件人名称>
	 */
	Map<String, String> recipientsMap;

	/**
	 * 抄送列表，<抄送邮箱，收件人名称>
	 */
	Map<String, String> ccMap;

	/**
	 * 模板名称
	 */
	String templateName;

	/**
	 * 模板参数信息，<变量名，变量值>
	 */
	Map<String, String> templateParamMap;

	/**
	 * 附件
	 */
	LinkedHashMap<String, String> attachmentParam;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, String> getRecipientsMap() {
		return recipientsMap;
	}

	public void setRecipientsMap(Map<String, String> recipientsMap) {
		this.recipientsMap = recipientsMap;
	}

	public Map<String, String> getCcMap() {
		return ccMap;
	}

	public void setCcMap(Map<String, String> ccMap) {
		this.ccMap = ccMap;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Map<String, String> getTemplateParamMap() {
		return templateParamMap;
	}

	public void setTemplateParamMap(Map<String, String> templateParamMap) {
		this.templateParamMap = templateParamMap;
	}

	public LinkedHashMap<String, String> getAttachmentParam() {
		return attachmentParam;
	}

	public void setAttachmentParam(LinkedHashMap<String, String> attachmentParam) {
		this.attachmentParam = attachmentParam;
	}

	@Override
	public String toString() {
		return "MailVo{" +
				"subject='" + subject + '\'' +
				", body='" + body + '\'' +
				", recipientsMap=" + recipientsMap +
				", ccMap=" + ccMap +
				", templateName='" + templateName + '\'' +
				", templateParamMap=" + templateParamMap +
				'}';
	}
}
