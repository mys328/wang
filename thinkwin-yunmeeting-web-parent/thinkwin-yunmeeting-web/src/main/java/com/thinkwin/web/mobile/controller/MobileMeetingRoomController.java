package com.thinkwin.web.mobile.controller;

import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.core.SaasUserWeb;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.DateTypeFormat;
import com.thinkwin.common.utils.PropertyUtil;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.utils.validation.MeetingValidationUtil;
import com.thinkwin.common.vo.meetingVo.MeetingDetailsVo;
import com.thinkwin.schedule.service.TimerService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.web.timer.MeetingStatusAmend;
import com.thinkwin.yuncm.service.MeetingDynamicService;
import com.thinkwin.yuncm.service.MeetingReserveService;
import com.thinkwin.yuncm.service.YuncmMeetingService;
import com.thinkwin.yunmeeting.weixin.service.WxUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * 类说明：
 * @author lining 2017/8/11
 * @version 1.0
 *
 */
@Controller
@RequestMapping("/yunMeeting/scanMeeting")
public class MobileMeetingRoomController {

    private final Logger log = LoggerFactory.getLogger(MobileMeetingRoomController.class);

    @Resource
    private WxUserService wxUserService;
    @Resource
    private LoginRegisterService loginRegisterService;
    @Resource
    private MeetingReserveService meetingReserveService;
    @Resource
    private YuncmMeetingService yuncmMeetingService;
    @Resource
    private MeetingDynamicService meetingDynamicService;
    @Resource
    private UserService userService;

    /**
     * 预订会议页面
     * @param tenantId
     * @param meetingRoomId
     * @return
     */
    @RequestMapping("/reserveMeetingPage")
    @ResponseBody
    public ModelAndView reserveMeetingPage(String tenantId, String meetingRoomId){
        ModelAndView mv=new ModelAndView();
        YuncmMeetingRoom meetingRoom=new YuncmMeetingRoom();
        TenantContext.setTenantId(tenantId);
        meetingRoom=this.yuncmMeetingService.selectByidYuncmMeetingRoom(meetingRoomId);
        mv.addObject("tenantId",tenantId);
        mv.addObject("meetingRoomId",meetingRoomId);
        mv.addObject("meetingRoomName",meetingRoom.getName());

        mv.setViewName("scanMeeting/reserveMeeting");
        return  mv;

    }

