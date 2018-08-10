package com.thinkwin.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 邮件发送实现类
 *
 * @Description:
 * @project mailUtil
 */
public class MailSender {
	private MimeMessage mimeMsg; // MIME邮件对象
	private Session session; // 邮件会话对象
	private Properties props; // 系统属性
	private String smtpPort;//邮件发送端口

	private Multipart mp; // Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成
	private String username;// 发件人的用户名
	private String password;// 发件人的密码
	private String nickname;// 发件人的昵称
	private Boolean isSSL;// 是否使用ssl

	public String getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
		props.setProperty("mail.smtp.socketFactory.port", this.smtpPort);
	}
	public Boolean getIsSSL() {
		return isSSL;
	}

	public void setIsSSL(Boolean isSSL) {
		this.isSSL = isSSL;
	}

	private static Logger log = LoggerFactory.getLogger(MailSender.class);

	/**
	 * 有参构造器
	 *
	 * @param smtp
	 */
	public MailSender(String smtp, String portNum) {
		setSmtpHost(smtp);
		this.setSmtpPort(portNum);
		createMimeMessage();
	}

	/**
	 * 设置邮件发送的SMTP主机
	 *
	 * @param hostName
	 *            SMTP 发送主机
	 * @Description:
	 * @return void
	 */
	public void setSmtpHost(String hostName) {
		if (props == null)
			props = System.getProperties();
		props.put("mail.smtp.host", hostName);
//		log.debug("set system properties success ：mail.smtp.host= " + hostName);

	}

	/**
	 * 创建邮件对象
	 *
	 * @return
	 * @Description:
	 * @return boolean
	 */
	public void createMimeMessage() {
		// 获得邮件会话对象
		session = Session.getDefaultInstance(props, null);
		// 创建MIME邮件对象
		mimeMsg = new MimeMessage(session);
		mp = new MimeMultipart();
//		log.debug(" create session and mimeMessage success");
	}

	/**
	 * 设置权限鉴定配置
	 *
	 * @param need
	 *            是否需要权限
	 * @Description:
	 * @return void
	 */
	public void setNeedAuth(boolean need) {
		if (props == null)
			props = System.getProperties();
		if (need) {
			props.put("mail.smtp.auth", "true");
		} else {
			props.put("mail.smtp.auth", "false");
		}
//		log.debug("set smtp auth success：mail.smtp.auth= " + need);

	}

	/**
	 * 设置发送邮件的主题
	 *
	 * @param subject
	 *            邮件的主题
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	public void setSubject(String subject) throws UnsupportedEncodingException,
			MessagingException {
		mimeMsg.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
//		log.debug("set mail subject success, subject= " + subject);

	}

	/**
	 *
	 * @param mailBody
	 *            邮件的正文内容
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	public void setBody(String mailBody) throws MessagingException {
		BodyPart bp = new MimeBodyPart();
		bp.setContent("" + mailBody, "text/html;charset=utf-8");
		mp.addBodyPart(bp);
//		log.debug("set mail body content success,mailBody= " + mailBody);
	}

	/**
	 * 添加邮件附件
	 *
	 * @param filePath
	 *            文件绝对路径
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	public void addFileAffix(String filePath,String viewFileName) throws MessagingException {
		BodyPart bp = new MimeBodyPart();
		FileDataSource fileds = new FileDataSource(filePath);
		bp.setDataHandler(new DataHandler(fileds));
		bp.setFileName(viewFileName);
		mp.addBodyPart(bp);
//		log.debug("mail add file success,filename= " + filePath);
	}

	/**
	 * 设置发件人邮箱地址
	 *
	 * @param sender
	 *            发件人邮箱地址
	 * @throws UnsupportedEncodingException
	 * @throws AddressException
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	public void setSender(String sender) throws UnsupportedEncodingException,
			AddressException, MessagingException {
		nickname = MimeUtility.encodeText(nickname, "utf-8", "B");
		mimeMsg.setFrom(new InternetAddress(nickname + " <" + sender + ">"));
//		log.debug(" set mail sender and nickname success , sender= " + sender
//				+ ",nickname=" + nickname);
	}

	/**
	 * 设置收件人邮箱地址
	 *
	 * @param receiver
	 *            收件人邮箱地址
	 * @throws AddressException
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	public void setReceiver(String receiver) throws AddressException,
			MessagingException {
		if(StringUtils.isBlank(receiver)){
			return;
		}
		String [] receiverArr =receiver.split(",");
		InternetAddress[] toall=new InternetAddress[receiverArr.length];
		for (int i = 0; i < toall.length; i++) {
			toall[i]=new InternetAddress(receiverArr[i]);
		}
		mimeMsg.setRecipients(Message.RecipientType.TO,toall);
//		log.debug("set mail receiver success,receiver = " + toall);
	}

	/**
	 * 设置抄送人的邮箱地址
	 *
	 * @param copyto
	 *            抄送人邮箱地址
	 * @throws AddressException
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	public void setCopyTo(String copyto) throws AddressException,
			MessagingException {

		if(StringUtils.isBlank(copyto)){
			return;
		}
		String [] copytoArr =copyto.split(",");
		InternetAddress[] toall=new InternetAddress[copytoArr.length];
		for (int i = 0; i < toall.length; i++) {
			toall[i]=new InternetAddress(copytoArr[i]);
		}

		mimeMsg.setRecipients(Message.RecipientType.CC,toall);
	}

	/**
	 * 设置发件人用户名密码进行发送邮件操作
	 *
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	public void sendout() throws MessagingException {
		Transport transport =null;
		Address[] toArr=null;
		Address[] cCArr=null;
		List<Address> toList=null;
		List<Address> cCList=null;
		Session mailSession=null;
		try {
			mimeMsg.setContent(mp);
			mimeMsg.saveChanges();
			toArr = mimeMsg.getRecipients(Message.RecipientType.TO);
			cCArr = mimeMsg.getRecipients(Message.RecipientType.CC);

			toList = new ArrayList(Arrays.asList(toArr));
			if(cCArr!=null && cCArr.length>0) {
				cCList = new ArrayList(Arrays.asList(cCArr));
			}
			if (isSSL) {
				Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
				final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
				props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
				props.setProperty("mail.smtp.socketFactory.fallback", "false");
			}

			mailSession = Session.getInstance(props, null);
			transport = mailSession.getTransport("smtp");
			transport.connect((String) props.get("mail.smtp.host"), username,password);
			transport.sendMessage(mimeMsg,mimeMsg.getRecipients(Message.RecipientType.TO));
			if(mimeMsg.getRecipients(Message.RecipientType.CC) !=null){
				transport.sendMessage(mimeMsg,mimeMsg.getRecipients(Message.RecipientType.CC));
			}
		} catch (SendFailedException e) {
			Address[] invalid = e.getInvalidAddresses();
			List<Address> invalidList=null;
			if (invalid != null && invalid.length>0) {
				invalidList = new ArrayList(Arrays.asList(invalid));
				log.info("邮件: unsend mail: invalid address");
				for (int i = 0; i < invalid.length; i++) {
					log.info("邮件: invalid address:" + invalid[i]);
				}
			}
			Address[] validAddresses = e.getValidUnsentAddresses();
			if (validAddresses != null && validAddresses.length>0) {
				if(invalidList!=null && invalidList.size()>0) {
					toList.removeAll(invalidList);
					if(cCList !=null && cCList.size()>0) {
						cCList.removeAll(invalidList);
					}
					Address[] addressTo=null;
					Address[] addressCc=null;
					if(toList!=null && toList.size()>0) {
						addressTo = new Address[toList.size()];
						toList.toArray(addressTo);
						mimeMsg.setRecipients(Message.RecipientType.TO, addressTo);
					}
					if(cCList!=null && cCList.size()>0) {
						addressCc = new Address[cCList.size()];
						cCList.toArray(addressCc);
						mimeMsg.setRecipients(Message.RecipientType.CC, addressCc);
					}
					transport = mailSession.getTransport("smtp");
					transport.connect((String) props.get("mail.smtp.host"), username,password);
					transport.sendMessage(mimeMsg,mimeMsg.getRecipients(Message.RecipientType.TO));
					if(mimeMsg.getRecipients(Message.RecipientType.CC) !=null){
						transport.sendMessage(mimeMsg,mimeMsg.getRecipients(Message.RecipientType.CC));
					}
				}else {
					log.info("邮件: 本次发送失败，但未获取到错误的邮件地址");
				}
			}else {
				log.error("邮件: 因没有有效的邮件地址可发送，取消本次发送");
			}

		} catch (Exception e) {
			log.error("邮件: 邮件发送异常，已中断本次发送!");
			e.printStackTrace();
		} finally {
			if (transport != null) {
				transport.close();
			}
		}
	}

	/**
	 * 注入发件人用户名 ，密码 ，昵称
	 *
	 * @param username
	 *            发件人邮箱登录用户名
	 * @param password
	 *            发件人邮箱密码
	 * @param nickname
	 *            发件人显示的昵称
	 * @Description:
	 * @return void
	 */
	public void setNamePass(String username, String password, String nickname) {
		this.username = username;
		this.password = password;
		this.nickname = nickname;

	}

}
