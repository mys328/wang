package com.thinkwin.common.utils;

import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.impl.SimpleLog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

public class PropertyUtil {
	private static Properties prop;
	static {
		InputStream fis;
		try {

			prop = new Properties();
			fis = getResourceAsStream("/server.properties");
			prop.load(fis);
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
	
	//企云会主站
	public static String getYunMeetingServer() {
		return prop.getProperty("yunMeetingServer");
	}
	//微信站点
	public static String getWechatServer() {return prop.getProperty("wechatServer");}
    //H5站点
    public static String getH5Server() {return prop.getProperty("h5Server");}
    //菜单-预订会议
    public static String getH5Reservation() {return prop.getProperty("reservation");}
    //菜单-查询会议
    public static String getH5Query() {return prop.getProperty("query");}


	

	

	private static ClassLoader getContextClassLoader() {
		ClassLoader classLoader = null;

		if (classLoader == null) {
			try {
				Method method = Thread.class.getMethod("getContextClassLoader",null);

				try {
					classLoader = (ClassLoader) method.invoke(Thread.currentThread(), null);
				} catch (IllegalAccessException e) {
					;
				} catch (InvocationTargetException e) {

					if (e.getTargetException() instanceof SecurityException) {
						;
					} else {
						throw new LogConfigurationException(
								"Unexpected InvocationTargetException", e
										.getTargetException());
					}
				}
			} catch (NoSuchMethodException e) {

			}
		}

		if (classLoader == null) {
			classLoader = SimpleLog.class.getClassLoader();
		}

		return classLoader;
	}

	@SuppressWarnings("unchecked")
	public static InputStream getResourceAsStream(final String name) {
		return (InputStream) AccessController
				.doPrivileged(new PrivilegedAction() {
					@Override
				    public Object run() {
						ClassLoader threadCL = getContextClassLoader();

						if (threadCL != null) {
							return threadCL.getResourceAsStream(name);
						} else {
							return ClassLoader.getSystemResourceAsStream(name);
						}
					}
				});
	}

}
