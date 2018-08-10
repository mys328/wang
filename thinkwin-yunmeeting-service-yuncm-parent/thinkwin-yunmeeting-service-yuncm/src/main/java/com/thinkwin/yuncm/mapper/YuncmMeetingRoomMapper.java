package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.YuncmMeetingRoom;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface YuncmMeetingRoomMapper extends Mapper<YuncmMeetingRoom> {

    /**
     * 全部区域搜索
     * @param searchKey 搜索关键字
     * @return
     */
    List<YuncmMeetingRoom> selectSearchMeetingRoom(String searchKey);

    /**
     * 区域搜索
     * @param searchKey 搜索关键字
     * @param areaId 区域ID
     * @return
     */
    List<YuncmMeetingRoom> selectSearchAreaMeetingRoom(Map map);

    /**
     * 根据会议id查看会议室所在地
     * @param id 会议id
     * @return
     */
    List<YuncmMeetingRoom> selectYuncmMeetingRoomYunmeetingConference(String id);

    /**
     * 预定会议 按条件筛选会议室 不带时间
     * @param map
     * @return
     */
    List<YuncmMeetingRoom> selectScreeningMeetingRoomYunmeeting(Map map);

    /**
     * 查询某个时间段某个会议室的占用情况
     * @param map
     * @return
     */
    List<YuncmMeetingRoom> findMeetingRoomTakeInfo(Map map);


    List<YuncmMeetingRoom> findMeetingRoomAndRole(Map map);

    /**
     * 查询会议室
     * @param map
     * @return
     */
    YuncmMeetingRoom findMeetingRoom(Map map);

    /**
     * 根据条件查询会议室
     * @param map
     * @return
     */
    List<YuncmMeetingRoom> selectConditionMeetingRoom(Map map);

}