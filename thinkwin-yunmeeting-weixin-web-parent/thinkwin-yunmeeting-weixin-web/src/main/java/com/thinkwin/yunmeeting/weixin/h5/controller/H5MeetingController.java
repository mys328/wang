package com.thinkwin.yunmeeting.weixin.h5.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.publish.CommandMessage;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.MeetingJudge;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.TimeUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.utils.validation.MeetingValidationUtil;
import com.thinkwin.common.vo.meetingVo.*;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.mailsender.service.MailTemplateMsgService;
import com.thinkwin.publish.service.PublishService;
import com.thinkwin.schedule.service.TimerService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.*;
import com.thinkwin.yunmeeting.weixin.constant.MeetingConstant;
import com.thinkwin.yunmeeting.weixin.service.WxOAuth2Service;
import com.thinkwin.yunmeeting.weixin.service.WxTemplateMsgService;
import com.thinkwin.yunmeeting.weixin.service.WxUserService;
import com.thinkwin.yunmeeting.weixin.timer.MeetingStatusAmend;
import com.thinkwin.yunmeeting.weixin.timer.WechatInformTimer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * 类说明：
 * @author lining 2017/8/28
 * @version 1.0
 *
 */
@Controller
@RequestMapping("/wechat/h5Meeting")
public class H5MeetingController {

    private final Logger log = LoggerFactory.getLogger(H5MeetingController.class);


    @Resource
    private WxOAuth2Service wxOAuth2Service;
    @Resource
    private WxUserService wxUserService;
    @Resource
    private LoginRegisterService loginRegisterService;
    @Resource
    private UserService userService;
    @Resource
    private MeetingScreeningService meetingScreeningService;
    @Resource
    private MeetingReserveService meetingReserveService;
    @Resource
    private MeetingDynamicService meetingDynamicService;
    @Resource
    private WxTemplateMsgService wxTemplateMsgService;
    @Resource
    private YuncmMeetingService yuncmMeetingService;
    @Resource
    private YuncmRoomAreaService yuncmRoomAreaService;
    @Resource
    private ConferenceService conferenceService;
    @Resource
    private InfoReleaseTerminalService infoReleaseTerminalService;
    @Resource
    private TimerService timerService;
    @Resource
    private SysLogService sysLogService;
    @Resource
    private MailTemplateMsgService mailTemplateMsgService;
    @Resource
    private PublishService publishService;
    @Resource
    SysSetingService sysSetingService;
    private final static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd ");


    /**
     * @param request
     * @param response
     * @param tenantId
     * @param openId
     * @param userId
     * @param isCreate    0:我参与 1:我创建
     * @param meetingType recent近期，future未来，history历史
     * @return
     */
    @RequestMapping("/meetingList")
    @ResponseBody
    public ResponseResult
    meetingList(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId, String userId, String isCreate, String meetingType, String pageNum) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();
        BasePageEntity page = new BasePageEntity();
        if (StringUtils.isNotBlank(pageNum)) {
            page.setCurrentPage(Integer.parseInt(pageNum));
        }

        SysUser sysUser = this.check(tenantId, openId, userId);
        if (null == sysUser) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(sysUser.getOpenId()) || StringUtils.isBlank(sysUser.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }

