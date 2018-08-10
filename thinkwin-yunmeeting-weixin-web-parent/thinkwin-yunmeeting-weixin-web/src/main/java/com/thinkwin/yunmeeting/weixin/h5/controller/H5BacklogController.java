package com.thinkwin.yunmeeting.weixin.h5.controller;

import com.github.pagehelper.PageInfo;
import com.thinkwin.auth.service.OrganizationService;
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
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.meetingVo.MeetingParticipantsVo;
import com.thinkwin.common.vo.meetingVo.MeetingVo;
import com.thinkwin.common.vo.meetingVo.PersonsVo;
import com.thinkwin.common.vo.mobile.MobileMeetingNum;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.publish.service.PublishService;
import com.thinkwin.schedule.service.TimerService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.*;
import com.thinkwin.yunmeeting.weixin.constant.MeetingConstant;
import com.thinkwin.yunmeeting.weixin.service.WxTemplateMsgService;
import com.thinkwin.yunmeeting.weixin.timer.WechatInformTimer;
import org.apache.commons.collections.map.HashedMap;
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

/*
 * 类说明：待办事项
 * @author lining 2017/10/10
 * @version 1.0
 *
 */
@Controller
@RequestMapping("/wechat/h5Backlog")
public class H5BacklogController {

    private final Logger log = LoggerFactory.getLogger(H5BacklogController.class);

    @Resource
    private UserService userService;
    @Resource
    private MeetingScreeningService meetingScreeningService;
    @Resource
    private MeetingReserveService meetingReserveService;
    @Resource
    private WxTemplateMsgService wxTemplateMsgService;
    @Resource
    private MeetingDynamicService meetingDynamicService;
    @Resource
    private TimerService timerService;
    @Resource
    private SysLogService sysLogService;
    @Resource
    private FileUploadService fileUploadService;
    @Resource
    private OrganizationService organizationService;
    @Resource
    InfoReleaseTerminalService infoReleaseTerminalService;
    @Resource
    ConferenceService conferenceService;
    @Resource
    PublishService publishService;
    @Resource
    SysSetingService  sysSetingService;
    @Resource
    YuncmMeetingService yuncmMeetingService;


    /**
     * 我的待办数量统计
     * @param request
     * @param response
     * @param tenantId
     * @param userId
     * @param openId
     * @return
     */
    @RequestMapping("/myBacklogCount")
    @ResponseBody
    public ResponseResult myBacklogCount(HttpServletRequest request, HttpServletResponse response,  String tenantId,String userId,String openId) {
        ResponseResult responseResult=new ResponseResult();
        boolean success = false;
        if(StringUtils.isNotBlank(tenantId)){
            TenantContext.setTenantId(tenantId);
        }else{
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.ParameterIsNull.getDescription());
            return responseResult;
        }
        try {
              //获取当前用户角色
            List<SysUserRole> SysUserRoles = userService.getCurrentUserRoleIds(userId);
            //判断当前用户时候有权限
            for(SysUserRole role : SysUserRoles){
                if("3".equals(role.getRoleId())){
                    success = true;
                    break;
                }
            }
            if(success){
                Integer num = this.meetingScreeningService.h5MyAuditMeeting();
                responseResult.setIfSuc(1);
                responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());
                responseResult.setData(num);
            }else {
                responseResult.setIfSuc(0);
                responseResult.setMsg(BusinessExceptionStatusEnum.PermissionDenied.getDescription());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
      return responseResult;
    }

