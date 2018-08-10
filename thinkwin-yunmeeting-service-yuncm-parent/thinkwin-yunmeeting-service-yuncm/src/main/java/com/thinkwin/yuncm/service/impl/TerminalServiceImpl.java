package com.thinkwin.yuncm.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.thinkwin.common.dto.publish.*;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.model.publish.PlatformClientVersionUpgradeRecorder;
import com.thinkwin.common.model.publish.PlatformInfoClientVersionLib;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.PingYinUtil;
import com.thinkwin.common.utils.TimeUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.publish.*;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.dictionary.service.DictionaryService;
import com.thinkwin.publish.service.ConfigManagerService;
import com.thinkwin.publish.service.PlatformClientVersionUpgradeRecorderService;
import com.thinkwin.publish.service.PlatformInfoClientVersionLibService;
import com.thinkwin.publish.service.PublishService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.mapper.*;
import com.thinkwin.yuncm.service.TerminalService;
import com.thinkwin.yuncm.util.TerminalVoUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("terminalService")
public class TerminalServiceImpl implements TerminalService {
	private static Logger logger = LoggerFactory.getLogger(TerminalServiceImpl.class);

	private final static String TERMINAL_TYPE = "18500";
	private final static String BROADCAST_UNIT = "16500";
	private final static String BROADCAST_SPEED = "17500";
	private final static String TERMINAL_TYPE_PREFIX = "TERMINAL_TYPE_";

	@Resource
	InfoReleaseTerminalMapper infoReleaseTerminalMapper;

	@Resource
	InfoProgramMapper infoProgramMapper;

	@Resource
	InfoProgrameLabelMapper infoProgrameLabelMapper;

	@Resource
	YuncmMeetingRoomMapper yuncmMeetingRoomMapper;

	@Resource
	InfoBroadcastMessageMapper infoBroadcastMessageMapper;

	@Resource
	InfoProgramTerminalMiddleMapper infoProgramTerminalMiddleMapper;

	@Resource
	InfoMessageTerminalMiddleMapper infoMessageTerminalMiddleMapper;

	@Resource
	BizImageRecorderMapper bizImageRecorderMapper;

	@Resource
	DictionaryService dictionaryService;

	@Resource
	PublishService publishService;

	@Resource
	PlatformInfoClientVersionLibService platformInfoClientVersionLibService;

	@Resource
	SaasTenantService saasTenantCoreService;

	@Resource
	PlatformClientVersionUpgradeRecorderService platformClientVersionUpgradeRecorderService;

	@Autowired
    InfoLabelProgramMiddleMapper infoLabelProgramMiddleMapper;

	@Resource
    ConfigManagerService configManagerService;

	@Override
	public TerminalDto getTerminalById(String terminalId) {
		InfoReleaseTerminal terminal = infoReleaseTerminalMapper.selectByPrimaryKey(terminalId);

		if(null == terminal){
			return null;
		}

		SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
		if(null == saasTenant){
			return null;
		}

		String isInnerTest = saasTenant.getIsInnerTest();

		List<TerminalProgramDto> programDtos = infoProgramMapper.selectProgramByTerminalIds(Arrays.asList(terminalId), isInnerTest);

		TerminalProgramDto programDto = CollectionUtils.isEmpty(programDtos) ? null : programDtos.get(0);
		TerminalDto dto = TerminalVoUtil.fromInfoTerminal(terminal, null, programDto);
		dto.setId(terminal.getId());
		dto.setHardwareId(terminal.getHardwareId());
		dto.setName(terminal.getTerminalName());
		if(StringUtils.isBlank(dto.getName())){
			dto.setName(dto.getHardwareId());
		}
		dto.setResolution(terminal.getResolutionRatio());
		dto.setBackgroundUrl(terminal.getBackgroundUrl());
		dto.setVoice(terminal.getTerminalVolume());
		dto.setLight(terminal.getTerminalBrightness());
		dto.setCity(terminal.getCity());
		dto.setProvince(terminal.getProvince());
		dto.setCounty(terminal.getCounty());
		dto.setStreet(terminal.getStreet());
		if(programDto!=null){
			TerminalProgramVo vo = new TerminalProgramVo();
			vo.setName(programDto.getName());
			if (programDto.getStartTime() != null) {
				vo.setStartTime(programDto.getStartTime().getTime());
			}
			dto.setProgram(vo);
		}
		dto.setStatus(0);
		if(StringUtils.isNotBlank(terminal.getStatus())){
			dto.setStatus(Integer.parseInt(terminal.getStatus()));
		}

		//平台公共终端背景图片地址
		String defaultBackgroundUrl=null;
		defaultBackgroundUrl=this.configManagerService.getTerminalBackgrounp();
		dto.setDefaultBackgroundUrl(defaultBackgroundUrl);

		if(StringUtils.isNotBlank(terminal.getMeetingRoomId())){
			YuncmMeetingRoom room = yuncmMeetingRoomMapper.selectByPrimaryKey(terminal.getMeetingRoomId());
			if(room != null && "0".equals(room.getDeleteState())){
				TerminalMeetingRoomVo vo = new TerminalMeetingRoomVo();
				dto.setMeetingRoom(vo);
				vo.setName(room.getName());
				vo.setId(room.getId());
			}
		}

//		Map<String, String> map = getTerminalTypeMap();
//		if(StringUtils.isNotBlank(terminal.getTerminalTypeId())){
//			dto.setType(map.get(TERMINAL_TYPE_PREFIX + terminal.getTerminalTypeId().toUpperCase()));
//		}
		dto.setType(terminal.getTerminalTypeId());

		return dto;
	}

	@Override
	public TerminalDto getTerminalById4Mobile(String terminalId) {
		InfoReleaseTerminal terminal = infoReleaseTerminalMapper.selectByPrimaryKey(terminalId);

		if(null == terminal){
			return null;
		}

		SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
		if(null == saasTenant){
			return null;
		}

		String isInnerTest = saasTenant.getIsInnerTest();

		List<TerminalProgramDto> programDtos = infoProgramMapper.selectProgramByTerminalIds(Arrays.asList(terminalId), isInnerTest);

		TerminalProgramDto programDto = CollectionUtils.isEmpty(programDtos) ? null : programDtos.get(0);
		TerminalDto dto = TerminalVoUtil.fromInfoTerminal(terminal, null, programDto);
		dto.setId(terminal.getId());
		dto.setHardwareId(terminal.getHardwareId());
		dto.setName(terminal.getTerminalName());
		if(StringUtils.isBlank(dto.getName())){
			dto.setName(dto.getHardwareId());
		}
		dto.setResolution(terminal.getResolutionRatio());
		dto.setBackgroundUrl(terminal.getBackgroundUrl());
		dto.setVoice(terminal.getTerminalVolume());
		dto.setLight(terminal.getTerminalBrightness());
		dto.setCity(terminal.getCity());
		dto.setProvince(terminal.getProvince());
		dto.setCounty(terminal.getCounty());
		dto.setStreet(terminal.getStreet());
		if(programDto!=null){
			TerminalProgramVo vo = new TerminalProgramVo();
			vo.setName(programDto.getName());
			if (programDto.getStartTime() != null) {
				vo.setStartTime(programDto.getStartTime().getTime());
			}
			dto.setProgram(vo);
		}
		dto.setStatus(0);
		if(StringUtils.isNotBlank(terminal.getStatus())){
			dto.setStatus(Integer.parseInt(terminal.getStatus()));
		}

		//平台公共终端背景图片地址
		String defaultBackgroundUrl=null;
		defaultBackgroundUrl=this.configManagerService.getTerminalBackgrounp();
		if(StringUtils.isBlank(dto.getBackgroundUrl())){
			dto.setBackgroundUrl(defaultBackgroundUrl);
		}

		if(StringUtils.isNotBlank(terminal.getMeetingRoomId())){
			YuncmMeetingRoom room = yuncmMeetingRoomMapper.selectByPrimaryKey(terminal.getMeetingRoomId());
			if(room != null){
				TerminalMeetingRoomVo vo = new TerminalMeetingRoomVo();
				dto.setMeetingRoom(vo);
				vo.setName(room.getName());
				vo.setId(room.getId());
			}
		}

//		Map<String, String> map = getTerminalTypeMap();
//		if(StringUtils.isNotBlank(terminal.getTerminalTypeId())){
//			dto.setType(map.get(TERMINAL_TYPE_PREFIX + terminal.getTerminalTypeId().toUpperCase()));
//		}
		dto.setType(terminal.getTerminalTypeId());

		return dto;
	}

