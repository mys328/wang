package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.YunmeetingConference;
import com.thinkwin.common.vo.meetingVo.RoomStatisticsVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface YunmeetingConferenceMapper extends Mapper<YunmeetingConference> {

    /**
     * 方法名：selectCurrentMonthAllMeeting</br>
     * 描述：查询本月所有会议</br>
     * 参数：userId 用户Id不为空则查询的是该用户预定的会议</br>
     * 参数：time  当前月不为空查询当前月的会议为空查询所有会议</br>
     * 返回值：</br>
     */
    List<YunmeetingConference> selectCurrentMonthAllMeeting(Map map);

    /**
     * 查看未来的关于我的会议
     *
     * @param map
     * @return
     */
    List<YunmeetingConference> selectFutureYunmeetingConference(Map map);


    /**
     * 条件搜索我的会议
     *
     * @param map
     * @return
     */
    List<YunmeetingConference> selectSearchYunmeetingConference(Map map);

    /**
     * 查看我审核的 条件
     * @param map
     * @return
     */
    List<YunmeetingConference> selectAuditYunmeetingConference(Map map);

    /**
     * 查看我审核的搜索  条件
     * @param map
     * @return
     */
    List<YunmeetingConference> selectAuditSearchYunmeetingConference(Map map);

    /**
     * 查看会议室下的所有会议
     * @param roomId
     * @return
     */
    List<YunmeetingConference> selectRoomAllYunmeetingConference(String roomId);

    /**
     * 查询某时间段某个会议室的所有会议
     * @return 会议列表
     */
    List<YunmeetingConference> findByMeetingRoomIdAndMeetingtakeStartDate(Map map);

    /**
     * 查询七天前所有我参与的会议的信息
     * @return
     */
    List<YunmeetingConference> selectUnreadMessageSevenDaysAgo(Map map);


    /**
     * 查看当前会议室占用信息
     * @return
     */
    List<YunmeetingConference> selectCurrentMeetingRoomOccupyInfo(Map map);

    /**
     * 微信端-我组织的会议
     * @param map
     * @return
     */
    List<YunmeetingConference> organizeMeeting(Map map);

    /**
     * 微信端-我参与的会议
     * @param map
     * @return
     */
    List<YunmeetingConference> aboutMeeting(Map map);


    List<YunmeetingConference> findByMyMeeting(Map map);

    /**
     * 获取某个时间会议室下的会议
     * @param map
     * @return
     */
    List<YunmeetingConference>  selectSectionTimeRoomYunmeetingConference(Map map);

    List<YunmeetingConference>  h5MyYunMeetingKindCount1(Map map);

    List<YunmeetingConference>  h5MyYunMeetingKindCount2(Map map);

    /**
     * 方法名：selectMeeting</br>
     * 描述：查询会议  根据用户Id和会议Id不为空条件查询</br>
     */
    YunmeetingConference selectMeeting(Map map);

    /**
     * 查询某个时间段某个会议室的有多少会议
     * @param map
     * @return
     */
    List<YunmeetingConference> findMeetingTakeInfo(Map map);

    /**
     * 根据会议室id批量查询会议
     * @param map
     * @return
     */
    List<RoomStatisticsVo> selectMeetingAndUserName(Map map);


    /**
     * 查询会议列表全部时的会议
     * @param map
     * @return
     */
    List<YunmeetingConference> selectAllMeetingScreen(Map map);

    /**
     * 根据日期的起始时间获取相关会议
     * @param map
     * @return
     */
    List<YunmeetingConference> findMeetingConferenceByTime(Map map);
    /**
     * 根据日期的起始时间获取相关会议
     * @param map
     * @return
     */
    List<YunmeetingConference> findMeetingConferenceByTimeNew(Map map);

    /**
     * 查询某人在某个时间段会议占用状态
     * @param map
     * @return
     */
    List<YunmeetingConference> findByUserByOccupy(Map map);

    YunmeetingConference findUserParticipantsStatus(Map map);

    /**
     * 获取当前会议
     * @param map
     * @return
     */
    public List<YunmeetingConference>  selectCurrentConference(Map map);

    /**
     * 获取下一个会议
     * @param map
     * @return
     */
    public List<YunmeetingConference> selectNextConference(Map map);


    /**
     * 获取当天的会议
     * @param map
     * @return
     */
    public List<YunmeetingConference> selectSameDayConference(Map map);

    public YunmeetingConference selectMeetingByMeetingIdAndUserId(Map<String, Object> map);

}