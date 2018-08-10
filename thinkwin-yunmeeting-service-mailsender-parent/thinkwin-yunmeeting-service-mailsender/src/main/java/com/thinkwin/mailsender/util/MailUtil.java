package com.thinkwin.mailsender.util;

import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.thinkwin.common.utils.ConfigLoader;
import com.thinkwin.common.utils.MailSender;
import com.thinkwin.schedule.service.TimerService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import freemarker.template.TemplateException;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;

/**
 * 邮件发送工具类
 * 
 * @Description:
 * @project mailUtil
 */
public class MailUtil {
	private static Log log = LogFactory.getLog(MailUtil.class);
	//private static UusafeDao commDao = GenericDao.getInstance();
	
	private static String server;
	private static String sender;
	private static String username;
	private static String password;
	private static String nickname;
	
	/**查询邮件配置信息*/
	private static void getEmailSetting() {
		/*
		String sql = "SELECT content AS CONTENT FROM sys_setting WHERE setting_id = 2";
		Map map = commDao.get(sql);
		JSONObject emailSetting = ( StringUtils.isEmpty(map.get("CONTENT") == null ? "" : map.get("CONTENT").toString()) ? null : JSONObject.fromObject(map.get("CONTENT").toString()) );
		log.info("emaill setting info:"+emailSetting);
		if (emailSetting != null) {
			server = emailSetting.getString("smtpServer");
			sender = emailSetting.getString("smtpUser");
			username = emailSetting.getString("smtpUser");
			password = emailSetting.getString("smtpPwd");
			nickname = emailSetting.getString("enSenderName");
		} else {
			log.info("数据库中邮件配置信息不正确==========读取配置文件中的邮件属性!");
			server = ConfigLoader.getServer();
			sender = ConfigLoader.getSender();
			username = ConfigLoader.getUsername();
			password = ConfigLoader.getPassword();
			nickname = ConfigLoader.getNickname();
		}*/
		server = ConfigLoader.getServer();
		sender = ConfigLoader.getSender();
		username = ConfigLoader.getUsername();
		password = ConfigLoader.getPassword();
		nickname = ConfigLoader.getNickname();
/*	JSONObject emailSetting =new JSONObject();
		return emailSetting;*/
	}
	
	/**查询setting配置信息*/
/*	public static JSONObject getSettingInfo(Integer settingId) {
		String sql = "SELECT content AS CONTENT FROM sys_setting WHERE setting_id = "+settingId;
		Map map = commDao.get(sql);
		JSONObject emailSetting = ( StringUtils.isEmpty(map.get("CONTENT") == null ? "" : map.get("CONTENT").toString()) ? null : JSONObject.fromObject(map.get("CONTENT").toString()) );
		return emailSetting;
	}*/
	