        try {
            List<YunmeetingConference> meetingList = new ArrayList<YunmeetingConference>();
            TenantContext.setTenantId(tenantId);
            //我组织
            if ("1".equals(isCreate)) {
                switch (meetingType) {
                    case "recent": //近七天
                    {
                        map = this.meetingScreeningService.h5MyYunmeetingSevenDays(userId, "1");
                    }
                    break;
                    case "future"://未来
                    {
                        map = this.meetingScreeningService.h5MyYunmeetingFuture(page, "1", userId);
                    }
                    break;
                    case "formerly"://历史
                    {
                        map = this.meetingScreeningService.h5MyYunmeetingFormerly(page, "1", userId);
                    }
                    break;
                    default:
                        break;
                }
            } else { //我参与
                switch (meetingType) {
                    case "recent": //近七天
                    {
                        map = this.meetingScreeningService.h5MyYunmeetingSevenDays(userId, "2");
                    }
                    break;
                    case "future"://未来
                    {
                        map = this.meetingScreeningService.h5MyYunmeetingFuture(page, "2", userId);
                    }
                    break;
                    case "formerly"://历史
                    {
                        map = this.meetingScreeningService.h5MyYunmeetingFormerly(page, "2", userId);
                    }
                    break;
                    default:
                        break;
                }

            }
            responseResult.setIfSuc(1);
            responseResult.setData(map);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 统计会议列表各分类数量
     *
     * @param request
     * @param response
     * @param tenantId
     * @param openId
     * @param userId
     * @param isCreate
     * @param meetingType
     * @return
     */
    @RequestMapping("/meetingListCount")
    @ResponseBody
    public ResponseResult meetingListCount(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId, String userId, String isCreate, String meetingType) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();
        TenantContext.setTenantId(tenantId);

        Map<String, Object> m1 = new HashMap<>();
        Map<String, Object> m2 = new HashMap<>();
        if (StringUtils.isNotBlank(isCreate)) {
            Integer num = this.meetingScreeningService.h5MyYunMeetingKindCount(userId, isCreate, meetingType);
            m1.put(meetingType, num);
            if ("1".equals(isCreate)) {
                map.put("create", m1);
            } else {
                map.put("about", m1);
            }

        } else {
            Integer num11 = this.meetingScreeningService.h5MyYunMeetingKindCount(userId, "1", "recent");
            Integer num12 = this.meetingScreeningService.h5MyYunMeetingKindCount(userId, "1", "future");
            Integer num13 = this.meetingScreeningService.h5MyYunMeetingKindCount(userId, "1", "formerly");
            Integer num01 = this.meetingScreeningService.h5MyYunMeetingKindCount(userId, "0", "recent");
            Integer num02 = this.meetingScreeningService.h5MyYunMeetingKindCount(userId, "0", "future");
            Integer num03 = this.meetingScreeningService.h5MyYunMeetingKindCount(userId, "0", "formerly");

            m1.put("recent", num11);
            m1.put("future", num12);
            m1.put("formerly", num13);
            map.put("create", m1);

            m2.put("recent", num01);
            m2.put("future", num02);
            m2.put("formerly", num03);
            map.put("about", m2);


        }
        responseResult.setIfSuc(1);
        responseResult.setData(map);
        return responseResult;
    }

    /**
     * 会议搜索
     *
     * @param request
     * @param response
     */
    @RequestMapping("/searchMeeting")
    @ResponseBody
    public ResponseResult searchMeeting(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId, String userId, String content) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();

