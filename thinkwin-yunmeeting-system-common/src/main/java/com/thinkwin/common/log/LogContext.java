package com.thinkwin.common.log;

/*
* log日志 多线程全局变量
* */
public class LogContext {

	static ThreadLocal<String> contents = new ThreadLocal<String>();


	public static String getContents() {
		return contents.get();
	}

	public static void setContents(String contents) {
		LogContext.contents.set(contents);
	}

	public static void clearContents(){
		LogContext.contents.remove();
	}

}