	/**
	 * 根据模板名称查找模板，加载模板内容后发送邮件
	 * 
	 * @param receiver
	 *            收件人地址
	 * @param subject
	 *            邮件主题
	 * @param map
	 *            邮件内容与模板内容转换对象
	 * @param templateName
	 *            模板文件名称
	 * @throws IOException
	 * @throws TemplateException
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	public static void sendMailByTemplate(String receiver, String subject,
			Map<String, String> map, String templateName) throws IOException,
			TemplateException, MessagingException {
		//getEmailSetting();
		String maiBody = "";
		MailSender mail = new MailSender(server, "");
		mail.setNeedAuth(true);
		mail.setNamePass(username, password, nickname);
		maiBody = TemplateFactory.generateHtmlFromFtl(templateName, map);
		mail.setSubject(subject);
		mail.setBody(maiBody);
		mail.setReceiver(receiver);
		mail.setSender(sender);
		mail.sendout();
	}

	/**
	 * 根据模板名称查找模板，加载模板内容后发送邮件
	 * 
	 * @param receiver
	 *            收件人地址
	 * @param subject
	 *            邮件主题
	 * @param map
	 *            邮件内容与模板内容转换对象
	 * @param templateName
	 *            模板文件名称 (不需要后缀)
	 * @throws IOException
	 * @throws TemplateException
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	public static void sendMailAndFileByTemplate(String receiver,String subject, String filePath,String viewFileName, Map<String, String> map,String templateName, boolean isSendOverDelFile) throws IOException, TemplateException,
			MessagingException {
		log.info("准备发送邮件:收件人"+receiver+" 主题： "+subject + "使用的邮件模板名称："+templateName);
		String maiBody = "";
		//getEmailSetting();
		MailSender mail = new MailSender(server,"");
		mail.setNeedAuth(true);
		mail.setNamePass(username, password, nickname);
	    maiBody = TemplateFactory.generateHtmlFromFtl(templateName, map);
		mail.setSubject(subject);
		mail.addFileAffix(filePath,viewFileName);
		mail.setBody(maiBody);
		mail.setReceiver(receiver);
		mail.setSender(sender);
		mail.sendout();
		File mailFile =new File(filePath);
		if(isSendOverDelFile){
			if(mailFile.exists()){//发送完邮件删除文件
				mailFile.delete();
			}
		}

	}
	
	/**
	 * 根据模板名称查找模板，加载模板内容后发送邮件
	 * 
	 * @param receiver
	 *            收件人地址
	 * @param subject
	 *            邮件主题
	 * @param map
	 *            邮件内容与模板内容转换对象
	 * @param templateContent
	 *            模板内容
	 * @throws IOException
	 * @throws TemplateException
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	public static void sendMailAndFileByTemplateStringContent(String receiver,String subject, String filePath,String viewFileName, Map<String, String> map,String templateContent, boolean isSendOverDelFile) throws IOException, TemplateException,
	MessagingException {
		log.info("准备发送邮件:收件人"+receiver+" 主题： "+subject + "使用的邮件模板内容："+templateContent);
		String maiBody = "";
		getEmailSetting();
		MailSender mail = new MailSender(server,"");
		mail.setNeedAuth(true);
		mail.setNamePass(username, password, nickname);
		mail.setSubject(subject);
		maiBody = TemplateFactory.generateHtmlFromFtlContent(templateContent, map);
		mail.addFileAffix(filePath,viewFileName);//UTF8编码，否则邮件乱码
		mail.setBody(maiBody);
		mail.setReceiver(receiver);
		mail.setSender(sender);
		mail.sendout();
		if(isSendOverDelFile){
			File mailFile =new File(filePath);
			if(mailFile.exists()){//发送完邮件删除文件
				mailFile.delete();
			}
		}
		
	}

	/**
	 * 普通方式发送邮件内容
	 * 
	 * @param receiver
	 *            收件人地址
	 * @param subject
	 *            邮件主题
	 * @param maiBody
	 *            邮件正文
	 * @throws IOException
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	public static void sendMail(String receiver, String subject, String maiBody)
			throws IOException, MessagingException {
		//getEmailSetting();
		MailSender mail = new MailSender(server,"");
		mail.setNeedAuth(true);
		mail.setNamePass(username, password, nickname);
		mail.setSubject(subject);
		mail.setBody(maiBody);
		mail.setReceiver(receiver);
		mail.setSender(sender);
		mail.sendout();
	}
	
	/**
	 * 普通方式发送邮件内容 [配置信息从参数传入,不从配置文件获取]
	 * 
	 * @param receiver
	 *            收件人地址
	 * @param subject
	 *            邮件主题
	 * @param maiBody
	 *            邮件正文
	 * @throws IOException
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	
	public static String sendMail(String mailServer,String serverPort,String userName,String passWd,String receiver, String subject, String maiBody,String nick)
			throws IOException, MessagingException {
		String resultStr = null;
		try  
		  {
		       String serverIp = mailServer;
		       final String username =userName;
		       final String password =passWd;
		           Properties props = new Properties(); 
		           props.setProperty("mail.smtp.host",serverIp);        //指定SMTP服务器  
		           props.setProperty("mail.smtp.auth","true");          //指定是否需要SMTP验证  
		           props.setProperty("mail.smtp.port", serverPort);     //指定端口
				   props.setProperty("mail.smtp.connectiontimeout", "6000");//6秒超时
				   props.setProperty("mail.smtp.timeout", "6000");
		         Session mailSession = Session.getInstance(props,new Authenticator(){
		             protected PasswordAuthentication getPasswordAuthentication() {
		                 return new PasswordAuthentication(username, password);
		             }});  //获得一个默认会话session 
		         mailSession.setDebug(true);//是否在控制台显示debug信息    
		         Message message=new MimeMessage(mailSession);  
		         if(StringUtils.isNotBlank(nick)){
		        	 message.setFrom(new InternetAddress (userName,nick));  
		         }else{
		        	 message.setFrom(new InternetAddress(userName));//发件人  
		         }
		         message.setRecipient(Message.RecipientType.TO,new InternetAddress(receiver));//收件人     
		         message.setSubject(subject);//邮件主题  
		         message.setText(maiBody);//邮件内容
		         message.saveChanges();  
		         Transport.send(message);  
		         log.info("Message sent.");
		  }    catch(Exception e)    { 
			  String errorStr = e.toString();
			  if(StringUtils.isNotBlank(errorStr)){
				  if(errorStr.indexOf("535") !=-1){
					  resultStr="535";//用户名或者密码错误
				  }else{
					  resultStr="error";
				  }
			  }
			  log.info(e.toString());
		  }  
		return resultStr;
	}
	
	/**
	 * 普通方式发送SSL邮件内容
	 * 
	 * @param receiver
	 *            收件人地址
	 * @param subject
	 *            邮件主题
	 * @param maiBody
	 *            邮件正文
	 * @throws IOException
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	public static String sendSslMail(String mailServer,String serverPort,String userName,String passWd,String receiver, String subject, String maiBody,String nick)
			throws IOException, MessagingException {
		String resultStr=null;
		  try {
			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			  final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
			  // Get a Properties object
			  Properties props = new Properties();
			  props.setProperty("mail.smtp.host", mailServer);
			  props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
			  props.setProperty("mail.smtp.socketFactory.fallback", "false");
			  props.setProperty("mail.smtp.port", serverPort);
			  props.setProperty("mail.smtp.connectiontimeout", "6000");
			  props.setProperty("mail.smtp.timeout", "6000");
			  props.setProperty("mail.smtp.socketFactory.port", serverPort);
			  props.put("mail.smtp.auth", "true");
			  final String username = userName;
			  final String password =passWd;
			  Session session = Session.getInstance(props, new Authenticator(){
			      protected PasswordAuthentication getPasswordAuthentication() {
			          return new PasswordAuthentication(username, password);
			      }});
			  session.setDebug(true);
			  Message msg = new MimeMessage(session);
			     if(StringUtils.isNotBlank(nick)){
			    	 msg.setFrom(new InternetAddress (userName,nick));  
			     }else{
			    	 msg.setFrom(new InternetAddress(userName));//发件人  
			     }
			  msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver,false));
			  msg.setSubject(subject);
			  msg.setText(maiBody);
			  msg.setSentDate(new Date());
			  Transport.send(msg);
			  log.info("Message sent.");
		} catch (Exception e) {
			  String errorStr = e.toString();
			  if(StringUtils.isNotBlank(errorStr)){
				  if(errorStr.indexOf("535") !=-1){
					  resultStr="535";//用户名或者密码错误
				  }else{
					  resultStr="error";
				  }
			  }else{
				  resultStr="error";
			  }
			e.printStackTrace();
		}
		  return resultStr;
	}

	/**
	 * 普通方式发送邮件内容，并且附带文件附件
	 * 
	 * @param receiver
	 *            收件人地址
	 * @param subject
	 *            邮件主题
	 * @param filePath
	 *            文件的绝对路径
	 * @param maiBody
	 *            邮件正文
	 * 
	 * @throws IOException
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	public static void sendMailAndFile(String receiver, String subject,
			String filePath,String viewFileName, String maiBody) throws IOException,
			MessagingException {
		//getEmailSetting();
		MailSender mail = new MailSender(server, "");
		mail.setNeedAuth(true);
		mail.setNamePass(username, password, nickname);
		mail.setSubject(subject);
		mail.setBody(maiBody);
		mail.addFileAffix(filePath,viewFileName);
		mail.setReceiver(receiver);
		mail.setSender(sender);
		mail.sendout();
	}


	
	/**
	 * 根据模板名称查找模板，加载模板内容后发送邮件
	 * 
	 * @param receiver
	 *            收件人地址
	 * @param subject
	 *            邮件主题
	 * @param map
	 *            邮件内容与模板内容转换对象
	 * @param templateName
	 *            模板文件名称
	 * @throws IOException
	 * @throws TemplateException
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	public static void sendMailAndFileByTemplate(String receiver,String subject, String filePath,String viewFileName, Map<String, String> map,String templateName) throws IOException, TemplateException,
			MessagingException {
		log.info("准备发送邮件:收件人"+receiver+" 主题： "+subject + "使用的邮件模板名称："+templateName);
		//getEmailSetting();
		String maiBody = "";
		MailSender mail = new MailSender(server, "");
		mail.setNeedAuth(true);
		mail.setNamePass(username, password, nickname);
	    maiBody = TemplateFactory.generateHtmlFromFtl(templateName, map);
		mail.setSubject(subject);
		mail.addFileAffix(filePath,viewFileName);
		mail.setBody(maiBody);
		mail.setReceiver(receiver);
		mail.setSender(sender);
		mail.sendout();
		File mailFile =new File(filePath);
		if(mailFile.exists()){//发送完邮件删除文件
			mailFile.delete();
		}
	}
	/**
	 * 根据模板名称查找模板，加载字符串模板内容后发送邮件
	 * 
	 * @param receiver
	 *            收件人地址
	 * @param subject
	 *            邮件主题
	 * @param map
	 *            邮件内容与模板内容转换对象
	 * @param viewFileName
	 *            模板文件名称
	 * @throws IOException
	 * @throws TemplateException
	 * @throws MessagingException
	 * @Description:
	 * @return void
	 */
	public static void sendMailAndFileByTemplateStringContent(String receiver,String subject, String filePath,String viewFileName, Map<String, String> map,String templateContent) throws IOException, TemplateException,
	MessagingException {
		log.info("准备发送邮件:收件人"+receiver+" 主题： "+subject + "使用的邮件模板字符串内容："+templateContent);
		log.info("server:"+server+" username:"+username+" password:" +password +" nickname:"+nickname);
		//getEmailSetting();
		String maiBody = "";
		MailSender mail = new MailSender(server, "");
		mail.setNeedAuth(true);
		mail.setNamePass(username, password, nickname);
		log.info("----------------->>>>mailProcessor:sendMailAndFileByTemplateStringContent  ftl组合html前 ");
		maiBody = TemplateFactory.generateHtmlFromFtlContent(templateContent, map);
		log.info("准备发送邮件:解析邮件模板获取到的maiBody:"+maiBody);
		mail.setSubject(subject);
		mail.addFileAffix(filePath,viewFileName);
		mail.setBody(maiBody);
		mail.setReceiver(receiver);
		mail.setSender(sender);
		mail.sendout();
		File mailFile =new File(filePath);
		if(mailFile.exists()){//发送完邮件删除文件
			mailFile.delete();
		}
	}