    /**
     * 我的待办列表
     * @param request
     * @param response
     * @param tenantId
     * @param userId
     * @param openId
     * @param status 状态 0待办，1已办
     * @param content 搜索关键字
     * @return
     */
    @RequestMapping("/myBacklogList")
    @ResponseBody
    public ResponseResult myBacklogList(HttpServletRequest request, HttpServletResponse response, String tenantId, String userId, String openId, String status, String content,BasePageEntity page) {
        ResponseResult responseResult=new ResponseResult();
        if(StringUtils.isNotBlank(tenantId)){
            TenantContext.setTenantId(tenantId);
        }else{
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.ParameterIsNull.getDescription());
            return responseResult;
        }
        Map<String,Object> map = new HashedMap();
        PageInfo<YunmeetingConference> conferences = new PageInfo<YunmeetingConference>();
        if(!StringUtils.isNotBlank(content)) {
            conferences = this.meetingScreeningService.h5MyAuditMeetingInfo(status, userId,page);
        }else{
            conferences = this.meetingScreeningService.h5AuditSearchYunmeetingConference(status,userId,content,page);
        }
        map.put("list",conferences.getList());
        MobileMeetingNum num = this.meetingScreeningService.selectYunmeetingConferenceNumber();
        map.put("number",num);
        map.put("total",conferences.getPages());
        map.put("currentPage",conferences.getPageNum());
        responseResult.setIfSuc(1);
        responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());
        responseResult.setData(map);
        return responseResult;
    }

    /**
     * 会议代办详情
     * @param meetingId
     * @param userId
     * @return
     */
    @RequestMapping("/setBacklogInfo")
    @ResponseBody
    public ResponseResult setBacklogInfo(HttpServletRequest request, HttpServletResponse response,String meetingId,String userId,String tenantId,String openId) {
        ResponseResult responseResult=new ResponseResult();

        SysUser user=this.check(tenantId,openId,userId);
        if (null == user) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(user.getOpenId()) || StringUtils.isBlank(user.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }
        if(StringUtils.isNotBlank(tenantId)){
            TenantContext.setTenantId(tenantId);
        }else{
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.ParameterIsNull.getDescription());
            return responseResult;
        }
        if(StringUtils.isNotBlank(meetingId)){
            YunmeetingConference conference = this.meetingScreeningService.h5AuditYunmeetingConferenceInfo(meetingId,userId);
            if(null != conference){
                //获取审核人
                if(StringUtils.isNotBlank(conference.getExamineName())){
                    //获取创建者
                    SysUser sysUser = this.userService.selectUserByUserId(conference.getExamineName());
                    if(null != sysUser){
                     /*   Map<String,String> picMap=fileUploadService.selectFileCommon(sysUser.getPhoto());*/
                        Map<String, String> picMap = userService.getUploadInfo(sysUser.getPhoto());
                        if(null != picMap){
                            String xamineNameUrl = picMap.get("big");
                            if(StringUtils.isNotBlank(xamineNameUrl)) {
                                conference.setExamineNameBigPicture(xamineNameUrl);
                            }
                            String primary = picMap.get("primary");
                            if(StringUtils.isNotBlank(primary)){
                                conference.setExamineNameUrl(primary);
                            }
                            String in = picMap.get("in");
                            if(StringUtils.isNotBlank(in)){
                                conference.setExamineNameInPicture(in);
                            }
                            String small = picMap.get("small");
                            if(StringUtils.isNotBlank(small)){
                                conference.setExamineNameSmallPicture(small);
                            }
                        }
                        conference.setExamineName(sysUser.getUserName());
                    }
                }
                //此处判断组织机构是否存在
                SysOrganization sysOrganization = this.organizationService.selectOrganiztionById(conference.getHostUnit());
                conference.setHostUnit(sysOrganization.getOrgName());
            }
            responseResult.setIfSuc(1);
            responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());
            responseResult.setData(conference);
        }else{
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.ParameterIsNull.getDescription());
        }
        return responseResult;
    }
    /**
     * 审批待办事项
     * @param request
     * @param response
     * @param tenantId
     * @param userId
     * @param openId
     * @param meetingId
     * @param auditStatus 0未通过，1通过
     * @param reason  原因
     * @param address 会议室地点
     * @return
     */
    @RequestMapping("/setBacklog")
    @ResponseBody
    public ResponseResult setBacklog(HttpServletRequest request, HttpServletResponse response,  String tenantId,String userId,String openId,String meetingId,String auditStatus,String reason, String address,String roomId) {
        ResponseResult responseResult=new ResponseResult();

        if(StringUtils.isNotBlank(tenantId)){
            TenantContext.setTenantId(tenantId);
        }else{
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.ParameterIsNull.getDescription());
            return responseResult;
        }
        SysUser user = this.check(tenantId, openId, userId);
        if (null == user) {
            responseResult.setIfSuc(-1);
            responseResult.setMsg("用户认证失败");
            return responseResult;
        }else{
            if(StringUtils.isBlank(user.getOpenId()) || StringUtils.isBlank(user.getIsSubscribe())){
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
                return responseResult;
            }
        }
        String userName = user.getUserName();
        //根据会议Id查询会议信息
        YunmeetingConference yunmeetingConference = meetingReserveService.selectMeetingByMeetingId(meetingId);
        if (null != yunmeetingConference) {

            //检查该会议是否已被其它会议室管理员审核
            if(!MeetingConstant.MeetingStatus.AUDIT.equals(yunmeetingConference.getState())){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "会议已经被审核");
            }
            //判断会议室是否正常使用中
            roomId = meetingReserveService.getMeetingIdAndRoomId(meetingId);
            YuncmMeetingRoom room = this.yuncmMeetingService.selectByYuncmMeetingRoom(roomId);
            if(!"2".equals(room.getState())){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "会议室已停用");
            }
            //开始审核会议
            if (StringUtils.isNotBlank(meetingId) && StringUtils.isNotBlank(auditStatus)) {
                YunmeetingConferenceAudit yunmeetingConferenceAudit = new YunmeetingConferenceAudit();
                yunmeetingConferenceAudit.setAuditAnnotations(reason);
                yunmeetingConferenceAudit.setBaseConfrerenId(meetingId);
                //修改会议表审核状态
                  yunmeetingConferenceAudit.setState(auditStatus);
                boolean b = meetingReserveService.insertH5MeetingAuditInfo(yunmeetingConferenceAudit,userId);
                if (b) {
                    //修改会议表审核状态
                    if(auditStatus.equals("1")){
                        yunmeetingConference.setState("2");
                    }else{
                        yunmeetingConference.setState("0");
                    }
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
                        if (auditStatus.equals("1")) {
                            if (null != meetingVo) {
                                insertWechatNotice(meetingVo, "1");
                            }
                            //判断该时段有无其他待审核会议
                            List<YunmeetingConference> yunmeetingConferences = checkMoreAuditMeeting(meetingId, roomId, yunmeetingConference.getTakeStartDate().getTime(), yunmeetingConference.getTakeEndDate().getTime());
                            if(null!=yunmeetingConferences&&yunmeetingConferences.size()>0){
                                for(YunmeetingConference yunmeetingConference1 : yunmeetingConferences){
                                    //处理审核未通过
                                    auditMeetingUtil(yunmeetingConference1,userId);
                                }
                            }
                            //终端推送审核通过指令
                            pushJudge(meetingReserveService.getMeetingIdAndRoomId(meetingId),tenantId,yunmeetingConference,"");
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
                                for (YunmeetingMessageInform yunmeetingMessageInform : yunmeetingMessageInforms) {
                                    settingWechatInformTimer(yunmeetingMessageInform.getId(), meetingVo1, yunmeetingMessageInform.getInformTime());
                                }
                            }
                        } else {
                            if (null != meetingVo) {
                                insertWechatNotice(meetingVo, "2");
                            }
                            yunmeetingDynamics.setContent(name + "预定" + fomatDate(yunmeetingConference.getTakeStartDate(), yunmeetingConference.getTakeEndDate()) + "在" + address + "召开的“" + yunmeetingConference.getConferenceName() + "”会议未通过审批，未通过原因：" + (StringUtils.isNotBlank(reason)?reason:""));
                        }
                        yunmeetingDynamics.setConferenceId(meetingId);
                        yunmeetingDynamics.setParticipantsId(reservationPersonId);
                        meetingDynamicService.insertMeetingDynamic(yunmeetingDynamics, "1",userId);
                    }
                    //增加会议操作日志
                    //sysLogService.createLog(BusinessType.meetingOperationOp.toString(), EventType.audit_meeting.toString(), "【" + userName + "】审核【" + yunmeetingConference.getConferenceName() + "】成功", null, Loglevel.info.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
                }
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
            }
        }
        return responseResult;
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
    public ResponseResult selectMoreAuditMeeting(String meetingId, String meetingName, String roomId, long start, long end,String tenantId,String openId,String userId) {
        ResponseResult responseResult=new ResponseResult();
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
        String userName = sysUser.getUserName();
        if(StringUtils.isNotBlank(tenantId)){
            TenantContext.setTenantId(tenantId);
        }else{
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.ParameterIsNull.getDescription());
            return responseResult;
        }
        //查询当前用户权限
        List<SysUserRole> userRoleByUserIdAndRoleId = userService.getUserRoleByUserIdAndRoleId(userId, "3");
        if (userRoleByUserIdAndRoleId == null) {
            //增加会议操作日志
            sysLogService.createLog(BusinessType.meetingOperationOp.toString(), EventType.audit_meeting.toString(), userName + "审核" + meetingName + "失败", BusinessExceptionStatusEnum.PermissionDenied.getDescription(), Loglevel.info.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PermissionDenied.getDescription(), BusinessExceptionStatusEnum.PermissionDenied.getCode());
        }
        YunmeetingConference conference = meetingReserveService.selectMeetingByMeetingId(meetingId);
        Date date = new Date();
        if(date.getTime() >= conference.getTakeStartDate().getTime()){
            responseResult.setIfSuc(1);
            responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());
            responseResult.setData("2");
        }else {
            List<YunmeetingConference> yunmeetingConferences = checkMoreAuditMeeting(meetingId, roomId, start, end);
            if (null != yunmeetingConferences && yunmeetingConferences.size() > 0) {
                responseResult.setIfSuc(1);
                responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());
                responseResult.setData("1");
            } else {
                responseResult.setIfSuc(1);
                responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());
                responseResult.setData("0");
            }
        }
        return responseResult;
    }














    //权限认证
    public SysUser check(String tenantId,String openId,String userId){
        SysUser user=null;
        //if(StringUtils.isNotBlank(tenantId) && StringUtils.isNotBlank(openId) &&StringUtils.isNotBlank(userId)){
        if(StringUtils.isNotBlank(tenantId) &&StringUtils.isNotBlank(userId)){
            TenantContext.setTenantId(tenantId);
            user=this.userService.authSysUser(null,null,userId);
        }
        return user;
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
        if(informTime != null) {
            String taskId = timerService.schedule(wechatInformTimer, informTime);
            if (StringUtils.isNotBlank(taskId)) {
                RedisUtil.set(tenantId + "_QYH_Timer_Wechat_" + informId, taskId);
            }
        }

    }

    /**
     * 方法名：auditMeetingUtil</br>
     * 描述：审核未通过会议工具类</br>
     * 参数：yunmeetingConference 会议实体</br>
     * 返回值：</br>
     */
    private void auditMeetingUtil(YunmeetingConference yunmeetingConference,String userId){
        String meetingId = yunmeetingConference.getId();
        String reason = "会议室已被预订";
        YunmeetingConferenceAudit yunmeetingConferenceAudit = new YunmeetingConferenceAudit();
        yunmeetingConferenceAudit.setAuditAnnotations(reason);
        yunmeetingConferenceAudit.setBaseConfrerenId(meetingId);
        yunmeetingConferenceAudit.setState("0");
        boolean b2 = meetingReserveService.insertH5MeetingAuditInfo(yunmeetingConferenceAudit,userId);
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
                if (terminals != null) {
                    //将模板信息存入redis
                    String programId = this.conferenceService.getProgramId(terminals.get(0).getId());
                    log.info("---------------------获取节目id："+programId+"---------------------------");
                    if(StringUtils.isNotBlank(programId)) {
                        String type = RedisUtil.get(tenantId + "_QYH_play_program_" + programId);
                        log.info("---------------------获取模板类型："+type+"---------------------------");
                        String flag = MeetingJudge.jidgeMeetingState(conferences, conference, type,getTime());
                        log.info("---------------------判断模板类型为："+flag+"---------------------------");
                        if (!"0".equals(flag)) {
                            log.info("---------------------进行推送类型："+flag+"---------------------------");
                            pushMessages(terminals, getFlag(type), tenantId);
                        }else {
                            if(!"".equals(state)){
                                pushMessages(terminals, getFlag(type), tenantId);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * 取消推送指令
     * @param terminals
     * @param flag
     * @param tenantId
     */
    public void pushMessages(List<InfoReleaseTerminal> terminals,String flag, String tenantId){
        try {
            CommandMessage cmd = new CommandMessage();
            List<String> list = new ArrayList<>();
            Map map = new HashMap();
            if("".equals(flag)){
                map.put("type", null);
            }else {
                map.put("type", flag);
            }
            for (InfoReleaseTerminal terminal : terminals) {
                list.add(terminal.getId());
            }
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

}
