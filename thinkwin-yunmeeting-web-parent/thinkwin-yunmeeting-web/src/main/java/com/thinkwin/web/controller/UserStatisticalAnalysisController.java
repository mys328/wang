package com.thinkwin.web.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.sun.org.apache.regexp.internal.RE;
import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.PermissionService;
import com.thinkwin.auth.service.UserStatisticalAnalysisService;
import com.thinkwin.common.ComparatorUserStatisticalDataVo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.model.db.YunmeetingDynamics;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.CreateRandomNumber;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.SMSCode;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.statisticalAnalysisVo.ConferenceNumInfoVo;
import com.thinkwin.common.vo.statisticalAnalysisVo.UserStatisticalDataVo;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.MeetingDynamicService;
import com.thinkwin.yuncm.service.MeetingReserveService;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: yinchunlei
 * Date: 2017/10/24.
 * Company: thinkwin
 * 人员统计分析controller层
 */
@Controller
public class UserStatisticalAnalysisController {
    private static Logger log = LoggerFactory.getLogger(UserStatisticalAnalysisController.class);

    @Resource
    private UserStatisticalAnalysisService userStatisticalAnalysisService;
    @Resource
    private MeetingReserveService meetingReserveService;
    SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
    SimpleDateFormat sdf1 =   new SimpleDateFormat( "HH:mm:ss" );

