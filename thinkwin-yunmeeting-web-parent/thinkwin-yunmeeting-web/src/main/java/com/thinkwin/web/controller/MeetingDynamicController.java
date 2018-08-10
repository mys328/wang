package com.thinkwin.web.controller;

import com.thinkwin.TenantUserVo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.model.db.YunmeetingDynamics;
import com.thinkwin.common.model.db.YunmeetingDynamicsClickRecord;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.vo.meetingVo.DynamicVo;
import com.thinkwin.common.vo.meetingVo.MeetingDetailsVo;
import com.thinkwin.common.vo.meetingVo.MeetingDynamicVo;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.MeetingDynamicService;
import com.thinkwin.yuncm.service.MeetingReserveService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * 类名: MeetingDynamicController </br>
 * 描述:</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/8/9 </br>
 */
@Controller
@RequestMapping(value = "/dynamic", method = RequestMethod.POST)
public class MeetingDynamicController {

    @Resource
    MeetingDynamicService meetingDynamicService;
    @Resource
    MeetingReserveService meetingReserveService;

    /**
     * 方法名：selectRecentMeetingDynamic</br>
     * 描述：查询会议动态</br>
     * 参数：pageEntity 分页实体</br>
     * 参数：search 搜索  为空为不搜索</br>
     * 参数：all 全部  是否查询全部 1为查全部  0或不传为查近期</br>
     * 返回值：</br>
     */
    @RequestMapping("/selectrecentmeetingdynamic")
    @ResponseBody
    public Object selectRecentMeetingDynamic(BasePageEntity pageEntity, String search, String all) {
        //获取用户Id
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        Map<String, Object> map = new HashMap<>();
        //是否为近期会议动态
        boolean flag = false;
        if (StringUtils.isBlank(all) || !all.equals("1")) {
            map = dateFormat(-7, null);
            flag = true;
        } else {
            map = dateFormat(-7, "1");
            map.put("all", "1");
        }
        //先查询所有动态获取会议Id
        List<String> meetingIds = meetingDynamicService.selectMeetingDynamic(pageEntity, map);
        map = new HashMap<>();
        if (StringUtils.isNotBlank(search)) {
            map.put("search", search);
        }
        List<MeetingDynamicVo> list = new ArrayList<>();
        if (null != meetingIds) {
            for (String meetingId : meetingIds) {
                map.put("meetingId", meetingId);
                /*//根据会议Id查询会议预订人  修改预订人不参会看不到预定和修改的会议动态问题
                YunmeetingConference yunmeetingConference1 = meetingReserveService.findUserParticipantsStatus(map);
                if(null!=yunmeetingConference1){
                    map.put("reservationPersonId",yunmeetingConference1.getReservationPersonId());
                }*/
                List<DynamicVo> dynamicVos = meetingDynamicService.selectRecentMeetingDynamic(map);
                if (null != dynamicVos) {
                    MeetingDynamicVo meetingDynamicVo = new MeetingDynamicVo();
                    boolean b1 = false;
                    //循环动态信息
                    for(DynamicVo dynamicVo:dynamicVos){
                        String dynamicId = dynamicVo.getId();
                        //查询动态已读信息
                        YunmeetingDynamicsClickRecord yunmeetingDynamicsClickRecord = meetingDynamicService.selectDynamicClickInfo(dynamicId, userId);
                        if(null==yunmeetingDynamicsClickRecord){
                            //增加已读动态信息
                            yunmeetingDynamicsClickRecord = new YunmeetingDynamicsClickRecord();
                            yunmeetingDynamicsClickRecord.setDynamicsId(dynamicId);
                            yunmeetingDynamicsClickRecord.setParticipantsId(userId);
                            yunmeetingDynamicsClickRecord.setCreateTime(new Date());
                            yunmeetingDynamicsClickRecord.setId(CreateUUIdUtil.Uuid());
                            meetingDynamicService.insertDynamicClickInfo(yunmeetingDynamicsClickRecord);
                        }
                        if(!b1){
                            if(StringUtils.isNotBlank(dynamicVo.getDateState())) {
                                if (dynamicVo.getDateState().equals("1")) {
                                    b1 = true;
                                }
                            }
                        }
                    }
                    meetingDynamicVo.setDateState("0");
                    if(b1){
                        meetingDynamicVo.setDateState("1");
                    }
                    meetingDynamicVo.setDynamics(dynamicVos);
                    meetingDynamicVo.setId(meetingId);
                    //根据会议Id查询会议
                    YunmeetingConference yunmeetingConference = meetingReserveService.selectMeetingByMeetingId(meetingId);
                    if(null!=yunmeetingConference) {
                        String state = yunmeetingConference.getState();
                        if((!state.equals("0")&&!state.equals("1"))||userId.equals(yunmeetingConference.getReservationPersonId())) {
                            meetingDynamicVo.setTitle(yunmeetingConference.getConferenceName());
                            meetingDynamicVo.setStart(yunmeetingConference.getTakeStartDate().getTime());
                            meetingDynamicVo.setEnd(yunmeetingConference.getTakeEndDate().getTime());
                            meetingDynamicVo.setStatus(yunmeetingConference.getIsPublic());
                            meetingDynamicVo.setLocation("");
                            list.add(meetingDynamicVo);
                        }
                    }
                }
            }
            if(list.size()>0){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), list);
            }else{
                if(flag){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "近期没有会议动态", list);
                }
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(),list, BusinessExceptionStatusEnum.DataNull.getCode());
    }

    /**
     * 方法名：dateFormat</br>
     * 描述：格式化时间（当前时间减七天）</br>
     */
    private static Map<String, Object> dateFormat(Integer days, String all) {
        Map<String, Object> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        if (StringUtils.isNotBlank(all)) {
            map.put("startTime", new Date());
        } else {
            map.put("startTime", calendar.getTime());
            map.put("endTime", new Date());
        }
        return map;
    }

    @RequestMapping(value = "/viewmoredynamicpage", method = RequestMethod.GET)
    public String viewMoreDynamicPage() {
        return "/meeting_res/dynamics";
    }
}
