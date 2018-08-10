package com.thinkwin.yunmeeting.weixin.controller;

import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.model.core.SaasUserOauth;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.utils.Base64;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.PropertyUtil;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.MeetingDynamicService;
import com.thinkwin.yuncm.service.MeetingReserveService;
import com.thinkwin.yuncm.service.YuncmMeetingService;
import com.thinkwin.yunmeeting.weixin.constant.MeetingConstant;
import com.thinkwin.yunmeeting.weixin.service.WxOAuth2Service;
import com.thinkwin.yunmeeting.weixin.service.WxUserService;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 类说明：扫码会议室二维码快速预订会议
 * @author lining 2017/7/28
 * @version 1.0
 *
 */
@RestController
@RequestMapping("/wechat/scanMeetingSign")
public class WxScanMeetingSignController {

    private final Logger log = LoggerFactory.getLogger(WxScanMeetingSignController.class);


    @Resource
    private WxOAuth2Service wxOAuth2Service;
    @Resource
    private WxUserService wxUserService;
    @Resource
    private SaasTenantService saasTenantCoreService;
    @Resource
    private MeetingReserveService meetingReserveService;
    @Resource
    private YuncmMeetingService yuncmMeetingService;
    @Resource
    private UserService userService;
    @Resource
    MeetingDynamicService meetingDynamicService;


    /**
     * 二维码参数为空
     * @return
     */
    @RequestMapping("/error")
    public String errorBrowser(){

        return "scanMeetingSign/error";
    }

    /**
     * 签到返回信息页面
     * @return
     */
    @RequestMapping("/info")
    public String info(){

        return "scanMeetingSign/info";
    }