	@Override
	public String sendScreenshotCommand(CaptureScreenshotRequest request) {
		String tenantId = TenantContext.getTenantId();

		String requestId = request.getRequestId();
		List<String> terminals = request.getIds();

		String key = tenantId + "_publish_screen_" + requestId;
		if(StringUtils.isBlank(requestId)){

			List<InfoReleaseTerminal> terminalList = null;
			if(CollectionUtils.isEmpty(terminals)){
				terminalList = infoReleaseTerminalMapper.selectAll();
			} else {
				terminalList = infoReleaseTerminalMapper.selectTerminals(terminals);
			}

			if(CollectionUtils.isEmpty(terminalList)){
				return "";
			}

			Map<String, InfoReleaseTerminal> terminalMap = terminalList.stream()
					.collect(Collectors.toMap(InfoReleaseTerminal::getId, Function.identity()));

			terminals = terminalList.stream()
					.filter(t -> "1".equals(t.getStatus()))
					.map(InfoReleaseTerminal::getId)
					.collect(Collectors.toList());

			if(CollectionUtils.isEmpty(terminals)){
				return "";
			}

			Map<String,String> terminalCommandMap = terminals.stream()
					.collect(Collectors.toMap(Function.identity(), s-> "{\"terminalId\":\""+s+"\"}"));

			requestId = CreateUUIdUtil.Uuid();
			key = tenantId + "_publish_screen_" + requestId;
//			String firstTerminal = terminals.get(0);
//			RedisUtil.hset(key, firstTerminal, "");
			RedisUtil.hmset(key, terminalCommandMap);
		} else {
			if(CollectionUtils.isEmpty(terminals)){
				return requestId;
			}

//			Collection<String> all = RedisUtil.hgetAll(key).values();
//			if(CollectionUtils.isEmpty(all)){
//				return requestId;
//			}
//
//			Set<String> terminalIdList = new HashSet<>();
//			for(String json : all){
//				if(StringUtils.isNotBlank(json)){
//					TerminalScreenshotDto dto = JSON.parseObject(json, TerminalScreenshotDto.class);
//					terminalIdList.add(dto.getTerminalId());
//				}
//			}
//
//			for(String terminalId : terminals){
//				if(!terminalIdList.contains(terminalId)){
//					terminals.add(terminalId);
//				}
//			}
//
//			if(CollectionUtils.isEmpty(all)){
//				return requestId;
//			}

			List<InfoReleaseTerminal> terminalsList = infoReleaseTerminalMapper.selectTerminals(terminals);

			if(CollectionUtils.isEmpty(terminalsList) || terminalsList.size() < terminals.size()){
				logger.error("终端列表参数错误.");
				return "";
			}

		}

		RedisUtil.expire(key, TERMINAL_SCREENSHOT_TIMEOUT);

		Date nowTime = new Date();
		CommandMessage cmd = new CommandMessage();
		cmd.setCmd("10009");
		cmd.setTenantId(tenantId);
		cmd.setTerminals(terminals.stream().distinct().collect(Collectors.toList()));
		cmd.setRequestId(requestId);
		cmd.setTimestamp(nowTime.getTime());
		publishService.pushCommand(cmd);

		return requestId;
	}

	@Override
	public CaptureScreenshotResponse refreshScreenshot(CaptureScreenshotRequest request){
		String tenantId = TenantContext.getTenantId();

		CaptureScreenshotResponse response = new CaptureScreenshotResponse();


		String requestId = request.getRequestId();
		String key = tenantId + "_publish_screen_" + requestId;

		if(!RedisUtil.exists(key)){
			return response;
		}

		Collection<String> all = RedisUtil.hgetAll(key).values();
		if(CollectionUtils.isEmpty(all)){
			return response;
		}

		Map<String,TerminalScreenshotDto> dtoMap = new HashMap<>();
		for(String json : all){
			if(StringUtils.isNotBlank(json)){
				TerminalScreenshotDto dto = JSON.parseObject(json, TerminalScreenshotDto.class);
				dtoMap.put(dto.getTerminalId(), dto);
			}
		}

		SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
		if(null == saasTenant){
			return response;
		}

		List<String> terminalIds = request.getIds();

		if(CollectionUtils.isNotEmpty(terminalIds)){
			terminalIds = terminalIds.stream().distinct()
					.collect(Collectors.toList());
		} else {
			terminalIds = new ArrayList<>(dtoMap.keySet());
		}

		if(CollectionUtils.isEmpty(terminalIds)){
			return response;
		}

		List<TerminalScreenshotVo> terminals = new ArrayList<>();
		response.setTerminals(terminals);
		response.setRequestId(request.getRequestId());

		String isInnerTest = saasTenant.getIsInnerTest();
		List<TerminalProgramDto> programDtos = infoProgramMapper.selectProgramByTerminalIds(terminalIds, isInnerTest);
		Map<String, TerminalProgramDto> programDtoMap = programDtos.stream()
				.collect(Collectors.toMap(TerminalProgramDto::getTerminalId, Function.identity()));

		Example example = new Example(InfoReleaseTerminal.class);
//		Example.Criteria criteria = example.createCriteria().andEqualTo("status", "1");
		if(CollectionUtils.isNotEmpty(terminalIds)){
			example.createCriteria().andIn("id", terminalIds);
		}

		List<InfoReleaseTerminal> terminalList = infoReleaseTerminalMapper.selectByExample(example);

		Collection<String> meetingroomIds = terminalList.stream()
				.filter(t->t.getMeetingRoomId() != null)
				.map(InfoReleaseTerminal::getMeetingRoomId)
				.collect(Collectors.toSet());

		Map<String, YuncmMeetingRoom> roomMap = getTerminalMeetingRoom(meetingroomIds);

		for(InfoReleaseTerminal terminal : terminalList){
			String terminalId = terminal.getId();
			TerminalScreenshotVo vo = new TerminalScreenshotVo();
			vo.setRequestId(requestId);
			vo.setTerminalId(terminalId);
			vo.setTerminalName(terminal.getTerminalName());
			if(StringUtils.isBlank(vo.getTerminalName())){
				vo.setTerminalName(terminal.getHardwareId());
			}
			vo.setRoomName("");
			vo.setProgramName("");
			vo.setPictureUrl("");
			vo.setSmallPictureUrl("");
			if(roomMap.containsKey(terminal.getMeetingRoomId())){
				vo.setRoomName(roomMap.get(terminal.getMeetingRoomId()).getName());
			}

			if(programDtoMap.containsKey(terminalId)){
				vo.setProgramName(programDtoMap.get(terminalId).getName());
			}

			TerminalScreenshotDto dto = dtoMap.get(terminalId);
			if(null != dto){
				vo.setPictureUrl(dto.getPictureUrl());
				vo.setSmallPictureUrl(dto.getSmallPictureUrl());
			}
			vo.setStatus(1);
			if(StringUtils.isNotBlank(terminal.getStatus())){
				int status = Integer.parseInt(terminal.getStatus());
				if(status == 1){
					vo.setStatus(0);
				}
			}
			terminals.add(vo);
		}

		return response;
	}

	private Map<String, YuncmMeetingRoom> getTerminalMeetingRoom(Collection<String> meetingroomIds){
		Map<String, YuncmMeetingRoom> roomMap = new HashMap<>();
		if(CollectionUtils.isNotEmpty(meetingroomIds)){
			Example ex = new Example(YuncmMeetingRoom.class);
			ex.createCriteria().andEqualTo("deleteState", 0)
					.andIn("id", meetingroomIds);
			List<YuncmMeetingRoom> meetingRooms = yuncmMeetingRoomMapper.selectByExample(ex);
			if(CollectionUtils.isEmpty(meetingRooms)){
				return roomMap;
			}

			roomMap = meetingRooms.stream()
					.collect(Collectors.toMap(YuncmMeetingRoom::getId, Function.identity()));
		}

		return roomMap;
	}

