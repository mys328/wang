package com.thinkwin.web.controller;

import com.alibaba.fastjson.JSON;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.publish.*;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.BizImageRecorder;
import com.thinkwin.common.model.db.InfoProgramTerminalMiddle;
import com.thinkwin.common.model.db.InfoReleaseTerminal;
import com.thinkwin.common.model.db.SysAttachment;
import com.thinkwin.common.model.publish.PlatformTenantTerminalMiddle;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.FastdfsVo;
import com.thinkwin.common.vo.FileVo;
import com.thinkwin.common.vo.publish.*;
import com.thinkwin.common.dto.publish.TerminalScreenshotDto;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.publish.service.PlatformTenantTerminalMiddleService;
import com.thinkwin.publish.service.PublishService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.web.ResourceNotFoundException;
import com.thinkwin.yuncm.service.BizImageRecorderService;
import com.thinkwin.yuncm.service.ConferenceService;
import com.thinkwin.yuncm.service.InfoReleaseTerminalService;
import com.thinkwin.yuncm.service.TerminalService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.thinkwin.yuncm.service.TerminalService.TERMINAL_SCREENSHOT_TIMEOUT;

@RequestMapping(value={"/terminal", "/terminalClient"})
@Controller
public class TerminalClientController {
	private static Logger logger = LoggerFactory.getLogger(TerminalClientController.class);

	@Resource
	TerminalService terminalService;

	@Resource
	FileUploadService fileUploadService;

	@Resource
	InfoReleaseTerminalService infoReleaseTerminalService;

	@Resource
	BizImageRecorderService bizImageRecorderService;

	@Resource
	PlatformTenantTerminalMiddleService platformTenantTerminalMiddleService;

	@Resource
	PublishService publishService;

	@Resource
	ConferenceService conferenceService;

	@Resource
	SaasTenantService saasTenantCoreService;


	/**
	 * 获取终端详情
	 * @return
	 */
	@RequestMapping(value = "/get")
	@ResponseBody
	public Object get(@RequestParam("terminalId")String terminalId) {
		BusinessExceptionStatusEnum errMsg = BusinessExceptionStatusEnum.Success;
		Object result = null;

		if(StringUtils.isEmpty(terminalId)){
			errMsg = BusinessExceptionStatusEnum.TerminalNotExists;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, errMsg.getDescription(), result);
		}

		TerminalDto dto = terminalService.getTerminalById(terminalId);

