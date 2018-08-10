package com.thinkwin.auth.service;

import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.vo.statisticalAnalysisVo.ConferenceNumInfoVo;
import com.thinkwin.common.vo.statisticalAnalysisVo.UserStatisticalDataVo;

import java.util.List;
import java.util.Map;

/**
 * User: yinchunlei
 * Date: 2017/10/24.
 * Company: thinkwin
 * 按人员处理统计分析sevice层
 */
public interface UserStatisticalAnalysisService {

    /**
     * 根据开始时间和结束时间统计人员总数和未参会人数功能
     * @param startTime
     * @param endTime
     * @return
     */
    public Map getUserTotalNumAndNumberOfAbsentParticipants(String startTime,String endTime);

    /**
     * 获取人员统计分析页面数据功能接口(与搜索功能共用该接口)
     * @param orgId
     * @param queryCriteria
     * @return
     */
    public List<UserStatisticalDataVo> getUserStatisticalData(List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate, String orgId, String queryCriteria,String tenantId);

    /**
     * 获取会议数量信息功能
     * @param userId
     * @param byMeetingRoomIdAndMeetingtakeStartDate
     * @return
     */
    public List<ConferenceNumInfoVo> getConferenceNumInfo(String userId, List<YunmeetingConference> byMeetingRoomIdAndMeetingtakeStartDate);
}