    /**
     * 扫描会议二维码签到
     * @param tenantId
     * @param meetingId
     * @param createTime
     * @param timeStep
     * @return
     */
    @RequestMapping("/scanMeetingSignQR")
    @ResponseBody
    public void scanMeetingSignQR(HttpServletRequest request, HttpServletResponse response,String tenantId, String meetingId,String createTime,String timeStep){
       /* timeStep = Base64.decode(timeStep);
        tenantId = Base64.decode(tenantId);
        meetingId = Base64.decode(meetingId);
        createTime = Base64.decode(createTime);*/
        if(StringUtils.isBlank(tenantId) || StringUtils.isBlank(meetingId) || StringUtils.isBlank(timeStep)){
            try {
                response.sendRedirect("scanMeetingSign/info");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(StringUtils.isBlank(createTime) || createTime.equals("null")){
            createTime = String.valueOf(new Date().getTime());
        }
        //校验会议有效性及二维码过期
        //获取当前时间
        Long nowTime=System.currentTimeMillis();
        //二维码是否超期 1正常，0超期
        String expire="0";
        int step=Integer.parseInt(timeStep);
        Long createQrTime=Long.parseLong(createTime);
        Long cj=(nowTime-createQrTime)/1000;
        if(step==0){  //无限制
            expire="1";
        }else if(cj<step){ //二维码未过期
            expire="1";
        }
        try {
            //weixin Oauth2
            String state=tenantId+";"+meetingId+";"+expire;
            String url=wxOAuth2Service.oauth2buildAuthorizationUrl("/wechat/scanMeetingSign/reserveMeetingControl","snsapi_base",state);
            log.info("##############Url="+url);
            response.sendRedirect(url);

        }catch(Exception e){
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

        //String yunMeetingServer= PropertyUtil.getYunMeetingServer();
        //二维码是否超期 1正常，0超期
        String isExpire="0";

        // 用户同意授权后，能获取到code
        String code = request.getParameter("code");
        String state = request.getParameter("state");

        WxMpUser wxMpUser=null;

        try {
            String [] params=state.split(";");
            String tenantId=params[0];
            String meetingId=params[1];
            String expire=params[2];

            //签到返回默认页
            mv.addObject("tenantId",tenantId);
            mv.setViewName("scanMeetingSign/info");

            //二维码是否过期
            if(isExpire.equals(expire)){
                mv.addObject("code", MeetingConstant.ScanMeetingSignMsg.EXPIRE);
                mv.addObject("msg","会议二维码已经过期");
                return mv;
            }

            //会议是否有效：签到时间内、取消、删除
            TenantContext.setTenantId(tenantId);
            //YunmeetingConference meeting=this.meetingReserveService.selectMeetingByMeetingId(meetingId);


            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxOAuth2Service.oauth2getAccessToken(code);

            List<SaasUserOauth> userOauth=null;
            if(null!=wxMpOAuth2AccessToken) {
                wxMpUser = this.wxUserService.getWxUserInfo(wxMpOAuth2AccessToken.getOpenId());
                SaasUserOauth tempOauth = new SaasUserOauth();
                tempOauth.setOauthUnionId(wxMpUser.getUnionId());
                tempOauth.setTenantId(tenantId);
                tempOauth.setIsBind(1);
                userOauth = this.saasTenantCoreService.selectOAuthLoginInfo(tempOauth);
                if(userOauth != null) {
                    log.info("userOauth{}" + userOauth.toString());
                }
            }

            //封装要显示到视图的数据

            //是否是该租户的用户
            if(null!=userOauth) {
                //该会议的参会人
                String userId = userOauth.get(0).getUserId();
                //查询用户姓名
                String userName = "";
                SysUser sysUser = userService.selectUserByUserId(userId);
                if(null != sysUser){
                    //增加用户状态判断
                    Integer status = sysUser.getStatus();
                    if(status == 2 || status == 3 || status == 89){
                        //你不是该会议的参会人
                        mv.addObject("code", MeetingConstant.ScanMeetingSignMsg.NOT_PERSON);
                        mv.addObject("msg","您不是该会议的参会人");
                        return mv;
                    }
                    userName = sysUser.getUserName();
                }
                /*if(null != yuncmMeetingRooms){
                    String roomState = yuncmMeetingRooms.get(0).getState();
                    if(!roomState.equals("2")){
                        mv.addObject("code", MeetingConstant.ScanMeetingSignMsg.MEETING_INVALID);
                        mv.addObject("msg","会议二维码已经过期");
                        return mv;
                    }
                }*/
                Map<String,Object> map = new HashMap<>();
                map.put("meetingId",meetingId);
                map.put("userId",userId);
                YunmeetingConference yunmeetingConference = meetingReserveService.selectMeetingByMeetingIdAndUserId(map);
                if(null != yunmeetingConference) {
                    //会议是否失效
                    if (yunmeetingConference.getDeleteState().equals("1") || yunmeetingConference.getState().equals("5") || yunmeetingConference.getState().equals("4")){
                        mv.addObject("code", MeetingConstant.ScanMeetingSignMsg.MEETING_INVALID);
                        mv.addObject("msg","会议二维码已经过期");
                        return mv;
                    }
                    //是否已签到
                    YunmeetingMeetingSign yunmeetingMeetingSign = meetingReserveService.selectSignInfo(meetingId, userId);
                    if(null != yunmeetingMeetingSign){
                        //已经签到
                        mv.addObject("code", MeetingConstant.ScanMeetingSignMsg.REPEAT);
                        mv.addObject("msg",userName+"，您好！您已签到，请直接参加会议。");
                        return mv;
                    } else {
                        //开始签到
                        yunmeetingMeetingSign = new YunmeetingMeetingSign();
                        yunmeetingMeetingSign.setConfrerenId(meetingId);
                        yunmeetingMeetingSign.setId(CreateUUIdUtil.Uuid());
                        yunmeetingMeetingSign.setParticipantsId(userId);
                        yunmeetingMeetingSign.setSignTime(new Date());
                        yunmeetingMeetingSign.setSignSource("3");
                        boolean b = meetingReserveService.insertMeetingSign(yunmeetingMeetingSign);
                        if(b){
                            String reservationPersonId = yunmeetingConference.getReservationPersonId();
                            //增加动态
                            YunmeetingDynamics yunmeetingDynamics = new YunmeetingDynamics();
                            yunmeetingDynamics.setConferenceId(meetingId);
                            yunmeetingDynamics.setParticipantsId(reservationPersonId);
                            yunmeetingDynamics.setContent(userName + "签到成功！");
                            meetingDynamicService.insertMeetingDynamic(yunmeetingDynamics, "1",userId);
                            Map<String,Object> objectMap = new HashMap<>();
                            String organizerId = yunmeetingConference.getOrganizerId();
                            SysUser sysUser1 = userService.selectUserByUserId(organizerId);
                            if(null != sysUser1){
                                objectMap.put("orgName",sysUser1.getUserName());
                            }
                            objectMap.put("data",formatDate(yunmeetingConference.getTakeStartDate(),yunmeetingConference.getTakeEndDate()));
                            //获取会议室地址
                            List<YuncmMeetingRoom> yuncmMeetingRooms = meetingReserveService.selectMeetingRoomByMeetingId(meetingId);
                            if(null != yuncmMeetingRooms){
                                objectMap.put("location",yuncmMeetingRooms.get(0).getLocation());
                            }
                            mv.addAllObjects(objectMap);
                            mv.addObject("code", MeetingConstant.ScanMeetingSignMsg.SUCCESS);
                            mv.addObject("msg",userName + "，您好！欢迎参加“" +yunmeetingConference.getConferenceName()+"”会议。");
                            return mv;
                        }
                    }
                }else{
                    //你不是该会议的参会人
                    mv.addObject("code", MeetingConstant.ScanMeetingSignMsg.NOT_PERSON);
                    mv.addObject("msg","您不是该会议的参会人");
                    return mv;
                }
            }else{
                //非此租户的用户
                mv.addObject("code", MeetingConstant.ScanMeetingSignMsg.NOT_PERSON);
                mv.addObject("msg","您不是该会议的参会人");
                return mv;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mv.addObject("code", MeetingConstant.ScanMeetingSignMsg.FAIL);
            mv.addObject("msg","系统异常");
        }
        return mv;
    }

    private String formatDate(Date startTime,Date endTime){
        if(null != startTime && null != endTime){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String staTime = simpleDateFormat.format(startTime);
            String enTime = simpleDateFormat.format(endTime);
            return staTime +"-"+ enTime;
        }
        return null;
    }

}