    @Resource
    private PermissionService permissionService;
    /**
     * 跳转到统计分析主页功能接口
     * @return
     */
    @RequestMapping("/gotoStatisticalAnalysisHomePage")
    public String gotoStatisticalAnalysisHomePage(HttpServletRequest request){
        ///////////////////////////添加资源拦截功能/////////////////////////////////
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null != userInfo) {
            String userId = userInfo.getUserId();
            if(StringUtils.isNotBlank(userId)) {
                //在此处需要获取用户请求的路径
                String url = request.getRequestURI();
                boolean userJurisdiction = permissionService.getUserJurisdiction(userId,url);
                if(userJurisdiction){
                    return "statisticAnalysis/statistical";
                }
            }
        }
        return "redirect:/logout";
        ////////////////////////////添加资源拦截功能///////////////////////////
        //return "statisticAnalysis/statistical"
    }


    /**
     * 跳转到按人员统计分析页面
     * @return
     */
    @RequestMapping("gotoUserStatisticalPage")
    public String gotoUserStatisticalPage(){
        return "";
    }

    /**
     *获取用户总数和为参会人数功能接口
     * @return
     */
    @RequestMapping("getUserTotalNumAndNumberOfAbsentParticipants")
    @ResponseBody
    public Map getUserTotalNumAndNumberOfAbsentParticipants(String startTime, String endTime){
        Map map = new HashMap();
        Map userTotalNumAndNumberOfAbsentParticipants = userStatisticalAnalysisService.getUserTotalNumAndNumberOfAbsentParticipants(startTime,endTime);
        if(null != userTotalNumAndNumberOfAbsentParticipants){
            String errorMessage = (String) userTotalNumAndNumberOfAbsentParticipants.get("error");
            if(StringUtils.isNotBlank(errorMessage)){
                map.put("error",errorMessage);
                return map;
            }else {
                Integer userTotalNum = (Integer) userTotalNumAndNumberOfAbsentParticipants.get("userTotalNum");
                map.put("userTotalNum",userTotalNum);//部门下总人数
                Integer numberOfAbsentParticipants = (Integer)userTotalNumAndNumberOfAbsentParticipants.get("numberOfAbsentParticipants");
                map.put("numberOfAbsentParticipants",numberOfAbsentParticipants);//
            }
        }

        return map;
    }

    @Resource
    private MeetingDynamicService meetingDynamicService;
    /**
     * 获取人员统计分析页面数据功能接口(与搜索功能共用该接口)
     * paramType;//排序条件 //默认为0，按照会议时长排序； 按照1，会议数量排序；按照2，会议留言数排序；按照3，个人签到率排序； 按照4，个人相应率排序；
     * sortType;//排序类型 默认为1， 0为升序 1为降序
     * @return
     */
    /*@RequestMapping("getUserStatisticalData")
    @ResponseBody
    public ResponseResult getUserStatisticalData(String startTime, String endTime, String orgId, String queryCriteria,BasePageEntity basePageEntity,Integer paramType,Integer sortType,String customLogo){
        String tenantId = TenantContext.getTenantId();
        if(StringUtils.isBlank(tenantId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(),BusinessExceptionStatusEnum.ParamErr.getCode());
        }
        Map map = new HashMap();
        String customLogoo1 = null;
        long l = System.currentTimeMillis();
        List<UserStatisticalDataVo> list = new ArrayList<>();
        String customLogoo = RedisUtil.get("customLogo");
        if(null == customLogo || "".equals(customLogo) || (StringUtils.isNotBlank(customLogoo) && !customLogoo.equals(customLogo))) {
            RedisUtil.remove("customLogo");
            // /获取某段时间内的所有会议
            List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate = meetingReserveService.findMeetingConferenceByTime(startTime, endTime);
            list = userStatisticalAnalysisService.getUserStatisticalData(byMeetingRoomIdAndMeetingtakeStartDate, orgId, queryCriteria, tenantId);
            String s = JSON.toJSONString(list);
            RedisUtil.set("UserStatisticalDataVoListInfo"+tenantId,s);
            String sixByteRandomNumber = CreateRandomNumber.createSixByteRandomNumber();
            RedisUtil.set("customLogo",sixByteRandomNumber);
            customLogoo1 = sixByteRandomNumber;
        }else {
            String userStatisticalDataVoListInfo = RedisUtil.get("UserStatisticalDataVoListInfo"+tenantId);
            list = JSON.parseArray(userStatisticalDataVoListInfo, UserStatisticalDataVo.class);
            customLogoo1 = customLogoo;
        }
        int userTotalNum = 0;
            List<UserStatisticalDataVo> MemberArticleBeanPage = new ArrayList<UserStatisticalDataVo>();
            if (null != list && list.size() > 0) {
                if(null != list && list.size() > 1){
                    if(null == paramType){
                        paramType = 0;
                    }
                    if(null == sortType){
                        sortType = 1;
                    }
                    ComparatorUserStatisticalDataVo comparatorUserStatisticalDataVo = new ComparatorUserStatisticalDataVo(paramType,sortType);
                    Collections.sort(list, comparatorUserStatisticalDataVo);
                }
                userTotalNum = list.size();
                ////////////////////////////////////////////list分页功能//////////////////////////////////////////
                Integer currentPage = basePageEntity.getCurrentPage();
                Integer pageSize = basePageEntity.getPageSize();
                int currIdx = (currentPage > 1 ? (currentPage -1) * pageSize : 0);
                for (int i = 0; i < pageSize && i < list.size() - currIdx; i++) {
                    UserStatisticalDataVo memberArticleBean = list.get(currIdx + i);
                    MemberArticleBeanPage.add(memberArticleBean);
                }
                /////////////////////////////////////////////list分页功能/////////////////////////////////////
            }
            PageInfo pageInfo = new PageInfo<>();
            pageInfo.setTotal(userTotalNum);
            pageInfo.setList(MemberArticleBeanPage);
            map.put("statisticalAnalysisInfos", pageInfo);
            map.put("customLogo",customLogoo1);
             long ll = System.currentTimeMillis();
             log.info("xxx查看使用的时间 ："+(ll-l));//查看使用的时间
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map, BusinessExceptionStatusEnum.Success.getCode());
    }*/

    /**
     * 获取人员统计分析页面数据功能接口(与搜索功能共用该接口)
     * paramType;//排序条件 //默认为0，按照会议时长排序； 按照1，会议数量排序；按照2，会议留言数排序；按照3，个人签到率排序； 按照4，个人相应率排序；
     * sortType;//排序类型 默认为1， 0为升序 1为降序
     * @return
     */
    @RequestMapping("getUserStatisticalData")
    @ResponseBody
    public ResponseResult getUserStatisticalData(String startTime, String endTime, String orgId, String queryCriteria,BasePageEntity basePageEntity,Integer paramType,Integer sortType,String customLogo){
        String tenantId = TenantContext.getTenantId();
        if(StringUtils.isBlank(tenantId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(),BusinessExceptionStatusEnum.ParamErr.getCode());
        }
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null == userInfo){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(),BusinessExceptionStatusEnum.ParamErr.getCode());
        }
        String userId = userInfo.getUserId();
        if(StringUtils.isBlank(userId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(),BusinessExceptionStatusEnum.ParamErr.getCode());
        }
        Map map = new HashMap();
        String customLogoo1 = null;
        long l = System.currentTimeMillis();
        List<UserStatisticalDataVo> list = new ArrayList<>();
        String customLogoo = RedisUtil.get(tenantId+"_customLogo_"+userId);
        int userTotalNum = 0;
        List<UserStatisticalDataVo> MemberArticleBeanPage = new ArrayList<UserStatisticalDataVo>();
        if(null == customLogo || "".equals(customLogo) || (StringUtils.isNotBlank(customLogoo) && !customLogoo.equals(customLogo))) {
            RedisUtil.remove(tenantId+"_customLogo_"+userId);
            // /获取某段时间内的所有会议
            List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate = meetingReserveService.findMeetingConferenceByTime(startTime, endTime);
            list = userStatisticalAnalysisService.getUserStatisticalData(byMeetingRoomIdAndMeetingtakeStartDate, orgId, queryCriteria, tenantId);
            if(null != list && list.size() >0) {
                //  if(null != list && list.size() > 1){
                if(null == paramType){
                    paramType = 0;
                }
                if(null == sortType){
                    sortType = 1;
                }
                ComparatorUserStatisticalDataVo comparatorUserStatisticalDataVo = new ComparatorUserStatisticalDataVo(paramType,sortType);
                Collections.sort(list, comparatorUserStatisticalDataVo);
                // }
                String s = JSON.toJSONString(list);
                RedisUtil.set(tenantId + "_UserStatisticalDataVoListInfo_" + userId, s);
                String sixByteRandomNumber = CreateRandomNumber.createSixByteRandomNumber();
                RedisUtil.set(tenantId +"_customLogo_" +  userId, sixByteRandomNumber);
                RedisUtil.expire(tenantId +"_customLogo_" +  userId,900);
                customLogoo1 = sixByteRandomNumber;
            }
        }else {
            String userStatisticalDataVoListInfo = RedisUtil.get(tenantId + "_UserStatisticalDataVoListInfo_" + userId);
            list = JSON.parseArray(userStatisticalDataVoListInfo, UserStatisticalDataVo.class);
            customLogoo1 = customLogoo;
        }
        if (null != list && list.size() > 0) {
            userTotalNum = list.size();
            ////////////////////////////////////////////list分页功能//////////////////////////////////////////
            Integer currentPage = basePageEntity.getCurrentPage();
            Integer pageSize = basePageEntity.getPageSize();
            int currIdx = (currentPage > 1 ? (currentPage -1) * pageSize : 0);
            for (int i = 0; i < pageSize && i < list.size() - currIdx; i++) {
                UserStatisticalDataVo memberArticleBean = list.get(currIdx + i);
                MemberArticleBeanPage.add(memberArticleBean);
            }
            /////////////////////////////////////////////list分页功能/////////////////////////////////////
        }
        PageInfo pageInfo = new PageInfo<>();
        pageInfo.setTotal(userTotalNum);
        pageInfo.setList(MemberArticleBeanPage);
        map.put("statisticalAnalysisInfos", pageInfo);
        map.put("customLogo",customLogoo1);
        long ll = System.currentTimeMillis();
        log.info("xxx查看使用的时间 ："+(ll-l));//查看使用的时间
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map, BusinessExceptionStatusEnum.Success.getCode());
    }


    /**
     * 获取某个人员的会议数量信息功能接口
     * @return
     */
    @RequestMapping("getConferenceNumInfo")
    @ResponseBody
    public ResponseResult getConferenceNumInfo(String startTime, String endTime, String userId){
        if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime) && StringUtils.isNotBlank(userId)) {
            //获取某段时间内的所有会议
            List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate = meetingReserveService.findMeetingConferenceByTime(startTime, endTime);
            if(null != byMeetingRoomIdAndMeetingtakeStartDate && byMeetingRoomIdAndMeetingtakeStartDate.size() > 0) {
                List<ConferenceNumInfoVo> list = userStatisticalAnalysisService.getConferenceNumInfo(userId, byMeetingRoomIdAndMeetingtakeStartDate);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), list, BusinessExceptionStatusEnum.Success.getCode());
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), null, BusinessExceptionStatusEnum.DataNull.getCode());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "请求参数有误！");
    }

}
