package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.dto.publish.TerminalMessageDto;
import com.thinkwin.common.model.db.InfoBroadcastMessage;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface InfoBroadcastMessageMapper extends Mapper<InfoBroadcastMessage> {

	List<TerminalMessageDto> selectMessageByTerminalIds(@Param("terminalIds")List<String> terminalIds);

	void updateMessageStatus(@Param("terminalId")String terminalId, @Param("msgId")String msgId, @Param("status")Integer status);

}