package com.thinkwin.publish.service.impl;

import com.thinkwin.common.dto.publish.CommandMessage;
import com.thinkwin.common.messageGrouping.IMessageGrouping;
import com.thinkwin.publish.service.PublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("publishService")
public class PublishServiceImpl implements PublishService {
	private static Logger logger = LoggerFactory.getLogger(PublishServiceImpl.class);

	@Resource
	IMessageGrouping messageGrouping;

	@Override
	public void pushCommand(CommandMessage cmd) {
		if(null == cmd){
			logger.error("publish: 下行指令为空");
			return;
		}

		logger.info("publish: 发送下行指令: {}", cmd);
		messageGrouping.processMessageGrouping(cmd);
	}
}
