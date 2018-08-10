package com.thinkwin.yunmeeting.framework.datasource.dynamicdatasource;
/**
 * Db context 助手类
 */
public class DBContextHolder {

	private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

	public static void setDBType(String dbType) {
		contextHolder.set(dbType);
	}

	public static String getDBType() {
		return (String) contextHolder.get();
	}

	public static void clearDBType() {
		contextHolder.remove();
	}

}