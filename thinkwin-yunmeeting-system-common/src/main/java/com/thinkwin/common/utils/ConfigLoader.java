package com.thinkwin.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 邮件发送配置信息加载类
 * 
 * @Description:
 * @project mailUtil
 */
public class ConfigLoader {
	//日志记录对象
	private static Logger log = LoggerFactory.getLogger(ConfigLoader.class);
	// 配置文件路径
	private static String mailPath = "mail.properties";
	// 邮件发送SMTP主机
	private static String server;
	// 发件人邮箱地址
	private static String sender;
	// 发件人邮箱用户名
	private static String username;
	// 发件人邮箱密码
	private static String password;
	// 发件人显示昵称
	private static String nickname;
	static {
		// 类初始化后加载配置文件
		InputStream in = ConfigLoader.class.getClassLoader()
				.getResourceAsStream(mailPath);
		Properties props = new Properties();
		try {
			props.load(in);
		} catch (IOException e) {
			log.error("load mail setting error,pleace check the file path:"
					+ mailPath);
			log.error(e.toString(), e);
		}
		server = props.getProperty("mail.hostName");
		sender = props.getProperty("mail.fromAddress");
		username = props.getProperty("mail.userName");
		password = props.getProperty("mail.password");
		nickname = props.getProperty("mail.fromName");
		log.debug("load mail setting success,file path:" + mailPath);
	}

	public static String getServer() {
		return server;
	}

	public static String getSender() {
		return sender;
	}

	public static String getUsername() {
		return username;
	}

	public static String getPassword() {
		return password;
	}

	public static String getNickname() {
		return nickname;
	}

	public static void setMailPath(String mailPath) {
		ConfigLoader.mailPath = mailPath;
	}

}
