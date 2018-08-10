package com.thinkwin.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.publish.CommandMessage;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.utils.validation.MeetingValidationUtil;
import com.thinkwin.common.vo.meetingVo.*;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.mailsender.service.MailTemplateMsgService;
import com.thinkwin.mailsender.service.YunmeetingSendMailService;
import com.thinkwin.promotion.service.PricingConfigService;
import com.thinkwin.publish.service.PublishService;
import com.thinkwin.schedule.service.TimerService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.web.timer.MeetingStatusAmend;
import com.thinkwin.web.timer.WechatInformTimer;
import com.thinkwin.yuncm.service.*;
import com.thinkwin.yunmeeting.weixin.service.WxTemplateMsgService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 类名: MeetingController </br>
 * 描述: 会议controller</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/8/2 </br>
 */
@RequestMapping(value = "/meeting", method = RequestMethod.POST)
@Controller
public class MeetingController {

    @Resource
    MeetingReserveService meetingReserveService;
    @Resource
    UserService userService;
    @Resource
    FileUploadService fileUploadService;
    @Resource
    MeetingDynamicService meetingDynamicService;
    @Resource
    YuncmRoomAreaService yuncmRoomAreaService;
    @Resource
    WxTemplateMsgService wxTemplateMsgService;
    @Resource
    YuncmMeetingService yuncmMeetingService;
    @Resource
    TimerService timerService;
    @Resource
    SysLogService sysLogService;
    @Resource
    YunmeetingSendMailService mailSenderService;
    @Resource
    MailTemplateMsgService mailTemplateMsgService;
    @Resource
    SaasTenantService saasTenantCoreService;
    @Resource
    PublishService publishService;
    @Resource
    InfoReleaseTerminalService infoReleaseTerminalService;
    @Resource
    ConferenceService conferenceService;
    @Resource
    SysSetingService sysSetingService;
    @Resource
    SyncProgramService syncProgramService;
    /**
     * 方法名：addMeeting</br>
     * 描述：增加会议</br>
     * 参数：[meetingVo]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping("/addorupdatemeeting")
    @ResponseBody
    public Object addOrUpdatMeeting(String data) {
        MeetingDetailsVo meetingDetailsVo = JSON.parseObject(data, new TypeReference<MeetingDetailsVo>() {
        });
        //获取用户Id
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        String userName = userInfo.getUserName();
        String contents = "";
        String eventTypes = "";
        String roomId = "";
        //校验前台传参以及给实体赋值
        Map<String, Object> map = MeetingValidationUtil.addMeetingValidation(meetingDetailsVo, userId);
        if (null == map) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }

        //校验预定时间是否在可预订范围内
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start=format.format(meetingDetailsVo.getStart());
        String end=format.format(meetingDetailsVo.getEnd());
        if(!checkMeetingTime(userInfo.getTenantId(),meetingDetailsVo.getResourceId(),start,end)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "不在可预订时间范围内");
        }
        //获取会议最大最小时长
        YuncmRoomReserveConf conf = yuncmMeetingService.selectYuncmRoomReserveConf();
        if(conf.getMeetingMaximum() != 0) {
            if (TimeUtil.timeMillisecond(conf.getMeetingMaximum(), "hour") < meetingDetailsVo.getEnd() - meetingDetailsVo.getStart()) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "会议时长不能大于" + conf.getMeetingMaximum() + "小时");
            }
        }
        if(conf.getMeetingMinimum() != 0) {
            if (TimeUtil.timeMillisecond(conf.getMeetingMinimum(), "minute") > meetingDetailsVo.getEnd() - meetingDetailsVo.getStart()) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "会议时长不能小于" + conf.getMeetingMinimum() + "分钟");
            }
        }

        //获取是否为增加状态
        String state = (String) map.get("state");
        //获取会议实体
        YunmeetingConference yunmeetingConference = (YunmeetingConference) map.get("yunmeetingConference");
        //获取会议Id
        String conferenceId = yunmeetingConference.getId();
        //在变更之前查询会议
        YunmeetingConference meeting = new YunmeetingConference();
        //获取原会议室id
        String meetingRoomId = meetingReserveService.getMeetingIdAndRoomId(conferenceId);
        String local = "";
        if(state.equals("update")){
            meeting = meetingReserveService.selectMeetingByMeetingId(conferenceId);
            if(meeting.getState().equals("5")){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.MeetingNotExists.getDescription(), BusinessExceptionStatusEnum.MeetingNotExists.getCode());
            }
            //查询变更前的会议室
            List<YuncmMeetingRoom> yuncmMeetingRooms = meetingReserveService.selectMeetingRoomByMeetingId(conferenceId);
            if (null != yuncmMeetingRooms) {
                local = yuncmMeetingRooms.get(0).getName();
            }
        }
        String conferenceName = yunmeetingConference.getConferenceName();
        //判断主办方是否为空
        String hostUnit = yunmeetingConference.getHostUnit();
        if (StringUtils.isBlank(hostUnit)) {
            //查询当前用户组织信息
            SysUser sysUser = userService.selectUserByUserId(userId);
            if (null != sysUser) {
                yunmeetingConference.setHostUnit(sysUser.getOrgId());
            }
        }
        //获取会议通知实体
        YunmeetingMessageInform yunmeetingMessageInform = (YunmeetingMessageInform) map.get("yunmeetingMessageInform");
        //获取提前提醒时间List
        List<Date> list = (List<Date>) map.get("times");
        //保存会议和会议室中间表
        if (map.containsKey("yummeetingConferenceRoomMiddle")) {
            //获取会议和会议室中间表
            YummeetingConferenceRoomMiddle yummeetingConferenceRoomMiddle = (YummeetingConferenceRoomMiddle) map.get("yummeetingConferenceRoomMiddle");
            roomId = yummeetingConferenceRoomMiddle.getRoomId();
            //查询会议室是否被删除
            YuncmMeetingRoom meetingRoom = yuncmMeetingService.findMeetingRoom(roomId);
            if(null==meetingRoom) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.MeetingRoomNotExists.getDescription(), BusinessExceptionStatusEnum.MeetingRoomNotExists.getCode());
            }
            //判断会议室是否被占用
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Boolean meetingRoomTakeInfo = false;
            Map<String, Object> map1 = new HashMap<>();
            if(state.equals("save")){
                contents = userName+"预定会议失败";
                eventTypes =  EventType.meeting_reservation.toString();
                map1.put("meetingRoomId", roomId);
                map1.put("begin", simpleDateFormat.format(yunmeetingConference.getTakeStartDate()) + ":00");
                map1.put("end", simpleDateFormat.format(yunmeetingConference.getTakeEndDate()) + ":00");
                map1.put("auditState", "auditState");
                meetingRoomTakeInfo = yuncmMeetingService.findMeetingRoomTakeInfo(map1);
            }else{
                contents = userName+"更改会议信息失败";
                eventTypes =  EventType.update_meeting.toString();
                map1.put("meetingRoomId", roomId);
                map1.put("begin", simpleDateFormat.format(yunmeetingConference.getTakeStartDate()) + ":00");
                map1.put("end", simpleDateFormat.format(yunmeetingConference.getTakeEndDate()) + ":00");
                map1.put("meetingId", conferenceId);
                map1.put("auditState", "auditState");
                meetingRoomTakeInfo = yuncmMeetingService.findMeetingRoomTakeInfo(map1);
            }
            if(meetingRoomTakeInfo){
                //增加会议操作日志
                sysLogService.createLog(BusinessType.meetingOperationOp.toString(),eventTypes,contents,BusinessExceptionStatusEnum.MeetingRoomBeOccupied.getDescription(), Loglevel.error.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.MeetingRoomBeOccupied.getDescription(), BusinessExceptionStatusEnum.MeetingRoomBeOccupied.getCode());
            }
            //查询用户是否有预定权限
            Boolean flag = selectUserMeetingRoomRole(roomId);
            if (!flag) {
                //增加会议操作日志
                sysLogService.createLog(BusinessType.meetingOperationOp.toString(), EventType.meeting_reservation.toString(),contents,BusinessExceptionStatusEnum.PermissionDenied.getDescription(),Loglevel.error.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PermissionDenied.getDescription(), BusinessExceptionStatusEnum.PermissionDenied.getCode());
            }
            yunmeetingConference = meetingReserveService.reserveMeeting(yummeetingConferenceRoomMiddle, yunmeetingConference);
        }
        Map<String, Object> map1 = new HashMap<>();
        //保存或修改会议表
        boolean b = false;
        if (state.equals("save")) {
            b = meetingReserveService.insertMeeting(yunmeetingConference);
        } else {
            b = meetingReserveService.updateMeeting(yunmeetingConference);
            //增加删除签到信息   2018/07/24  扫码签到分支修改   weining
            meetingReserveService.delectSignInfoByMeetingId(conferenceId);
        }
        if (b) {
            //向终端发送变更指令
            pushMessage(roomId,userInfo.getTenantId(),yunmeetingConference,meetingRoomId);
            b = false;
            //增加提前提醒的id集合
            List<String> yunmeetingMessageInformIds = new ArrayList<>();
            //保存会议通知表
            if (null != yunmeetingMessageInform) {
                if (null != list && list.size() > 0) {
                    if (state.equals("update")) {
                        //删除会议通知表信息处理
                        deleteMeetingInform(conferenceId);
                    }
                    for (Date time : list) {
                        String messageInformId = CreateUUIdUtil.Uuid();
                        yunmeetingMessageInform.setInformTime(time);
                        yunmeetingMessageInform.setId(messageInformId);
                        yunmeetingMessageInformIds.add(messageInformId);
                        b = meetingReserveService.insertMeetingMessageInform(yunmeetingMessageInform);
                    }
                }else{
                    //删除会议通知表信息处理  处理修改的时候把提醒取消问题
                    deleteMeetingInform(conferenceId);
                    if(StringUtils.isNotBlank(yunmeetingMessageInform.getInformType())){
                        yunmeetingMessageInform.setId(CreateUUIdUtil.Uuid());
                        b = meetingReserveService.insertMeetingMessageInform(yunmeetingMessageInform);
                    }
                }
            }
            //保存会议参与人员
            List<MeetingParticipantsVo> userIds = meetingDetailsVo.getAttendees();
            //处理前台没传参会人时 默认为组织者
            if (null == userIds) {
                String organizerId = yunmeetingConference.getOrganizerId();
                MeetingParticipantsVo meetingParticipantsVo = new MeetingParticipantsVo();
                meetingParticipantsVo.setId(organizerId);
                meetingParticipantsVo.setDep("0");
                SysUser sysUser = userService.selectUserByUserId(organizerId);
                meetingParticipantsVo.setName(sysUser.getUserName());
                userIds.add(meetingParticipantsVo);
            }
            if (state.equals("save")) {
                b = meetingReserveService.insertMeetingPeople(userIds, conferenceId, userId);
                if (b) {
                    //增加动态信息
                    map1.put("meetingId", yunmeetingConference.getId());
                    map1.put("content",userName + "创建了" + yunmeetingConference.getConferenceName() + "会议。");
                    map1.put("userId",userId);
                    List<MeetingParticipantsVo> wechatUserId = meetingDynamicService.insertMeetingDynamicByMeetingId(map1);
                    MeetingVo meetingVo = wrapperMeetingVoUtil(wechatUserId, yunmeetingConference);
                    if(null!=meetingVo) {
                        if (yunmeetingConference.getIsAudit().equals("0")) {
                            //增加微信通知
                            insertWechatNotice(meetingVo, "save");
                            //增加微信定时提醒
                            if (null != list && list.size() > 0) {
                                int i = 0;
                                if (yunmeetingMessageInform.getInformType().indexOf("1") != -1) {
                                    for (Date time : list) {
                                        settingWechatInformTimer(yunmeetingMessageInformIds.get(i), meetingVo, time);
                                        i++;
                                    }
                                }
                            }
                        }else{
                            //增加微信审核通知
                            insertWechatAudit(userId, yunmeetingConference);
                        }
                    }
                    if (yunmeetingMessageInform.getInformType().indexOf("2") != -1) {
                        //发送邮件通知
                        mailTemplateMsgService.createMeetingNotice(conferenceId);
                    }
                }
            } else {
                if(meeting.getIsAudit().equals("0")&&yunmeetingConference.getIsAudit().equals("1")){
                    //发送取消会议邮件
                    if(yunmeetingMessageInform.getInformType().indexOf("2")!= -1){
                        mailTemplateMsgService.cancelMeetingNotice(conferenceId,true);
                    }
                    //增加微信取消通知
                    MeetingVo meetingVo = wrapperMeetingVoUtil(userIds, yunmeetingConference);
                    if(null!=meetingVo) {
                        //增加微信通知
                        insertWechatNotice(meetingVo, "cancel");
                    }
                }
                contents = userName+"更改了会议"+conferenceName+"会议信息";
                eventTypes =  EventType.update_meeting.toString();
                //先删除所有参会人员
                boolean b1 = meetingReserveService.deleteMeetingPeople(conferenceId);
                if (b1) {
                    b = meetingReserveService.insertMeetingPeople(userIds, conferenceId, userId);
                    if (b) {
                            //增加动态信息
                        map1.put("meetingId", yunmeetingConference.getId());
                        map1.put("content",  userName + "修改了会议信息，请及时查看。");
                        //根据会议Id查询会议室
                        List<YuncmMeetingRoom> yuncmMeetingRooms = meetingReserveService.selectMeetingRoomByMeetingId(meeting.getId());
                        String address = "";
                        if (null != yuncmMeetingRooms) {
                            address = yuncmMeetingRooms.get(0).getName();
                        }
                        if (meeting.getTakeStartDate().getTime() != yunmeetingConference.getTakeStartDate().getTime()
                            || meeting.getTakeEndDate().getTime() != yunmeetingConference.getTakeEndDate().getTime()
                            || !address.equals(local)) {
                            String content = userName + "于" + fomatDate(meeting.getTakeStartDate(), meeting.getTakeEndDate()) + "" +
                                    "在" + local + "召开的“" + meeting.getConferenceName() + "”，" +
                                    "变更到" + fomatDate(yunmeetingConference.getTakeStartDate(), yunmeetingConference.getTakeEndDate()) + "" +
                                    "在" + address + "召开。";
                            if (StringUtils.isNotBlank(yunmeetingConference.getModifyReason())) {
                                content = content + "变更原因：" + yunmeetingConference.getModifyReason() + "。";
                            }
                            map1.put("content", content);
                            //发送邮件通知
                            if (yunmeetingMessageInform.getInformType().indexOf("2") != -1) {
                                Map<String,Object> templateMaps = new HashMap<>();
                                templateMaps.put("startBefore",fomatDate(meeting.getTakeStartDate(), meeting.getTakeEndDate()));
                                templateMaps.put("addressBefore",local);
                                if (StringUtils.isNotBlank(yunmeetingConference.getModifyReason())) {
                                    templateMaps.put("changeReason",yunmeetingConference.getModifyReason());
                                }
                                //发送邮件通知
                                mailTemplateMsgService.changeMeetingNotice(conferenceId,templateMaps);
                            }
                        }else{
                            if (yunmeetingMessageInform.getInformType().indexOf("2") != -1) {
                                //发送邮件通知
                                mailTemplateMsgService.changeMeetingNotice(conferenceId,null);
                            }
                        }
                        map1.put("userId",userId);
                        List<MeetingParticipantsVo> wechatUserId = meetingDynamicService.insertMeetingDynamicByMeetingId(map1);
                        MeetingVo meetingVo = wrapperMeetingVoUtil(wechatUserId, yunmeetingConference);
                        if(null!=meetingVo) {
                            if (yunmeetingConference.getIsAudit().equals("0")) {
                                //增加微信通知
                                insertWechatNotice(meetingVo, "update");
                                //增加微信定时提醒
                                if (null != list && list.size() > 0) {
                                    if (yunmeetingMessageInform.getInformType().indexOf("1") != -1) {
                                        for (Date time : list) {
                                            settingWechatInformTimer(yunmeetingMessageInform.getId(), meetingVo, time);
                                        }
                                    }
                                }
                            }else{
                                //增加微信审核通知
                                insertWechatAudit(userId, yunmeetingConference);
                            }
                        }
                    }
                }
            }
            if (b) {
                //设置定时修改会议状态
                settingMeetingTimerTask(yunmeetingConference);
                //增加会议操作日志
                //sysLogService.createLog(BusinessType.meetingOperationOp.toString(), eventTypes,contents,null,Loglevel.info.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), yunmeetingConference.getId(), "");
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
    }

    /**
     * 方法名：deleteMeetingInform</br>
     * 描述：删除会议消息通知表信息</br>
     * 参数：</br>
     * 返回值：</br>
     */
    private void deleteMeetingInform(String conferenceId){
        String tenantId = TenantContext.getTenantId();
        //删除之前查询一下会议消息表  取消微信会议定时通知
        List<YunmeetingMessageInform> yunmeetingMessageInforms = meetingReserveService.selectMeetingMessageeInform(conferenceId);
        if(null!=yunmeetingMessageInforms) {
            for (YunmeetingMessageInform yunmeetingMessageInform1 : yunmeetingMessageInforms) {
                String id = yunmeetingMessageInform1.getId();
                //根据会议Id获取redis里面的定时Id
                String timerId = RedisUtil.get(tenantId + "_QYH_Timer_Wechat_" + id);
                if (StringUtils.isNotBlank(timerId)) {
                    timerService.cancelTask(timerId);
                }
                RedisUtil.remove(tenantId + "_QYH_Timer_Wechat_" + id);
            }
        }
        //先删除所有会议通知表
        meetingReserveService.deleteMeetingMessageInform(conferenceId);
    }
    /**
     * 方法</br>
     * 描述：查询当前用户是否能创建会议</br>
     * 参数：roomId</br>
     * 返回值：</br>
     */
    @RequestMapping("/selectuserrole")
    @ResponseBody
    public Object selectUserRole(String roomId) {
        //查询当前租户是否为付费状态
        SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
        Map<String,Object> map = new HashMap<>();
        if(null!=saasTenant){
            map.put("tenantType",saasTenant.getTenantType());
        }
        Boolean b = selectUserMeetingRoomRole(roomId);
        if(b){
            map.put("isCreate",1);
        }else{
            map.put("isCreate",0);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),map);
    }

    /**
     * 方法名：</br>
     * 描述：查询当前用户是否能创建会议</br>
     * 参数：roomId</br>
     * 返回值：</br>
     */
    public Boolean selectUserMeetingRoomRole(String roomId) {
        //获取用户Id
        String userId = TenantContext.getUserInfo().getUserId();
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
     * 方法名：MeetingDayView</br>
     * 描述：查询会议日视图</br>
     * 参数：start 指定月数时间</br>
     * 返回值：</br>
     */
    @RequestMapping("/selectmeetingdayview")
    @ResponseBody
    public Object selectMeetingDayView(Long start) {
        //获取用户Id
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        List<String> listTime = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long ss = new Date().getTime();
        if (null == start || 0 == start) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        String time = format.format(start);
        try {
            Date parseTime = format.parse(time);
            //查询指定月所有会议信息
            Map<String, Object> map = new HashMap<>();
            map.put("myAll", "myAll");
            map.put("userId", userId);
            map.put("currentMonth", time);
            map.put("showAudit","showAudit");
            map.put("showCancel","showCancel");
            List<YunmeetingConference> yunmeetingConferences = meetingReserveService.selectMeetingDayViewByTime(map);
            if (null != yunmeetingConferences && yunmeetingConferences.size() > 0) {
                for (YunmeetingConference yunmeetingConference : yunmeetingConferences) {
                    Date takeStartDate = yunmeetingConference.getTakeStartDate();
                    if (null != takeStartDate) {
                        listTime.add(format.format(takeStartDate));
                    }
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), listTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(),listTime, BusinessExceptionStatusEnum.DataNull.getCode());
    }

    /**
     * 方法名：selectSetTimeMeeting</br>
     * 描述：查询指定时间的会议</br>
     * 参数：start  指定时间</br>
     * 返回值：</br>
     */
    @RequestMapping(value = "/selectsettimemeeting")
    @ResponseBody
    public Object selectSetTimeMeeting(Long start,String all) {
        //获取用户Id
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userId = userInfo.getUserId();
        List<MeetingDayViewVo> list = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (null == start || 0 == start) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        String time = format.format(start);
        try {
            Date parseTime = format.parse(time);
            Map<String, Object> map = new HashMap<>();
            if(StringUtils.isBlank(all)||all.equals("0")){
                map.put("myAll", "myAll");
                map.put("showAudit", "showAudit");
            }
            map.put("userId", userId);
            map.put("days", "days");
            map.put("date", parseTime);
            map.put("showCancel","showCancel");
            //获取当前用户是否有审核权限
            Map<String,Object> userAuditRole = getUserAuditRole();
            String isAuthstr = (String) userAuditRole.get("isAuthstr");
            List<YunmeetingConference> yunmeetingConferences = meetingReserveService.selectSetTimeMeeting(map);
            if (null == yunmeetingConferences) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, isAuthstr,list ,BusinessExceptionStatusEnum.DataNull.getCode());
            }
            //处理未审核会议时间问题
            DateSegmentUtil dateSegmentUtil = new DateSegmentUtil();
            for (YunmeetingConference yunmeetingConference : yunmeetingConferences) {
                if (null != yunmeetingConference) {
                    String meetingId = yunmeetingConference.getId();
                    Date takeStartDate = yunmeetingConference.getTakeStartDate();
                    Date takeEndDate = yunmeetingConference.getTakeEndDate();
                    if (null != takeStartDate && null != takeEndDate) {
                        String state = yunmeetingConference.getState();
                        if (null != state) {
                            if (!state.equals("5") || !state.equals("0")) {
                                MeetingDayViewVo meetingDayViewVo = new MeetingDayViewVo();
                                meetingDayViewVo.setStart(takeStartDate.getTime());
                                meetingDayViewVo.setEnd(takeEndDate.getTime());
                                meetingDayViewVo.setMeetingId(meetingId);
                                meetingDayViewVo.setMeetingSubject(yunmeetingConference.getConferenceName());
                                //根据用户Id查询该用户是否和该会议相关
                                Map<String,Object> conditionMap = new HashMap<>();
                                conditionMap.put("meetingId",yunmeetingConference.getId());
                                conditionMap.put("userId",userId);
                                conditionMap.put("myAll","myAll");
                                List<YunmeetingConference> yunmeetingConferences1 = meetingReserveService.selectSetTimeMeeting(conditionMap);
                                if(null!=yunmeetingConferences1&&yunmeetingConferences1.size()>0){
                                    meetingDayViewVo.setState("0");
                                    meetingDayViewVo.setIsPublic("1");
                                    meetingDayViewVo.setIsSelectDeteils("1");
                                }else{
                                    meetingDayViewVo.setIsSelectDeteils("0");
                                    String isPublic = yunmeetingConference.getIsPublic();
                                    if(isPublic.equals("0")){
                                        meetingDayViewVo.setIsPublic("0");
                                    }else{
                                        meetingDayViewVo.setIsPublic("1");
                                    }
                                }
                                if(userId.equals(yunmeetingConference.getReservationPersonId())){
                                    meetingDayViewVo.setIsReservationPerson("1");
                                }else{
                                    meetingDayViewVo.setIsReservationPerson("0");
                                }
                                //查询会议预订人和参与者
                                String organizerId = yunmeetingConference.getOrganizerId();
                                if (organizerId.equals(userId)) {
                                    meetingDayViewVo.setState("1");
                                }
                                //根据用户Id查询用户信息
                                SysUser sysUser = userService.selectUserByUserId(organizerId);
                                if (null != sysUser) {
                                    meetingDayViewVo.setName(sysUser.getUserName());
                                    meetingDayViewVo.setPhone(sysUser.getPhoneNumber());
                                    //获取头像路径
                                    String photo = sysUser.getPhoto();
                                    if (StringUtils.isNotBlank(photo)) {
                                       /* Map<String, String> photos = fileUploadService.selectFileCommon(photo);*/
                                        Map<String, String> photos = userService.getUploadInfo(photo);
                                        if(null != photos) {
                                            meetingDayViewVo.setPhoto(photos.get("primary"));
                                            meetingDayViewVo.setBigPicture(photos.get("big"));
                                            meetingDayViewVo.setInPicture(photos.get("in"));
                                            meetingDayViewVo.setSmallPicture(photos.get("small"));
                                        }
                                    }
                                }
                                //查询会议室信息
                                List<YuncmMeetingRoom> yuncmMeetingRooms = meetingReserveService.selectMeetingRoomByMeetingId(meetingId);
                                if (null != yuncmMeetingRooms) {
                                    YuncmMeetingRoom yuncmMeetingRoom = yuncmMeetingRooms.get(0);
                                    meetingDayViewVo.setResourceId(yuncmMeetingRoom.getId());
                                    meetingDayViewVo.setLocation(yuncmMeetingRoom.getName());
                                    if(state.equals("1")){
                                        //meetingDayViewVo.setState("2");
                                        dateSegmentUtil.setSegment(yunmeetingConference.getTakeStartDate(),yunmeetingConference.getTakeEndDate(),yuncmMeetingRoom.getId());
                                        continue;
                                    }
                                }
                                list.add(meetingDayViewVo);
                            }
                        }
                    }
                }
            }
            //待审核会议时间格式化处理
            Map<String, List<DateSegmentVo>> stringListMap = dateSegmentUtil.generatorDateSegmentGroup();
            if(null!=stringListMap) {
                Iterator<Map.Entry<String, List<DateSegmentVo>>> iterator = stringListMap.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String, List<DateSegmentVo>> next = iterator.next();
                    String key = next.getKey();
                    List<DateSegmentVo> value = next.getValue();
                    for (DateSegmentVo dateSegmentVo : value) {
                        MeetingDayViewVo meetingDayViewVo = new MeetingDayViewVo();
                        meetingDayViewVo.setState("2");
                        meetingDayViewVo.setStart(dateSegmentVo.getStartDate().getTime());
                        meetingDayViewVo.setEnd(dateSegmentVo.getEndDate().getTime());
                        meetingDayViewVo.setResourceId(key);
                        list.add(meetingDayViewVo);
                    }
                }
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,isAuthstr, list);
        } catch (ParseException e) {
            e.printStackTrace();
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
        }
    }

    /**
     * 方法名：selectMeetingDetails</br>
     * 描述：查询会议详情根据会议Id</br>
     * 参数：meetingId 会议室Id</br>
     * 返回值：</br>
     */
    @RequestMapping("/selectmeetingdetails")
    @ResponseBody
    public Object selectMeetingDetails(String meetingId) {
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        if (StringUtils.isNotBlank(meetingId)) {
            //查询会议详情
            MeetingDetailsVo meetingDetailsVo = selectMeetingDeteilsUtil(meetingId,userId);
            if(null!=meetingDetailsVo) {
                //查询当前租户是否为付费状态
                SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
                Map<String,Object> map = new HashMap<>();
                if(null!=saasTenant){
                    meetingDetailsVo.setTenantType(saasTenant.getTenantType());
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), meetingDetailsVo);
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.MeetingNotExists.getDescription(), BusinessExceptionStatusEnum.MeetingNotExists.getCode());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 方法名：cancelMeeting</br>
     * 描述：根据会议Id取消预订会议</br>
     * 参数：meetingId 会议Id</br>
     * 返回值：</br>
     */
    @RequestMapping("/cancelmeeting")
    @ResponseBody
    public Object cancelMeeting(String meetingId, String cancelReason) {
        try {
            if(StringUtils.isNotBlank(cancelReason)) {
                cancelReason = URLDecoder.decode(cancelReason, "utf-8");
                if(cancelReason.length()>200){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "取消原因长度受限");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        String userName = userInfo.getUserName();
        String tenantId = userInfo.getTenantId();
        if (StringUtils.isNotBlank(meetingId)) {
            String roomId = meetingReserveService.getMeetingIdAndRoomId(meetingId);
            List<YunmeetingConference> conferences = this.conferenceService.getNextConference(roomId, new Date());
            //根据会议Id查询会议信息
            YunmeetingConference yunmeetingConference = meetingReserveService.selectMeetingByMeetingId(meetingId);
            if(null!=yunmeetingConference){
                boolean isDelete = false;
                if(yunmeetingConference.getState().equals("5") || yunmeetingConference.getState().equals("0")){
                    yunmeetingConference.setDeleteState("1");
                    isDelete = true;
                }else {
                    yunmeetingConference.setState("5");
                    yunmeetingConference.setCancelReason(cancelReason);
                }
                boolean b = meetingReserveService.updateMeeting(yunmeetingConference);
                if (b) {
                    if(!isDelete) {
                        //根据会议Id获取redis里面的定时Id
                        String timerId1 = RedisUtil.get(tenantId + "_QYH_Timer_Meeting_" + meetingId +"3");
                        String timerId2 = RedisUtil.get(tenantId + "_QYH_Timer_Meeting_" + meetingId +"4");
                        //取消定时器
                        if (StringUtils.isNotBlank(timerId1)&&StringUtils.isNotBlank(timerId2)) {
                            timerService.cancelTask(timerId1);
                            timerService.cancelTask(timerId2);
                        }
                        //删除redis里面的定时器
                        RedisUtil.remove(tenantId + "_QYH_Timer_Meeting_" + meetingId +"3");
                        RedisUtil.remove(tenantId + "_QYH_Timer_Meeting_" + meetingId +"4");
                        //增加动态
                        Map<String, Object> map = new HashMap<>();
                        map.put("meetingId", yunmeetingConference.getId());
                        String content = userName + "取消了会议，请知晓。";
                        if(StringUtils.isNotBlank(cancelReason)){
                            content += "取消原因：“" + cancelReason + "”。";
                        }
                        map.put("content", content);
                        map.put("userId",userId);
                        List<MeetingParticipantsVo> userIds = meetingDynamicService.insertMeetingDynamicByMeetingId(map);
                        //获取预定人Id
                        boolean flag = false;
                        String reservationPersonId = yunmeetingConference.getReservationPersonId();
                        if (null != userIds && userIds.size() > 0) {
                            for (MeetingParticipantsVo meetingParticipantsVo : userIds) {
                                if (meetingParticipantsVo.getId().equals(reservationPersonId)) {
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if (!flag) {
                            MeetingParticipantsVo meetingParticipantsVo = new MeetingParticipantsVo();
                            meetingParticipantsVo.setId(reservationPersonId);
                            userIds.add(meetingParticipantsVo);
                        }
                        //为待审核会议的时候只发送给预定人
                        String state = yunmeetingConference.getState();
                        if (yunmeetingConference.getIsAudit().equals("1") && (state.equals("0") || state.equals("1"))) {
                            userIds = new ArrayList<>();
                            MeetingParticipantsVo meetingParticipantsVo = new MeetingParticipantsVo();
                            meetingParticipantsVo.setId(reservationPersonId);
                            userIds.add(meetingParticipantsVo);
                        }
                        MeetingVo meetingVo = wrapperMeetingVoUtil(userIds, yunmeetingConference);
                        if(null!=meetingVo) {
                            //增加微信通知
                            insertWechatNotice(meetingVo, "cancel");
                        }
                        //查询会议通知信息表
                        List<YunmeetingMessageInform> yunmeetingMessageInforms = meetingReserveService.selectMeetingMessageeInform(meetingId);
                        if (null != yunmeetingMessageInforms && yunmeetingMessageInforms.size() > 0) {
                            String informType = yunmeetingMessageInforms.get(0).getInformType();
                            if (informType.indexOf("2") != -1) {
                                //增加发送邮件通知
                                mailTemplateMsgService.cancelMeetingNotice(meetingId,false);
                            }
                        }
                    }
                    MeetingDetailsVo meetingDetailsVo = selectMeetingDeteilsUtil(meetingId,userId);
                    //增加会议操作日志
                    sysLogService.createLog(BusinessType.meetingOperationOp.toString(), EventType.cancel_reservation.toString(),userName+"预定的"+yunmeetingConference.getConferenceName()+"被取消",null,Loglevel.info.toString());
                    //向终端推送会议信息
                    cancelPushMessage(tenantId,yunmeetingConference,conferences,roomId);
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),meetingDetailsVo);
                }
            }else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 方法名：auditMeeting</br>
     * 描述：审核会议</br>
     * 参数：meetingId 会议Id</br>
     * 参数：auditState 审核状态 2 通过 0未通过
     * 参数：reason 原因  审核未通过原因
     * 返回值：</br>
     */
    @RequestMapping("/auditmeeting")
    @ResponseBody
    public Object auditMeeting(String meetingId, String auditState, String reason, String address, String roomId) {
        //获取用户Id和用户名称
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        String userName = userInfo.getUserName();
        //根据会议Id查询会议信息
        YunmeetingConference yunmeetingConference = meetingReserveService.selectMeetingByMeetingId(meetingId);
        if (null != yunmeetingConference) {
            //开始审核会议
            if (StringUtils.isNotBlank(meetingId) && StringUtils.isNotBlank(auditState)) {
                if(StringUtils.isNotBlank(reason)&&reason.length()>200){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "审核原因长度受限");
                }
                if(StringUtils.isBlank(reason)&&auditState.equals("0")){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "审核未通过原因不能为空");
                }
                //判断会议室是否正常使用中
                roomId = meetingReserveService.getMeetingIdAndRoomId(meetingId);
                YuncmMeetingRoom room = this.yuncmMeetingService.selectByYuncmMeetingRoom(roomId);
                if(!"2".equals(room.getState())){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "会议室已停用");
                }
                YunmeetingConferenceAudit yunmeetingConferenceAudit = new YunmeetingConferenceAudit();
                yunmeetingConferenceAudit.setAuditAnnotations(reason);
                yunmeetingConferenceAudit.setBaseConfrerenId(meetingId);
                if (auditState.equals("2")) {
                    yunmeetingConferenceAudit.setState("1");
                } else {
                    yunmeetingConferenceAudit.setState("0");
                }
                boolean b = meetingReserveService.insertMeetingAuditInfo(yunmeetingConferenceAudit);
                if (b) {
                    yunmeetingConference.setState(auditState);
                    boolean b2 = belongCalendar(new Date(), yunmeetingConference.getTakeStartDate(), yunmeetingConference.getTakeEndDate());
                    if(b2){
                        yunmeetingConference.setState("3");
                    }
                    boolean b1 = meetingReserveService.updateMeeting(yunmeetingConference);
                    if (b1) {
                        //获取预定人Id
                        String reservationPersonId = yunmeetingConference.getReservationPersonId();
                        //根据预订人Id查询用户姓名
                        SysUser sysUser = userService.selectUserByUserId(reservationPersonId);
                        String name = "";
                        if (null != sysUser) {
                            name = sysUser.getUserName();
                        }
                        YunmeetingDynamics yunmeetingDynamics = new YunmeetingDynamics();
                        List<MeetingParticipantsVo> list = new ArrayList<>();
                        if (StringUtils.isNotBlank(sysUser.getWechat())) {
                            MeetingParticipantsVo meetingParticipantsVo = new MeetingParticipantsVo();
                            meetingParticipantsVo.setId(reservationPersonId);
                            list.add(meetingParticipantsVo);
                        }
                        MeetingVo meetingVo = wrapperMeetingVoUtil(list, yunmeetingConference);
                        if (auditState.equals("2")) {
                            if (null != meetingVo) {
                                insertWechatNotice(meetingVo, "1");
                            }
                            //判断该时段有无其他待审核会议
                            List<YunmeetingConference> yunmeetingConferences = checkMoreAuditMeeting(meetingId, roomId, yunmeetingConference.getTakeStartDate().getTime(), yunmeetingConference.getTakeEndDate().getTime());
                            if(null!=yunmeetingConferences&&yunmeetingConferences.size()>0){
                                for(YunmeetingConference yunmeetingConference1 : yunmeetingConferences){
                                    //处理审核未通过
                                    auditMeetingUtil(yunmeetingConference1);
                                }
                            }
                            //终端推送审核通过指令
                            pushJudge(meetingReserveService.getMeetingIdAndRoomId(meetingId),userInfo.getTenantId(),yunmeetingConference,"");
                            yunmeetingDynamics.setContent(name + "预定" + fomatDate(yunmeetingConference.getTakeStartDate(), yunmeetingConference.getTakeEndDate()) + "在" + address + "召开的“" + yunmeetingConference.getConferenceName() + "”会议通过审批。");
                            //审核通过  给所有参会人发送微信通知
                            List<PersonsVo> personsVos = meetingReserveService.selectAllParticipantByMeetingId(meetingId);
                            List<MeetingParticipantsVo> list1 = new ArrayList<>();
                            for (PersonsVo personsVo : personsVos) {
                                MeetingParticipantsVo meetingParticipantsVo = new MeetingParticipantsVo();
                                meetingParticipantsVo.setId(personsVo.getUserId());
                                list1.add(meetingParticipantsVo);
                            }
                            MeetingVo meetingVo1 = wrapperMeetingVoUtil(list1, yunmeetingConference);
                            //增加微信通知
                            insertWechatNotice(meetingVo1, "save");
                            //根据会议Id查询会议通知表
                            List<YunmeetingMessageInform> yunmeetingMessageInforms = meetingReserveService.selectMeetingMessageeInform(meetingId);
                            //增加微信定时提醒
                            if (null != yunmeetingMessageInforms && yunmeetingMessageInforms.size() > 0) {
                                //增加邮件通知
                                if(yunmeetingMessageInforms.get(0).getInformType().indexOf("2") != -1){
                                    //发送审核通过邮件
                                    mailTemplateMsgService.attendMeetingNotice(meetingId,1,"");
                                    //发送参会通知邮件
                                    mailTemplateMsgService.createMeetingNotice(meetingId);
                                }
                                for (YunmeetingMessageInform yunmeetingMessageInform : yunmeetingMessageInforms) {
                                    if(null!=yunmeetingMessageInform.getInformTime()){
                                        settingWechatInformTimer(yunmeetingMessageInform.getId(), meetingVo1, yunmeetingMessageInform.getInformTime());
                                    }
                                }
                            }
                        } else {
                            if (null != meetingVo) {
                                insertWechatNotice(meetingVo, "2");
                            }
                            //根据会议Id查询会议通知表
                            List<YunmeetingMessageInform> yunmeetingMessageInforms = meetingReserveService.selectMeetingMessageeInform(meetingId);
                            if (null != yunmeetingMessageInforms && yunmeetingMessageInforms.size() > 0) {
                                //增加邮件通知
                                if (yunmeetingMessageInforms.get(0).getInformType().indexOf("2") != -1) {
                                    //发送审核未通过邮件
                                    mailTemplateMsgService.attendMeetingNotice(meetingId,0,reason);
                                }
                            }
                            yunmeetingDynamics.setContent(name + "预定" + fomatDate(yunmeetingConference.getTakeStartDate(), yunmeetingConference.getTakeEndDate()) + "在" + address + "召开的“" + yunmeetingConference.getConferenceName() + "”会议未通过审批，未通过原因：" + reason);
                        }
                        yunmeetingDynamics.setConferenceId(meetingId);
                        yunmeetingDynamics.setParticipantsId(reservationPersonId);
                        meetingDynamicService.insertMeetingDynamic(yunmeetingDynamics, "1",userId);
                    }
                    //Map<String,Object> map = new HashMap<>();
                    //map.put("state",yunmeetingConference.getState());
                    MeetingDetailsVo meetingDetailsVo = selectMeetingDeteilsUtil(meetingId,userId);
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),meetingDetailsVo );
                }
            } else {
                //增加会议操作日志
                sysLogService.createLog(BusinessType.meetingOperationOp.toString(), EventType.audit_meeting.toString(), userName + "审核" + yunmeetingConference.getConferenceName() + "失败", BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), Loglevel.error.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
    }

    /**
     * 判断时间是否在时间段内
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 方法名：auditMeetingUtil</br>
     * 描述：审核未通过会议工具类</br>
     * 参数：yunmeetingConference 会议实体</br>
     * 返回值：</br>
     */
    private void auditMeetingUtil(YunmeetingConference yunmeetingConference){
        String userId = TenantContext.getUserInfo().getUserId();
        String meetingId = yunmeetingConference.getId();
        String reason = "会议室已被预订";
        YunmeetingConferenceAudit yunmeetingConferenceAudit = new YunmeetingConferenceAudit();
        yunmeetingConferenceAudit.setAuditAnnotations(reason);
        yunmeetingConferenceAudit.setBaseConfrerenId(meetingId);
        yunmeetingConferenceAudit.setState("0");
        boolean b2 = meetingReserveService.insertMeetingAuditInfo(yunmeetingConferenceAudit);
        if(b2){
            yunmeetingConference.setState("0");
            boolean b3 = meetingReserveService.updateMeeting(yunmeetingConference);
            if(b3){
                //获取预定人Id
                String reservationPersonId = yunmeetingConference.getReservationPersonId();
                //根据预订人Id查询用户姓名
                SysUser sysUser = userService.selectUserByUserId(reservationPersonId);
                String name = "";
                if (null != sysUser) {
                    name = sysUser.getUserName();
                }
                List<MeetingParticipantsVo> list = new ArrayList<>();
                if (StringUtils.isNotBlank(sysUser.getWechat())) {
                    MeetingParticipantsVo meetingParticipantsVo = new MeetingParticipantsVo();
                    meetingParticipantsVo.setId(reservationPersonId);
                    list.add(meetingParticipantsVo);
                }
                MeetingVo meetingVo = wrapperMeetingVoUtil(list, yunmeetingConference);
                if (null != meetingVo) {
                    insertWechatNotice(meetingVo, "2");
                }
                //根据会议Id查询会议室信息
                List<YuncmMeetingRoom> yuncmMeetingRooms = meetingReserveService.selectMeetingRoomByMeetingId(meetingId);
                if(null!=yuncmMeetingRooms&&yuncmMeetingRooms.size()>0){
                    for(YuncmMeetingRoom yuncmMeetingRoom:yuncmMeetingRooms){
                        YunmeetingDynamics yunmeetingDynamics = new YunmeetingDynamics();
                        yunmeetingDynamics.setContent(name + "预定" + fomatDate(yunmeetingConference.getTakeStartDate(), yunmeetingConference.getTakeEndDate()) + "在" + yuncmMeetingRoom.getName() + "召开的“" + yunmeetingConference.getConferenceName() + "”会议未通过审批，未通过原因：" + reason);
                        yunmeetingDynamics.setConferenceId(meetingId);
                        yunmeetingDynamics.setParticipantsId(reservationPersonId);
                        meetingDynamicService.insertMeetingDynamic(yunmeetingDynamics, "1",userId);
                    }
                }
                //根据会议Id查询会议通知表
                List<YunmeetingMessageInform> yunmeetingMessageInforms = meetingReserveService.selectMeetingMessageeInform(meetingId);
                //增加微信定时提醒
                if (null != yunmeetingMessageInforms && yunmeetingMessageInforms.size() > 0) {
                    //增加邮件通知
                    if(yunmeetingMessageInforms.get(0).getInformType().indexOf("2") != -1){
                        //发送审核未通过邮件
                        mailTemplateMsgService.attendMeetingNotice(meetingId,0,reason);
                    }
                }
            }
        }
    }

    /**
     * 方法名：selectMoreAuditMeeting</br>
     * 描述：查询同一时间段有无其他需审核会议</br>
     * 参数：meetingId 会议Id</br>
     * 参数：roomId 会议室Id
     * 参数：start 开始时间
     * 参数：end 结束时间
     * 返回值：</br>
     */
    @RequestMapping("/selectmoreauditmeeting")
    @ResponseBody
    public Object selectMoreAuditMeeting(String meetingId, String meetingName, String roomId, long start, long end) {
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        String userName = userInfo.getUserName();
        //查询当前用户权限
        List<SysUserRole> userRoleByUserIdAndRoleId = userService.getUserRoleByUserIdAndRoleId(userId, "3");
        if (userRoleByUserIdAndRoleId == null) {
            //增加会议操作日志
            sysLogService.createLog(BusinessType.meetingOperationOp.toString(), EventType.audit_meeting.toString(), userName + "审核" + meetingName + "失败", BusinessExceptionStatusEnum.PermissionDenied.getDescription(), Loglevel.error.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PermissionDenied.getDescription(), BusinessExceptionStatusEnum.PermissionDenied.getCode());
        }
        List<YunmeetingConference> yunmeetingConferences = checkMoreAuditMeeting(meetingId, roomId, start, end);
        if (null != yunmeetingConferences && yunmeetingConferences.size() > 0) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), 1);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), 0);
    }

    /**
     * 方法名：checkMoreAuditMeeting</br>
     * 描述：校验同一时段是否有别的待审核会议</br>
     * 参数：meetingId 会议Id</br>
     * 参数：roomId 会议室Id
     * 参数：start 开始时间
     * 参数：end 结束时间
     * 返回值：</br>
     */
    public List<YunmeetingConference> checkMoreAuditMeeting(String meetingId, String roomId, long start, long end) {
        //查询某时段有无其他需审核会议
        Map<String, Object> map = new HashMap<>();
        map.put("meetingRoomId", roomId);
        if (start > 0 && end > 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startTime = format.format(start);
            String endTime = format.format(end);
            try {
                Date startDateTime = format.parse(startTime);
                Date endDateTime = format.parse(endTime);
                map.put("begin", startDateTime);
                map.put("end", endDateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        List<YunmeetingConference> meetingTakeInfo = meetingReserveService.findMeetingTakeInfo(map);
        List<YunmeetingConference> meetingTakeInfo1 = new ArrayList<>();
        //flag  当前时段是否存在其他待审核会议
        boolean flag = false;
        if (null != meetingTakeInfo) {
            for (YunmeetingConference yunmeetingConference : meetingTakeInfo) {
                String id = yunmeetingConference.getId();
                if (!meetingId.equals(id)) {
                    meetingTakeInfo1.add(yunmeetingConference);
                }
            }
        }
        return meetingTakeInfo1;
    }
    /**
     * 方法名：replyMeeting</br>
     * 描述：会议回复接口</br>
     * 参数：meetingId 会议Id</br>
     * 参数: replyState 会议状态 1接受 2暂定 0拒绝</br>
     * 返回值：</br>
     */
    @RequestMapping("/replymeeting")
    @ResponseBody
    public Object replyMeeting(String meetingId, String replyState) {
        TenantUserVo userInfo = TenantContext.getUserInfo();
        //获取用户Id
        String userId = userInfo.getUserId();
        //获取用户名称
        String userName = userInfo.getUserName();
        if (StringUtils.isNotBlank(meetingId) && StringUtils.isNotBlank(replyState)) {
            boolean b = false;
            //先根据会议Id和用户Id查询有没有回复记录
            YunmeetingParticipantsReply yunmeetingParticipantsReply = meetingReserveService.selectParticipantsReply(meetingId, userId);
            if(yunmeetingParticipantsReply!=null){
                yunmeetingParticipantsReply.setReplyState(replyState);
                yunmeetingParticipantsReply.setReplyTime(new Date());
               b =  meetingReserveService.updateMeetingReply(yunmeetingParticipantsReply);
            }else {
                yunmeetingParticipantsReply = new YunmeetingParticipantsReply();
                yunmeetingParticipantsReply.setParticipantsId(userId);
                yunmeetingParticipantsReply.setConferenceId(meetingId);
                yunmeetingParticipantsReply.setId(CreateUUIdUtil.Uuid());
                yunmeetingParticipantsReply.setReplyState(replyState);
                yunmeetingParticipantsReply.setReplyTime(new Date());
                b = meetingReserveService.insertMeetingReply(yunmeetingParticipantsReply);
            }
            if (b) {
                //增加动态
                String content = "";
                if (replyState.equals("1")) {
                    content = userName + "接受了此次会议！";
                } else if (replyState.equals("2")) {
                    content = userName + "暂定了此次会议！";
                } else if (replyState.equals("0")) {
                    content =  userName + "拒绝了此次会议！";
                }
                Map<String,Object> map = new HashMap<>();
                map.put("meetingId",meetingId);
                map.put("content",content);
                map.put("dynamicType","1");
                map.put("userId",userId);
                meetingDynamicService.insertMeetingDynamicByMeetingId(map);
                //查询会议详情
                MeetingDetailsVo meetingDetailsVo = selectMeetingDeteilsUtil(meetingId,userId);
                if (null != meetingDetailsVo) {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), meetingDetailsVo);
                }
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 方法名：meetingSign</br>
     * 描述：会议签到接口</br>
     * 参数：</br>
     * 返回值：</br>
     */
    @RequestMapping("/meetingsign")
    @ResponseBody
    public Object meetingSign(String meetingId) {
        //获取用户信息
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        String userName = userInfo.getUserName();
        if (StringUtils.isNotBlank(meetingId)) {
            YunmeetingMeetingSign yunmeetingMeetingSign = new YunmeetingMeetingSign();
            yunmeetingMeetingSign.setConfrerenId(meetingId);
            yunmeetingMeetingSign.setId(CreateUUIdUtil.Uuid());
            yunmeetingMeetingSign.setParticipantsId(userId);
            yunmeetingMeetingSign.setSignTime(new Date());
            yunmeetingMeetingSign.setSignSource("1");
            boolean b = meetingReserveService.insertMeetingSign(yunmeetingMeetingSign);
            if (b) {
                //根据会议Id查询会议预订人
                YunmeetingConference yunmeetingConference = meetingReserveService.selectMeetingByMeetingId(meetingId);
                String reservationPersonId = yunmeetingConference.getReservationPersonId();
                //增加动态
                YunmeetingDynamics yunmeetingDynamics = new YunmeetingDynamics();
                yunmeetingDynamics.setConferenceId(meetingId);
                yunmeetingDynamics.setParticipantsId(reservationPersonId);
                yunmeetingDynamics.setContent(userName + "签到成功！");
                meetingDynamicService.insertMeetingDynamic(yunmeetingDynamics, "1",userId);
                //查询会议详情
                MeetingDetailsVo meetingDetailsVo = selectMeetingDeteilsUtil(meetingId,userId);
                if (null != meetingDetailsVo) {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), meetingDetailsVo);
                }
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 方法名：selectRecentMeeting</br>
     * 描述：查询近期会议</br>
     * 参数：basePageEntity分页实体</br>
     * 返回值：</br>
     */
    @RequestMapping("/selectrecentmeeting")
    @ResponseBody
    public Object selectRecentMeeting(BasePageEntity basePageEntity) {
        basePageEntity.setPageSize(30);
        //获取当前用户是否有审核权限
        Map<String,Object> map = getUserAuditRole();
        //查询近期会议
        List<RecentMeetingVo> list = meetingReserveService.selectRecentMeeting(basePageEntity);
        if (null != list && list.size() > 0) {
            map.put("recentMeeting", list);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
        }else{
            YunmeetingConference yunmeetingConference = new YunmeetingConference();
            yunmeetingConference.setDeleteState("0");
            List<YunmeetingConference> yunmeetingConferences = meetingReserveService.selectMeeting(yunmeetingConference);
            if(null!=yunmeetingConferences&&yunmeetingConferences.size()>0){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "近期没有会议安排", map);
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "您还没有会议记录，请从会议预订开始吧", map);
        }
    }

    /**
     * 方法名：addMeetingDynamic</br>
     * 描述：增加会议动态</br>
     * 参数：yunmeetingDynamics 会议动态实体</br>
     * 返回值：</br>
     */
    @RequestMapping("/addmeetingdynamic")
    @ResponseBody
    public Object addMeetingDynamic(YunmeetingDynamics yunmeetingDynamics) {
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        //查询会议详情
        String meetingId = yunmeetingDynamics.getConferenceId();
        yunmeetingDynamics.setParticipantsId(userId);
        String content = yunmeetingDynamics.getContent();
        if(StringUtils.isBlank(content)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription());
        }
        if(content.length()>200){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"用户留言长度受限" );
        }
        boolean b = meetingDynamicService.insertMeetingDynamic(yunmeetingDynamics,"0",userId);
        MeetingDetailsVo meetingDetailsVo = selectMeetingDeteilsUtil(meetingId,userId);
        if (null != meetingDetailsVo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), meetingDetailsVo);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
    }

    public Map<String,Object> getUserAuditRole(){
        //获取当前用户
        TenantUserVo userInfo = TenantContext.getUserInfo();
        //获取用户Id
        String userId = userInfo.getUserId();
        Map<String, Object> map = new HashMap<>();
        //查询当前用户是否有审核权限
        List<SysUserRole> userRoleByUserIdAndRoleId = userService.getUserRoleByUserIdAndRoleId(userId, "3");
        if (null != userRoleByUserIdAndRoleId && userRoleByUserIdAndRoleId.size() > 0) {
            //获取待审核条数
            Integer integer = meetingReserveService.selectAuthstrNum();
            map.put("authstrNum", integer);
            map.put("isAuthstr", "1");
        } else {
            map.put("isAuthstr", "0");
        }
        return map;
    }

    private String fomatDate(Date start, Date end) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        SimpleDateFormat sf1 = new SimpleDateFormat("HH:mm");
        String startTime = sf.format(start);
        String endTime = sf1.format(end);
        return startTime + "~" + endTime;
    }

    //增加微信提醒公共类
    private void insertWechatNotice(MeetingVo meetingVo, String types) {
        //增加微信通知
        if (StringUtils.isNotBlank(types)) {
            if (types.equals("save")) {       //增加会议通知
                wxTemplateMsgService.createMeetingNotice(meetingVo);
            } else if (types.equals("0")) {        //需要管理员审核的通知
                wxTemplateMsgService.attendMeetingNotice(meetingVo, 0);
            } else if (types.equals("update")) {       //会议变更提醒
                wxTemplateMsgService.changeMeetingNotice(meetingVo);
            } else if (types.equals("1")) {        //审核通过的提醒
                wxTemplateMsgService.attendMeetingNotice(meetingVo, 1);
            } else if (types.equals("2")) {        //审核未通过的提醒
                wxTemplateMsgService.attendMeetingNotice(meetingVo, 2);
            } else if (types.equals("cancel")) {
                wxTemplateMsgService.cancelMeetingNotice(meetingVo);
            }
        }
    }

    /**
     * 方法名：wrapperMeetingVoUtil</br>
     * 描述：封装微信通知需要的vo类</br>
     * 参数：wechatUserId  用户人员Id</br>
     * 参数：yunmeetingConference 会议实体<br/>
     * 返回值：</br>
     */
    private MeetingVo wrapperMeetingVoUtil(List<MeetingParticipantsVo> wechatUserId, YunmeetingConference yunmeetingConference) {
        if (null != wechatUserId && wechatUserId.size() > 0) {
            MeetingVo meetingVo = new MeetingVo();
            meetingVo.setStart(yunmeetingConference.getTakeStartDate().getTime());
            meetingVo.setMeetingSubject(yunmeetingConference.getConferenceName());
            meetingVo.setMeetingContent(yunmeetingConference.getConterenceContent());
            meetingVo.setCancelReason(yunmeetingConference.getCancelReason());
            meetingVo.setConferenceId(yunmeetingConference.getId());
            //获取会议Id
            String id = yunmeetingConference.getId();
            //根据会议Id查询会议室地点
            List<YuncmMeetingRoom> yuncmMeetingRooms = meetingReserveService.selectMeetingRoomByMeetingId(id);
            if (null != yuncmMeetingRooms && yuncmMeetingRooms.size() > 0) {
                meetingVo.setMeetingAddress(yuncmMeetingRooms.get(0).getLocation());
            }
            meetingVo.setUserIds(wechatUserId);
            return meetingVo;
        }
        return null;
    }
    //增加微信审核公共类
    private void insertWechatAudit(String userId, YunmeetingConference yunmeetingConference) {
        String isAudit = yunmeetingConference.getIsAudit();
        if (StringUtils.isNotBlank(isAudit)) {
            if (isAudit.equals("1")) {
                //根据用户Id和角色Id查询会议室管理员
                List<SysUser> userRoleByUserIdAndRoleId = userService.selectBoardroomManager(userId);
                if (null != userRoleByUserIdAndRoleId && userRoleByUserIdAndRoleId.size() > 0) {
                    for(SysUser sysUser:userRoleByUserIdAndRoleId){
                        String wechat = sysUser.getWechat();
                        if (StringUtils.isNotBlank(wechat)) {
                            List<MeetingParticipantsVo> wechatUserId = new ArrayList<>();
                            MeetingParticipantsVo meetingParticipantsVo = new MeetingParticipantsVo();
                            meetingParticipantsVo.setId(sysUser.getId());
                            wechatUserId.add(meetingParticipantsVo);
                            MeetingVo meetingVo = wrapperMeetingVoUtil(wechatUserId, yunmeetingConference);
                            if(null!=meetingVo){
                                //增加微信通知
                                insertWechatNotice(meetingVo, "0");
                            }
                        }
                    }
                }
            }
        }
    }
    /*private String queryCurrentUserRole(MeetingDetailsVo meetingDetailsVo){
        //获取当前用户Id
        String userId = TenantContext.getUserInfo().getUserId();
        String role = "0";
        boolean flag =false;
        boolean isAdmin = false;
        List<MeetingParticipantsVo> allAttendees = meetingDetailsVo.getAllAttendees();
        for(MeetingParticipantsVo meetingParticipantsVo:allAttendees){
            String id = meetingParticipantsVo.getId();
            if(id.equals(userId)){
                flag = true;
                break;
            }
        }
        //获取预订人Id
        String reserveId = (String) meetingDetailsVo.getBookeder().get("id");
        //查询当前用户权限
        List<SysUserRole> userRoleByUserIdAndRoleId = userService.getUserRoleByUserIdAndRoleId(userId, "3");
        //增加当前用户角色
        if (null != userRoleByUserIdAndRoleId && userRoleByUserIdAndRoleId.size() > 0) {
            isAdmin = true;
        }
        if(isAdmin){
            role = "1";
        }
        if(flag){
            role = "2";
        }
        if(isAdmin&&flag){
            role = "7";
        }
        if(reserveId.equals(userId)){
            role = "6";
        }
        if(flag&&userId.equals(reserveId)){
            role = "3";
        }
        if(isAdmin&&userId.equals(reserveId)){
            role = "4";
        }
        if(flag&&userId.equals(reserveId)&&isAdmin){
            role = "5";
        }
        return role;
    }*/

    /**
     * 方法名：settingMeetingTimerTask</br>
     * 描述：设置会议定时修改状态工具类</br>
     * 参数：yunmeetingConference 会议实体对象</br>
     * 返回值：</br>
     */
    ExecutorService executor = Executors.newWorkStealingPool();
    private void settingMeetingTimerTask(YunmeetingConference yunmeetingConference) {
        String tenantId = TenantContext.getTenantId();
        String meetingId = yunmeetingConference.getId();
        //根据会议Id获取redis里面的定时Id
        String timerId = RedisUtil.get(tenantId + "_QYH_Timer_Meeting_" + meetingId + "3");
        String timerId1 = RedisUtil.get(tenantId + "_QYH_Timer_Meeting_" + meetingId + "4");
        if (StringUtils.isNotBlank(timerId)&&StringUtils.isNotBlank(timerId1)) {
            timerService.cancelTask(timerId);
            timerService.cancelTask(timerId1);
        }
        executor.execute(() -> {
            //设置定时任务
            MeetingStatusAmend meetingStatusAmend = new MeetingStatusAmend();
            meetingStatusAmend.setMeetingId(meetingId);
            meetingStatusAmend.setTenantId(tenantId);
            meetingStatusAmend.setState("3");
            String startTaskId = timerService.schedule(meetingStatusAmend, yunmeetingConference.getTakeStartDate());
            meetingStatusAmend.setState("4");
            String endTaskId = timerService.schedule(meetingStatusAmend, yunmeetingConference.getTakeEndDate());
            //任务Id存到Redis里面
            if(StringUtils.isNotBlank(startTaskId)){
                RedisUtil.set(tenantId + "_QYH_Timer_Meeting_" + meetingId + "3", startTaskId);
            }
            if(StringUtils.isNotBlank(endTaskId)){
                RedisUtil.set(tenantId + "_QYH_Timer_Meeting_" + meetingId + "4", endTaskId);
            }
        });
    }

    /**
     * 方法名：settingWechatInformTimer</br>
     * 描述：设置微信开始会议定时提醒工具类</br>
     * 参数：informId 会议提醒Id</br>
     * 参数：meetingVo 会议Vo</br>
     * 参数：informTime 定时触发时间</br>
     * 返回值：</br>
     */
    private void settingWechatInformTimer(String informId, MeetingVo meetingVo, Date informTime) {
        String tenantId = TenantContext.getTenantId();
        //根据会议Id获取redis里面的定时Id
        String timerId = RedisUtil.get(tenantId + "_QYH_Timer_Wechat_" + informId);
        if (StringUtils.isNotBlank(timerId)) {
            timerService.cancelTask(timerId);
        }
        WechatInformTimer wechatInformTimer = new WechatInformTimer();
        wechatInformTimer.setMeetingVo(meetingVo);
        wechatInformTimer.setMessageInformId(informId);
        wechatInformTimer.setTenantId(tenantId);
        String taskId = timerService.schedule(wechatInformTimer, informTime);
        if(StringUtils.isNotBlank(taskId)){
            RedisUtil.set(tenantId + "_QYH_Timer_Wechat_" + informId, taskId);
        }
    }

    //查询会议详情工具类
    private MeetingDetailsVo selectMeetingDeteilsUtil(String meetingId,String userId) {
        //查询会议详情
        MeetingDetailsVo meetingDetailsVo = meetingReserveService.selectMeetingDetails(meetingId);
        if (null != meetingDetailsVo) {
            //查询当前人员权限和是否显示签到按钮
            meetingDetailsVo = meetingReserveService.isShowSignButton(meetingDetailsVo,userId);
            return meetingDetailsVo;
        }
        return null;
    }


    /**
     * 获取会议预定参与人员数量
     * @param attendees 会议人员和机构
     * @return
     */
    @RequestMapping("/getParticipantsNumber")
    @ResponseBody
    public Object getParticipantsNumber(String attendees) {

        int num = 0;
        JSONObject json = JSONObject.fromObject(attendees);
        JSONArray jsonArr = json.getJSONArray("attendees");
        Map<String,String> map = new HashMap<>();
        List<String> dep =new ArrayList<String>();
        for(int i=0;i < jsonArr.size();i++){
            JSONObject obj = jsonArr.getJSONObject(i);
             if("0".equals(obj.get("dep").toString())){
                map.put(obj.get("id").toString(),obj.get("id").toString());
             }else{
                 dep.add(obj.get("id").toString());
             }
        }
        if(dep.size()!=0){
            for(int i = 0;i < dep.size(); i++){
                List<SysUser> sysUsers = userService.selectAddressListStructure(dep.get(i).toString());
                for(SysUser er : sysUsers){
                    map.put(er.getId(),er.getId());
                }
            }

            num = map.size();
        }else{
           num = map.size();
        }

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), num);

    }

    /**
     * 方法名：checkMeetingTime</br>
     * 描述：校验会议时间是否在可预订范围内</br>
     * @param tenantId 租户Id</br>
     * @param meetingRoomId 会议室Id</br>
     * @param start 会议开始时间</br>
     * @param end 会议结束时间</br>
     * 返回值：</br>
     */
    public boolean checkMeetingTime(String tenantId,String meetingRoomId,String start,String end){
        start = start.substring(start.indexOf(" ")+1,start.length());
        end = end.substring(end.indexOf(" ")+1,end.length());
        boolean flag = false;
        TenantContext.setTenantId(tenantId);
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        YuncmRoomReserveConf yuncmRoomReserveConf = yuncmMeetingService.selectYuncmRoomReserveConf();
        Date startTime = yuncmRoomReserveConf.getReserveTimeStart();
        Date endTime = yuncmRoomReserveConf.getReserveTimeEnd();
        String formatStartTime = sf.format(startTime);
        String formatEndTime = sf.format(endTime);
        formatStartTime = formatStartTime.substring(formatStartTime.indexOf(" ")+1,formatStartTime.length());
        formatEndTime = formatEndTime.substring(formatEndTime.indexOf(" ")+1,formatEndTime.length());
        try {
            Date parseStartTime = sf.parse(formatStartTime);
            Date parseEndTime = sf.parse(formatEndTime);
            Date parseStartTime1 = sf.parse(start);
            Date parseEndTime1 = sf.parse(end);
            if(parseStartTime1.getTime()>=parseStartTime.getTime()&&parseEndTime1.getTime()<=parseEndTime.getTime()){
                flag = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 变更会议向终端推送指令新建修改
     */
    public void pushMessage(String roomId,String tenantId,YunmeetingConference conference,String meetingRoomId) {
        try {
            if(!"".equals(meetingRoomId)) {
                if (roomId.equals(meetingRoomId)) {
                    pushJudge(roomId, tenantId, conference,"");
                } else {
                    pushJudge(roomId, tenantId, conference,"");
                    pushJudge(meetingRoomId, tenantId, conference,"update");
                }
            }else {
                pushJudge(roomId, tenantId, conference,"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消会议
     * @param tenantId
     * @param conference
     * @param conferences
     * @param roomId
     */
    public void cancelPushMessage(String tenantId,YunmeetingConference conference,List<YunmeetingConference> conferences,String roomId){
        try {
            List<InfoReleaseTerminal> terminals = this.infoReleaseTerminalService.selectInfoReleaseTerminalByMettingRoomId(roomId);
            if (terminals != null) {
                    for (InfoReleaseTerminal terminal : terminals) {
                        //将模板信息存入redis
                        String programId = this.conferenceService.getProgramId(terminal.getId());
                        if (StringUtils.isNotBlank(programId)) {
                            String type = RedisUtil.get(tenantId + "_QYH_play_program_" + programId);
                            if(type == null){
                                type =  ProgramTypeUtil.getProgramType(syncProgramService.getInfoProgramComponentMiddleList(programId));
                                RedisUtil.set(tenantId + "_QYH_play_program_" + programId, type);
                            }
                            String flag = MeetingJudge.cancelJidgeMeetingState(conferences, conference, type);
                            if (!"0".equals(flag)) {
                                pushMessages(terminal.getId(), getFlag(type), tenantId);
                            }
                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 取消推送指令
     * @param flag
     * @param tenantId
     */
    public void pushMessages(String terminalId,String flag, String tenantId){
        try {
            CommandMessage cmd = new CommandMessage();
            List<String> list = new ArrayList<>();
            Map map = new HashMap();
            if("".equals(flag)){
                map.put("type", null);
            }else {
                map.put("type", flag);
            }
            list.add(terminalId);
            cmd.setCmd("10101");
            cmd.setTenantId(tenantId);
            cmd.setTerminals(list);
            cmd.setData(map);
            cmd.setRequestId(CreateUUIdUtil.Uuid());
            cmd.setTimestamp(new Date().getTime());
            publishService.pushCommand(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新建修改推送
     * @param roomId
     * @param tenantId
     * @param conference
     */
    public void pushJudge(String roomId,String tenantId,YunmeetingConference conference,String state){

        try {
            List<InfoReleaseTerminal> terminals = this.infoReleaseTerminalService.selectInfoReleaseTerminalByMettingRoomId(roomId);
            if (terminals != null) {
                List<YunmeetingConference> conferences = this.conferenceService.getNextConference(roomId, new Date());
                    for (InfoReleaseTerminal terminal : terminals) {
                        //将模板信息存入redis
                        String programId = this.conferenceService.getProgramId(terminal.getId());
                        if (StringUtils.isNotBlank(programId)) {
                            String type = RedisUtil.get(tenantId + "_QYH_play_program_" + programId);
                            if(type == null){
                                type =  ProgramTypeUtil.getProgramType(syncProgramService.getInfoProgramComponentMiddleList(programId));
                                RedisUtil.set(tenantId + "_QYH_play_program_" + programId, type);
                            }
                            String flag = MeetingJudge.jidgeMeetingState(conferences, conference, type, getTime());
                            if (!"0".equals(flag)) {
                                pushMessages(terminal.getId(), getFlag(type), tenantId);
                            } else {
                                if (!"".equals(state)) {
                                    pushMessages(terminal.getId(), getFlag(type), tenantId);
                                }
                            }
                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 获取会议提前开始时间
     * @return
     */
    public String getTime(){
        SysSetting setting = this.sysSetingService.findByKey("meetingroom.time");
        String time = "";
        if (setting != null) {
            time = setting.getContent();
        } else {
            time = "0";
        }
        return time;
    }

    private String getFlag(String type){
        String flag = "";
        String str[] = type.split(",");
        for (int i = 0; i < str.length; i++) {
            if(!"C8001".equals(str[i]) && !"C9001".equals(str[i])){
                flag += str[i]+",";
            }
        }
        return flag;
    }

}