		if(null == dto){
			errMsg = BusinessExceptionStatusEnum.TerminalNotExists;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, errMsg.getDescription(), result);
		}

		result = dto;

		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, errMsg.getDescription(), result);
	}

	/**
	 * 发送获取实时画面指令
	 * @return
	 */
	@RequestMapping(value = "/sendScreenshotCommand", method = RequestMethod.POST)
	@ResponseBody
	public Object sendScreenshotCommand(@ModelAttribute("request")CaptureScreenshotRequest request) {
        String tenantId = TenantContext.getTenantId();

        if(StringUtils.isBlank(tenantId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "暂无在线终端。", null);
        }

        String requestId = terminalService.sendScreenshotCommand(request);

		if(StringUtils.isBlank(request.getRequestId())
				&& StringUtils.isBlank(requestId)){
			if(CollectionUtils.isEmpty(request.getIds()) || request.getIds().size() > 1){
				return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "暂无在线终端。", null);
			} else {
				return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "终端已离线。", null);
			}
		}

		Map<String, Object> response = new HashMap<>();
		response.put("requestId", requestId);
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", response);
	}

	/**
	 * 刷新终端实时画面
	 * @return
	 */
	@RequestMapping(value = "/refreshScreenshot", method = RequestMethod.POST)
	@ResponseBody
	public Object refreshScreenshot(@ModelAttribute("request")CaptureScreenshotRequest request, HttpServletRequest req) {
        String id = req.getSession().getId();
        CaptureScreenshotResponse response = terminalService.refreshScreenshot(request);
		if(null == response){
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "操作超时", null);
		}

		if(CollectionUtils.isNotEmpty(response.getTerminals())){
			String token = req.getParameter("token");
			for(TerminalScreenshotVo vo : response.getTerminals()){
                String pictureUrl = vo.getPictureUrl();
                if(StringUtils.isNotBlank(pictureUrl)){
                    String substring = pictureUrl.substring(0, pictureUrl.lastIndexOf("?"));
                    String substring1 = pictureUrl.substring(pictureUrl.indexOf("?"));
                    pictureUrl = substring+";jsessionid="+id+substring1;
					vo.setPictureUrl(pictureUrl+"&token=" + token);
				}
                String smallPictureUrl = vo.getSmallPictureUrl();
                if(StringUtils.isNotBlank(smallPictureUrl)){
                    String substring = smallPictureUrl.substring(0, smallPictureUrl.lastIndexOf("?"));
                    String substring1 = smallPictureUrl.substring(smallPictureUrl.indexOf("?"));
                    smallPictureUrl = substring+";jsessionid="+id+substring1;
					vo.setSmallPictureUrl(smallPictureUrl+"&token=" + token);
				}
			}

			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", response);
		}

		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "终端已离线", response);
	}

	/**
	 * 获取终端列表
	 * @return
	 */
	@RequestMapping(value = "/queryTerminals", method = RequestMethod.POST)
	@ResponseBody
	public Object queryTerminals(@ModelAttribute("request")SearchTerminalRequest request) {
		BusinessExceptionStatusEnum errMsg = BusinessExceptionStatusEnum.Success;
		Object result = null;

		if(null == request){
			errMsg = BusinessExceptionStatusEnum.InvalidSearchRequest;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, errMsg.getDescription(), result);
		}

		SearchTerminalResponse response = terminalService.queryTerminals(request);

		result = response;

		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, errMsg.getDescription(), result);
	}

	/**
	 * 更新终端信息
	 * @return
	 */
	@RequestMapping(value = "/updateTerminal", method = RequestMethod.POST)
	@ResponseBody
	public Object updateTerminal(TerminalVo terminalVo) throws IOException {
		String terminalId = terminalVo.getTerminalId();
		String name = terminalVo.getName();
		if(StringUtils.isBlank(name)){
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"终端名称不能为空",null);
		}
		String roomId = terminalVo.getRoomId();
		MultipartFile picture = terminalVo.getPicture();
		UpdateTerminalRequst request = new UpdateTerminalRequst();
		request.setId(terminalId);
		request.setName(name);
		request.setRoomId(roomId);
        String city = terminalVo.getCity();
        String province = terminalVo.getProvince();
        String county = terminalVo.getCounty();
        String street = terminalVo.getStreet();
        if(StringUtils.isBlank(city) && StringUtils.isBlank(province)&& StringUtils.isBlank(county) && StringUtils.isBlank(street)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"终端位置不能为空",null);
        }
        request.setCity(city);
		request.setProvince(province);
		request.setCounty(county);
		request.setStreet(street);

		if(StringUtils.isBlank(terminalId)){
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "终端ID为空", null);
		}

		InfoReleaseTerminal terminal = infoReleaseTerminalService.selectInfoReleaseTerminalById(terminalId);

		if(null == terminal){
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "终端不存在", null);
		}

		String tenantId = TenantContext.getTenantId();
		if(picture != null){
			request.setBackgroundPicture(picture.getBytes());

			String ext = picture.getOriginalFilename().substring(picture.getOriginalFilename().lastIndexOf(".") + 1);
			request.setPictureExt(ext);

			if(StringUtils.isNotBlank(request.getBackgroundId())){
				List<SysAttachment> sysAttachments = this.fileUploadService.deleteFileUpload(terminal.getBackgroundId(),tenantId);
				FileUploadUtil.deleteFile(sysAttachments);
			}

			String userId = TenantContext.getUserInfo().getUserId();
			List<FastdfsVo> vos = FileUploadUtil.fileUpload(null, request.getBackgroundPicture(),request.getPictureExt());
			FileVo fileVo = fileUploadService.insertFileUpload( "3", userId,null, null, request.getPictureExt(),vos);
			request.setBackgroundId(fileVo.getId());
			request.setBackgroundUrl(fileVo.getPrimary());
		}
		//表示清空租户图片，用公共图片
        if(terminalVo.getUpdateBackground().equals("1")){
	        if(StringUtils.isNotBlank(terminal.getBackgroundId())){
		        List<SysAttachment> sysAttachments = this.fileUploadService.deleteFileUpload(terminal.getBackgroundId(),tenantId);
		        FileUploadUtil.deleteFile(sysAttachments);
	        }
            request.setBackgroundId("");
            request.setBackgroundUrl("");
        }
		terminalService.updateTerminal(request);
		pushMessage(terminalId,tenantId,terminalVo,terminal);
		//如果终端名称不等于空  维护租户终端中间表的终端名称字段
		if(StringUtils.isNotBlank(request.getName())){
			PlatformTenantTerminalMiddle platformTenantTerminalMiddle = platformTenantTerminalMiddleService.selectPTenantTerminalMByTerminalId(request.getId());
			platformTenantTerminalMiddle.setTerminalName(request.getName());
			platformTenantTerminalMiddleService.updatePTenantTerminalMByEntity(platformTenantTerminalMiddle);
		}
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", null);
	}

	/**
	 * 插播消息
	 * @return
	 */
	@RequestMapping(value = "/broadcastMessage", method = RequestMethod.POST)
	@ResponseBody
	public Object broadcastMessage(@RequestBody BroadcastMessageVo command) {
		terminalService.broadcastMessage(command.getTerminals(), command.getMessage());
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", null);
	}

	/**
	 * 发布节目
	 * @return
	 */
	@RequestMapping(value = "/publishProgram", method = RequestMethod.POST)
	@ResponseBody
	public Object publishProgram(@ModelAttribute("request") PublishProgramRequest request, HttpServletRequest req) {
		String pagePath = syncProgramFile(request.getProgramId(), req);
		if(StringUtils.isBlank(pagePath)){
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "找不到节目文件", null);
		}
		pagePath = pagePath+"&tenantId="+TenantContext.getTenantId();
		PublishProgramResponse response = terminalService.publishProgram(request, pagePath);
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", response);
	}

	private String syncProgramFile(String programId, HttpServletRequest req){
		String path = req.getSession().getServletContext().getRealPath("/");

		String fileDir = path + "template"+ File.separator + programId;
		String newPath = fileDir + File.separator ;
		File filePath = new File(fileDir);
		logger.debug("节目路径: " + filePath.toString());
		if(!LocalCacheUitl.containsKey(programId)){
			LocalCacheUitl.put(programId,programId);
		}
		String cachekey = (String) LocalCacheUitl.get(programId);

		synchronized (cachekey){
			if(!filePath.exists()){
				List<BizImageRecorder> fileList = bizImageRecorderService.findByBizIDType(programId, "2");

				if(CollectionUtils.isNotEmpty(fileList) && fileList.size() == 1){
					logger.debug("节目记录信息: {}", fileList.get(0).getImageId());
					SysAttachment sysAttachment=this.fileUploadService.selectByidFile(fileList.get(0).getImageId());
					logger.debug("节目附件信息: {}", sysAttachment.getAttachmentPath());
					FileUploadUtil.syncProgramFile(newPath, filePath, sysAttachment);
				} else {
					logger.error("publish: 获取节目文件失败, 找不到文件, 节目ID: {}", programId);
				}
			}
		}

		if(!filePath.exists()){
			return null;
//			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "找不到节目文件", null);
		}

		String pageName = findIndexPath(newPath + "program");
		if(StringUtils.isBlank(pageName)){
			return null;
//			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "找不到节目文件", null);
		}

		String pagePath = "";
		pagePath += req.getScheme() + "://" + req.getServerName();
		if(req.getServerPort() != 80){
			pagePath += ":" + req.getServerPort();
		}
		pagePath = pagePath + "/system/program?programId=" + programId;
//		pagePath += "/template/" + programId + "/program/" + pageName;

		return pagePath;
	}

	/**
	 * 远程重启
	 * @return
	 */
	@RequestMapping(value = "/rebootTerminal")
	@ResponseBody
	public Object rebootTerminal(@RequestParam("terminals")List<String> terminals) {
		terminalService.rebootTerminal(terminals);
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", null);
	}

	/**
	 * 删除终端当前节目
	 * @return
	 */
	@RequestMapping(value = "/deleteTerminalProgram")
	@ResponseBody
	public Object deleteTerminalProgram(String terminalId) {
		List<InfoProgramTerminalMiddle> list = terminalService.selectProgramTerminalMiddleByTerminalId(terminalId);
		String programId = "";
		if(null !=list && list.size() > 0){
            for (InfoProgramTerminalMiddle itm:list) {
                if(null != itm){
                     programId = itm.getProgramId();
                    if(org.apache.commons.lang3.StringUtils.isBlank(programId)){
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "该节目已删除");
                    }
                }
            }
		}else{
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "该节目已删除");
        }
		terminalService.deleteTerminalProgram(terminalId);
		RedisUtil.remove(TenantContext.getTenantId() + "_QYH_play_program_" + programId);
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", null);
	}

	/**
	 * 获取节目标签
	 * @return
	 */
	@RequestMapping(value = "/getProgramTags")
	@ResponseBody
	public Object getProgramTags() {
		String tenantId = TenantContext.getTenantId();

		boolean isCustomTenant = false;
		String cacheKey = tenantId + "_publish_tenantInfo";
		String tenantInfo = RedisUtil.get(cacheKey);
		if(StringUtils.isNotBlank(tenantInfo)){
			if("1".equals(tenantInfo)){
				isCustomTenant = true;
			}
		} else {
			SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
			String isCustomTenantStr = saasTenant.getIsCustomizedTenant();
			if(StringUtils.isBlank(isCustomTenantStr)){
				isCustomTenantStr = "0";
			}

			RedisUtil.set(cacheKey, isCustomTenantStr, 3600);
			isCustomTenant = "1".equals(isCustomTenantStr);
		}

		List<ProgramTag> tags = terminalService.getProgramTags();
		Map<String, Object> params = new HashMap<>(5);
		params.put("isCustomTenant", isCustomTenant);
		params.put("tags", tags);
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", params);
	}

	/**
	 * 获取节目列表
	 * @return
	 */
	@RequestMapping(value = "/getPrograms")
	@ResponseBody
	public Object getPrograms(String tagId, Integer pageNum, Integer pageSize) {
		GetProgramResponse response = terminalService.getPrograms(tagId, pageNum, pageSize);
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", response);
	}

	/**
	 * 升级终端APP版本
	 * @return
	 */
	@RequestMapping(value = "/updateApp")
	@ResponseBody
	public Object updateApp(String terminalId) {
		Integer result = terminalService.upgradeTerminalApp(terminalId);
		if(result == -1){
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "参数错误", null);
		} else if(result == -2){
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "已是最新版本", null);
		} else if(result == -3){
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "系统异常", null);
		} else if(result == -4){
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "正在升级中", null);
		} else if(result == -9){
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "终端已离线", null);
		}
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", null);
	}

	/**
	 * 获取终端app升级状态
	 * @return
	 */
	@RequestMapping(value = "/queryUpgradeStatus")
	@ResponseBody
	public Object queryUpgradeStatus(String id) {
		TerminalAppUpgradeStatus status = terminalService.queryUpgradeStatus(id);
		if(null == status){
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "升级操作超时", null);
		} else if(status.getResult() != null){
			if(status.getResult() == -2){
				return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "终端已离线", null);
			}
		}
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", status);
	}

	/**
	 * 上传终端截屏
	 * @return
	 */
	@RequestMapping(value = "/uploadSnapshot", method = RequestMethod.POST)
	@ResponseBody
	public Object uploadSnapshot(String terminalId, String requestId, MultipartFile picture, HttpServletRequest request) throws IOException {
		logger.debug("publish: 正在接收终端实时画面, 终端ID: {}, requestId: {}", terminalId, requestId);

		String tenantId = TenantContext.getTenantId();
		String key = tenantId + "_publish_screen_" + requestId;
		String msg = request.getParameter("msg");

		if(StringUtils.isNotBlank(msg)){
			logger.error("publish: 处理获取实时画面指令异常: {}", msg);
			TerminalScreenshotDto vo = new TerminalScreenshotDto();
			vo.setTerminalId(terminalId);
			vo.setRequestId(requestId);
			vo.setTenantId(tenantId);
			vo.setTimestamp(new Date().getTime());
			vo.setStatus(1);

			RedisUtil.hset(key, terminalId, JSON.toJSONString(vo));

			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "已记录异常", null);
		}

		if(picture != null){

			//key的超时时间设置为23秒，在 TTL < 3 秒时客户端可能无法下载到图片，所以判定操作超时
			Long ttl = RedisUtil.ttl(key);

			boolean timeout = ttl == null || ttl < 3;
			if(timeout){
				return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "操作超时", null);
			}

			String ext = picture.getOriginalFilename().substring(picture.getOriginalFilename().lastIndexOf(".") + 1);

			String bigScreenKey = tenantId + "_publish_screen_" + terminalId + "_" + requestId;
