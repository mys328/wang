package com.thinkwin.auth.util;

public class AuthDubboMain {
	// 如果 spring 配置文件的位置是默认的，则可以直接这样启动服务
	public static void main(String[] args) throws Exception {
		com.alibaba.dubbo.container.Main.main(args);
	}
}