	@Override
	public SearchTerminalResponse queryTerminals(SearchTerminalRequest request) {
		SearchTerminalResponse response = new SearchTerminalResponse();
		response.setPageInfo(new PageInfoVo());

		SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
		if(null == saasTenant){
			return null;
		}

		String isInnerTest = saasTenant.getIsInnerTest();

		TerminalStatsDto statsDto = infoReleaseTerminalMapper.getTerminalStats(request.getWord(), isInnerTest);

		List<InfoReleaseTerminal> terminals = null;

		if(request.getPageNum() == null){
			request.setPageNum(1);
		}

		if(request.getPageSize() == null ){
			request.setPageSize(15);
		}

		Page<?> pageInfo = PageHelper.startPage(request.getPageNum(), request.getPageSize());

		String keyWord = "";
		if(StringUtils.isNotBlank(request.getWord())){
			keyWord = request.getWord().trim();
		}

		terminals = infoReleaseTerminalMapper.searchTerminal(keyWord, request.getStatus(),request.getType(), isInnerTest);

		response.getPageInfo().setPageNum(pageInfo.getPageNum());
		response.getPageInfo().setPages(pageInfo.getPages());
		response.getPageInfo().setTotal(pageInfo.getTotal());
		response.getPageInfo().setMore(pageInfo.getPageNum() < pageInfo.getPages());

		if(CollectionUtils.isNotEmpty(terminals)){
			String verType = "1";
			if("1".equals(saasTenant.getIsInnerTest())){
				verType = "0";
			}
			PlatformInfoClientVersionLib clientVersion = platformInfoClientVersionLibService.getTerminalVersion(verType);

//			Map<String, String> map = getTerminalTypeMap();

			List<String> terminalIds = terminals.stream()
					.map(InfoReleaseTerminal::getId)
					.collect(Collectors.toList());

			List<TerminalProgramDto> programDtos = infoProgramMapper.selectProgramByTerminalIds(terminalIds, isInnerTest);

			if(CollectionUtils.isNotEmpty(programDtos)){
				//-1是正在发布状态
				programDtos = programDtos.stream()
						.filter(p -> !"-1".equals(p.getReleaseStatus()))
						.collect(Collectors.toList());

				for(TerminalProgramDto programDto : programDtos){
					setProgramStatus(programDto);
				}
			}

			Map<String, TerminalProgramDto> programDtoMap = programDtos.stream()
					.collect(Collectors.toMap(TerminalProgramDto::getTerminalId, Function.identity()));
			List<TerminalMessageDto> terminalMessageDtos = infoBroadcastMessageMapper.selectMessageByTerminalIds(terminalIds);
			Map<String, TerminalMessageDto> messageMap = terminalMessageDtos.stream()
					.collect(Collectors.toMap(TerminalMessageDto::getTerminalId, Function.identity()));
			List<String> meetingroomIds = terminals.stream()
					.filter(t-> t.getMeetingRoomId() != null)
					.map(InfoReleaseTerminal::getMeetingRoomId)
					.collect(Collectors.toList());

			Map<String, YuncmMeetingRoom> roomMap = getTerminalMeetingRoom(meetingroomIds);

			List<TerminalDto> terminalDtos = new ArrayList<TerminalDto>();
			for(InfoReleaseTerminal terminal : terminals){
				TerminalDto dto = TerminalVoUtil.fromInfoTerminal(terminal, messageMap.get(terminal.getId()), programDtoMap.get(terminal.getId()));
				if(dto!=null && StringUtils.isBlank(dto.getBackgroundUrl())) {
					dto.setBackgroundUrl(this.configManagerService.getTerminalBackgrounp());
				}
				terminalDtos.add(dto);

//				if(StringUtils.isNotBlank(terminal.getTerminalTypeId())){
//					dto.setType(map.get(TERMINAL_TYPE_PREFIX + terminal.getTerminalTypeId().toUpperCase()));
//				}
				dto.setType(terminal.getTerminalTypeId());

				if(clientVersion != null){
					dto.setLatestVersion(clientVersion.getVerNum());
					if(!clientVersion.getVerNum().equals(dto.getVersion())){
						dto.setUpgradable(true);
					}
				}

				if(StringUtils.isNotBlank(terminal.getMeetingRoomId())){
					YuncmMeetingRoom room = roomMap.get(terminal.getMeetingRoomId());
					if(room != null){
						TerminalMeetingRoomVo vo = new TerminalMeetingRoomVo();
						dto.setMeetingRoom(vo);
						vo.setName(room.getName());
						vo.setId(room.getId());
					}
				}
			}

			response.setTerminals(terminalDtos);
		}
		response.setStats(statsDto);

		return response;
	}

	private void setProgramStatus(TerminalProgramDto programDto){
		Date nowTime = new Date();

		if(programDto.isLongPlay()){
			programDto.setStatus("1");
		} else if(programDto.getStartTime() == null && programDto.getEndTime() == null){
			programDto.setStatus("0");
		} else if(programDto.getStartTime() != null && programDto.getEndTime() != null){
			if(programDto.getStartTime().before(nowTime) && programDto.getEndTime().after(nowTime)){
				programDto.setStatus("1");
			} else{
				programDto.setStatus("0");
			}
		} else if(programDto.getStartTime() == null){
			if(programDto.getEndTime().after(nowTime)){
				programDto.setStatus("1");
			} else{
				programDto.setStatus("0");
			}
		} else if(programDto.getEndTime() == null){
			if(programDto.getStartTime().before(nowTime)){
				programDto.setStatus("1");
			} else{
				programDto.setStatus("0");
			}
		}
	}

	@Override
	public void updateTerminal(UpdateTerminalRequst request) {
		String tenantId = TenantContext.getTenantId();

		if(StringUtils.isBlank(tenantId) || StringUtils.isBlank(request.getId())){
			return;
		}

		InfoReleaseTerminal t = infoReleaseTerminalMapper.selectByPrimaryKey(request.getId());

		if(null == t){
			return;
		}

		InfoReleaseTerminal terminal = new InfoReleaseTerminal();
		terminal.setId(request.getId());

		if(StringUtils.isNotBlank(request.getProvince())){
			terminal.setProvince(request.getProvince());
		}

		if (StringUtils.isNotBlank(request.getCity())) {
			terminal.setCity(request.getCity());
		}

		if(StringUtils.isNotBlank(request.getCounty())){
			terminal.setCounty(request.getCounty());
		}

		if(StringUtils.isNotBlank(request.getStreet())){
			terminal.setStreet(request.getStreet());
		}

		if(StringUtils.isNotBlank(request.getRoomId())){
			terminal.setMeetingRoomId(request.getRoomId());
		}

		if(StringUtils.isNotBlank(request.getName())){
			terminal.setTerminalName(request.getName().trim());
		}

		//删除租户背景图片置空
        terminal.setBackgroundId(request.getBackgroundId());
        terminal.setBackgroundUrl(request.getBackgroundUrl());
		terminal.setModifier(TenantContext.getUserInfo().getUserName());
		terminal.setModifyTime(new Date());

		infoReleaseTerminalMapper.updateByPrimaryKeySelective(terminal);

		InfoReleaseTerminal newTerminal = infoReleaseTerminalMapper.selectByPrimaryKey(request.getId());

		if(!StringUtils.equals(t.getBackgroundUrl(), newTerminal.getBackgroundUrl())){
			CommandMessage cmd = new CommandMessage();
			cmd.setTimestamp(new Date().getTime());
			cmd.setCmd("10008");
			cmd.setTenantId(TenantContext.getTenantId());
			cmd.setTerminals(Arrays.asList(t.getId()));

			Map<String, Object> params = new HashMap<>();
			params.put("picURL", newTerminal.getBackgroundUrl());

			if(StringUtils.isBlank(newTerminal.getBackgroundUrl())){
				String picURL = configManagerService.getTerminalBackgrounp();
				params.put("picURL", picURL);
			}
			cmd.setData(params);

			publishService.pushCommand(cmd);
		}
	}