//			byte[] bigPicture = ResizeImage.resizeImageByte(picture.getBytes(), 968, 544);
			RedisUtil.set(bigScreenKey.getBytes(), picture.getBytes(), TERMINAL_SCREENSHOT_TIMEOUT);

			String smallScreenKey = tenantId + "_publish_screen_" + terminalId + "_" + requestId + "_small";
			byte[] smallPicture = ResizeImage.resizeImageByte(picture.getBytes(), 240, 135);
			RedisUtil.set(smallScreenKey.getBytes(), smallPicture, TERMINAL_SCREENSHOT_TIMEOUT);

			StringBuilder contextpath = new StringBuilder();
			contextpath.append(request.getScheme()).append("://").append(request.getServerName());
			if(request.getServerPort() != 80){
				contextpath.append(":").append(request.getServerPort());
			}

			contextpath.append(request.getContextPath());
			if(StringUtils.isEmpty(request.getContextPath())){
				contextpath.append("/");
			}
			contextpath.append("terminal/screenshot");
			contextpath.append("?requestId=").append(requestId).append("&terminalId=").append(terminalId);
			String picUrl = contextpath.toString();
			String smallPicUrl = contextpath.append("&small=1").toString();

//			String userId = TenantContext.getUserInfo().getUserId();
//			List<FastdfsVo> vos = FileUploadUtil.fileUpload(null, picture.getBytes(), ext);
//			FileVo fileVo = fileUploadService.insertFileUpload("3", userId, null, null, ext, vos);

			TerminalScreenshotDto screenshotDto = new TerminalScreenshotDto();
			screenshotDto.setTerminalId(terminalId);
			screenshotDto.setRequestId(requestId);
			screenshotDto.setTenantId(tenantId);
			screenshotDto.setPictureUrl(picUrl);
			screenshotDto.setSmallPictureUrl(smallPicUrl);
			screenshotDto.setExt(ext);
			screenshotDto.setStatus(0);
			screenshotDto.setTimestamp(new Date().getTime());

			RedisUtil.hset(key, terminalId, JSON.toJSONString(screenshotDto));

			logger.debug("publish: 终端实时画面上传成功, 终端ID: {}, requestId: {}", terminalId, requestId);
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "上传成功", null);
		}

		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "图片为空", null);
	}

	@RequestMapping(value = "/screenshot", method=RequestMethod.GET)
	public ResponseEntity<byte[]> screenshot(String requestId, String terminalId, String small){
		String tenantId = TenantContext.getTenantId();
		String key = tenantId + "_publish_screen_" + requestId;

		if(!RedisUtil.exists(key)){
			throw new ResourceNotFoundException();
		}

		String value = RedisUtil.hget(key, terminalId);
		TerminalScreenshotDto vo = JSON.parseObject(value, TerminalScreenshotDto.class);
		String ext = vo.getExt();

		String screenKey = tenantId + "_publish_screen_" + terminalId + "_" + requestId;
		if(StringUtils.isNotBlank(small)){
			screenKey = tenantId + "_publish_screen_" + terminalId + "_" + requestId + "_small";
		}
		byte[] picture = RedisUtil.get(screenKey.getBytes());

		if(ArrayUtils.isEmpty(picture)){
			throw new ResourceNotFoundException();
		}

		if(StringUtils.isBlank(ext)){
			ext = "png";
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("image", ext));
//		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		headers.setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).getHeaderValue());
		return new ResponseEntity<>(picture, headers, HttpStatus.OK);
	}

	/**
	 * 获取会议室绑定的终端设备
	 * @return
	 */
	@RequestMapping(value = "/getRoomTerminals")
	@ResponseBody
	public Object getRoomTerminals(String meetingRoomId, Integer status) {
		List<MeetingRoomTerminalDto> rooms = terminalService.getRoomTerminals(meetingRoomId, status);
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", rooms);
	}

	/**
	 * 获取插播消息配置数据
	 * @return
	 */
	@RequestMapping(value = "/getBroadcastConfig")
	@ResponseBody
	public Object getBroadcastConfig() {
		BroadcastConfig config = terminalService.getBroadcastConfig();
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", config);
	}

	/**
	 * 获取APP最近一次更新记录
	 * @return
	 */
	@RequestMapping(value = "/getLatestUpdateLog")
	@ResponseBody
	public Object getLatestUpdateLog() {
		TerminalUpdateLog log = terminalService.getLatestUpdateLog();
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", log);
	}

	/**
	 * 刷新节目发布状态
	 * @return
	 */
	@RequestMapping(value = "/refreshTerminalProgramStatus")
	@ResponseBody
	public Object refreshTerminalProgramStatus(String requestId) {
		Integer ifSuc = 1;
		if(StringUtils.isBlank(requestId)){
			ifSuc = 0;
			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(ifSuc, "参数为空", null);
		}

		List<PublishProgramErrorBlock> errorBlocks = terminalService.refreshTerminalProgramStatus(requestId);
		Map<String, Object> params = new HashMap<>();
		params.put("errMsg", errorBlocks);

		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(ifSuc, "", params);
	}


	/**
	 * 获取定制节目列表
	 * @return
	 */
	@RequestMapping(value = "/getDingZhiProgram")
	@ResponseBody
	public Object getDingZhiProgram(Integer pageNum, Integer pageSize) {
		ResponseResult result = new ResponseResult();
		result.setIfSuc(1);
		result.setMsg("操作成功");
		Map response = terminalService.getDingZhiProgram(pageNum, pageSize);

		if(null == response){
			result.setIfSuc(0);
			result.setMsg("传入参数异常");
			return result;
		}

		boolean IsCustomTenant = false;
		if(response.get("tenant") != null){
			IsCustomTenant = (boolean)response.get("tenant");
		}

		if(!IsCustomTenant){
			result.setCode("1");
			result.setMsg("企业权限变更，请刷新后重试。");
			return result;
		}

		GetProgramResponse data = (GetProgramResponse)response.get("data");
		result.setData(data);

		return result;
	}

	/**
	 * 查询租户是否包含定制节目
	 * @return
	 */
	@RequestMapping(value = "/getCustomProgramStatus")
	@ResponseBody
	public Object getCustomProgramStatus() {
		String tenantId = TenantContext.getTenantId();
		boolean hasCustomProgram = terminalService.getCustomProgramStatus(tenantId);
		Map<String, Object> params = new HashMap<>();
		params.put("hasCustomProgram", hasCustomProgram);

		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", params);
	}

	/**
	 * 获取节目页面
	 * @return
	 */
	@RequestMapping(value = "/getProgramPage")
	@ResponseBody
	public Object getProgramPage(HttpServletRequest request, String programId) {
//		if(StringUtils.isEmpty(programId)){
//			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "节目不存在", null);
//		}
//
//		String path = request.getSession().getServletContext().getRealPath("/");
//
//		String fileDir = path + "template"+ File.separator + programId;
//		String newPath = fileDir + File.separator ;
//		File filePath = new File(fileDir);
//		logger.info("节目路径: " + filePath.toString());
//		if(!filePath.exists()){
//			List<BizImageRecorder> fileList = bizImageRecorderService.findByBizIDType(programId, "2");
//
//			if(CollectionUtils.isNotEmpty(fileList) && fileList.size() == 1){
//				SysAttachment sysAttachment=this.fileUploadService.selectByidFile(fileList.get(0).getImageId());
//				FileUploadUtil.syncProgramFile(newPath, filePath, sysAttachment);
//			} else {
//				logger.error("publish: 获取节目文件失败, 找不到文件, 节目ID: {}", programId);
//			}
//		}
//
//		if(!filePath.exists()){
//			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "找不到节目文件", null);
//		}
//
//		String pageName = findIndexPath(newPath + "program");
//		if(StringUtils.isBlank(pageName)){
//			return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "找不到节目文件", null);
//		}
//
//		Map<String, Object> params = new HashMap<>();
//
//		String pagePath = "";
//		pagePath += request.getScheme() + "://" + request.getServerName();
//		if(request.getServerPort() != 80){
//			pagePath += ":" + request.getServerPort();
//		}
//
//		pagePath += "/template/" + programId + "/program/" + pageName;
//
//		params.put("programPageUrl", pagePath);
		return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", null);
	}

	/**
	 * 节目目录下搜索.html文件
	 * @param programRootPath 存储节目文件的根目录
	 * @return html文件名称
	 */
	private String findIndexPath(String programRootPath){
		File rootPath = new File(programRootPath);
		if(rootPath.exists()){
			String[] fileList = rootPath.list();
			for(int i=0; i<fileList.length; i++) {
				if(fileList[i].endsWith(".html")){
					return fileList[i];
				}
			}
		}

		return "";
	}

	public void pushMessage(String terminalId,String tenantId,TerminalVo terminalVo,InfoReleaseTerminal terminal ){

		try {
			List<String> list =new ArrayList<>();
			Map map = new HashMap();
			//将模板信息存入redis
			String programId = this.conferenceService.getProgramId(terminalId);
			String flag = "";
			if(StringUtils.isNotBlank(programId)) {
				String type = RedisUtil.get(tenantId + "_QYH_play_program_" + programId);
				if(type != null){
					String str[] = type.split(",");
					boolean b = false;
					for (int i = 0; i < str.length; i++) {
						 if("C8001".equals(str[i])){
						 	if(terminal.getMeetingRoomId() != null){
						 		if(!terminal.getMeetingRoomId().equals(terminalVo.getRoomId())){
									flag += "C8001,";
									b = true;
								}
							}else {
								flag += "C8001,";
								b = true;
							}
						 }
						if("C9001".equals(str[i])){
							if(terminal.getCity() != null ){
								if(!terminal.getCity().equals(terminalVo.getCity())){
									flag += "C9001,";
								}else {
									if(terminal.getCounty() != null) {
										if (!terminal.getCounty().equals(terminalVo.getCounty())) {
											flag += "C9001,";
										}
									}else {
										flag += "C9001,";
									}
								}
							}else {
								flag += "C9001,";
							}
						}
					}
					for (int i = 0; i < str.length; i++){
						/**
						 * 当前节目
						 */
						if ("C8002".equals(str[i])) {
							if (b) {
								flag += "C8002,";
							}
						}
						/**
						 * 下前节目
						 */
						if ("C8003".equals(str[i])) {
							if (b) {
								flag += "C8003,";
							}
						}
						/**
						 * 当天会议
						 */
						if ("C8005".equals(str[i])) {
							if (b) {
								flag +=  "C8005,";
							}
						}
						/**
						 * 本周的会议
						 */
						if ("C8006".equals(str[i])) {
							if (b) {
								flag +=  "C8006,";
							}
						}

						/**
						 * 当天未开始的会议
						 */
						if ("C8007".equals(str[i])) {
							if (b) {
								flag +=  "C8007,";
							}
						}
					}


				}
			}
			if(terminalId != null && !"".equals(flag)){
                map.put("type", flag);
                list.add(terminalId);
                CommandMessage cmd = new CommandMessage();
                cmd.setCmd("10101");
                cmd.setTenantId(tenantId);
                cmd.setTerminals(list);
                cmd.setData(map);
                cmd.setRequestId(CreateUUIdUtil.Uuid());
                cmd.setTimestamp(new Date().getTime());
                publishService.pushCommand(cmd);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