    /**
     * 预订会议页面
     * @param tenantId
     * @param meetingRoomId
     * @return
     */
    @RequestMapping("/reserveMeeting")
    @ResponseBody
    public ResponseResult reserveMeeting(String tenantId, String meetingRoomId, String userId,String title,String start,String end){
        String wechatSerer= PropertyUtil.getWechatServer();

        ResponseResult responseResult=new ResponseResult();
        Map<String,Object> maps= new HashMap<>();
        maps.put("tenantId",tenantId);
        maps.put("meetingRoomId",meetingRoomId);
        maps.put("url",wechatSerer+"/wechat/scanMeeting/scanMeetingRoomQR?tenantId="+tenantId+"&meetingRoomId="+meetingRoomId);


        //校验数据---会议室无效
        TenantContext.setTenantId(tenantId);
        SysUser sysUser=this.userService.selectUserByUserId(userId);
        YuncmMeetingRoom room=this.yuncmMeetingService.selectByidYuncmMeetingRoom(meetingRoomId);
        if(null==room ||room.getDeleteState().equals("1")){
            responseResult.setMsg("会议室无效");
            responseResult.setIfSuc(0);
            return responseResult;
        }

        //校验数据----预订者的权限
        if(checkRoomAuth(tenantId,meetingRoomId,userId)){
            responseResult.setMsg("没有预订权限");
            responseResult.setIfSuc(0);
            return responseResult;
        }
        //校验预定时间是否在可预订范围内
        if(!checkMeetingTime(tenantId,meetingRoomId,start,end)){
            responseResult.setMsg("不在可预订时间范围内");
            responseResult.setIfSuc(0);
            return responseResult;
        }
        //校验数据----时间段占用
        if(checkMeetingRoonTake(tenantId,meetingRoomId,start,end)){
            responseResult.setMsg("该时段已被占用");
            responseResult.setIfSuc(0);
            return responseResult;
        }
        //校验数据----手机号不在当前企业下
        if(!checkPhoneNumber(tenantId,userId)){
            responseResult.setMsg("手机号不在当前企业下");
            responseResult.setIfSuc(0);
            return responseResult;
        }

        TenantContext.setTenantId(tenantId);
        MeetingDetailsVo meetingDetailsVo=new MeetingDetailsVo();

        meetingDetailsVo.setTitle(title);
        Date startTime= DateTypeFormat.StrToDate(start+":00");
        Date endTime=DateTypeFormat.StrToDate(end+":00");
        meetingDetailsVo.setStart(startTime.getTime());
        meetingDetailsVo.setEnd(endTime.getTime());
        meetingDetailsVo.setResourceId(meetingRoomId);
        Map<String,Object> local=new HashMap<String,Object>();
        local.put("id",meetingRoomId);
        meetingDetailsVo.setLocation(local);
        Map<String,Object> dept=new HashMap<String,Object>();
        dept.put("id",sysUser.getOrgId());
        meetingDetailsVo.setDepartment(dept);
        meetingDetailsVo.setContents("");

        //校验前台传参以及给实体赋值
        Map<String, Object> map = MeetingValidationUtil.addMeetingValidation(meetingDetailsVo, userId);
        if (null == map) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        //获取是否为增加状态
        String state = (String) map.get("state");
        //获取会议实体
        YunmeetingConference yunmeetingConference = (YunmeetingConference) map.get("yunmeetingConference");
        //获取会议通知实体
        YunmeetingMessageInform yunmeetingMessageInform = (YunmeetingMessageInform) map.get("yunmeetingMessageInform");
        //获取提前提醒时间List
        List<Date> list = (List<Date>) map.get("times");
        //获取会议Id
        String conferenceId = yunmeetingConference.getId();
        //保存会议和会议室中间表
        if(map.containsKey("yummeetingConferenceRoomMiddle")) {
            //获取会议和会议室中间表
            YummeetingConferenceRoomMiddle yummeetingConferenceRoomMiddle = (YummeetingConferenceRoomMiddle) map.get("yummeetingConferenceRoomMiddle");
            yunmeetingConference = meetingReserveService.reserveMeeting(yummeetingConferenceRoomMiddle,yunmeetingConference);
        }
        Map<String, Object> map1 = new HashMap<>();
        //保存或修改会议表
        boolean b = false;
        if (state.equals("save")) {
            b = meetingReserveService.insertMeeting(yunmeetingConference);
            if (b) {
                //增加参会人员
                YunmeetingParticipantsInfo yunmeetingParticipantsInfo = new YunmeetingParticipantsInfo();
                yunmeetingParticipantsInfo.setParticipantsNamePinyin(sysUser.getUserNamePinyin());
                yunmeetingParticipantsInfo.setParticipantsName(sysUser.getUserName());
                yunmeetingParticipantsInfo.setType("0");
                yunmeetingParticipantsInfo.setParticipantsId(userId);
                yunmeetingParticipantsInfo.setOrgId(sysUser.getOrgId());
                yunmeetingParticipantsInfo.setIsInner("1");
                yunmeetingParticipantsInfo.setCreateTime(new Date());
                yunmeetingParticipantsInfo.setId(CreateUUIdUtil.Uuid());
                yunmeetingParticipantsInfo.setCreaterId(userId);
                yunmeetingParticipantsInfo.setConferenceId(conferenceId);
                meetingReserveService.insertYunmeetingParticipantsInfo(yunmeetingParticipantsInfo);
                //增加动态信息
                map1.put("meetingId", yunmeetingConference.getId());
                map1.put("content", sysUser.getUserName() +"创建了" + yunmeetingConference.getConferenceName() + "会议。");
                map1.put("userId",userId);
                meetingDynamicService.insertMeetingDynamicByMeetingId(map1);
            }
        }
        //设置定时修改会议状态
        settingMeetingTimerTask(yunmeetingConference);
        responseResult.setIfSuc(1);
        responseResult.setData(maps);
        return  responseResult;

    }

    @Resource
    private TimerService timerService;
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
     * 查询会议室占用情况
     * @param tenantId
     * @param meetingRoomId
     * @param start
     * @param end
     * @return
     */
    public boolean checkMeetingRoonTake(String tenantId,String meetingRoomId,String start,String end){
        TenantContext.setTenantId(tenantId);
        return this.yuncmMeetingService.findMeetingRoomTakeInfo(meetingRoomId,start,end,"0");
    }

    /**
     * 查询手机号是否在该租户下
     * @param tenantId
     * @param userId
     * @return
     */
    public boolean checkPhoneNumber(String tenantId,String userId){
        TenantContext.setTenantId("0");
        SaasUserWeb tempUser=new SaasUserWeb();
        tempUser.setUserId(userId);
        tempUser.setTenantId(tenantId);
        SaasUserWeb userWeb=this.loginRegisterService.selectUserLoginInfo(tempUser);
        return (null!=userWeb)?true:false;
    }

    /**
     * 校验某用户是否有该会议预订权限
     * @param tenantId
     * @param userId
     * @param RoomId
     * @return
     */
    public boolean checkRoomAuth(String tenantId,String RoomId,String userId){
        TenantContext.setTenantId(tenantId);
        boolean flag=true;
        if(this.yuncmMeetingService.findMeetingRoomId(RoomId)){
           flag=!this.yuncmMeetingService.findMeetingRoomAndUser(RoomId,userId);
        }else{
            flag=false;
        }

       return flag;
    }

}
