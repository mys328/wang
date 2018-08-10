package com.thinkwin.publish.service;

import com.thinkwin.common.dto.publish.CommandMessage;

public interface PublishService {

	/**
	 * 向终端推送指令
	 * @param cmd
	 */
	void pushCommand(CommandMessage cmd);

}
