package com.thinkwin.web.controller;

import com.thinkwin.TenantUserVo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.vo.meetingVo.MeetingStatisticsVo;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.MeetingStatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 类名: HomePageController </br>
 * 描述: 首页controller</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/7/28 </br>
 */
@Controller
@RequestMapping("/home")
public class HomePageController {
    @Resource
    MeetingStatisticsService meetingStatisticsService;


    /**
     * 方法名：meetingRoomStatistics</br>
     * 描述：查询会议统计和个人统计接口</br>
     * 参数：[]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/meetingstatistics", method = RequestMethod.POST)
    @ResponseBody
    public Object meetingStatistics() {
        //获取用户Id
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        String tenantId = userInfo.getTenantId();
        MeetingStatisticsVo meetingStatisticsVo = meetingStatisticsService.selectMeetingStatics(userId,tenantId);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), meetingStatisticsVo);
    }
}
