package com.thinkwin.web.controller;

import com.github.pagehelper.PageInfo;
import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.PermissionService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.promotion.CapacityConfig;
import com.thinkwin.common.dto.promotion.PricingConfigDto;
import com.thinkwin.common.dto.publish.CommandMessage;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.FastdfsVo;
import com.thinkwin.common.vo.FileVo;
import com.thinkwin.common.vo.meetingVo.MeetingRoomReserveVo;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.goodscenter.service.ProductOrderService;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.promotion.service.PricingConfigService;
import com.thinkwin.publish.service.PublishService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.web.service.FileUploadCommonService;
import com.thinkwin.yuncm.service.*;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 会议室controller
 * 开发人员:daipengkai
 * 创建时间:2017/7/13
 */
@RequestMapping("/meetingRoom")
@Controller
public class MeetingRoomController {

    @Resource
    YuncmMeetingService yuncmMeetingService;
    @Resource
    YuncmRoomAreaService yuncmRoomAreaService;
    @Resource
    SearchMeetingRoomService searchMeetingRoomService;
    @Resource
    FileUploadService fileUploadService;
    @Resource
    SysLogService sysLogService;
    @Resource
    UserService userService;
    @Resource
    FileUploadCommonService fileUploadCommonService;
    @Resource
    SaasTenantService saasTenantCoreService;
    @Resource
    InfoReleaseTerminalService infoReleaseTerminalService;
    @Resource
    ProductOrderService productOrderService;
    @Resource
    MeetingRoomStatisticsService meetingRoomStatisticsService;
    @Resource
    PermissionService permissionService;
    @Resource
    PricingConfigService pricingConfigService;
    @Resource
    PublishService publishService;
    @Resource
    SysSetingService sysSetingService;
    @Resource
    ConferenceService conferenceService;


    private String source = "3";

    private final Logger logger = LoggerFactory.getLogger(MeetingRoomController.class);


