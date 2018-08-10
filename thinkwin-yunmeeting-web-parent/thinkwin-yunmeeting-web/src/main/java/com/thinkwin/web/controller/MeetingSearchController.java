package com.thinkwin.web.controller;

import com.github.pagehelper.PageInfo;
import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.SaasUserWeb;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.model.db.SysUserRole;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.MeetingReserveService;
import com.thinkwin.yuncm.service.MeetingScreeningService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
  *  会议搜索筛选
  *
  *  开发人员:daipengkai
  *  创建时间:2017/8/7
  *
  */
@RequestMapping("/search")
@Controller
public class MeetingSearchController {


    @Resource
    MeetingScreeningService meetingScreeningService;

    @Resource
    private UserService userService;
    @Resource
    private LoginRegisterService loginRegisterService;

    @Resource
    MeetingReserveService meetingReserveService;




    @RequestMapping(value = "/skipMeetingScreening")
    public ModelAndView skipMeetingScreening(String type){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type",type);
        return new ModelAndView("meeting_res/meeting",map);
    }

    @RequestMapping(value = "/skipMeetingScreeningVerify")
    public ModelAndView skipMeetingScreeningVerify(String type){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type",type);
        return new ModelAndView("meeting_res/verify",map);
    }

    /**
     *  筛选我的会议
     * @param userId  用户id
     * @param meetingType 会议筛选，0：最近7天的会议，1：未来的会议，2：过去的会议，（为空默认为0）
     * @param myType 我的筛选：0：我的全部会议，1：我组织的会议，2：我参与的会议，（为空默认为0）
     * @param page 分页对象
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/selectMyMeeting",method = RequestMethod.POST)
    @ResponseBody
    public Object selectMyMeeting(String userId,String meetingType,String myType,String searchKey,BasePageEntity page) throws ParseException {

        if(!StringUtils.isNotBlank(meetingType)){
            meetingType = "0";
        }
        if(!StringUtils.isNotBlank(myType)){
            myType = "0";
        }
        //获取当前用户是否有审核权限
        Map<String,Object> userAuditRole = getUserAuditRole();
        String isAuthstr = (String) userAuditRole.get("isAuthstr");
        //判断是否为搜索
        if(!StringUtils.isNotBlank(searchKey)) {
            //查看我的近7天会议
            if ("0".equals(meetingType)) {
                Map<String, Object> objects = this.meetingScreeningService.selectMyYunmeetingConferenceSevenDays(userId, myType);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, isAuthstr, objects);
            }
            //查看我未来的会议
            if ("1".equals(meetingType)) {
                Map<String, Object> objects = this.meetingScreeningService.selectYunmeetingConferenceFuture(page, myType, userId);

                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, isAuthstr, objects);
            }
            //查看我过去的会议
            if ("2".equals(meetingType)) {
                Map<String, Object> objects = this.meetingScreeningService.selectYunmeetingConferenceFormerly(page, myType, userId);

                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, isAuthstr, objects);
            }
        }else{
            if("0".equals(meetingType)){
                    //关键字搜索
                Map<String, Object> map = this.meetingScreeningService.selectSearchYunmeetingConferenceAfter(page, meetingType, myType, userId, searchKey);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, isAuthstr, map);


            }else{
                //关键字搜索
                Map<String, Object> map = this.meetingScreeningService.selectSearchYunmeetingConference(page, meetingType, myType, userId, searchKey);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, isAuthstr, map);

            }
        }

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
    }


    /**
     * 筛选我审核的会议
     * @param userId 用户id
     * @param auditType 审核状态，0：显示全部类型，1：待审批，2：已通过，3：未通过，（为空显示全部）
     * @param page 分页
     * @return
     */
    @RequestMapping(value = "/selectAuditMeeting",method = RequestMethod.POST)
    @ResponseBody
    public Object selectAuditMeeting(String userId,String auditType,String searchKey,BasePageEntity page) {

        if(!StringUtils.isNotBlank(auditType)){
            auditType = "3";
        }
        if(!StringUtils.isNotBlank(searchKey)) {
            Map<String, Object> conferencePageInfo = this.meetingScreeningService.selectAuditYunmeetingConference(page, auditType, userId);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),conferencePageInfo);
        }else{

            Map<String, Object> conferencePageInfo = this.meetingScreeningService.selectAuditSearchYunmeetingConference(page, auditType, userId,searchKey);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),conferencePageInfo);
        }
    }


    /**
     *  筛选我的会议
     * @param userId  用户id
     * @param meetingType 会议筛选，0：最近7天的会议，1：未来的会议，2：过去的会议，（为空默认为0）
     * @param myType 我的筛选：0：我的全部会议，1：我组织的会议，2：我参与的会议，（为空默认为0）
     * @param page 分页对象
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/searchMeeting",method = RequestMethod.POST)
    @ResponseBody
    public Object searchMeeting(String userId,String meetingType,String myType,String searchKey,BasePageEntity page) throws ParseException {
        if(!StringUtils.isNotBlank(meetingType)){
            meetingType = "0";
        }
        if(!StringUtils.isNotBlank(myType)){
            myType = "0";
        }
        Map<String, Object> map = this.meetingScreeningService.selectSearchYunmeetingConference(page, meetingType, myType, userId, searchKey);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);

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

}