        SysUser sysUser = this.check(tenantId, openId, userId);
        if (null == sysUser) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(sysUser.getOpenId()) || StringUtils.isBlank(sysUser.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }
        //切换数据源
        TenantContext.setTenantId(tenantId);
        try {
            List<YunmeetingConference> list = this.meetingReserveService.findByMyMeeting(userId, content, null, null, null);
            map.put("meetings", list);
            responseResult.setIfSuc(1);
            responseResult.setData(map);
            responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 会议详情
     *
     * @param request
     * @param response
     */
    @RequestMapping("/meetingInfo")
    @ResponseBody
    public ResponseResult meetingInfo(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId, String userId, String meetingId) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();

        SysUser sysUser = this.check(tenantId, openId, userId);
        if (null == sysUser) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(sysUser.getOpenId()) || StringUtils.isBlank(sysUser.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }
        //切换数据源
        TenantContext.setTenantId(tenantId);
        TenantUserVo tenantUserVo = new TenantUserVo();
        tenantUserVo.setUserId(userId);
        tenantUserVo.setTenantId(tenantId);
        TenantContext.setUserInfo(tenantUserVo);

        try {
            //查询会议详情
            MeetingDetailsVo meetingDetailsVo = selectMeetingDeteilsUtil(meetingId, userId);
            if(null!=meetingDetailsVo){
                map.put("meeting", meetingDetailsVo);
                responseResult.setIfSuc(1);
                responseResult.setData(map);
            }else{
                responseResult.setIfSuc(0);
                responseResult.setMsg("会议不存在");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 保存会议
     *
     * @param request
     * @param response
     */
    @RequestMapping("/saveMeeting")
    @ResponseBody
    public ResponseResult saveMeeting(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId, String userId, String data) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> maps = new HashMap<>();
        MeetingDetailsVo meetingDetailsVo = JSON.parseObject(data, new TypeReference<MeetingDetailsVo>() {
        });

        SysUser sysUser = this.check(tenantId, openId, userId);
        if (null == sysUser) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(sysUser.getOpenId()) || StringUtils.isBlank(sysUser.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }
        //切换数据源
        TenantContext.setTenantId(tenantId);

        try {
            String userName = sysUser.getUserName();
            String contents = "";
            String eventTypes = "";
            String roomId = "";
            //校验前台传参以及给实体赋值
            Map<String, Object> map = MeetingValidationUtil.addMeetingValidation(meetingDetailsVo, userId);
            if (null == map) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
            }
            //检查会议开始时间大于当前时间
            Long nowDate=System.currentTimeMillis();
            if(nowDate>meetingDetailsVo.getStart()){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, MeetingConstant.MeetingDateMsgInfo.GREATER_BEGIN_TIME);
            }
            //检查会议开始时间大于结束时间
            if(meetingDetailsVo.getEnd()<=meetingDetailsVo.getStart()){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, MeetingConstant.MeetingDateMsgInfo.GREATER_BEGIN_TIME);
            }
            //校验预定时间是否在可预订范围内
            SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String start=format.format(meetingDetailsVo.getStart());
            String end=format.format(meetingDetailsVo.getEnd());
            if(!checkMeetingTime(tenantId,meetingDetailsVo.getResourceId(),start,end)){
                responseResult.setMsg("不在可预订时间范围内");
                responseResult.setIfSuc(0);
                return responseResult;
            }
            //获取会议最大最小时长
            YuncmRoomReserveConf conf = yuncmMeetingService.selectYuncmRoomReserveConf();
            if(conf.getMeetingMaximum() != 0){
                if(TimeUtil.timeMillisecond(conf.getMeetingMaximum(),"hour") < meetingDetailsVo.getEnd() - meetingDetailsVo.getStart() ){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "会议时长不能大于"+conf.getMeetingMaximum()+"小时");
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
            //获取原会议室id
            String meetingRoomId = meetingReserveService.getMeetingIdAndRoomId(conferenceId);
            //在变更之前查询会议
            YunmeetingConference meeting = new YunmeetingConference();
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
                Map<String,Object> map1 = new HashMap<>();
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
                if (meetingRoomTakeInfo) {
                    //增加会议操作日志
                    sysLogService.createLog(BusinessType.meetingOperationOp.toString(),eventTypes,contents,BusinessExceptionStatusEnum.MeetingRoomBeOccupied.getDescription(), Loglevel.error.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.MeetingRoomBeOccupied.getDescription(), BusinessExceptionStatusEnum.MeetingRoomBeOccupied.getCode());
                }
                //查询用户是否有预定权限
                Boolean flag = selectUserMeetingRoomRole(roomId,userId);
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
                //终端推送消息
                log.info("----------终端推送------------");
                pushMessage(roomId,tenantId,yunmeetingConference,meetingRoomId);
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
                    SysUser sysUser1 = userService.selectUserByUserId(organizerId);
                    meetingParticipantsVo.setName(sysUser1.getUserName());
                    userIds.add(meetingParticipantsVo);
                }
                if (state.equals("save")) {
                    b = meetingReserveService.insertMeetingPeople(userIds, conferenceId, userId);
                    if (b) {
                        //增加动态信息
                        map1.put("meetingId", yunmeetingConference.getId());
                        map1.put("content", sysUser.getUserName() + "创建了" + yunmeetingConference.getConferenceName() + "会议。");
                        map1.put("userId",userId);
                        List<MeetingParticipantsVo> wechatUserId = meetingDynamicService.insertMeetingDynamicByMeetingId(map1);
                        MeetingVo meetingVo = wrapperMeetingVoUtil(wechatUserId, yunmeetingConference);
                        if(null!=meetingVo) {
                            if (yunmeetingConference.getIsAudit().equals("0")) {
                                //增加微信通知
                                insertWechatNotice(meetingVo, "save");
                                //增加微信定时提醒
                                if (null != list && list.size() > 0) {
                                    if (yunmeetingMessageInform.getInformType().indexOf("1") != -1) {
                                        int i = 0;
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
                }else {
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
                    settingMeetingTimerTask(yunmeetingConference);
                    maps.put("meetingId",yunmeetingConference.getId());
                    responseResult.setData(maps);
                    responseResult.setIfSuc(1);
                    responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());
                    //增加会议操作日志
                    //sysLogService.createLog(BusinessType.meetingOperationOp.toString(), eventTypes,contents,null,Loglevel.info.toString());
                    return responseResult;
                }
            }

            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.OperateDBError.getDescription());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }


    /**
     * 添加会议描述
     *
     * @param request
     * @param response
     */
    @RequestMapping("/addDescribe")
    @ResponseBody
    public ResponseResult addDescribe(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId, String userId, String content, String meetingId) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();

        SysUser sysUser = this.check(tenantId, openId, userId);
        if (null == sysUser) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(sysUser.getOpenId()) || StringUtils.isBlank(sysUser.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }
        //切换数据源
        TenantContext.setTenantId(tenantId);

        try {
            YunmeetingConference conference = this.meetingReserveService.selectMeetingByMeetingId(meetingId);
            //if (null!=conference && StringUtils.isNotBlank(content)) {
                conference.setConterenceContent(content);
                this.meetingReserveService.updateMeeting(conference);
            //}

            responseResult.setIfSuc(1);
            responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 会议响应（回复）
     *
     * @param request
     * @param response
     * @param replyState 0谢绝，1接受，2暂定
     */
    @RequestMapping("/replyMeeting")
    @ResponseBody
    public ResponseResult replyMeeting(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId, String userId, String meetingId, String replyState) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();

        SysUser sysUser = this.check(tenantId, openId, userId);
        if (null == sysUser) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(sysUser.getOpenId()) || StringUtils.isBlank(sysUser.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }
        //切换数据源
        TenantContext.setTenantId(tenantId);

        try {
            TenantUserVo userInfo = TenantContext.getUserInfo();
            //获取用户Id
            //String userId = userInfo.getUserId();
            //获取用户名称
            String userName = sysUser.getUserName();
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
                        content = userName + "拒绝了此次会议！";
                    }
                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("meetingId",meetingId);
                    map1.put("content",content);
                    map1.put("dynamicType","1");
                    map1.put("userId",userId);
                    meetingDynamicService.insertMeetingDynamicByMeetingId(map1);

                    responseResult.setIfSuc(1);
                    responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());
                    return responseResult;
                } else {
                    responseResult.setIfSuc(0);
                    responseResult.setMsg(BusinessExceptionStatusEnum.Failure.getDescription());
                }
            }
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.Failure.getDescription());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 会议变更
     *
     * @param request
     * @param response
     */
    @RequestMapping("/changeMeeting")
    @ResponseBody
    public ResponseResult changeMeeting(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId, String userId, String meetingId) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();

        SysUser sysUser = this.check(tenantId, openId, userId);
        if (null == sysUser) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(sysUser.getOpenId()) || StringUtils.isBlank(sysUser.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }
        //切换数据源
        TenantContext.setTenantId(tenantId);

        try {

            responseResult.setIfSuc(1);
            responseResult.setData(map);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 会议取消
     *
     * @param request
     * @param response
     */
    @RequestMapping("/cancelMeeting")
    @ResponseBody
    public ResponseResult cancelMeeting(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId, String userId, String meetingId) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();

        SysUser sysUser = this.check(tenantId, openId, userId);
        if (null == sysUser) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(sysUser.getOpenId()) || StringUtils.isBlank(sysUser.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }
        //切换数据源
        TenantContext.setTenantId(tenantId);

        try {
            String userName = sysUser.getUserName();
            //根据会议Id查询会议信息
            YunmeetingConference yunmeetingConference = meetingReserveService.selectMeetingByMeetingId(meetingId);
            if (null != yunmeetingConference) {
                String roomId = meetingReserveService.getMeetingIdAndRoomId(meetingId);
                List<YunmeetingConference> conferences = this.conferenceService.getNextConference(roomId, new Date());
                boolean isDelete = false;
                if(yunmeetingConference.getState().equals("5") || yunmeetingConference.getState().equals("0")){
                    yunmeetingConference.setDeleteState("1");
                    isDelete = true;
                }else {
                    yunmeetingConference.setState("5");
                }
                boolean b = meetingReserveService.updateMeeting(yunmeetingConference);
                if (b) {
                    if (!isDelete) {
                        //根据会议Id获取redis里面的定时Id
                        String timerId1 = RedisUtil.get(tenantId + "_QYH_Timer_Meeting_wechat_" + meetingId +"3");
                        String timerId2 = RedisUtil.get(tenantId + "_QYH_Timer_Meeting_wechat_" + meetingId +"4");
                        //取消定时器
                        if (StringUtils.isNotBlank(timerId1)&&StringUtils.isNotBlank(timerId2)) {
                            timerService.cancelTask(timerId1);
                            timerService.cancelTask(timerId2);
                        }
                        //删除redis里面的定时器
                        RedisUtil.remove(tenantId + "_QYH_Timer_Meeting_wechat_" + meetingId +"3");
                        RedisUtil.remove(tenantId + "_QYH_Timer_Meeting_wechat_" + meetingId +"4");
                        //增加动态
                        Map<String, Object> tempMap = new HashMap<>();
                        tempMap.put("meetingId", yunmeetingConference.getId());
                        String content =  sysUser.getUserName() + "取消了会议，请知晓。";
                        tempMap.put("content", content);
                        tempMap.put("userId",userId);
                        List<MeetingParticipantsVo> userIds = meetingDynamicService.insertMeetingDynamicByMeetingId(tempMap);
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
                    //向终端推送信息
                    cancelPushMessage(tenantId,yunmeetingConference,conferences,roomId);
                    responseResult.setIfSuc(1);
                    responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());
                }
            } else {
                responseResult.setIfSuc(0);
                responseResult.setMsg(BusinessExceptionStatusEnum.Failure.getDescription());
            }

        } catch (Exception e) {
            e.printStackTrace();
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.Failure.getDescription());
        }
        return responseResult;
    }

    /**
     * 会议签到
     *
     * @param request
     * @param response
     */
    @RequestMapping("/signMeeting")
    @ResponseBody
    public ResponseResult signMeeting(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId, String userId, String meetingId) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();

        SysUser sysUser = this.check(tenantId, openId, userId);
        if (null == sysUser) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(sysUser.getOpenId()) || StringUtils.isBlank(sysUser.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }
        //切换数据源
        TenantContext.setTenantId(tenantId);

        try {

            String userName = sysUser.getUserName();
            if (StringUtils.isNotBlank(meetingId)) {
                YunmeetingMeetingSign yunmeetingMeetingSign = new YunmeetingMeetingSign();
                yunmeetingMeetingSign.setConfrerenId(meetingId);
                yunmeetingMeetingSign.setId(CreateUUIdUtil.Uuid());
                yunmeetingMeetingSign.setParticipantsId(userId);
                yunmeetingMeetingSign.setSignTime(new Date());
                yunmeetingMeetingSign.setSignSource("2");
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

                    responseResult.setIfSuc(1);
                    responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());
                }
            } else {
                responseResult.setIfSuc(0);
                responseResult.setMsg(BusinessExceptionStatusEnum.Failure.getDescription());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 会议审核
     *
     * @param request
     * @param response
     */
    @RequestMapping("/auditMeeting")
    @ResponseBody
    public ResponseResult auditMeeting(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId, String userId, String meetingId, String auditState, String reason,String address) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();

        SysUser sysUser = this.check(tenantId, openId, userId);
        if (null == sysUser) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(sysUser.getOpenId()) || StringUtils.isBlank(sysUser.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }
        //切换数据源
        TenantContext.setTenantId(tenantId);

        try {

            //根据会议Id查询会议信息
            YunmeetingConference yunmeetingConference = meetingReserveService.selectMeetingByMeetingId(meetingId);
            if (null != yunmeetingConference) {
                //开始审核会议
                if (StringUtils.isNotBlank(meetingId) && StringUtils.isNotBlank(auditState)) {
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
                            SysUser user = userService.selectUserByUserId(reservationPersonId);
                            String name = "";
                            if (null != user) {
                                name = user.getUserName();
                            }
                            YunmeetingDynamics yunmeetingDynamics = new YunmeetingDynamics();
                            List<MeetingParticipantsVo> list = new ArrayList<>();
                            if (StringUtils.isNotBlank(user.getWechat())) {
                                MeetingParticipantsVo meetingParticipantsVo = new MeetingParticipantsVo();
                                meetingParticipantsVo.setId(reservationPersonId);
                                list.add(meetingParticipantsVo);
                            }
                            MeetingVo meetingVo = wrapperMeetingVoUtil(list, yunmeetingConference);
                            if (auditState.equals("2")) {
                                if (null != meetingVo) {
                                    insertWechatNotice(meetingVo, "1");
                                }
                                //根据会议Id查询会议室Id
                                List<YuncmMeetingRoom> yuncmMeetingRooms = meetingReserveService.selectMeetingRoomByMeetingId(meetingId);
                                String roomId = "";
                                if(null!=yuncmMeetingRooms&&yuncmMeetingRooms.size()>0){
                                    roomId = yuncmMeetingRooms.get(0).getId();
                                }
                                //判断该时段有无其他待审核会议
                                List<YunmeetingConference> yunmeetingConferences = checkMoreAuditMeeting(meetingId, roomId, yunmeetingConference.getTakeStartDate().getTime(), yunmeetingConference.getTakeEndDate().getTime());
                                if(null!=yunmeetingConferences&&yunmeetingConferences.size()>0){
                                    for(YunmeetingConference yunmeetingConference1 : yunmeetingConferences){
                                        //处理审核未通过
                                        auditMeetingUtil(yunmeetingConference1);
                                    }
                                }
                                yunmeetingDynamics.setContent(name + "预定" + fomatDate(yunmeetingConference.getTakeStartDate(), yunmeetingConference.getTakeEndDate()) + "在" + address + "召开的“" + yunmeetingConference.getConferenceName() + "”会议通过审批。");
                                //终端推送审核通过指令
                                pushJudge(meetingReserveService.getMeetingIdAndRoomId(meetingId),tenantId,yunmeetingConference,"");
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
                        responseResult.setIfSuc(1);
                        responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());
                        return responseResult;
                    }
                } else {
                    responseResult.setMsg(BusinessExceptionStatusEnum.ParameterIsNull.getDescription());
                    responseResult.setCode(BusinessExceptionStatusEnum.ParameterIsNull.getCode());
                }
            }
            responseResult.setMsg(BusinessExceptionStatusEnum.Failure.getDescription());
            responseResult.setCode(BusinessExceptionStatusEnum.Failure.getCode());
            responseResult.setIfSuc(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 添加会议动态
     *
     * @param request
     * @param response
     */
    @RequestMapping("/addMeetingDynamic")
    @ResponseBody
    public ResponseResult addMeetingDynamic(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId, String userId, String meetingId, String content) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();

        SysUser sysUser = this.check(tenantId, openId, userId);
        if (null == sysUser) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(sysUser.getOpenId()) || StringUtils.isBlank(sysUser.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }
        //切换数据源
        TenantContext.setTenantId(tenantId);

        try {
            if (StringUtils.isNotBlank(content)) {
                YunmeetingDynamics yunmeetingDynamics = new YunmeetingDynamics();
                yunmeetingDynamics.setContent(content);
                yunmeetingDynamics.setParticipantsId(userId);
                yunmeetingDynamics.setConferenceId(meetingId);
                boolean b = meetingDynamicService.insertMeetingDynamic(yunmeetingDynamics, "0",userId);
                if (b) {
                    responseResult.setIfSuc(1);
                    responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 参会回复统计
     *
     * @param request
     * @param response
     * @param tenantId
     * @param openId
     * @param userId
     * @param meetingId
     * @param type                类型0全部，1接受，2暂定，3谢绝，4未回复
     * @param content             搜索内容
     * @param reservationPersonId 预订人Id
     * @return
     */
    @RequestMapping("/participantStatistics")
    @ResponseBody
    public ResponseResult participantStatistics(HttpServletRequest request, HttpServletResponse response, String tenantId,
                                                String openId, String userId, String meetingId, String type, String content,
                                                String reservationPersonId) {
        ResponseResult responseResult = new ResponseResult();

        SysUser sysUser = this.check(tenantId, openId, userId);
        if (null == sysUser) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(sysUser.getOpenId()) || StringUtils.isBlank(sysUser.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }
        //切换数据源
        TenantContext.setTenantId(tenantId);

        try {
            Map<String,Object> meetingReplay = meetingReserveService.findMeetingReplay(type, content, meetingId, reservationPersonId);
            //计算参会人员回复
            meetingReplay.put("nums",meetingReserveService.countMeetingReplyNum(meetingId, reservationPersonId));
            responseResult.setIfSuc(1);
            responseResult.setData(meetingReplay);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 签到统计
     *
     * @param request
     * @param response
     * @param tenantId
     * @param openId
     * @param userId
     * @param meetingId
     * @param type      0全部，1已签到，2未签到
     * @param content   搜索
     * @return
     */
    @RequestMapping("/signStatistics")
    @ResponseBody
    public ResponseResult signStatistics(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId, String userId, String meetingId, String type, String content) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();

        SysUser sysUser = this.check(tenantId, openId, userId);
        if (null == sysUser) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(sysUser.getOpenId()) || StringUtils.isBlank(sysUser.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }
        //切换数据源
        TenantContext.setTenantId(tenantId);
        try {
            List<MeetingSignVo> meetingSign = meetingReserveService.findMeetingSign(type, meetingId, content);
            List<Integer> integers = meetingReserveService.countMeetingSignNum(meetingId);
            map.put("meetingSign",meetingSign);
            map.put("nums",integers);
            responseResult.setIfSuc(1);
            responseResult.setData(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    //查询会议详情工具类
    private MeetingDetailsVo selectMeetingDeteilsUtil(String meetingId, String userId) {
        //查询会议详情
        MeetingDetailsVo meetingDetailsVo = meetingReserveService.selectMeetingDetails(meetingId);
        if (null != meetingDetailsVo) {
            //查询当前人员权限和是否显示签到按钮
            meetingDetailsVo = meetingReserveService.isShowSignButton(meetingDetailsVo,userId);
            int[] count = new int[4];

            for (MeetingParticipantsVo mpv : meetingDetailsVo.getAllAttendees()) {
                if (!"3".equals(mpv.getStatu())) {
                    count[0] = count[0] + 1;
                }
                if (mpv.getSignTime() != null) {
                    count[1] = count[1] + 1;

                    //当前人员是否签到
                    if(mpv.getId().equals(userId)){
                        meetingDetailsVo.setSignStatus("1");
                    }

                }
                if (userId.equals(mpv.getId())) {
                    meetingDetailsVo.setReplyStatus(mpv.getStatu());
                }
            }
            meetingDetailsVo.setReplyInfo("已回复" + count[0] + "，共" + meetingDetailsVo.getAllAttendees().size() + "人");
            count[2] = meetingDetailsVo.getAllAttendees().size() - count[1];
            meetingDetailsVo.setSignInfo("已签到" + count[1] + "，未签到" + count[2] + "人");
            return meetingDetailsVo;
        }
        return null;
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


    //权限认证
    public SysUser check(String tenantId, String openId, String userId) {
        SysUser user = null;
//        if(StringUtils.isBlank(tenantId) && StringUtils.isBlank(openId) &&StringUtils.isBlank(userId)){
        if (StringUtils.isNotBlank(tenantId) && StringUtils.isNotBlank(userId)) {
            TenantContext.setTenantId(tenantId);
            user = this.userService.authSysUser(null, openId, userId);
        }
        return user;
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
                        //查询用户是否绑定微信
                        String wechat = sysUser.getWechat();
                        if (StringUtils.isNotBlank(wechat)) {
                            List<MeetingParticipantsVo> wechatUserId = new ArrayList<>();
                            MeetingParticipantsVo meetingParticipantsVo = new MeetingParticipantsVo();
                            meetingParticipantsVo.setId(userId);
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

    private String fomatDate(Date start, Date end) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        SimpleDateFormat sf1 = new SimpleDateFormat("HH:mm");
        String startTime = sf.format(start);
        String endTime = sf1.format(end);
        return startTime + "~" + endTime;
    }

    /**
     * 方法名：</br>
     * 描述：查询当前用户是否能创建会议</br>
     * 参数：roomId</br>
     * 返回值：</br>
     */
    public Boolean selectUserMeetingRoomRole(String roomId,String userId) {
        //获取用户Id
        //String userId = TenantContext.getUserInfo().getUserId();
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
        String timerId = RedisUtil.get(tenantId + "_QYH_Timer_Meeting_wechat_" + meetingId + yunmeetingConference.getState());
        if (StringUtils.isNotBlank(timerId)) {
            timerService.cancelTask(timerId);
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
        String timerId = RedisUtil.get(tenantId + "_QYH_Timer_Wechat_wechat_" + informId);
        if (StringUtils.isNotBlank(timerId)) {
            timerService.cancelTask(timerId);
        }
        WechatInformTimer wechatInformTimer = new WechatInformTimer();
        wechatInformTimer.setMeetingVo(meetingVo);
        wechatInformTimer.setMessageInformId(informId);
        wechatInformTimer.setTenantId(tenantId);
        String taskId = timerService.schedule(wechatInformTimer, informTime);
        if(StringUtils.isNotBlank(taskId)) {
            RedisUtil.set(tenantId + "_QYH_Timer_Wechat_wechat_" + informId, taskId);
        }
    }
    //查询会议详情工具类
    /*private MeetingDetailsVo selectMeetingDeteilsUtil(String meetingId) {
        //查询会议详情
        MeetingDetailsVo meetingDetailsVo = meetingReserveService.selectMeetingDetails(meetingId);
        if (null != meetingDetailsVo) {
            //查询当前人员权限和是否显示签到按钮
            meetingDetailsVo = meetingReserveService.isShowSignButton(meetingDetailsVo, userId);
            return meetingDetailsVo;
        }
        return null;
    }*/

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
            log.info("---------------------进入推送方法---------------------------");
            log.info("---------------原会议室id："+roomId+"-----------------更改的会议室id："+meetingRoomId+"----------------------------");
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
            log.error("=====================异常："+e.getMessage()+"==========================");
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
                            String flag = MeetingJudge.cancelJidgeMeetingState(conferences, conference, type);
                            if (!"0".equals(flag)) {
                                pushMessages(terminal.getId(), getFlag(type), tenantId);
                            }
                     }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("=====================异常："+e.getMessage()+"==========================");
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
            cmd.setTimestamp(System.currentTimeMillis());
            log.info("---------------------推送成功："+cmd+"---------------------------");
            publishService.pushCommand(cmd);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("=====================异常："+e.getMessage()+"==========================");
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
            log.info("---------------------获取终端信息---------------------------");
            List<InfoReleaseTerminal> terminals = this.infoReleaseTerminalService.selectInfoReleaseTerminalByMettingRoomId(roomId);
            log.info("---------------------终端信息："+terminals+"---------------------------");
            if (terminals != null) {
                List<YunmeetingConference> conferences = this.conferenceService.getNextConference(roomId, new Date());
                log.info("---------------------获取会议信息："+conferences+"---------------------------");
                for (InfoReleaseTerminal terminal : terminals) {
                    //将模板信息存入redis
                    String programId = this.conferenceService.getProgramId(terminal.getId());
                    log.info("---------------------获取节目id："+programId+"---------------------------");
                    if(StringUtils.isNotBlank(programId)) {
                        String type = RedisUtil.get(tenantId + "_QYH_play_program_" + programId);
                        log.info("---------------------获取模板类型："+type+"---------------------------");
                        String flag = MeetingJudge.jidgeMeetingState(conferences, conference, type,getTime());
                        log.info("---------------------判断模板类型为："+flag+"---------------------------");
                        if (!"0".equals(flag)) {
                            log.info("---------------------进行推送类型："+flag+"---------------------------");
                            pushMessages(terminal.getId(), getFlag(type), tenantId);
                        }else {
                            if(!"".equals(state)){
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
