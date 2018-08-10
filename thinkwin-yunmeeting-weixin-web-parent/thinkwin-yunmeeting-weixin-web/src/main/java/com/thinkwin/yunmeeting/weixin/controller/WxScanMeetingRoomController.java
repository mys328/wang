package com.thinkwin.yunmeeting.weixin.controller;

import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.core.SaasUserOauth;
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
import com.thinkwin.common.vo.mobile.MobileMeetingRoomVo;
import com.thinkwin.schedule.service.TimerService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.MeetingDynamicService;
import com.thinkwin.yuncm.service.MeetingReserveService;
import com.thinkwin.yuncm.service.YuncmMeetingService;
import com.thinkwin.yunmeeting.weixin.service.WxOAuth2Service;
import com.thinkwin.yunmeeting.weixin.service.WxUserService;
import com.thinkwin.yunmeeting.weixin.timer.MeetingStatusAmend;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * 类说明：扫码会议室二维码快速预订会议
 * @author lining 2017/7/28
 * @version 1.0
 *
 */
@RestController
@RequestMapping("/wechat/scanMeeting")
public class WxScanMeetingRoomController {

    private final Logger log = LoggerFactory.getLogger(WxScanMeetingRoomController.class);


    @Resource
    private WxOAuth2Service wxOAuth2Service;
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
     * 扫描会议室二维码
     * @param tenantId
     * @param meetingRoomId
     * @return
     */
    @RequestMapping("/scanMeetingRoomQR")
    @ResponseBody
    public ModelAndView scanMeetingRoomQR(String tenantId, String meetingRoomId){
        ModelAndView mv=new ModelAndView();

        TenantContext.setTenantId(tenantId);
        YuncmMeetingRoom meetingRoom=this.yuncmMeetingService.selectByidYuncmMeetingRoom(meetingRoomId);
        if(null==meetingRoom ||meetingRoom.getDeleteState().equals("1") ){
            mv.setViewName("scanMeeting/errorQr");
            return  mv;
        }

        MobileMeetingRoomVo room=new MobileMeetingRoomVo();
        room.setMeetingRoomName(meetingRoom.getName());
        room.setMeetingRoomStatus(meetingRoom.getState());
        room.setCapacity(meetingRoom.getPersionNumber());
        room.setLocation(meetingRoom.getLocation());
        String meetingStatus="free";
        room.setMeetingRoomStatus(meetingStatus);
        room.setImgPath(meetingRoom.getImageUrl());
        //扩展设备赋值
        if(null!=meetingRoom.getDeviceServices()) {
            for (String s : meetingRoom.getDeviceServices()) {
                switch (s) {
                    case "001":
                        room.setHasWhiteboard(1);
                        break;
                    case "002":
                        room.setHasVedioMeeting(1);
                        break;
                    case "003":
                        room.setHasDisplay(1);
                        break;
                    case "004":
                        room.setHasMicrophone(1);
                        break;
                    default:
                        break;
                }
            }
        }
        List<MobileMeetingRoomVo.Meetings> roomVos=new ArrayList<>();
        Date nowDate=new Date();
        String beginTime= DateTypeFormat.DateToStr(new Date(),"yyyy-MM-dd");
        String endTime=DateTypeFormat.DateToStr(new Date(),"yyyy-MM-dd");
        //查询出当天所有会议
        List<YunmeetingConference> meetings=this.meetingReserveService.findByMeetingRoomIdAndMeetingtakeStartDate(meetingRoomId,beginTime+" 00:00:00",endTime+" 23:59:59");
        for(YunmeetingConference m:meetings){
            //过滤出已过期会议
            if(nowDate.before(m.getTakeStartDate()) || nowDate.before(m.getTakeEndDate())) {
                //判断当前时间正在开始的会议
                if(nowDate.after(m.getTakeStartDate()) && nowDate.before(m.getTakeEndDate())){
                    room.setMeetingRoomStatus("busy");
                }
                MobileMeetingRoomVo.Meetings vm = new MobileMeetingRoomVo.Meetings();
                String begin = DateTypeFormat.DateToStr(m.getTakeStartDate(), "HH:mm");
                String end = DateTypeFormat.DateToStr(m.getTakeEndDate(), "HH:mm");
                vm.setBeginTime(begin);
                vm.setEndTime(end);
                SysUser sysUser=this.userService.selectUserByUserId(m.getReservationPersonId());
                vm.setCreateUser((null!=sysUser)?sysUser.getUserName():null);
                vm.setDurasion(DateTypeFormat.getDateDiff(m.getTakeStartDate(), m.getTakeEndDate()));
                roomVos.add(vm);
            }
        }

        room.setMeetingRoomsList(roomVos);

        mv.addObject("room",room);
        mv.addObject("tenantId",tenantId);
        mv.addObject("meetingRoomId",meetingRoomId);
        mv.setViewName("scanMeeting/meetingRoomInfo");
        return  mv;

    }