	public static class mailTaskRunnable implements Runnable{

		@Override
		public void run() {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("tss", "能看到这个东东吗？是模板中来的啊");
			try {
				MailUtil.sendMailByTemplate("yangyiqian@thinkwin.com.cn", "测试邮件", paramMap, "abddd");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TemplateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("/config/spring/applicationContext-activemq.xml");

		MailUtil.server = "smtp.exmail.qq.com";
		MailUtil.sender = "yangyiqian@thinkwin.com.cn";
		MailUtil.username = "yangyiqian@thinkwin.com.cn";
		MailUtil.password = "cps6688CPS";
		MailUtil.nickname = "skw";

		TimerService service = (TimerService)ctx.getBean("timerService");

		service.schedule(new mailTaskRunnable(), 2, TimeUnit.SECONDS);

	/*	String receiver = "maojianduo@uusafe.com";
		String sub = "测试";
		String filePath = "/home/test/tmp2/1465962223590_url_list_templatewnag.xls";
		String viewFileName = "201606220500-201606221900.xls";
		String templateName = "template_reportRss.ftl";
		Map<String, String> map = new HashMap<String, String>();
		map.put("content", "1111111");*/

		
/*		try {
			sendMail("yangyiqian@thinkwin.com.cn", "ttt", "tttttttsssssssssssssssss");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}
}