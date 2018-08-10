package com.thinkwin.push.util;

import com.thinkwin.common.utils.PropertiesUtil;
import com.thinkwin.push.netty.NettyServerBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushDubboMain {

	private final static Logger logger = LoggerFactory.getLogger(NettyServerBootstrap.class);

	public static void main(String[] args) throws Exception {
		logger.info("netty开始启动");
		//启动netty服务
		new Thread(new Netty()).start();

		com.alibaba.dubbo.container.Main.main(args);
	}

	public static class Netty implements Runnable {
		@Override
		public void run() {
			try {
				new NettyServerBootstrap(PropertiesUtil.getInt("pushService.port"));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}