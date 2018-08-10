package com.thinkwin.yuncm.service;

import com.thinkwin.common.dto.publish.*;
import com.thinkwin.common.model.db.InfoProgram;
import com.thinkwin.common.model.db.InfoProgramTerminalMiddle;
import com.thinkwin.common.vo.publish.*;

import java.util.List;
import java.util.Map;

public interface TerminalService {
	int TERMINAL_SCREENSHOT_TIMEOUT = 120;
	int TERMINAL_UPGRADE_TIMEOUT = 300;

	TerminalDto getTerminalById(String terminalId);

    TerminalDto getTerminalById4Mobile(String terminalId);

	String sendScreenshotCommand(CaptureScreenshotRequest request);

	CaptureScreenshotResponse refreshScreenshot(CaptureScreenshotRequest request);

	SearchTerminalResponse queryTerminals(SearchTerminalRequest request);

	void updateTerminal(UpdateTerminalRequst request);

	void broadcastMessage(List<String> terminals, BroadcastCommandVo commandVo);

	PublishProgramResponse publishProgram(PublishProgramRequest request, String pagePath);

	List<PublishProgramErrorBlock> refreshTerminalProgramStatus(String requestId);

	Map getDingZhiProgram(Integer pageNum, Integer pageSize);

	boolean getCustomProgramStatus(String tenantId);

	void rebootTerminal(List<String> terminals);

	void deleteTerminalProgram(String terminalId);

	List<ProgramTag> getProgramTags();

	GetProgramResponse getPrograms(String tag, Integer pageNum, Integer pageSize);

	/**
	 * 查询会议室及绑定的终端设备
	 * @param meetingRoomId 会议室ID
	 * @param status 过滤会议室是否绑定了终端设备，1：已绑定；0：未绑定
	 * @return
	 */
	List<MeetingRoomTerminalDto> getRoomTerminals(String meetingRoomId, Integer status);

	Integer upgradeTerminalApp(String terminalId);

	TerminalAppUpgradeStatus queryUpgradeStatus(String terminalId);

	//获取插播消息配置数据
	BroadcastConfig getBroadcastConfig();

	//获取APP最近一次更新记录
	TerminalUpdateLog getLatestUpdateLog();

	/**
	 * 处理下行指令发送结果回执
	 * @param command
	 * @arrived 指令是否已送达
	 */
	void handleReceipt(Command command, boolean arrived);

	/**
	 * 处理上行指令
	 * @param response
	 */
	void handleResponse(CommandResponse response);

	/**
	 * 更新终端在线状态
	 * @param terminalId 终端ID
	 * @param status 1：在线，0：离线
	 */
	boolean updateTerminalStatus(String terminalId, Integer status);


	/**
	 * 根据终端id获取绑定的节目信息
	 * @param terminalId 终端id
	 * @return
	 */
	InfoProgramTerminalMiddle getInfoProgramTerminalMiddleByTerminalId(String terminalId);

	/**
	 * 获取节目信息
	 * @param programId
	 * @return
	 */
	TerminalProgramCommandDto selectProgramCommandById(String programId);

	/**
	 * 根据终端id查询是否有绑定的节目功能接口
	 * @param termianlId
	 * @return
	 */
	public List<InfoProgramTerminalMiddle> selectProgramTerminalMiddleByTerminalId(String termianlId);


	/**
	 * 获取节目信息
	 * @param programId
	 * @return
	 */
	public TerminalProgramCommandDto obtainProgramCommandById(String programId,String tenantId);

}