	@Override
	@Transactional
	public void broadcastMessage(List<String> terminals, BroadcastCommandVo commandVo) {
		if(CollectionUtils.isEmpty(terminals)){
			return;
		}

		List<InfoReleaseTerminal> terminalsList = infoReleaseTerminalMapper.selectTerminals(terminals);

		if(CollectionUtils.isEmpty(terminalsList) || terminalsList.size() < terminals.size()){
			logger.error("终端列表参数错误.");
			return;
		}

		if(commandVo.getLength() < 1){
			logger.error("插播消息参数错误，msg: {}", commandVo);
			return;
		}

		//String msgId = insertMessage(terminals, commandVo);
		Map map = insertMessage(terminals, commandVo);
		if(null == map){
			return;
		}
		String msgId = (String) map.get("msgId");
		if(StringUtils.isBlank(msgId)){
			return;
		}

		List<SysDictionary> speedDict = dictionaryService.selectSysDictionaryByParentId(BROADCAST_SPEED);
		List<SysDictionary> unitDict = dictionaryService.selectSysDictionaryByParentId(BROADCAST_UNIT);

		Map<String, String> speedMap = speedDict.stream()
				.collect(Collectors.toMap(SysDictionary::getDictCode, SysDictionary::getDictValue));

		Map<String, String> unitMap = unitDict.stream()
				.collect(Collectors.toMap(SysDictionary::getDictCode, SysDictionary::getDictValue));


		Date nowTime = new Date();
		Map<String, Object> params = new HashMap<>();
		params.put("msgId", msgId);
		params.put("content", commandVo.getContent());
		params.put("expire", DateUtils.addSeconds(nowTime, (int)map.get("timeLength")).getTime());
//		params.put("unit", Integer.parseInt(unitMap.get(commandVo.getUnit())));
		//length已转为为秒
		params.put("unit", 1);
		params.put("length", map.get("timeLength"));
		params.put("speed", Integer.parseInt(speedMap.get(commandVo.getSpeed())));
		CommandMessage cmd = new CommandMessage();
		cmd.setCmd("10002");
		cmd.setRequestId(msgId);
		cmd.setTenantId(TenantContext.getTenantId());
		cmd.setTerminals(terminals);
		cmd.setData(params);
		cmd.setTimestamp(nowTime.getTime());
		publishService.pushCommand(cmd);
	}

	@Override
	public PublishProgramResponse publishProgram(PublishProgramRequest request, String pagePath) {
		PublishProgramResponse response = new PublishProgramResponse();
		if(CollectionUtils.isEmpty(request.getTerminals())){
			response.setMsg("终端列表为空");
			return response;
		}

		Date nowTime = new Date();

		List<String> terminals = request.getTerminals();
		String programId = request.getProgramId();

		Set<String> offlineTerminal = new HashSet<>(17);

		List<InfoReleaseTerminal> terminalList = infoReleaseTerminalMapper.selectTerminals(terminals);

		if(CollectionUtils.isEmpty(terminalList) || terminalList.size() < request.getTerminals().size()){
			response.setMsg("终端列表参数错误.");
			return response;
		}

		String tenantId = TenantContext.getTenantId();
		String requestId = CreateUUIdUtil.Uuid();

		Set<String> terminalIdList = new HashSet<>();

		for(InfoReleaseTerminal t : terminalList){
			terminalIdList.add(t.getTenantId());
		}

		List<String> pushTerminal = new ArrayList<>();

		Example programCondition = new Example(InfoProgramTerminalMiddle.class);
		programCondition.createCriteria().andIn("terminalId", terminalIdList);
		List<InfoProgramTerminalMiddle> infoProgramTerminalMiddles = infoProgramTerminalMiddleMapper.selectByExample(programCondition);

		Map<String, InfoProgramTerminalMiddle> terminalProgramMap = infoProgramTerminalMiddles.stream()
				.collect(Collectors.toMap(InfoProgramTerminalMiddle::getTerminalId, Function.identity()));

		Set<String> offlineTerminalIdList = new HashSet<>();
		for(InfoReleaseTerminal terminal : terminalList){
			if(StringUtils.isBlank(terminal.getStatus()) || terminal.getStatus().equals("0")){
				offlineTerminal.add(terminal.getTerminalName());
				offlineTerminalIdList.add(terminal.getId());
			} else {
				InfoProgramTerminalMiddle middle = terminalProgramMap.get(terminal.getId());

				if(null == middle || "-1".equals(middle.getReleaseStatus())){
					pushTerminal.add(terminal.getId());
					continue;
				}

				if("1".equals(middle.getIsLongPlay())
						|| (middle.getPlayerEndTime() != null && nowTime.before(middle.getPlayerEndTime()))){
					offlineTerminal.add(terminal.getTerminalName());
					offlineTerminalIdList.add(terminal.getId());
				} else {
					pushTerminal.add(terminal.getId());
				}
			}
		}

		if(request.getTerminals().size() == offlineTerminal.size()){
            PublishProgramErrorBlock block = new PublishProgramErrorBlock();
            block.setTerminals(new ArrayList<>(offlineTerminal));
            block.setMsg("原因可能是终端已有播放计划或已离线。");
            response.setErrMsg(new ArrayList<>());
            response.getErrMsg().add(block);
            return response;
		}

		if(CollectionUtils.isEmpty(pushTerminal)){
			response.setMsg("终端列表为空");
			return response;
		}


		Map<String,String> terminalMap = request.getTerminals().stream()
				.collect(Collectors.toMap(Function.identity(), s-> ""));

		response.setRequestId(requestId);

		String key = tenantId + "_publish_program_publish_" + requestId;
		RedisUtil.hmset(key, terminalMap);
		RedisUtil.expire(key, 130);

		for(String terminalId : offlineTerminalIdList){
			RedisUtil.hset(key, terminalId, "0");
		}

        Example example = new Example(InfoProgramTerminalMiddle.class);
        example.createCriteria().andIn("terminalId", pushTerminal);
        InfoProgramTerminalMiddle t = new InfoProgramTerminalMiddle();
        t.setProgramId(programId);
        t.setReleaseStatus("-1");
        t.setPlayerStartTime(request.getStart());
        t.setPlayerEndTime(request.getEnd());
        t.setIsLongPlay("0");
        t.setIsLeisurePlay("0");
        if(request.isStanding()){
            t.setIsLongPlay("1");
        }
        if(request.isIdle()){
            t.setIsLeisurePlay("1");
        }

        infoProgramTerminalMiddleMapper.updateByExampleSelective(t, example);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
        if(null == saasTenant){
	        return response;
        }

        String isInnerTest = saasTenant.getIsInnerTest();

        TerminalProgramCommandDto dto = infoProgramMapper.getProgramCommandById(programId, isInnerTest);

        CommandMessage cmd = new CommandMessage();
        cmd.setRequestId(requestId);
        cmd.setCmd("10001");
        cmd.setTenantId(TenantContext.getTenantId());

        cmd.setData(dto);
        if(request.getStart() != null){
            dto.setStart(sdf.format(request.getStart()));
        }
        if (request.getEnd() != null) {
            dto.setEnd(sdf.format(request.getEnd()));
        }

        dto.setStanding(request.isStanding());
        dto.setAttachmentUrl(pagePath);

        cmd.setTerminals(terminals.stream().distinct().collect(Collectors.toList()));
        cmd.setTimestamp(nowTime.getTime());
        publishService.pushCommand(cmd);

		return response;
	}

