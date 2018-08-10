package com.thinkwin.yunmeeting.weixin.h5.controller;

import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.Sublist;
import com.thinkwin.common.vo.mobile.MobileRoomScreenVo;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.MeetingScreeningService;
import com.thinkwin.yuncm.service.SearchMeetingRoomService;
import com.thinkwin.yuncm.service.YuncmMeetingService;
import com.thinkwin.yuncm.service.YuncmRoomAreaService;
import com.thinkwin.yunmeeting.weixin.service.WxOAuth2Service;
import com.thinkwin.yunmeeting.weixin.service.WxUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 类说明：
 * @author lining 2017/8/28
 * @version 1.0
 *
 */
@Controller
@RequestMapping("/wechat/h5MeetingRoom")
public class H5MeetingRoomController {

    private final Logger log = LoggerFactory.getLogger(H5MeetingRoomController.class);


    @Resource
    private WxOAuth2Service wxOAuth2Service;
    @Resource
    private WxUserService wxUserService;
    @Resource
    private LoginRegisterService loginRegisterService;
    @Resource
    private UserService userService;

    @Resource
    private YuncmRoomAreaService yuncmRoomAreaService;

    @Resource
    private MeetingScreeningService meetingScreeningService;

    @Resource
    private SearchMeetingRoomService searchMeetingRoomService;

    @Resource
    private YuncmMeetingService yuncmMeetingService;

    /**
     * 会议室列表
     * @param request
     * @param response
     */
    @RequestMapping("/meetingRoomList")
    @ResponseBody
    public ResponseResult meetingRoomList(HttpServletRequest request, HttpServletResponse response,String tenantId,String openId,String userId,String number,String devices,String area,String startTime,String endTime,String isAudit,String content,String type,String currentPage, String pageSize) {
        ResponseResult responseResult=new ResponseResult();
        Map<String, Object> map=new HashMap<>();

        SysUser sysUser=this.check(tenantId,openId,userId);
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

          List<YuncmMeetingRoom> rooms = searchMeetingRoomService.selectH5MeetingScreenYuncmMeetingRoom(userId,number,devices,startTime,endTime,area,isAudit,content,type);
            //当前用户是否有权限预定会议
            List<YuncmMeetingRoom> meetingRooms = new ArrayList<YuncmMeetingRoom>();
            for(YuncmMeetingRoom room : rooms){
                boolean success = this.selectUserMeetingRoomRole(room.getId(),userId);
                if(success){
                    meetingRooms.add(room);
                }
            }
          //判断是否需要分页
            if(StringUtils.isNotBlank(currentPage) && StringUtils.isNotBlank(pageSize)) {
                //分页处理
                List<YuncmMeetingRoom> list = Sublist.page(Integer.parseInt(currentPage), Integer.parseInt(pageSize), meetingRooms);
                map.put("room", list);
                int num = 0;
                if (meetingRooms.size() % Integer.parseInt(pageSize) == 0) {
                    num = meetingRooms.size() / Integer.parseInt(pageSize);
                } else {
                    num = meetingRooms.size() / Integer.parseInt(pageSize) + 1;
                }
                map.put("total", num);
                map.put("currentPage", currentPage);
            }else{
                map.put("room", meetingRooms);
                map.put("total", 1);
                map.put("currentPage", 1);
            }
          responseResult.setIfSuc(1);
          responseResult.setData(map);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 会议室筛选条件
     * @param request
     * @param response
     */
    @RequestMapping("/screenParam")
    @ResponseBody
    public ResponseResult screenParam(HttpServletRequest request, HttpServletResponse response,String tenantId,String openId,String userId) {
        ResponseResult responseResult=new ResponseResult();
        Map<String, Object> map=new HashMap<>();

        TenantContext.setTenantId(tenantId);

        try {
            List<MobileRoomScreenVo> devices=new ArrayList<>();
            List<MobileRoomScreenVo> areas=new ArrayList<>();

            //设备
            List<YuncmDeviceService> deviceList=this.yuncmRoomAreaService.getDeviceAll();
            for(YuncmDeviceService d:deviceList){
                MobileRoomScreenVo vo=new MobileRoomScreenVo();
                vo.setId(d.getId());
                vo.setName(d.getName());
                devices.add(vo);
            }

            //区域
            List<YuncmRoomArea> areaList=new ArrayList<>();
            areaList=this.yuncmRoomAreaService.getRoomAreaAll();
            for(YuncmRoomArea a:areaList){
                MobileRoomScreenVo vo=new MobileRoomScreenVo();
                vo.setId(a.getId());
                vo.setName(a.getName());
                areas.add(vo);
            }

            map.put("devices",devices);
            map.put("areas",areas);

            responseResult.setIfSuc(1);
            responseResult.setData(map);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 日历看板-会议详情
     * @param request
     * @param response
     * @param tenantId
     * @param openId
     * @param userId 用户Id
     * @param roomId 会议室Id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @RequestMapping("/myMeeting")
    @ResponseBody
    public ResponseResult myMeeting(HttpServletRequest request, HttpServletResponse response,String tenantId,String openId,String userId,String roomId,String startTime,String endTime) {
        ResponseResult responseResult=new ResponseResult();
        Map<String, Object> map=new HashMap<>();

        SysUser sysUser=this.check(tenantId,openId,userId);
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

            List<YunmeetingConference> conferences = meetingScreeningService.selectTimeYunmeetingConference(roomId,startTime,endTime);
            map.put("conference",conferences);
            responseResult.setIfSuc(1);
            responseResult.setData(map);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    /**
     * 获取预订设置接口
     * @param request
     * @param response
     * @param tenantId
     * @param openId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/selectMeetingRoomReserve")
    @ResponseBody
    public Object selectMeetingRoomReserve(HttpServletRequest request, HttpServletResponse response,String tenantId,String openId,String userId) {
        ResponseResult responseResult=new ResponseResult();
        Map<String, Object> map=new HashMap<>();

        SysUser sysUser=this.check(tenantId,openId,userId);
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

        YuncmRoomReserveConf conf = this.yuncmMeetingService.selectYuncmRoomReserveConf();

        responseResult.setIfSuc(1);
        responseResult.setMsg(BusinessExceptionStatusEnum.Success.getDescription());
        responseResult.setData(conf);
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


}
