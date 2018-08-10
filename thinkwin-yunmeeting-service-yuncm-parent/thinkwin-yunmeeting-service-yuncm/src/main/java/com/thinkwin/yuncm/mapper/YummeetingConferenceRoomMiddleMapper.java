package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.YummeetingConferenceRoomMiddle;
import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.vo.meetingVo.RoomStatisticsVo;
import com.thinkwin.common.vo.meetingVo.YummeetingConferenceRoomMiddleVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface YummeetingConferenceRoomMiddleMapper extends Mapper<YummeetingConferenceRoomMiddle> {


    /**
     * 按时间段查看会议
     * @param map
     * @return
     */
    List<YummeetingConferenceRoomMiddle> selectTimeYunmeetingConference(Map map);


    /**
     *  查看会议室使用多的
     * @return
     */
    List<YummeetingConferenceRoomMiddleVo> selectMeetingRoomUseMany();

    /**
     * 获取任意时间段的所有会议数量
     * @param map
     * @return
     */
    List<RoomStatisticsVo> selectMeetingRoomStatistics(Map map);


}