	@Override
	public List<PublishProgramErrorBlock> refreshTerminalProgramStatus(String requestId){
		if(StringUtils.isBlank(requestId)){
			return null;
		}

		String tenantId = TenantContext.getTenantId();
		String key = tenantId + "_publish_program_publish_" + requestId;
		List<PublishProgramErrorBlock> errors = new ArrayList<>();
		Map<String, String> result = RedisUtil.hgetAll(key);
		if(MapUtils.isNotEmpty(result)){
			boolean timeout = RedisUtil.ttl(key) < 10;
			Set<String> offlineTerminal = new HashSet<>(17);

			int successCount = 0;
			for(String terminalId : result.keySet()){
				//状态为空是初始状态，表明还未收到推送服务的反馈
				//0是终端离线状态，1表明终端已有节目，-1为节目正在发布状态
				String status = result.get(terminalId);
				if((timeout && "".equals(status)) || "0".equals(status)){
					offlineTerminal.add(terminalId);
				} else if("1".equals(status)){
					offlineTerminal.add(terminalId);
				} else if("2".equals(status)){
					successCount++;
				}
			}

			List<InfoReleaseTerminal> terminalList = infoReleaseTerminalMapper.selectTerminals(new ArrayList<>(result.keySet()));

			if(CollectionUtils.isNotEmpty(terminalList)){
				List<String> nameList = new ArrayList<>();
				for(InfoReleaseTerminal terminal : terminalList){
					String terminalId = terminal.getId();
					if(offlineTerminal.contains(terminalId)){
						nameList.add(terminal.getTerminalName());
					} else if("0".equals(terminal.getStatus())){
						nameList.add(terminal.getTerminalName());
					}
				}

				if(CollectionUtils.isNotEmpty(nameList)){
					PublishProgramErrorBlock block = new PublishProgramErrorBlock();
					block.setTerminals(nameList);
					block.setMsg("原因可能是终端已有播放计划或已离线。");
					errors.add(block);
				}
			}

			if(successCount + offlineTerminal.size() == result.keySet().size()){
				return errors;
			}

			if(timeout || offlineTerminal.size() == result.keySet().size()){
				RedisUtil.remove(key);
				return errors;
			}

			if(successCount == result.keySet().size()){
				return errors;
			}

			return null;
		}

		return errors;
	}

	@Override
	public Map getDingZhiProgram(Integer pageNum, Integer pageSize) {
		Map result = new HashMap();
		GetProgramResponse response = new GetProgramResponse();
		response.setPrograms(Collections.emptyList());
		Page<?> pageInfo = null;

		String tenantId = TenantContext.getTenantId();
		if(StringUtils.isBlank(tenantId)){
			return null;
		}

		SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);

		boolean isCustomTenant = false;
		String cacheKey = tenantId + "_publish_tenantInfo";
		String tenantInfo = RedisUtil.get(cacheKey);
		if(StringUtils.isNotBlank(tenantInfo)){
			if("1".equals(tenantInfo)){
				isCustomTenant = true;
			}
		} else {
			String isCustomTenantStr = saasTenant.getIsCustomizedTenant();
			if(StringUtils.isBlank(isCustomTenantStr)){
				isCustomTenantStr = "0";
			}

			isCustomTenant = "1".equals(isCustomTenantStr);
			RedisUtil.set(cacheKey, isCustomTenantStr, 3600);
		}

		result.put("tenant", isCustomTenant);

		if(!isCustomTenant){
			return result;
		}

		if(pageNum != null){
			if(pageSize == null){
				pageSize = 15;
			}
			pageInfo = PageHelper.startPage(pageNum, pageSize);
		}

		String isInnerTest = saasTenant.getIsInnerTest();

		List<TenantProgramVo> programList = infoProgramMapper.getDingZhiProgram(isInnerTest);
		response.setPrograms(programList);
		if(pageInfo != null){
			PageInfoVo pageInfoVo = new PageInfoVo();
			pageInfoVo.setPageNum(pageNum);
			pageInfoVo.setPages(pageInfo.getPages());
			pageInfoVo.setTotal(pageInfo.getTotal());
			pageInfoVo.setMore(pageInfo.getPageNum() < pageInfo.getPages());
			response.setPageInfo(pageInfoVo);
		}

		result.put("data", response);

