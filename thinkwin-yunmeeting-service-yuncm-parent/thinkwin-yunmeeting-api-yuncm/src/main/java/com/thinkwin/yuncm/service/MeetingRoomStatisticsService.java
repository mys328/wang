package com.thinkwin.yuncm.service;

import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.YuncmMeetingRoom;
import com.thinkwin.common.vo.meetingVo.MeetingRoomStatisticsVo;
import com.thinkwin.common.vo.meetingVo.MeetingStatisticsDetails;
import com.thinkwin.common.vo.meetingVo.RoomStatisticsVo;

import java.util.List;
import java.util.Map;

/**
 * 会议室统计接口
 *  创建时间：  2017/10/24
 */
public interface MeetingRoomStatisticsService {
    /**
     * 首页会议室统计接口
     * @param staTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public Map<String,Object> selectMeetingRoomStatistics(String staTime,String endTime);

    /**
     * 获取会议总时长
     * @param statisticsVoList
     * @return
     */
    public long getMeetingTotalTime(List<RoomStatisticsVo> statisticsVoList);

    /**
     * 会议室统计详情
     * @param staTime 开始时间
     * @param endTime 结束时间
     * @param areaId 区域id
     * @param searchKey 搜索关键字
     * @param isAudit 是否审核 0 ，不需要 1， 需要
     * @return
     */
    Map<String,Object> selectMeetingRoomStatisticsDetails(String staTime,String endTime,String areaId,String searchKey,String isAudit,String type,BasePageEntity page);

    /**
     * 点击会议数量获取会议详情
     * @param roomId 会议室id
     * @param type 0:查看审核未通过的会议，1：查看会议
     * @param staTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    List<MeetingStatisticsDetails> selectMeetingDetails(String roomId,String type,String staTime, String endTime,BasePageEntity page);






}
