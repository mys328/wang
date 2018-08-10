package com.thinkwin.yuncm.service;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.YuncmMeetingRoom;
import com.thinkwin.common.model.db.YuncmRoomArea;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
  *
  *  会议室搜索列表查看
  *  开发人员:daipengkai
  *  创建时间:2017/7/12
  *
  */
public interface SearchMeetingRoomService {

    /**
     *  搜索会议室
     * @param searchKey 搜索关键字
     * @param page  当前页
     * @param countPage 每页个数
     * @return
     */
    PageInfo<YuncmMeetingRoom> selectSearchMeetingRoom(String searchKey, BasePageEntity page);


    /**
     *  区域搜索会议室
     * @param searchKey 搜索关键字
     * @param areaId 区域id
     * @param page  当前页
     * @param countPage 每页个数
     * @return
     */
    PageInfo<YuncmMeetingRoom> selectSearchAreaMeetingRoom(String searchKey,String areaId,BasePageEntity page);

    /**
     *  区域搜索会议室修改后1.1.1.2 搜索后区域下的会议室不显示
     * @param searchKey 搜索关键字
     * @param areaId 区域id
     * @param page  当前页
     * @param countPage 每页个数
     * @return
     */
    PageInfo<YuncmMeetingRoom> selectSearchAreaMeetingRoomChange(String searchKey,String areaId,BasePageEntity page);



    /**
     *  搜索会议室区域
     * @param searchKey 搜索关键字
     * @param rooms 搜索后的会议室
     * @return
     */
    List<YuncmRoomArea> selectSearchYuncmRoomArea(String searchKey);

    /**
     *  搜索会议室区域修改后1.1.1.2 搜索后区域下的会议室不显示
     * @param searchKey 搜索关键字
     * @param rooms 搜索后的会议室
     * @return
     */
    Map<String,Object> selectSearchYuncmRoomAreaChange(String searchKey);


    /**
     *  查看全部会议室
     * @param page  当前页
     * @param countPage 每页个数
     * @return
     */
    PageInfo<YuncmMeetingRoom> selectAllMeetingRoom(BasePageEntity page);


    /**
     *  查看区域会议室
     * @param areaId 区域id
     * @param page  当前页
     * @param countPage 每页个数
     * @return
     */
    PageInfo<YuncmMeetingRoom> selectAreaMeetingRoom(String areaId,BasePageEntity page);


    /**
     * 按条案件筛选会议室
     * @param userId
     * @param personNum
     * @param deviceService
     * @param staTime
     * @param endTime
     * @param areaId
     * @param is_audit
     * @return
     */
    List<YuncmMeetingRoom> selectMeetingScreenYuncmMeetingRoom(String userId, String personNum, String deviceService, String staTime, String endTime, String areaId, String isAudit);


    /**
     * 筛选使用次数最多的会议室
     * @return
     */
    List<YuncmMeetingRoom> selectUseFrequencyMany();

    /**
     * 筛选全部会议室
     * @param deviceService 设备名称
     * @param staTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    List<YuncmMeetingRoom> selectScreenAllMeetingRoom( String deviceService, String staTime, String endTime);

    /**
     * 获取点击的常用会议室信息
     * @param roomId
     * @return
     */
    List<YuncmMeetingRoom> selectClickCommonMeetingRoom(String roomId);


    /**
     * 按条案件筛选会议室H5用
     * @param userId
     * @param number
     * @param devices
     * @param startTime
     * @param endTime
     * @param area
     * @param isAudit
     * @return
     */
    List<YuncmMeetingRoom> selectH5MeetingScreenYuncmMeetingRoom(String userId, String number, String devices, String startTime, String endTime, String area,String isAudit,String content,String type);


    /**
     * 获取会议室数量
     * @return
     */
    public List<YuncmMeetingRoom> selectMeetingRoomNum();
}
