package com.thinkwin.yuncm.service;

import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.vo.meetingVo.MeetingStatisticsVo;

import java.util.Date;
import java.util.List;

/**
 * 类名: MeetingStatisticsService </br>
 * 描述: 会议统计接口</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/7/20 </br>
 */
public interface MeetingStatisticsService {
    /**
     * 方法名：selectMeetingStatics</br>
     * 描述：查询会议统计和个人统计总接口</br>
     * 参数：</br>
     * 返回值：</br>
     */
    MeetingStatisticsVo selectMeetingStatics(String userId,String tenantId);

    /**
     * 获取百分比
     * @param times
     * @return
     */
    public String formatPercent(double times);
}