    @RequestMapping(value = "/skipMeetingRoom")
    public String skipMeetingScreening(HttpServletRequest request){
        ///////////////////////////添加资源拦截功能/////////////////////////////////
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null != userInfo) {
            String userId = userInfo.getUserId();
            if(StringUtils.isNotBlank(userId)) {
                //在此处需要获取用户请求的路径
                String url = request.getRequestURI();
                boolean userJurisdiction = permissionService.getUserJurisdiction(userId,url);
                if(userJurisdiction){
                    return "room_manage/room";
                }
            }
        }
        return "redirect:/logout";
        ////////////////////////////添加资源拦截功能///////////////////////////
        //return "room_manage/room";
    }


    /**
     * 进入页面方法
     *
     * @return
     */
    @RequestMapping(value = "/selectAreaandRoom",method = RequestMethod.POST)
    @ResponseBody
    public Object selectAreaandRoom(BasePageEntity page) {

        Map<String, Object> map = new HashMap<String, Object>();
        //获取租户id
        String tenantId = TenantContext.getTenantId();
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
        //首页查看会议室
        List<YuncmRoomArea> roomAreas = this.yuncmMeetingService.selectAllListYuncmRoomArea();
        PageInfo<YuncmMeetingRoom> meetingRooms = this.yuncmMeetingService.selectAllListYuncmMeetingRoom(page);
        //获取总会议室个数
        PageInfo<YuncmMeetingRoom> rooms = this.searchMeetingRoomService.selectAllMeetingRoom(page);
        map.put("roomArea", roomAreas);
        map.put("room", meetingRooms);
        map.put("roomTotal", rooms.getTotal());
        map.put("userState",saasTenant.getTenantType());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
    }

    /**
     * 区域重命名
     *
     * @param areaId 区域ID
     * @param name   区域名称
     * @return
     */
    @RequestMapping(value = "/updateAreaName",method = RequestMethod.POST)
    @ResponseBody
    public Object updateAreaName(String areaId, String name,String userId ) {

        if (StringUtils.isNotBlank(areaId) && StringUtils.isNotBlank(name)) {
            if(name.length()>20){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"会议室区域名称长度受限");
            }
            YuncmRoomArea roomArea = this.yuncmMeetingService.updateYuncmRoomArea(areaId, name, userId);
            if (roomArea != null) {
                sysLogService.createLog(BusinessType.meetingOp.toString(),EventType.meeting_room_area.toString(), "会议室区域修改为"+name, "", Loglevel.info.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), roomArea);
            }
        }
        sysLogService.createLog(BusinessType.meetingOp.toString(),EventType.meeting_room_area.toString(), name+"修改失败", BusinessExceptionStatusEnum.Failure.getDescription(), Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }

    /**
     * 添加区域
     *
     * @param name 区域名称
     * @return
     */
    @RequestMapping(value = "/insertYuncmRoomArea",method = RequestMethod.POST)
    @ResponseBody
    public Object insertYuncmRoomArea(String name,String userId) {

        if (StringUtils.isNotBlank(name)) {
            if(name.length()>20){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"会议室区域名称长度受限");
            }
            YuncmRoomArea roomArea = this.yuncmMeetingService.insertYuncmRoomArea(name, userId, "0");
            if (roomArea != null) {
                sysLogService.createLog(BusinessType.meetingOp.toString(),EventType.meeting_room_area.toString(), name+"创建成功", "", Loglevel.info.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), roomArea);
            }
        }
        sysLogService.createLog(BusinessType.meetingOp.toString(),EventType.meeting_room_area.toString(), "会议室区域创建失败", BusinessExceptionStatusEnum.Failure.getDescription(), Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }



    /**
     * 查看区域会议室
     *http://api.yunmetting.com/ meetingRoom /selectAllMeetingRoom
     * @param areaId    区域ID
     * @param page      当前页
     * @return
     */
    @RequestMapping(value = "/selectAreaMeetingRoom",method = RequestMethod.POST)
    @ResponseBody
    public Object selectAreaMeetingRoom(String areaId,BasePageEntity page) {
        PageInfo<YuncmMeetingRoom> roomList = null;
        if (!"0".equals(areaId)) {
            //区域会议室
            roomList = this.searchMeetingRoomService.selectAreaMeetingRoom(areaId,page);
        }else{
            //全部会议室
            roomList = this.searchMeetingRoomService.selectAllMeetingRoom(page);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), roomList);
    }

    /**
     * 搜索区域会议室
     *
     * @param searchKey 搜索关键字
     * @param areaId    区域ID
     * @param page      当前页
     * @return
     */
    @RequestMapping(value = "/searchAreaMeetingRoom",method = RequestMethod.POST)
    @ResponseBody
    public Object searchAreaMeetingRoom(String searchKey, String areaId, BasePageEntity page) {
        PageInfo<YuncmMeetingRoom> roomList = null;
        if (!"0".equals(areaId)) {
            roomList = this.searchMeetingRoomService.selectSearchAreaMeetingRoomChange(searchKey, areaId, page);

            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), roomList);

        } else {
            //搜索全部
            Map<String, Object> map = new HashMap<String, Object>();
            PageInfo<YuncmMeetingRoom> rooms = this.searchMeetingRoomService.selectSearchMeetingRoom(searchKey, page);
            map.put("room", rooms);
            Map<String,Object> ma = this.searchMeetingRoomService.selectSearchYuncmRoomAreaChange(searchKey);
            map.put("roomTotal", ma.get("num"));
            map.put("roomArea", ma.get("list"));
            /*Map<String, Object> map = new HashMap<String, Object>();
            PageInfo<YuncmMeetingRoom> rooms = this.searchMeetingRoomService.selectSearchMeetingRoom(searchKey, page);
            map.put("room", rooms);
            map.put("roomTotal", rooms.getTotal());
            map.put("roomArea", this.searchMeetingRoomService.selectSearchYuncmRoomArea(searchKey));*/
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
        }
    }


    /**
     * 会议室停用
     *
     * @param id
     * @param startTime
     * @param endTime
     * @param state
     * @param operReason
     * @return
     */
    @RequestMapping(value = "/stopMeetingRoom",method = RequestMethod.POST)
    @ResponseBody
    public Object stopMeetingRoom(String id, String startTime, String endTime, String state, String operReason,String userId) {

        if(StringUtils.isNotBlank(operReason)&&operReason.length()>200){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"停用原因长度受限");
        }
        //判断参数是否为空
        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(state)) {
            if ("3".equals(state)) {
                if (!StringUtils.isNotBlank(startTime) && !StringUtils.isNotBlank(endTime)) {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
                }
            }
        } else {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        boolean success = this.yuncmRoomAreaService.updateStopYuncmMeetingRoom(id, startTime, endTime, state, operReason, userId);
        if (success) {
            //会议室停用推送
            pushMessage(id,TenantContext.getTenantId(),"del");
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }

    /**
     * 会议室启用
     *
     * @param id 会议室ID
     * @return
     */
    @RequestMapping(value = "/startMeetingRoom",method = RequestMethod.POST)
    @ResponseBody
    public Object startMeetingRoom(String id,String userId) {

        if (StringUtils.isNotBlank(id)) {
            boolean success = this.yuncmRoomAreaService.updateStartYuncmMeetingRoom(id, userId);
            if (success) {
                //会议室启用推送
                pushMessage(id,TenantContext.getTenantId(),"del");
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }

    /**
     * 会议室预定设置
     *
     * @param reserveTimeStart
     * @param reserveTimeEnd
     * @param meetingMaximum
     * @param meetingMinimum
     * @param reserveCycle
     * @returng
     */
    @RequestMapping(value = "/meetingRoomReserve",method = RequestMethod.POST)
    @ResponseBody
    public Object meetingRoomReserve(String reserveTimeStart, String reserveTimeEnd, String meetingMaximum, String meetingMinimum, String reserveCycle,String userId,String minutes,String qrDuration,String signSet) {

        if (StringUtils.isNotBlank(reserveTimeStart) && StringUtils.isNotBlank(reserveTimeEnd) && StringUtils.isNotBlank(meetingMaximum) && StringUtils.isNotBlank(meetingMinimum) && StringUtils.isNotBlank(reserveCycle)&& StringUtils.isNotBlank(qrDuration) && StringUtils.isNotBlank(signSet)) {
            //修改之前查询会议室设置
            YuncmRoomReserveConf yuncmRoomReserveConf = yuncmRoomAreaService.selectRoomReserveConf();
            YuncmRoomReserveConf conf = this.yuncmRoomAreaService.updateMeetingRoomReserve(reserveTimeStart, reserveTimeEnd, meetingMaximum, meetingMinimum, reserveCycle, userId,qrDuration,signSet);
            if (conf != null) {
                //设置会议提前显示时间   weining  企云会2.0需求添加
                if(StringUtils.isNotBlank(minutes)){
                    settingAdvanceDisplayTime(minutes);
                }
                //会议是设置信息比较
                String s = comparisonMeetingRoomSetting(yuncmRoomReserveConf);
                if(StringUtils.isNotBlank(s)){
                    s = "会议室设置保存成功";
                }
                sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.meeting_settins.toString(), s, "", Loglevel.info.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),conf);
            }
        }
        sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.meeting_settins.toString(), "会议室设置保存失败", BusinessExceptionStatusEnum.Failure.getDescription(),Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }


    /**
     * 新建会议室获取会议室区域设备
     *
     * @return
     */
    @RequestMapping(value = "/selectAreaAndDevice",method = RequestMethod.POST)
    @ResponseBody
    public Object selectAreaAndDevice() {

        Map<String, Object> map = new HashMap<String, Object>();
        List<YuncmDeviceService> services = this.yuncmMeetingService.selectYuncmDeviceService();
        List<YuncmRoomArea> areas = this.yuncmMeetingService.selectAllListYuncmRoomArea();
        map.put("roomArea", areas);
        map.put("device", services);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
    }


    /**
     * 新建会议室
     * @param roleId        预定权限
     * @param persionNumber 会议室容纳人数
     * @return
     */
    @RequestMapping(value = "/insertMeetingRoom",method = RequestMethod.POST)
    @ResponseBody
    public Object insertMeetingRoom(String userId,String name,String areaId,String persionNumber,String location,
                                    String deviceService,String isAudit,String  roleId,String imageUrl,String imageSize) throws IOException {

        if(StringUtils.isBlank(name)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        if(name.length()>20){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"会议室名称长度受限");
        }
        int i = Integer.parseInt(persionNumber);
        if(i<0 || i>9999){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"可容纳人数范围有误");
        }
        if(StringUtils.isBlank(location)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        if(location.length()>100){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"会议室物理地址长度受限");
        }
        //获取租户id
        String tenantId = TenantContext.getTenantId();
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
        //获取租户付费状态 判断会议室
        String mes = "";
        //获取用户创建的会议室数量
        int num = this.yuncmMeetingService.selectTenantMeetingRoomCount().size();
        mes = "企业当前已有"+num+"个会议室,达到授权最大限制,请联系企业管理员升级会议室容量";
        boolean bo = judgeMeetingRoomNum(saasTenant,num);
        if(bo){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, mes, BusinessExceptionStatusEnum.Failure.getCode());
        }
        YuncmMeetingRoom room = new YuncmMeetingRoom();
        room.setName(name);
        room.setAreaId(areaId);
        room.setLocation(location);
        room.setDeviceService(deviceService);
        if(StringUtils.isNotBlank(isAudit)){
            room.setIsAudit(isAudit);
        }else{
            room.setIsAudit("0");
        }
        room.setId(CreateUUIdUtil.Uuid());
        FileVo vo = null;
        if(StringUtils.isNotBlank(imageUrl)) {
            //解密
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] b = decoder.decodeBuffer(imageUrl.replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,", ""));
            String ext = "png";
            Map<String,String> maps = new HashMap<>();
            maps.put("big","300_225");
            maps.put("in","60_45");
            maps.put("small","60_45");
            //处理图片
            // Map<String,BufferedImage> imageMap = ResizeImage.resizeImage(b,maps);
            //调用图片上传
            List<FastdfsVo> vos = FileUploadUtil.fileUpload(maps,b,"png");
            vo = fileUploadService.insertFileUpload("3",userId,tenantId,saasTenant.getBasePackageSpaceNum(),"png",vos);
            //fileId = fileUploadCommonService.uploadFileCommon(imageMap,ext,b,tenantId,userId);
            if (!"0".equals(vo.getId())) {
                if (!"".equals(vo.getId())) {
                    room.setImageUrl(vo.getId());
                }else{
                    room.setImageUrl("");
                }
            } else {
                sysLogService.createLog(BusinessType.meetingOp.toString(),EventType.add_meeting_room.toString(), "创建会议室失败", BusinessExceptionStatusEnum.Failure.getDescription(), Loglevel.error.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription(), BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
            }
        }
        String codeId = this.obtainCode(tenantId,room.getId());
        if (!"".equals(codeId)) {
            room.setQrCodeUrl(codeId);
        }
        room.setPersionNumber(Integer.parseInt(persionNumber));
        boolean success = this.yuncmRoomAreaService.insertYuncmMeetingRoom(room, roleId, userId);
        if (success) {
            if(vo != null) {
            /*    Map<String, String> imgMap = this.fileUploadCommonService.selectFileCommon(fileId);*/
                //Map<String, String> imgMap = userService.getUploadInfo(fileId);
                Map map1 = new HashMap();
                String primary = vo.getPrimary();
                String big = vo.getBig();
                String in = vo.getIn();
                String small = vo.getSmall();
                map1.put("primary", primary);
                map1.put("big", big);
                map1.put("in", in);
                map1.put("small", small);
                userService.saveImageUrl(userId, map1, vo.getId());
            }
            sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.add_meeting_room.toString(), name+"创建成功", "",Loglevel.info.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.add_meeting_room.toString(), "创建会议室失败", BusinessExceptionStatusEnum.Failure.getDescription(),Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }


    /**
     * 修改会议室
     *
     * @param roleId        预定权限
     * @param persionNumber 会议室容纳人数
     * @return
     */
    @RequestMapping(value = "/updateMeetingRoom",method = RequestMethod.POST)
    @ResponseBody
    public Object updateMeetingRoom(String userId,String id,String name,String areaId,String persionNumber,String location,
                                    String deviceService,String isAudit,String roleId,String imageUrl,String imageState,String imageSize) throws IOException {

        if(StringUtils.isBlank(name)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        if(name.length()>30){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"会议室名称长度受限");
        }
        int i = Integer.parseInt(persionNumber);
        if(i<0 || i>9999){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"可容纳人数范围有误");
        }
        if(StringUtils.isBlank(location)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        if(location.length()>100){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"会议室物理地址长度受限");
        }
        String imgId="";
        //获取租户id
        String tenantId = TenantContext.getTenantId();
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
        if("1".equals(imageState)) {
            if (StringUtils.isNotBlank(imageUrl)) {
                //解密
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] b = decoder.decodeBuffer(imageUrl.replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,", ""));

                String ext = "png";
                Map<String,String> maps = new HashMap<>();
                maps.put("big","300_225");
                maps.put("in","60_45");
                maps.put("small","60_45");
                //处理图片
                //Map<String,BufferedImage> imageMap = ResizeImage.resizeImage(b,maps);
                //调用图片上传
                //String fileId = fileUploadCommonService.uploadFileCommon(imageMap,ext,b,tenantId,userId);
                List<FastdfsVo> vos = FileUploadUtil.fileUpload(maps,b,"png");
                FileVo vo = fileUploadService.insertFileUpload("3",userId,tenantId,saasTenant.getBasePackageSpaceNum(),"png",vos);
                if (!"0".equals(vo.getId())) {
                    if (!"".equals(vo.getId())) {
                        imgId = vo.getId();
                    }else{
                        imgId = "";
                    }
                } else {
                    sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.add_meeting_room.toString(), "修改会议室失败", BusinessExceptionStatusEnum.Failure.getDescription(), Loglevel.error.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription(), BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
                }
            }
        }
        //获取图片
        YuncmMeetingRoom meetingRoom = this.yuncmMeetingService.selectByYuncmMeetingRoom(id);
        if(!"0".equals(imageState)){
            if(StringUtils.isNotBlank(meetingRoom.getImageUrl())){
                List<SysAttachment> sysAttachments = this.fileUploadService.deleteFileUpload(meetingRoom.getImageUrl(),tenantId);
                boolean b = FileUploadUtil.deleteFile(sysAttachments);
                if(b){
                    userService.deleteImageUrl(meetingRoom.getImageUrl());
                }
            }
        }else{
            imgId = meetingRoom.getImageUrl();
        }
        boolean success = this.yuncmRoomAreaService.updateYuncmMeetingRoom(id, name, areaId, persionNumber, location, deviceService, isAudit, roleId, imgId, userId);
        if (success) {
            pushMessage(id,tenantId,"");
            if(StringUtils.isNotBlank(imgId)){
                Map<String, String> imgMap = fileUploadCommonService.selectFileCommon(imgId);
                Map map1 = new HashMap();
                String primary = imgMap.get("primary");
                String big = imgMap.get("big");
                String in = imgMap.get("in");
                String small = imgMap.get("small");
                map1.put("primary",primary);
                map1.put("big",big);
                map1.put("in",in);
                map1.put("small",small);
                userService.saveImageUrl(userId,map1,imgId);
            }
            //比对修改新消息
            String s = comparisonMeetingRoomInfo(meetingRoom);
            if(StringUtils.isNotBlank(s)){
                s = "会议室修改成功";
            }
            sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.update_meeting_room.toString(), s, "",Loglevel.info.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.update_meeting.toString(), "修改会议室失败", BusinessExceptionStatusEnum.Failure.getDescription(),Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }

    /**
     * 删除会议室
     *
     * @return
     */
    @RequestMapping(value = "/deleteMeetingRoom",method = RequestMethod.POST)
    @ResponseBody
    public Object deleteMeetingRoom(String id) {
        //获取租户Id
        String tenantId = TenantContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        YuncmMeetingRoom room = this.yuncmMeetingService.selectByYuncmMeetingRoom(id);
        //查询会议和会议室中间表
        List<YummeetingConferenceRoomMiddle> yummeetingCRMiddles = yuncmRoomAreaService.selectMCRoomMiddleByMRoomId(id);
        boolean b = false;
        //从fastdfs删除图片和二维码
        if (StringUtils.isNotBlank(room.getQrCodeUrl())) {

            SysAttachment sys = fileUploadService.downloadFlie(room.getQrCodeUrl());
            if(sys != null){
                FileManager fileManager = new FileManager(null);
                fileManager.deleteFileUpload(sys.getGroup(), sys.getAttachmentPath() + sys.getFileName());
                this.fileUploadService.deleteFileUpload(room.getQrCodeUrl(), tenantId);
            }

        }
        if (StringUtils.isNotBlank(room.getImageUrl())) {
            List<SysAttachment> sysAttachments = this.fileUploadService.deleteFileUpload(room.getImageUrl(),tenantId);
            boolean b1 = FileUploadUtil.deleteFile(sysAttachments);
            if(b1){
                userService.deleteImageUrl(room.getImageUrl());
            }
        }
        //如果为空说明没有开过会  物理删除会议室 deleteState参数为空
        if (null == yummeetingCRMiddles) {

            b = yuncmRoomAreaService.deleteYuncmMeetingRoom(id, null);
        } else {
            //如果不为空  逻辑删除会议室  deleteState参数为 1
            b = yuncmRoomAreaService.deleteYuncmMeetingRoom(id, "1");
        }
        if (b) {
            pushMessage(id,tenantId,"del");
            //删除会议室和终端关系
            this.infoReleaseTerminalService.updateInfoReleaseTerminalByRoomId(id);
            sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.del_meeting_room.toString(), room.getName()+"删除成功", "",Loglevel.info.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.del_meeting_room.toString(), room.getName()+"删除失败", "",Loglevel.info.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }


    /**
     * 删除会议室区域
     *
     * @return
     */
    @RequestMapping(value = "/deleteArea",method = RequestMethod.POST)
    @ResponseBody
    public Object deleteArea(String areaId) {
        boolean success = false;
        //获取租户Id1
        String tenantId = TenantContext.getTenantId();
        YuncmRoomArea area = this.yuncmMeetingService.selectByidYuncmRoomArea(areaId);
        //查询区域所有会议室
        List<YuncmMeetingRoom> rooms = this.yuncmMeetingService.selectAllMeetingRoom(areaId);
        if (rooms.size() > 0) {
            for (YuncmMeetingRoom room : rooms) {
                //查看会议室是否被使用过
                List<YummeetingConferenceRoomMiddle> yummeetingCRMiddles = yuncmRoomAreaService.selectMCRoomMiddleByMRoomId(room.getId());
                boolean b = false;
                FileManager fileManager = new FileManager(null);
                //从fastdfs删除图片和二维码
                if (StringUtils.isNotBlank(room.getQrCodeUrl())) {
                    boolean l = false;
                    SysAttachment sys = fileUploadService.downloadFlie(room.getQrCodeUrl());
                    if(sys != null){
                        l = fileManager.deleteFileUpload(sys.getGroup(), sys.getAttachmentPath() + sys.getFileName());
                    }
                    if(l) {
                        this.fileUploadService.deleteFileUpload(room.getQrCodeUrl(), tenantId);
                    }
                }
                if (StringUtils.isNotBlank(room.getImageUrl())) {
                    boolean l = false;
                    SysAttachment sys = fileUploadService.downloadFlie(room.getImageUrl());
                    if(sys != null){
                        l = fileManager.deleteFileUpload(sys.getGroup(), sys.getAttachmentPath() + sys.getFileName());
                    }
                    if(l) {
                        this.fileUploadService.deleteFileUpload(room.getImageUrl(), tenantId);
                    }
                }
                if (null == yummeetingCRMiddles) {
                    //如果为空说明没有开过会  物理删除会议室 deleteState参数为空
                    b = yuncmRoomAreaService.deleteYuncmMeetingRoom(room.getId(), null);
                } else {
                    //如果不为空  逻辑删除会议室  deleteState参数为 1
                    b = yuncmRoomAreaService.deleteYuncmMeetingRoom(room.getId(), "1");
                }
            }
            success = this.yuncmRoomAreaService.deleteYuncmRooArea(areaId, false);
        } else {
            success = this.yuncmRoomAreaService.deleteYuncmRooArea(areaId, true);
        }

        if (success) {
            sysLogService.createLog(BusinessType.meetingOp.toString(),EventType.meeting_room_area.toString(), area.getName()+"删除成功", "", Loglevel.info.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        sysLogService.createLog(BusinessType.meetingOp.toString(),EventType.meeting_room_area.toString(), area.getName()+"删除失败", BusinessExceptionStatusEnum.Failure.getDescription(), Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }


    /**
     * 生成二维码测试
     *
     * @return
     */
    @RequestMapping(value = "/code",method = RequestMethod.POST)
    @ResponseBody
    public Object code(HttpServletRequest request) {

        byte[] bytes = BarcodeUtil.encoderQRCode("二维码内容", "jpg", 15);
        String i = String.valueOf(bytes.length);
        FastDFSFile file = new FastDFSFile(bytes, "会议室二维码.jpg", "jpg", i, "");
        //上传文件
        FileManager fileManager = new FileManager(null);
        FastdfsVo fastdfsVo = fileManager.upload(file, null);
        //添加数据返回图片ID
        //获取用户信息
        TenantUserVo userInfo = TenantContext.getUserInfo();
        List<FastdfsVo> vos = FileUploadUtil.fileUpload(null,bytes,"png");
        FileVo  fileId = fileUploadService.insertFileUpload("3",userInfo.getUserId(), userInfo.getTenantId(), 5,"png",vos);
        //获取图片
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "修改成功");

    }




    /**
     * 下载全部二维码
     *
     * @return
     */
    @RequestMapping("/downloadQrcode")
    @ResponseBody
    public ResponseEntity<byte[]> downloadQrcode(HttpServletRequest request, HttpServletResponse response) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        String tenantId = TenantContext.getTenantId();
        //文档路径
        String path = request.getSession().getServletContext()
                .getRealPath("/WEB-INF/temp/" + date + "/" + tenantId + "/codeFile/");
        //文档路径
        String codePath = request.getSession().getServletContext()
                .getRealPath("/WEB-INF/temp/" + date + "/" + tenantId + "/mrprcode/");
        // 创建压缩包路径
        File fp = new File(codePath);
        if(!fp.exists()){
            fp.mkdirs();
        }
        //获取区域
        //File fp = new File(path+"mrprcode.zip");
        //二维码是否生成如果存在则直接下载 不存在则创建
        List<YuncmRoomArea> areas = new ArrayList<YuncmRoomArea>();
        // if(!fp.exists()) {
        areas = this.yuncmMeetingService.selectAllYuncmRoomArea();
        for (YuncmRoomArea area : areas) {
            //获取区域会议室
            List<YuncmMeetingRoom> meetingRooms = this.yuncmMeetingService.selectAllMeetingRoom(area.getId());
            for (YuncmMeetingRoom room : meetingRooms) {
                //获取二维码 如二维码不存在时则生成二维码
                String url = "";
                if(StringUtils.isNotBlank(room.getQrCodeUrl())){
                    url = this.fileUploadService.selectTenementByFile(room.getQrCodeUrl());
                }else{
                    //二维码不存在
                    String code = obtainCode(tenantId,room.getId());
                    url = this.fileUploadService.selectTenementByFile(code);
                }
                room.setQrCodeUrl(url);
            }
            //生成区域二维码文档
            JavaAgent.NotesMain(meetingRooms, path, area.getName());
        }

        //压缩zip包
        ZipCompressor zc = new ZipCompressor(codePath + "/mrprcode.zip");
        zc.compress(path);
        //  }
        File file = new File(codePath + "mrprcode.zip");
        InputStream inputStream = null;
        OutputStream outputStream = null;
        byte[] b = new byte[1024];
        int len = 0;
        try {
            //删除文档
            DeleteFile.deleteDir(new File(path));
            inputStream = new FileInputStream(file);
            outputStream = response.getOutputStream();

            response.setContentType("multipart/form-data charset=UTF-8");
            String filename = "mrprcode.zip";
            // 通常解决汉字乱码方法用URLEncoder.encode(...)
            String filenamedisplay = URLEncoder.encode(filename, "UTF-8");
            if ("FF".equals(getBrowser(request))) {
                // 针对火狐浏览器处理方式不一样了
                filenamedisplay = new String(filename.getBytes("UTF-8"),
                        "iso-8859-1");
            }
            response.addHeader("Content-Disposition", "attachment; filename=" + filenamedisplay);
            response.setContentLength((int) file.length());

            while ((len = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, len);
            }
            sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.print_code.toString(), "打印二维码", "",Loglevel.info.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                    outputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }


    /**
     * 会议室排序
     * @param userId 用户id
     * @param areaId 区域id
     * @param nowRoomId 拖动的id
     * @param purposeRoomId 需要替换的id
     * @return
     */
    @RequestMapping(value = "/mettingRoomSorting",method = RequestMethod.POST)
    @ResponseBody
    public Object mettingRoomSorting(String userId,String areaId,String nowRoomId,String purposeRoomId) {

        boolean success = this.yuncmMeetingService.updateMeetingRoomSorting(areaId, nowRoomId, purposeRoomId,userId);
        if(success){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }

    /**
     * 查看会议室预定设置
     *
     * @return
     */
    @RequestMapping(value = "/selectMeetingRoomReserve",method = RequestMethod.POST)
    @ResponseBody
    public Object selectMeetingRoomReserve() {

        YuncmRoomReserveConf conf = this.yuncmMeetingService.selectYuncmRoomReserveConf();
        MeetingRoomReserveVo meetingRoomReserveVo = new MeetingRoomReserveVo();
        BeanUtils.copyProperties(conf,meetingRoomReserveVo);
        //查询会议提前显示设置
        SysSetting settingKey = sysSetingService.findByKey("meetingroom.time");
        if(null != settingKey){
            String content = settingKey.getContent();
            meetingRoomReserveVo.setMinutes(content);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), meetingRoomReserveVo);
    }

    /**
     * 根据会议室id查看会议室
     *
     * @return
     */
    @RequestMapping(value = "/selectByIdMettingRoom",method = RequestMethod.POST)
    @ResponseBody
    public Object selectByIdMettingRoom(String roomId) {

        YuncmMeetingRoom meetingRoom = this.yuncmMeetingService.selectByidYuncmMeetingRoom(roomId);

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), meetingRoom);
    }

    /**
     *  会议室区域排序
     * @param userId 用户id
     * @param nowRoomId 当前拖动的id
     * @param purposeRoomId 需要替换的id
     * @return
     */
    @RequestMapping(value = "/mettingRoomAreaSorting",method = RequestMethod.POST)
    @ResponseBody
    public Object mettingRoomAreaSorting(String userId,String nowRoomId,String purposeRoomId,String moveType) {

        boolean success = this.yuncmMeetingService.updateMeetingRoomAreaSorting(nowRoomId,purposeRoomId,userId,moveType);
        if(success){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }

    /**
     *  按条件筛选会议室
     * @param userId 用户id
     * @return
     */
    @RequestMapping(value = "/screeningMettingRoom",method = RequestMethod.POST)
    @ResponseBody
    public Object screeningMettingRoom(String userId,String personNum,String deviceService,String areaId,String isAudit,String staTime,String endTime,String roomId,String currentPage) {
        Map<String,Object> map = new HashedMap();
        List<YuncmMeetingRoom> roomList = new ArrayList<YuncmMeetingRoom>();
        List<YuncmMeetingRoom> rooms = this.searchMeetingRoomService.selectMeetingScreenYuncmMeetingRoom(userId,personNum,deviceService,staTime,endTime,areaId,isAudit);
        if(StringUtils.isNotBlank(roomId)){
            //获取点击的常用会议室
            roomList = this.searchMeetingRoomService.selectClickCommonMeetingRoom(roomId);
        }
        if(roomList.size() != 0){
            for(YuncmMeetingRoom ro : rooms){
                boolean success = true;
                for(YuncmMeetingRoom room : roomList){
                    if(ro.getId().equals(room.getId())){
                        success = false;
                        break;
                    }
                }
                if(success){
                    roomList.add(ro);
                }
            }
            for(YuncmMeetingRoom ro : roomList){
                String device = "";
                String str [] = ro.getDeviceService().split("、");
                if(str.length > 3){
                    for(int i =0 ; i < str.length;i++){
                        if(i == 2){
                            device += str[i].toString()+"等";
                            break;
                        }else{
                            device += str[i].toString()+"、";
                        }
                    }
                    ro.setDeviceService(device);
                }
            }
            map.put("meetingRoom",roomList);
            map.put("roomTotal",roomList.size());
        }else{
            for(YuncmMeetingRoom room : rooms) {
                String device = "";
                String str[] = room.getDeviceService().split("、");
                if (str.length > 3) {
                    for (int i = 0; i < str.length; i++) {
                        if (i == 2) {
                            device += str[i].toString() + "等";
                            break;
                        } else {
                            device += str[i].toString() + "、";
                        }
                    }
                    room.setDeviceService(device);
                }
            }
            map.put("meetingRoom",rooms);
            map.put("roomTotal",rooms.size());
        }
        //当前用户是否有权限预定会议
        List<YuncmMeetingRoom> meetingRooms = new ArrayList<YuncmMeetingRoom>();
        for(YuncmMeetingRoom room : (List<YuncmMeetingRoom>)map.get("meetingRoom")){
            boolean success = this.selectUserMeetingRoomRole(room.getId(),userId);
            if(success){
                meetingRooms.add(room);
            }
        }
        map.put("meetingRoom",meetingRooms);
        map.put("roomTotal",meetingRooms.size());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
    }

    // 以下为服务器端判断客户端浏览器类型的方法
    private String getBrowser(HttpServletRequest request) {
        String UserAgent = request.getHeader("USER-AGENT").toLowerCase();
        if (UserAgent != null) {
            if (UserAgent.indexOf("msie") >= 0)
                return "IE";
            if (UserAgent.indexOf("firefox") >= 0)
                return "FF";
            if (UserAgent.indexOf("safari") >= 0)
                return "SF";
        }
        return null;
    }


    /**
     * 查看用户的会议室上限
     * @param num
     * @return
     */
    public boolean judgeMeetingRoomNum(SaasTenant saasTenant,int num){

        boolean success = false;
        //免费用户读取定价配置
        if("0".equals(saasTenant.getTenantType())){
            //获取定价配置
            PricingConfigDto configDto = pricingConfigService.getPricingConfig();
            //获取免费价格
            List<CapacityConfig> configs = configDto.getFreeAccountConfig();
            for(CapacityConfig config : configs){
                //会议室数量
                if("100".equals(config.getSku())){
                    saasTenant.setBuyRoomNumTotal(config.getQty());
                    saasTenant.setBasePackageRoomNum(0);
                }
                //储存空间
                if("101".equals(config.getSku())){
                    saasTenant.setBasePackageSpaceNum(config.getQty());
                }
                //员工人数
                if("102".equals(config.getSku())){
                    saasTenant.setExpectNumber(config.getQty());
                }
            }
        }
        if(num >= saasTenant.getBuyRoomNumTotal()){
           success = true;
        }
        return success;
    }

    /**
     * 筛选使用次数最多的会议室
     * @return
     */
    @RequestMapping(value = "/selectUseFrequencyMany",method = RequestMethod.POST)
    @ResponseBody
    public Object selectUseFrequencyMany(){
        //获取用户Id
        String userId = TenantContext.getUserInfo().getUserId();
        List<YuncmMeetingRoom> rooms = this.searchMeetingRoomService.selectUseFrequencyMany();
        //当前用户是否有权限预定会议
        List<YuncmMeetingRoom> meetingRooms = new ArrayList<YuncmMeetingRoom>();
        int i = 0;
        for(YuncmMeetingRoom room : rooms){
            i++;
            boolean success = this.selectUserMeetingRoomRole(room.getId(),userId);
            if(success){
                meetingRooms.add(room);
                if(i == 4){
                    break;
                }
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),meetingRooms);
    }


    /**
     * 筛选全部会议室
     * @param deviceService 设备
     * @param staTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @RequestMapping(value = "/selectScreenAllMeetingRoom",method = RequestMethod.POST)
    @ResponseBody
    public Object selectScreenAllMeetingRoom(String deviceService, String staTime, String endTime){
        //获取用户Id
        String userId = TenantContext.getUserInfo().getUserId();
        List<YuncmMeetingRoom> rooms = new ArrayList<YuncmMeetingRoom>();
        if(StringUtils.isNotBlank(staTime) && StringUtils.isNotBlank(endTime)){
            rooms = this.searchMeetingRoomService.selectScreenAllMeetingRoom(deviceService,staTime,endTime);
        }else{
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
        }
        //当前用户是否有权限预定会议
        List<YuncmMeetingRoom> meetingRooms = new ArrayList<YuncmMeetingRoom>();
        for(YuncmMeetingRoom room : rooms){
            boolean success = this.selectUserMeetingRoomRole(room.getId(),userId);
            if(success){
                meetingRooms.add(room);
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),meetingRooms);
    }




    /**
     * 获取会议室创建状态
     * @return
     */
    @RequestMapping(value = "/getAddRoomStatus",method = RequestMethod.POST)
    @ResponseBody
    public Object getAddRoomStatus() {

        //获取租户id
        String tenantId = TenantContext.getTenantId();
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
        //获取租户付费状态 判断会议室
        //获取租户付费状态 判断会议室
        String mes = "";
        //获取用户创建的会议室数量
        int num = this.yuncmMeetingService.selectTenantMeetingRoomCount().size();
        mes = "企业当前已有" + num + "个会议室,达到授权最大限制,请联系企业管理员升级会议室容量";
        boolean bo = judgeMeetingRoomNum(saasTenant, num);
        if (bo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, mes, BusinessExceptionStatusEnum.Failure.getCode());
        }else{
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, mes, BusinessExceptionStatusEnum.Success.getCode());
        }
    }

    public String obtainCode(String tenantId,String roomId){
        String wechatServer= PropertyUtil.getWechatServer();
        //生成会议室二维码
        byte[] bytes = BarcodeUtil.encoderQRCode(wechatServer+"/wechat/scanMeeting/scanMeetingRoomQR?tenantId="+tenantId+"&meetingRoomId="+roomId, "jpg", 15);
        String i = String.valueOf(bytes.length);
        //获取用户信息
        TenantUserVo userInfo = TenantContext.getUserInfo();
        List<FastdfsVo> vos = FileUploadUtil.fileUpload(null,bytes,"png");
        FileVo vo = fileUploadService.insertFileUpload("3",userInfo.getUserId(),tenantId,5,"png",vos);
        return vo.getId();
    }



    /**
     * 方法名：comparisonMeetingRoomInfo</br>
     * 描述：比对会议室修改的新消息处理</br>
     * 参数：meetingRoomBefore 会议室修改之前的数据</br>
     * 返回值：</br>
     */
    private String comparisonMeetingRoomInfo(YuncmMeetingRoom meetingRoomBefore){
        if(null!=meetingRoomBefore){
            //获取会议室Id
            String meetingRoomId = meetingRoomBefore.getId();
            //查询会议室修改之后的数据
            YuncmMeetingRoom meetingRoomAfter = this.yuncmMeetingService.selectByYuncmMeetingRoom(meetingRoomId);
            if(null!=meetingRoomAfter){
                String content = "";
                boolean flag = false;
                if(!meetingRoomAfter.getName().equals(meetingRoomBefore.getName())){
                    flag = true;
                    content =  "会议室名称修改为："+meetingRoomAfter.getName();
                }
                boolean flag1 = false;
                YuncmRoomArea yuncmRoomArea = yuncmRoomAreaService.selectRoomAreaByAreaId(meetingRoomAfter.getAreaId());
                String name = "";
                if(yuncmRoomArea != null){
                   name = yuncmRoomArea.getName();
                }
                if(!meetingRoomAfter.getAreaId().equals(meetingRoomBefore.getAreaId())){
                    if(StringUtils.isNotBlank(name)&&!flag){
                        flag1 = true;
                        content = meetingRoomAfter.getName()+"区域修改为："+ name;
                    }else {
                        content += "等";
                    }
                }
                boolean flag2 = false;
                if(!meetingRoomAfter.getLocation().equals(meetingRoomBefore.getLocation())){
                    if(flag){
                        content =  "会议室名称修改为："+meetingRoomAfter.getName()+"等";
                    }else {
                        if (flag1) {
                            content = meetingRoomAfter.getName() + "区域修改为：" + name + "等";
                        }
                    }
                    if(!flag&&!flag1){
                        flag2 = true;
                        content = meetingRoomAfter.getName()+"物理位置修改为："+meetingRoomAfter.getLocation();
                    }
                }
                if(!meetingRoomAfter.getPersionNumber().equals(meetingRoomBefore.getPersionNumber())){
                    if(flag){
                        content =  "会议室名称修改为："+meetingRoomAfter.getName()+"等";
                    }else {
                        if (flag1) {
                            content = meetingRoomAfter.getName() + "区域修改为：" + name + "等";
                        }else {
                            if (flag2) {
                                content = meetingRoomAfter.getName() + "物理位置修改为：" + meetingRoomAfter.getLocation() + "等";
                            }
                        }
                    }
                    if(!flag&&!flag1&&!flag2){
                        content = meetingRoomAfter.getName() + "容量修改为："+meetingRoomAfter.getPersionNumber();
                    }
                }
                return content;
            }
        }
        return null;
    }

    /**
     * 方法名：comparisonMeetingRoomSetting</br>
     * 描述：比对会议室设置</br>
     * 参数：yuncmRoomReserveConfBefore 会议室设置修改之前信息</br>
     * 返回值：</br>
     */
    private String comparisonMeetingRoomSetting(YuncmRoomReserveConf yuncmRoomReserveConfBefore){
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        //查询修改之后的会议室设置信息
        YuncmRoomReserveConf yuncmRoomReserveConfAfter = yuncmRoomAreaService.selectRoomReserveConf();
        if(null!=yuncmRoomReserveConfBefore&&null!=yuncmRoomReserveConfAfter){
            String content = "";
            if(yuncmRoomReserveConfBefore.getReserveTimeStart()!=yuncmRoomReserveConfAfter.getReserveTimeStart()
                    || yuncmRoomReserveConfBefore.getReserveTimeEnd()!=yuncmRoomReserveConfAfter.getReserveTimeEnd()){
                return content = "会议室可预订时间修改为："+ sf.format(yuncmRoomReserveConfAfter.getReserveTimeStart())+"-"+sf.format(yuncmRoomReserveConfAfter.getReserveTimeEnd());
            }
            if(yuncmRoomReserveConfBefore.getMeetingMaximum()!=yuncmRoomReserveConfAfter.getMeetingMaximum()){
                Integer meetingMaximum = yuncmRoomReserveConfAfter.getMeetingMaximum();
                if(meetingMaximum==0){
                    content = "单次会议最大时长设置为：不限制";
                }else{
                    content = "单次会议最大时长设置为："+meetingMaximum+"小时";
                }
                return content;
            }
            if(yuncmRoomReserveConfBefore.getMeetingMinimum()!=yuncmRoomReserveConfAfter.getMeetingMinimum()){
                Integer meetingMinimum = yuncmRoomReserveConfAfter.getMeetingMinimum();
                if(meetingMinimum==0){
                    content = "单次会议最小时长设置为：不限制";
                }else{
                    content = "单次会议最小时长设置为："+meetingMinimum+"分钟";
                }
                return content;
            }
            if(yuncmRoomReserveConfBefore.getReserveCycle()!=yuncmRoomReserveConfAfter.getReserveCycle()){
                return content = "会议可预订周期设置为："+yuncmRoomReserveConfAfter.getReserveCycle()+"天";
            }
        }
        return null;
    }



    /**
     * 方法名：</br>
     * 描述：查询当前用户是否能创建会议</br>
     * 参数：roomId</br>
     * 返回值：</br>
     */
    public Boolean selectUserMeetingRoomRole(String roomId,String userId) {
        //获取用户Id

        //查询会议室预定权限
        List<String> roomRole = yuncmRoomAreaService.selectMeetingRoomRole(roomId);
        boolean flag = false;
        for (String roleId : roomRole) {
            //根据用户Id和权限Id查询当前用户是否有预定权限
            List<SysUserRole> userRole = userService.getUserRoleByUserIdAndRoleId(userId, roleId);
            if (null != userRole && userRole.size() > 0) {
                flag = true;
                break;
            }
        }
        if (roomRole.size() == 0) {
            flag = true;
        }
        return flag;
    }



    /**
     * 新建会议室(修改后)
     * @param roleId        预定权限
     * @param persionNumber 会议室容纳人数
     * @return
     */
    @RequestMapping(value = "/insertMeetingRoomModify",method = RequestMethod.POST)
    @ResponseBody
    public Object insertMeetingRoomModify(String userId,String name,String areaId,String persionNumber,String location,
                                          String deviceService,String isAudit,String  roleId,MultipartFile file,String imageSize) throws IOException {

        //获取租户id
        String tenantId = TenantContext.getTenantId();
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
        //获取租户付费状态 判断会议室
        String mes = "";
        //获取用户创建的会议室数量
        int num = this.yuncmMeetingService.selectTenantMeetingRoomCount().size();
        mes = "企业当前已有"+num+"个会议室,达到授权最大限制,请联系企业管理员升级会议室容量";
        boolean bo = judgeMeetingRoomNum(saasTenant,num);
        if(bo){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, mes, BusinessExceptionStatusEnum.Failure.getCode());
        }
        YuncmMeetingRoom room = new YuncmMeetingRoom();
        room.setName(name);
        room.setAreaId(areaId);
        room.setLocation(location);
        room.setDeviceService(deviceService);
        if(StringUtils.isNotBlank(isAudit)){
            room.setIsAudit(isAudit);
        }else{
            room.setIsAudit("0");
        }
        room.setId(CreateUUIdUtil.Uuid());
        FileVo vo = null;
        if(file != null) {
            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            ext = "png";
            Map<String, String> map = new HashMap<>();
            map.put("big", "300_225");
            map.put("in", "240_240");
            map.put("small", "60_45");
            //处理图片
            //Map<String, BufferedImage> imageMap = ResizeImage.resizeImage(file.getBytes(), map);
            //fileId = fileUploadCommonService.uploadFileCommon(imageMap, ext, file.getBytes(), tenantId, userId);
            List<FastdfsVo> vos = FileUploadUtil.fileUpload(map,file.getBytes(),"png");
            vo = fileUploadService.insertFileUpload("3",userId,tenantId,saasTenant.getBasePackageSpaceNum(),"png",vos);
        }
        if(vo != null) {
            if (!"0".equals(vo.getId())) {
                if (!"".equals(vo.getId())) {
                    room.setImageUrl(vo.getId());
                }
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription(), BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
            }
        }
        String codeId = this.obtainCode(tenantId,room.getId());
        if (!"".equals(codeId)) {
            room.setQrCodeUrl(codeId);
        }
        room.setPersionNumber(Integer.parseInt(persionNumber));
        boolean success = this.yuncmRoomAreaService.insertYuncmMeetingRoom(room, roleId, userId);
        if (success) {
            if(vo != null){
                //Map<String, String> imgMap = fileUploadCommonService.selectFileCommon(fileId);
                Map map1 = new HashMap();
                String primary = vo.getPrimary();
                String big = vo.getBig();
                String in = vo.getIn();
                String small = vo.getSmall();
                map1.put("primary",primary);
                map1.put("big",big);
                map1.put("in",in);
                map1.put("small",small);
                userService.saveImageUrl(userId,map1,vo.getId());
            }
            sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.add_meeting_room.toString(), name+"创建成功", "",Loglevel.info.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.add_meeting_room.toString(), "创建会议室失败", BusinessExceptionStatusEnum.Failure.getDescription(),Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }

    /**
     * 修改会议室(修改后)http://api.yunmetting.com/meetingRoom/
     *
     * @param roleId        预定权限
     * @param persionNumber 会议室容纳人数
     * @return
     */
    @RequestMapping(value = "/updateMeetingRoomModify",method = RequestMethod.POST)
    @ResponseBody
    public Object updateMeetingRoomModify(String userId,String id,String name,String areaId,String persionNumber,String location,
                                          String deviceService,String isAudit,String roleId,String imageUrl,String imageState,String imageSize,MultipartFile file) throws IOException {

        String imgId="";
        //获取租户id
        String tenantId = TenantContext.getTenantId();
        SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
        if("1".equals(imageState)) {
            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
            ext = "png";
            Map<String,String> map = new HashMap<>();
            map.put("big","300_225");
            map.put("in","240_240");
            map.put("small","60_45");
            //处理图片
           // Map<String,BufferedImage> imageMap = ResizeImage.resizeImage(file.getBytes(),map);
           // String fileId = fileUploadCommonService.uploadFileCommon(imageMap,ext,file.getBytes(),tenantId,userId);
            List<FastdfsVo> vos = FileUploadUtil.fileUpload(map,file.getBytes(),"png");
            FileVo vo = fileUploadService.insertFileUpload("3",userId,tenantId,saasTenant.getBasePackageSpaceNum(),"png",vos);
            if (!"0".equals(vo.getId())) {
                if (!"".equals(vo.getId())) {
                    imgId = vo.getId();
                } else {
                    sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.add_meeting_room.toString(), "修改会议室失败", BusinessExceptionStatusEnum.Failure.getDescription(), Loglevel.error.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
                }
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription(), BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
            }
        }
        //获取图片
        YuncmMeetingRoom meetingRoom = this.yuncmMeetingService.selectByYuncmMeetingRoom(id);
        if(!"0".equals(imageState)){
            if(StringUtils.isNotBlank(meetingRoom.getImageUrl())){
                List<SysAttachment> sysAttachments = this.fileUploadService.deleteFileUpload(meetingRoom.getImageUrl(),tenantId);
                boolean b = FileUploadUtil.deleteFile(sysAttachments);
                if(b){
                    userService.deleteImageUrl(meetingRoom.getImageUrl());
                }
            }
        }else{
            imgId = meetingRoom.getImageUrl();

        }
        boolean success = this.yuncmRoomAreaService.updateYuncmMeetingRoom(id, name, areaId, persionNumber, location, deviceService, isAudit, roleId, imgId, userId);
        if (success) {
            pushMessage(id,tenantId,"");
            if(StringUtils.isNotBlank(imgId)){
                Map<String, String> imgMap = fileUploadCommonService.selectFileCommon(imgId);
                Map map1 = new HashMap();
                String primary = imgMap.get("primary");
                String big = imgMap.get("big");
                String in = imgMap.get("in");
                String small = imgMap.get("small");
                map1.put("primary",primary);
                map1.put("big",big);
                map1.put("in",in);
                map1.put("small",small);
                userService.saveImageUrl(userId,map1,imgId);
            }
            //比对修改新消息
            String s = comparisonMeetingRoomInfo(meetingRoom);
            if(StringUtils.isNotBlank(s)){
                s = "会议室修改成功";
            }
            sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.update_meeting_room.toString(), s, "",Loglevel.info.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.update_meeting.toString(), "修改会议室失败", BusinessExceptionStatusEnum.Failure.getDescription(),Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }





     public void pushMessage(String roomId,String tenantId,String type){

         try {
             List<InfoReleaseTerminal> terminals = this.infoReleaseTerminalService.selectInfoReleaseTerminalByMettingRoomId(roomId);
             if(terminals != null && terminals.size() != 0) {
                 for (InfoReleaseTerminal terminal : terminals) {
                     String programId = this.conferenceService.getProgramId(terminals.get(0).getId());
                     if(StringUtils.isNotBlank(programId)) {
                         String types = RedisUtil.get(tenantId + "_QYH_play_program_" + programId);
                         List<String> list = new ArrayList<>();
                         Map map = new HashMap();
                         if (terminals != null) {
                             if ("del".equals(type)) {
                                 String flag = "";
                                 String str[] = types.split(",");
                                 for (int i = 0; i < str.length; i++) {
                                     if (!"C9001".equals(str[i])) {
                                         flag += str[i] + ",";
                                     }
                                 }
                                 map.put("type", flag);
                             } else {
                                 map.put("type", "C8001");
                             }
                             list.add(terminal.getId());
                             CommandMessage cmd = new CommandMessage();
                             cmd.setCmd("10101");
                             cmd.setTenantId(tenantId);
                             cmd.setTerminals(list);
                             cmd.setData(map);
                             cmd.setRequestId(CreateUUIdUtil.Uuid());
                             cmd.setTimestamp(new Date().getTime());
                             publishService.pushCommand(cmd);
                             logger.info("会议室变更推送---------------" + cmd);
                         }
                     }
                 }
             }
         } catch (Exception e) {
             e.printStackTrace();
         }

     }

    /**
     * 方法名：settingAdvanceDisplayTime</br>
     * 描述：设置会议显示提前开始时间</br>
     * 参数：[minutes]</br>
     * 返回值：java.lang.Object</br>
     */
    //@RequestMapping(value = "/settingadvancedisplaytime",method = RequestMethod.POST)
    //@ResponseBody
    public boolean settingAdvanceDisplayTime(String minutes){
        SysSetting settingKey = sysSetingService.findByKey("meetingroom.time");
        boolean b;
        if(null == settingKey){
            settingKey = new SysSetting();
            settingKey.setId(CreateUUIdUtil.Uuid());
            settingKey.setContent(minutes);
            settingKey.setCreateTime(new Date());
            settingKey.setSettingKey("meetingroom.time");
             b = sysSetingService.add(settingKey);
        }else{
            settingKey.setContent(minutes);
            b = sysSetingService.update(settingKey);
        }
        return b;
    }

   /* *//**
     * 更改节目播放时间同步到终端
     *//*
    public void pushMessages(String minutes){

        //获取租户id
        String tenantId = TenantContext.getTenantId();
        //获取租户下会议室id
        List<YuncmMeetingRoom> rooms = this.yuncmMeetingService.selectYuncmMeetingRoomAll();
        if(rooms != null){
            List<String> list =new ArrayList<>();
            for (YuncmMeetingRoom room : rooms){
                List<InfoReleaseTerminal> terminals = this.infoReleaseTerminalService.selectInfoReleaseTerminalByMettingRoomId(room.getId());
                if(terminals != null){
                    for (InfoReleaseTerminal terminal : terminals){
                        list.add(terminal.getId());
                    }
                }
            }
            if(list.size() != 0){
                CommandMessage cmd = new CommandMessage();
                Map map = new HashMap();
                map.put("type",null);
                cmd.setCmd("10101");
                cmd.setTenantId(tenantId);
                cmd.setTerminals(list);
                cmd.setData(map);
                cmd.setRequestId(CreateUUIdUtil.Uuid());
                cmd.setTimestamp(new Date().getTime());
                publishService.pushCommand(cmd);
            }



        }



    }

*/




}