		return result;
	}

	@Override
	public boolean getCustomProgramStatus(String tenantId){
		SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
		if(null == saasTenant){
			return false;
		}

		String isInnerTest = saasTenant.getIsInnerTest();

		if(isInnerTest == null){
			isInnerTest = "";
		}

		boolean hasCustomProgram = infoProgramMapper.getCustomProgramStatus(isInnerTest);
		return hasCustomProgram;
	}

	@Override
	public void rebootTerminal(List<String> terminals) {
		Date nowTime = new Date();

		if(CollectionUtils.isEmpty(terminals)){
			return;
		}

		List<InfoReleaseTerminal> terminalsList = infoReleaseTerminalMapper.selectTerminals(terminals);

		if(CollectionUtils.isEmpty(terminalsList) || terminalsList.size() < terminals.size()){
			logger.error("终端列表参数错误.");
			return;
		}

		CommandMessage cmd = new CommandMessage();
		cmd.setCmd("20015");
		cmd.setTenantId(TenantContext.getTenantId());
		cmd.setTerminals(terminals.stream().distinct().collect(Collectors.toList()));
		cmd.setTimestamp(nowTime.getTime());
		publishService.pushCommand(cmd);
	}

	@Override
	public void deleteTerminalProgram(String terminalId) {
		if(StringUtils.isBlank(terminalId)){
			return;
		}
		Date nowTime = new Date();

		InfoReleaseTerminal terminal = infoReleaseTerminalMapper.selectByPrimaryKey(terminalId);
		if(null == terminal){
			return;
		}

		CommandMessage cmd = new CommandMessage();
		cmd.setCmd("10003");
		cmd.setTenantId(TenantContext.getTenantId());
		cmd.addTerminal(terminalId);
		cmd.setTimestamp(nowTime.getTime());
		publishService.pushCommand(cmd);

		Example example = new Example(InfoProgramTerminalMiddle.class);
		example.createCriteria().andEqualTo("terminalId", terminalId);
		InfoProgramTerminalMiddle condition = new InfoProgramTerminalMiddle();
		condition.setProgramId("");
		infoProgramTerminalMiddleMapper.updateByExampleSelective(condition, example);
	}

	@Override
	public List<ProgramTag> getProgramTags() {
		List<InfoProgrameLabel> labels = infoProgrameLabelMapper.selectAll();

		if(CollectionUtils.isNotEmpty(labels)){
			labels.sort(Comparator.comparing(InfoProgrameLabel::getLabelSort));
			List<ProgramTag> tags = new ArrayList<>(labels.size());
			for(InfoProgrameLabel label : labels){
                String id = label.getId();
                Example example = new Example(InfoLabelProgramMiddle.class);
                example.createCriteria().andEqualTo("programLabelId",id);
                List<InfoLabelProgramMiddle> infoLabelProgramMiddles = infoLabelProgramMiddleMapper.selectByExample(example);
                if(null != infoLabelProgramMiddles && infoLabelProgramMiddles.size() > 0) {
                    ProgramTag tag = new ProgramTag();
                    tags.add(tag);
                    tag.setId(label.getId());
                    tag.setName(label.getLabelName());
                }
			}

			return tags;
		}

		return null;
	}

	@Override
	public GetProgramResponse getPrograms(String tagId, Integer pageNum, Integer pageSize) {
		GetProgramResponse response = new GetProgramResponse();
		response.setPageInfo(new PageInfoVo());

		if(null == pageNum || pageNum < 0){
			pageNum = 1;
		}

		if(null == pageSize || pageSize > 50){
			pageSize = 15;
		}

		SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
		if(null == saasTenant){
			return response;
		}

		String isInnerTest = saasTenant.getIsInnerTest();

		Page<?> pageInfo = PageHelper.startPage(pageNum, pageSize);
		List<TerminalProgramDto> dtos = infoProgramMapper.getProgramByTag(tagId, isInnerTest);
		response.setPrograms(dtos);

		if(CollectionUtils.isNotEmpty(dtos)){
			List<String> programIds = dtos.stream()
					.map(TerminalProgramDto::getProgramId)
					.collect(Collectors.toList());

			List<BizImageRecorder> imageList = bizImageRecorderMapper.getImageByIds(programIds);

			if(CollectionUtils.isNotEmpty(imageList)){
				Map<String, Map<String, List<BizImageRecorder>>> imageMap = imageList.stream()
						.collect(Collectors.groupingBy(BizImageRecorder::getBizId, Collectors.groupingBy(BizImageRecorder::getImageType)));

				for(TerminalProgramDto dto : dtos){
					if(imageMap.containsKey(dto.getProgramId())){
						if(imageMap.get(dto.getProgramId()).containsKey("primary")) {
							BizImageRecorder imageRecorder = imageMap.get(dto.getProgramId()).get("primary").get(0);
							dto.setPhotoUrl(imageRecorder.getImageUrl());
						}

						if(imageMap.get(dto.getProgramId()).containsKey("in")){
							BizImageRecorder imageRecorder = imageMap.get(dto.getProgramId()).get("in").get(0);
							dto.setPhotoUrlSmall(imageRecorder.getImageUrl());
						}
					}

				}
			}

		}

		response.getPageInfo().setPageNum(pageInfo.getPageNum());
		response.getPageInfo().setPages(pageInfo.getPages());
		response.getPageInfo().setTotal(pageInfo.getTotal());
		response.getPageInfo().setMore(pageInfo.getPageNum() < pageInfo.getPages());

		return response;
	}

	@Override
	public List<MeetingRoomTerminalDto> getRoomTerminals(String meetingRoomId, Integer status) {

		Example example = new Example(YuncmMeetingRoom.class);
		example.createCriteria().andEqualTo("deleteState","0");
		List<YuncmMeetingRoom> rooms = yuncmMeetingRoomMapper.selectByExample(example);

		List<MeetingRoomTerminalDto> dtos = null;

		if(CollectionUtils.isNotEmpty(rooms)){
			dtos = new ArrayList<>();
			List<InfoReleaseTerminal> terminals = infoReleaseTerminalMapper.selectAll();

			Map<String, List<InfoReleaseTerminal>> terminalMap = terminals.stream()
					.filter(t->t.getMeetingRoomId() != null)
					.collect(Collectors.groupingBy(InfoReleaseTerminal::getMeetingRoomId));

			List<SysDictionary> terminalTypes = dictionaryService.selectSysDictionaryByParentId(TERMINAL_TYPE);

			Map<String, String> terminalTypeMap = terminalTypes.stream()
					.collect(Collectors.toMap(SysDictionary::getDictCode, SysDictionary::getDictValue));

			for(YuncmMeetingRoom room : rooms){
				MeetingRoomTerminalDto dto = new MeetingRoomTerminalDto();
				dto.setId(room.getId());
				dto.setLocation(room.getLocation());
				dto.setName(room.getName());
				dto.setStatus(Integer.parseInt(room.getState()));
				dtos.add(dto);

				List<InfoReleaseTerminal> bindedTerminals = terminalMap.get(room.getId());
				if(CollectionUtils.isNotEmpty(bindedTerminals)){
					dto.setTerminals(new ArrayList<RoomTerminal>());

					for(InfoReleaseTerminal t : bindedTerminals){
						RoomTerminal roomTerminal = new RoomTerminal();
						roomTerminal.setId(t.getId());
						roomTerminal.setName(t.getTerminalName());
						if(StringUtils.isNotBlank(t.getTerminalTypeId())){
							roomTerminal.setType(terminalTypeMap.get(t.getTerminalTypeId()));
						}
						roomTerminal.setVersion(t.getCurrClientVer());
						dto.getTerminals().add(roomTerminal);
					}
				}
			}

			if(CollectionUtils.isNotEmpty(dtos) && status != null){
				if(status.equals(1)){
					dtos = dtos.stream().filter(dto->CollectionUtils.isNotEmpty(dto.getTerminals()))
							.collect(Collectors.toList());
				} else if(status.equals(0)){
					dtos = dtos.stream().filter(dto->CollectionUtils.isEmpty(dto.getTerminals()))
							.collect(Collectors.toList());
				}
			}
		}

		return dtos;
	}
	private Map<String, String> getTerminalTypeMap(){
		List<SysDictionary> terminalTypes = dictionaryService.selectSysDictionaryByParentId(TERMINAL_TYPE);

		Map<String, String> terminalTypeMap = terminalTypes.stream()
				.collect(Collectors.toMap(SysDictionary::getDictCode, d->d.getDictValue().toUpperCase()));

		return terminalTypeMap;
	}

	@Override
	public Integer upgradeTerminalApp(String terminalId) {
		if(StringUtils.isBlank(terminalId)){
			return -1;
		}

		if(StringUtils.isBlank(TenantContext.getTenantId())){
			return -1;
		}

		InfoReleaseTerminal terminal = infoReleaseTerminalMapper.selectByPrimaryKey(terminalId);
		if(null == terminal){
			return -1;
		}

		if(StringUtils.isBlank(terminal.getStatus()) || "0".equals(terminal.getStatus())){
			return -9;
		}

		Date nowTime = new Date();

		SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
		String verType = "1";
		if("1".equals(saasTenant.getIsInnerTest())){
			verType = "0";
		}

		PlatformInfoClientVersionLib clientVersion = platformInfoClientVersionLibService.getTerminalVersion(verType);

		if(clientVersion.getVerNum().equalsIgnoreCase(terminal.getCurrClientVer())){
			return -2;
		}

		String key = TenantContext.getTenantId() + "_publish_upgrade_" + terminalId;
		TerminalAppUpgradeStatus status = new TerminalAppUpgradeStatus();
		status.setTerminalId(terminalId);
		status.setOldVersion(terminal.getCurrClientVer());
		status.setVersion(clientVersion.getVerNum());
		status.setStatus(-1);
		String result = RedisUtil.setnx(key, JSON.toJSONString(status), TERMINAL_UPGRADE_TIMEOUT);

		//获取锁失败，该终端正在升级
		if(StringUtils.isBlank(result)){
			String value = RedisUtil.get(key);
			status = JSON.parseObject(value, TerminalAppUpgradeStatus.class);

			//当前升级失败
			if(status.getResult() != null){
				boolean restart = false;
				restart = status.getResult().equals(0) || (status.getResult().equals(1)
						 && !status.getVersion().equalsIgnoreCase(terminal.getCurrClientVer()));

				if(restart){
					RedisUtil.del(key);
					status = new TerminalAppUpgradeStatus();
					status.setTerminalId(terminalId);
					status.setVersion(clientVersion.getVerNum());
					status.setStatus(-1);
					result = RedisUtil.setnx(key, JSON.toJSONString(status), TERMINAL_UPGRADE_TIMEOUT);

					if(StringUtils.isBlank(result)){
						value = RedisUtil.get(key);
						status = JSON.parseObject(value, TerminalAppUpgradeStatus.class);

						if(status.getVersion().equalsIgnoreCase(terminal.getCurrClientVer())){
							return -4;
						}
					}
				} else{
					return -2;
				}
			} else{
				return -4;
			}
		}

		Map<String, Object> params = new HashMap<>();
		params.put("verName", clientVersion.getVersionTitle());
		params.put("verNum", clientVersion.getVerNum());
		params.put("verSize", clientVersion.getSize());
		params.put("releaseStatus", clientVersion.getReasleStatus());
		params.put("verStatus", clientVersion.getVerStatus());
		params.put("url", clientVersion.getSysAttachmentUrl());

		CommandMessage cmd = new CommandMessage();
		cmd.setCmd("20010");
		cmd.setData(params);
		cmd.setTenantId(TenantContext.getTenantId());
		cmd.addTerminal(terminalId);
		cmd.setTimestamp(nowTime.getTime());
		publishService.pushCommand(cmd);

		return 0;
	}

	@Override
	public TerminalAppUpgradeStatus queryUpgradeStatus(String terminalId) {
		if(StringUtils.isBlank(terminalId)){
			return null;
		}

		String tenantId = TenantContext.getTenantId();
		if(StringUtils.isBlank(tenantId)){
			return null;
		}

		TerminalAppUpgradeStatus status = new TerminalAppUpgradeStatus();

		String key = tenantId + "_publish_upgrade_" + terminalId;

		InfoReleaseTerminal terminal = infoReleaseTerminalMapper.selectByPrimaryKey(terminalId);
		if(null == terminal){
			status.setResult(0);
			RedisUtil.expire(key, 3);
			return status;
		}

		String s = RedisUtil.get(key);

		if(StringUtils.isNotBlank(s)){
			status = JSON.parseObject(s, TerminalAppUpgradeStatus.class);
			Integer statusCode = status.getStatus();

			String ttlKey = tenantId + "_heartbeat_" +terminalId;
			String timestamp = RedisUtil.get(ttlKey);
			if(StringUtils.isNotBlank(timestamp)){
				Date lastActive = new Date(Long.valueOf(timestamp));
				Date nowTime = new Date();
				long dateDiff = TimeUtil.getDateDiff(lastActive, nowTime, TimeUnit.SECONDS);

				if((statusCode.equals(2) && dateDiff > 65)
						|| ((statusCode.equals(1) || statusCode.equals(-1))
						&& ("0".equals(terminal.getStatus()) || dateDiff > 45))){

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					logger.info("publish: 上次心跳时间 -> {}, 当前时间 -> {}, 终端状态 -> {}, 升级状态 -> {}"
							, formatter.format(lastActive) , formatter.format(nowTime)
							,terminal.getStatus(), statusCode);

					status.setResult(0);
					status.setMsg("终端已离线");
				}
			} else {
				status.setResult(0);
				status.setMsg("终端已离线");
			}
		}

		if(status.getResult() != null){
			RedisUtil.expire(key, 5);
		}

		return status;
	}


	@Override
	public BroadcastConfig getBroadcastConfig() {
		List<SysDictionary> lengthDictionary = dictionaryService.selectSysDictionaryByParentId(BROADCAST_UNIT);
		lengthDictionary.sort(Comparator.comparing(SysDictionary::getOrderNum));

		List<SysDictionary> speedDictionary = dictionaryService.selectSysDictionaryByParentId(BROADCAST_SPEED);
		speedDictionary.sort(Comparator.comparing(SysDictionary::getOrderNum));

		List<DictionaryEntity> length = new ArrayList<>(lengthDictionary.size());
		List<DictionaryEntity> speed = new ArrayList<>(speedDictionary.size());

		for(SysDictionary d : lengthDictionary){
			DictionaryEntity entity = new DictionaryEntity(d.getDictName(), d.getDictCode());
			entity.setIsDefault(d.getIsDefault());
			length.add(entity);
		}

		for(SysDictionary d : speedDictionary){
			DictionaryEntity entity = new DictionaryEntity(d.getDictName(), d.getDictCode());
			entity.setIsDefault(d.getIsDefault());
			speed.add(entity);
		}

		BroadcastConfig config = new BroadcastConfig();
		config.setLength(length);
		config.setSpeed(speed);

		return config;
	}

	@Override
	public TerminalUpdateLog getLatestUpdateLog() {
		TerminalUpdateLog log = new TerminalUpdateLog();
		SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
		String verType = "1";
		if("1".equals(saasTenant.getIsInnerTest())){
			verType = "0";
		}
		PlatformInfoClientVersionLib clientVersion = platformInfoClientVersionLibService.getTerminalVersion(verType);

		if(clientVersion != null){
			log.setAppVersion(clientVersion.getVerNum());
			log.setUpdateDesc(clientVersion.getChangeRecode());
		}
		return log;
	}

	private void groupCommand(List<CommandMessage> cmdList){
		Map<String, List<CommandMessage>> map = cmdList.stream()
				.collect(Collectors.groupingBy(CommandMessage::getCmd));
	}

	@Override
	public void handleReceipt(Command command, boolean arrived) {
		if(null == command){
			logger.error("publish: 指令回执为空");
			return;
		}

		logger.info("publish: 收到指令回执: {}, {}", command, arrived);

		String tenantId = TenantContext.getTenantId();
		String terminalId = command.getTerminalId();
		if(StringUtils.isBlank(tenantId) || StringUtils.isBlank(terminalId)){
			return;
		}

		if(StringUtils.isBlank(command.getCmd())){
			return;
		}

		InfoReleaseTerminal terminal = new InfoReleaseTerminal();
		terminal.setId(terminalId);
		terminal.setStatus("0");
		if(arrived){
			terminal.setStatus("1");
		}
		infoReleaseTerminalMapper.updateByPrimaryKeySelective(terminal);

		if("10002".equals(command.getCmd())){
			if(StringUtils.isBlank(command.getRequestId())){
				return;
			}

			Integer status = 2;
			if(arrived){
				status = 1;
			}

			infoBroadcastMessageMapper.updateMessageStatus(terminalId, command.getRequestId(), status);
		}
	}

	@Override
	public void handleResponse(CommandResponse response) {
		if(null == response){
			return;
		}

		logger.info("publish: 收到上行指令: {}", response);

		if(StringUtils.isBlank(response.getTenantId()) || StringUtils.isBlank(response.getTerminalId())){
			return;
		}

		if(StringUtils.isBlank(response.getCmd())){
			return;
		}

		String tenantId = response.getTenantId();
		String terminalId = response.getTerminalId();
		String requestId = response.getRequestId();

		switch (response.getCmd()){
			case "30019":{
				handleTerminalUpgrade(response);
				break;
			}
			case "40001":{
				handlePublishProgram(response);
				break;
			}
//			case "10002":{
//				String msgId = requestId;
//				infoBroadcastMessageMapper.updateMessageStatus(terminalId, msgId, 1);
//				break;
//			}
			case "20015":{
				InfoReleaseTerminal t = new InfoReleaseTerminal();
				t.setId(terminalId);
				t.setStatus("0");
				infoReleaseTerminalMapper.updateByPrimaryKeySelective(t);
				break;
			}
			default:
				break;
		}
	}

	private void handleTerminalUpgrade(CommandResponse response){
		String tenantId = response.getTenantId();
		String terminalId = response.getTerminalId();
		String requestId = response.getRequestId();

		if(StringUtils.isBlank(terminalId)){
			return;
		}

		String key = TenantContext.getTenantId() + "_publish_upgrade_" + terminalId;

		if(!RedisUtil.exists(key)){
			logger.error("publish: 更新终端版本信息失败, 操作超时, 终端ID: {}", terminalId);
			return;
		}

		Map<String, Object> data = response.getData();

		if(data == null || !data.containsKey("status")){
			logger.error("publish: 更新终端版本上行指令错误, 缺少更新状态, 终端ID: {}", terminalId);
			return;
		}

		InfoReleaseTerminal terminal = infoReleaseTerminalMapper.selectByPrimaryKey(terminalId);
		if(null == terminal || StringUtils.isBlank(terminal.getHardwareId())){
			logger.error("终端信息异常");
			return;
		}


		Integer statusCode = (Integer) data.get("status");

		TerminalAppUpgradeStatus status = new TerminalAppUpgradeStatus();
		status.setTenantId(tenantId);
		status.setTerminalId(terminalId);

		if(data.get("result") != null){
			status.setResult((int)data.get("result"));
		}

		if(data.get("version") != null){
			status.setVersion(data.get("version").toString());
		}

		if(data.get("msg") != null){
			status.setMsg(data.get("msg").toString());
		}

		if(data.get("progress") != null){
			status.setProgress(Integer.parseInt(data.get("progress").toString()));
		}

		if(data.get("status") != null){
			status.setStatus(Integer.parseInt(data.get("status").toString()));
		}

		Long ttl = RedisUtil.ttl(key);

		if(null == ttl || ttl < 0){
			logger.error("publish: 更新升级状态时Redis出错, 升级信息: {}", JSON.toJSONString(status));
			return;
		}

		String preVersion = "";
		String value = RedisUtil.get(key);
		if(StringUtils.isNotBlank(value)){
			try{
				TerminalAppUpgradeStatus currentStatus = JSON.parseObject(value, TerminalAppUpgradeStatus.class);
				if(currentStatus != null){
					preVersion = currentStatus.getOldVersion();
					status.setOldVersion(currentStatus.getOldVersion());
				}
			} catch (Exception e){
				logger.error("publish: 查询升级状态时Redis出错, 升级信息: {}", value);
			}
		}

		if(null == data.get("result")){
			RedisUtil.set(key, JSON.toJSONString(status), ttl.intValue());
			return;
		}


		RedisUtil.set(key, JSON.toJSONString(status), 3);

		if(data.get("result").equals(0)){
			logger.error("publish: 更新终端版本信息失败, 终端ID: {}, 异常信息: {}", terminalId, data.get("msg"));
			return;
		} else if(data.get("result").equals(1)){
//			if(status.getVersion().equals(terminal.getCurrClientVer())){
//				logger.error("publish: 更新终端版本信息失败, 终端版本已更新, 升级版本: {}, 终端ID: {}", status.getVersion(), terminalId);
//				return;
//			}

			String hardwareId = terminal.getHardwareId();

			try{
				InfoReleaseTerminal condition = new InfoReleaseTerminal();
				condition.setId(terminalId);
				condition.setCurrClientVer(status.getVersion());
				infoReleaseTerminalMapper.updateByPrimaryKeySelective(condition);
			} catch(Exception e){
				logger.error("publish: 更新终端版本信息失败, 异常信息: {}", e);
			}

			SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);

			if(null == saasTenant){
				logger.error("publish: 更新终端版本信息失败, 租户不存在, 租户ID: {}", tenantId);
				return;
			}

			try{
				PlatformClientVersionUpgradeRecorder upgradeRecorder = new PlatformClientVersionUpgradeRecorder();
				upgradeRecorder.setId(CreateUUIdUtil.Uuid());
				upgradeRecorder.setTenantId(tenantId);
				upgradeRecorder.setType("10001");
				upgradeRecorder.setTenantName(saasTenant.getTenantName());
				upgradeRecorder.setUserName(saasTenant.getContacts());
				upgradeRecorder.setPhoneNumber(saasTenant.getContactsTel());
				upgradeRecorder.setTenantNamePinyin(PingYinUtil.getPingYin(saasTenant.getTenantName()));
				upgradeRecorder.setLastVer(preVersion);
				upgradeRecorder.setCurrentVer(status.getVersion());
				upgradeRecorder.setHardwareId(hardwareId);
				upgradeRecorder.setUpgradeLog(status.getMsg());
				upgradeRecorder.setCreateTime(new Date());
				upgradeRecorder.setStatus(Integer.toString(status.getResult()));
				platformClientVersionUpgradeRecorderService.add(upgradeRecorder);
			} catch (Exception e){
				logger.error("publish: 记录终端版本升级日志失败, 异常信息: {}", e);
			}
		}
	}

	private void handlePublishProgram(CommandResponse response){
		String tenantId = TenantContext.getTenantId();
		String requestId = response.getRequestId();
		String terminalId = response.getTerminalId();


		if(StringUtils.isBlank(tenantId)
				|| StringUtils.isBlank(requestId)
				|| StringUtils.isBlank(terminalId)){
			return;
		}

		Map<String, Object> data = response.getData();
		if(!data.containsKey("programId")){
			logger.error("publish: 40001上行指令异常, 节目ID为空, 上行指令: {}", response);
			return;
		}

		String key = tenantId + "_publish_program_publish_" + requestId;

		Long ttl = RedisUtil.ttl(key);
		if(null == ttl || ttl < 0){
			logger.error("publish: 处理40001上行指令出错, 操作已超时, 上行指令: {}", response);
		}

		ttl += 3;
		RedisUtil.hset(key, terminalId, "2");
		RedisUtil.expire(key, ttl.intValue());

		String programId = data.get("programId").toString();

		try{
			Example example = new Example(InfoProgramTerminalMiddle.class);
			example.createCriteria()
					.andEqualTo("terminalId", terminalId)
					.andEqualTo("programId", programId)
					.andEqualTo("releaseStatus", "-1");

			InfoProgramTerminalMiddle t = new InfoProgramTerminalMiddle();
			t.setProgramId(programId);
			t.setReleaseStatus("1");
			infoProgramTerminalMiddleMapper.updateByExampleSelective(t, example);
		} catch (Exception e){
			logger.error("publish: 更新节目发布状态异常, 上行指令: {}, 异常信息: ", response, e);
		}
	}

	@Override
	public boolean updateTerminalStatus(String terminalId, Integer status){
//		if(StringUtils.isBlank(TenantContext.getTenantId())){
//			return false;
//		}

		if(StringUtils.isBlank(terminalId) || status == null){
			return false;
		}

		InfoReleaseTerminal terminal = new InfoReleaseTerminal();
		terminal.setId(terminalId);
		terminal.setStatus(status.toString());
		terminal.setModifyTime(new Date());
        int i = infoReleaseTerminalMapper.updateByPrimaryKeySelective(terminal);
        if(i>0){
            return true;
        }else {
            return false;
        }
    }

	@Override
	public InfoProgramTerminalMiddle getInfoProgramTerminalMiddleByTerminalId(String terminalId) {
		Example example = new Example(InfoProgramTerminalMiddle.class);
		example.createCriteria().andEqualTo("terminalId",terminalId);
		List<InfoProgramTerminalMiddle> middles = this.infoProgramTerminalMiddleMapper.selectByExample(example);
		if(middles.size()==0){
			return null;
		}else {
			return middles.get(0);
		}
	}

	@Override
	public TerminalProgramCommandDto selectProgramCommandById(String programId) {
		SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
		if(null == saasTenant){
			return null;
		}

		String isInnerTest = saasTenant.getIsInnerTest();

		return infoProgramMapper.getProgramCommandById(programId, isInnerTest);
	}

	private Map insertMessage(List<String> terminals, BroadcastCommandVo command){
	    Map map = new HashMap();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		InfoBroadcastMessage message = new InfoBroadcastMessage();
		message.setId(CreateUUIdUtil.Uuid());
		message.setMessageContent(command.getContent());
		message.setSpeed(command.getSpeed());
        Integer length = command.getLength();
        message.setLength(length);
        String unit = command.getUnit();
        message.setUnit(unit);
        Integer lengthInSeconds = 0;
        String type = "2";
        if(unit.indexOf("_SECOND") != -1){
            type = "1";
            lengthInSeconds = length;
        } else if(unit.indexOf("_HOUR") != -1){
            type = "3";
            lengthInSeconds = length * 3600;
        } else if(unit.indexOf("_DAY") != -1){
            type = "4";
            lengthInSeconds = length * 3600 * 24;
        }else{
            lengthInSeconds = length * 60;
        }

        Date date = new Date();
        message.setSendTime(date);
        message.setExpiryTime(DateUtils.addSeconds(date, lengthInSeconds));
		infoBroadcastMessageMapper.insertSelective(message);
        for (String terminalId:terminals) {
            if(StringUtils.isNotBlank(terminalId)){
				InfoReleaseTerminal infoReleaseTerminal = infoReleaseTerminalMapper.selectByPrimaryKey(terminalId);
                Example example = new Example(InfoMessageTerminalMiddle.class);
                example.createCriteria().andEqualTo("terminalId", terminalId);
                InfoMessageTerminalMiddle middle = new InfoMessageTerminalMiddle();
                middle.setBroadcastMessageId(message.getId());
                if(null != infoReleaseTerminal){
                    String status = infoReleaseTerminal.getStatus();
                    logger.debug("status ::::"+status);
                    if(StringUtils.isBlank(status)) {
                        middle.setMsgStatus(2);
                    }else if("0".equals(status)){
						middle.setMsgStatus(2);
					}else if("1".equals(status)){
						middle.setMsgStatus(0);
					}
                }else {
                    middle.setMsgStatus(0);
                }
                logger.debug("msgStatus :::"+middle.getMsgStatus());
                infoMessageTerminalMiddleMapper.updateByExampleSelective(middle, example);
            }

        }
        map.put("msgId",message.getId());
        map.put("timeLength",lengthInSeconds);
		return map;
	}


    /**
     * 根据终端id查询是否有绑定的节目功能接口
     * @param termianlId
     * @return
     */
    public List<InfoProgramTerminalMiddle> selectProgramTerminalMiddleByTerminalId(String termianlId){
        if(org.apache.commons.lang3.StringUtils.isNotBlank(termianlId)){
            Example example = new Example(InfoProgramTerminalMiddle.class);
            example.createCriteria().andEqualTo("terminalId",termianlId);
            return infoProgramTerminalMiddleMapper.selectByExample(example);
        }
        return null;
    }

	@Override
	public TerminalProgramCommandDto obtainProgramCommandById(String programId, String tenantId) {
		SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
		if(null == saasTenant){
			return null;
		}
		String isInnerTest = saasTenant.getIsInnerTest();

		return infoProgramMapper.queryProgramCommandById(programId, isInnerTest);
	}


}