    /**
     * 立即预订：判断浏览器跳转不同预订会议端
     * @param request
     * @param response
     * @param tenantId
     * @param meetingRoomId
     */
    @RequestMapping("/meetingPage")
    @ResponseBody
    public void meetingPage(HttpServletRequest request, HttpServletResponse response,String tenantId,String meetingRoomId) {
        String yunMeetingServer= PropertyUtil.getYunMeetingServer();
        try {
           if(StringUtils.isNotBlank(tenantId)&& StringUtils.isNotBlank(meetingRoomId)){

               String userAgent=request.getHeader("User-Agent");
               if(userAgent.contains("MicroMessenger")){
                   String state=tenantId+";"+meetingRoomId;
                   String url=wxOAuth2Service.oauth2buildAuthorizationUrl("/wechat/scanMeeting/reserveMeetingControl","snsapi_base",state);
                   log.info("##############Url="+url);
                   response.sendRedirect(url);
               }else{
                   response.sendRedirect(yunMeetingServer+"/yunMeeting/scanMeeting/reserveMeetingPage?tenantId="+tenantId+"&meetingRoomId="+meetingRoomId);
               }
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 微信端第二次身份认证，跳转不同预订会议端
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/reserveMeetingControl")
    @ResponseBody
    public ModelAndView reserveMeetingControl(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView();
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String yunMeetingServer= PropertyUtil.getYunMeetingServer();

        // 用户同意授权后，能获取到code
        String code = request.getParameter("code");
        String state = request.getParameter("state");

        WxMpUser wxMpUser=null;

        YuncmMeetingRoom meetingRoom=new YuncmMeetingRoom();

        try {
            String [] params=state.split(";");
            String tenantId=params[0];
            String meetingRoomId=params[1];

            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxOAuth2Service.oauth2getAccessToken(code);

            List<SaasUserOauth> userOauth=null;
            if(null!=wxMpOAuth2AccessToken) {
                wxMpUser = this.wxUserService.getWxUserInfo(wxMpOAuth2AccessToken.getOpenId());

            SaasUserOauth tempOauth = new SaasUserOauth();
            tempOauth.setOauthUnionId(wxMpUser.getUnionId());
            tempOauth.setTenantId(tenantId);
            TenantContext.setTenantId("0");
            userOauth=this.loginRegisterService.selectOAuthLoginInfo(tempOauth);
            TenantContext.setTenantId(tenantId);
            meetingRoom=this.yuncmMeetingService.selectByidYuncmMeetingRoom(meetingRoomId);
            }

        //封装要显示到视图的数据


        //视图名
        if(null!=userOauth) {
            mv.addObject("unionId",wxMpUser.getUnionId());
            mv.addObject("tenantId",tenantId);
            mv.addObject("meetingRoomName",meetingRoom.getName());
            mv.addObject("meetingRoomId",meetingRoomId);
            mv.setViewName("scanMeeting/reserveMeeting");
        }else{
            mv.setViewName("redirect:"+yunMeetingServer+"/yunMeeting/scanMeeting/reserveMeetingPage?tenantId="+tenantId+"&meetingRoomId="+meetingRoomId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mv;
    }

    @RequestMapping("/reserveMeeting")
    @ResponseBody
    public ResponseResult reserveMeeting(String tenantId, String meetingRoomId, String unionId,String title,String start,String end){
        ResponseResult responseResult=new ResponseResult();
        Map<String, Object> m=new HashMap<>();
        m.put("tenantId",tenantId);
        m.put("meetingRoomId",meetingRoomId);

        TenantContext.setTenantId("0");
        SaasUserOauth tempOauth=new SaasUserOauth();
        tempOauth.setOauthUnionId(unionId);
        tempOauth.setOauthType(1);
        List<SaasUserOauth> userOauth=this.loginRegisterService.selectOAuthLoginInfo(tempOauth);
        String userId = userOauth.get(0).getUserId();


        //校验数据---会议室无效
        TenantContext.setTenantId(tenantId);
        SysUser sysUser=this.userService.selectUserByUserId(userId);
        YuncmMeetingRoom room=this.yuncmMeetingService.selectByidYuncmMeetingRoom(meetingRoomId);
        if(null==room){
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
            responseResult.setMsg("微信用户不在当前企业下");
            responseResult.setIfSuc(0);
            return responseResult;
        }


        TenantContext.setTenantId(tenantId);
        MeetingDetailsVo meetingDetailsVo=new MeetingDetailsVo();

        meetingDetailsVo.setTitle(title);
        Date startTime=DateTypeFormat.StrToDate(start+":00");
        Date endTime=DateTypeFormat.StrToDate(end+":00");
        meetingDetailsVo.setStart(startTime.getTime());
        meetingDetailsVo.setEnd(endTime.getTime());
        meetingDetailsVo.setResourceId(meetingRoomId);
        Map<String,Object> local=new HashMap<String,Object>();
        local.put("id",meetingRoomId);
        meetingDetailsVo.setLocation(local);
        meetingDetailsVo.setContents("");
        Map<String,Object> dept=new HashMap<String,Object>();
        dept.put("id",sysUser.getOrgId());
        meetingDetailsVo.setDepartment(dept);

        //校验前台传参以及给实体赋值
        Map<String, Object> map = MeetingValidationUtil.addMeetingValidation(meetingDetailsVo, userId);
        if (null == map) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        //获取是否为增加状态
        String state = (String) map.get("state");
        //获取会议实体
        YunmeetingConference yunmeetingConference = (YunmeetingConference) map.get("yunmeetingConference");
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
                map1.put("content", sysUser.getUserName()+"创建了" + yunmeetingConference.getConferenceName() + "会议。");
                map1.put("userId",userId);
                meetingDynamicService.insertMeetingDynamicByMeetingId(map1);
            }
        }
        settingMeetingTimerTask(yunmeetingConference);
        m.put("url","/wechat/scanMeeting/scanMeetingRoomQR?tenantId="+tenantId+"&meetingRoomId="+meetingRoomId);
        responseResult.setIfSuc(1);
        responseResult.setData(m);
        responseResult.setMsg("success");
        return responseResult;
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
