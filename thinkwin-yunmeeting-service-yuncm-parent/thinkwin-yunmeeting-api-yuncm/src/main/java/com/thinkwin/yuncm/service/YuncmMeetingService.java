package com.thinkwin.yuncm.service;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会议模块接口
 * 开发人员:daipengkai
 * 创建时间:2017/7/10
 */
public interface YuncmMeetingService {

    /**
     * 点击页面进入会议室列表
     *
     * @return 返回List
     */
    PageInfo<YuncmMeetingRoom> selectAllListYuncmMeetingRoom(BasePageEntity page);

    /**
     * 查看单个会议室 会议室详情
     *
     * @return
     */
    YuncmMeetingRoom selectByidYuncmMeetingRoom(String id);

    /**
     * 查看全部区域 无区域创建默认区域
     *
     * @return
     */
    List<YuncmRoomArea> selectAllListYuncmRoomArea();

    /**
     * 根据区域ID查看单条区域
     * @return
     */
    YuncmRoomArea selectByidYuncmRoomArea(String id);

    /**
     * 创建区域
     *
     * @param name      区域名称
     * @param pid       区域上一级ID
     * @param userId    用户ID
     * @param isDefault 是否是默认区域
     * @return
     */
    YuncmRoomArea insertYuncmRoomArea(String name, String userId, String isDefault);

    /**
     * 区域重命名
     *
     * @param areaId 区域ID
     * @param name   区域名称
     * @param userId 用户id
     * @return
     */
    YuncmRoomArea updateYuncmRoomArea(String areaId, String name, String userId);

    /**
     * 查看会议室设备
     *
     * @param roomId
     * @return
     */
     Map<String,Object> findMeetingEquipment(YuncmMeetingRoom room);

    /**
     * 查看会议室设备
     *
     * @return
     */
    List<YuncmDeviceService> selectYuncmDeviceService();

    /**
     * 下载文件用查看全部区域
     *
     * @return
     */
    List<YuncmRoomArea> selectAllYuncmRoomArea();

    /**
     * 查看区域会议室 全部
     *
     * @param areaId
     * @return
     */
    List<YuncmMeetingRoom> selectAllMeetingRoom(String areaId);

    /**
     * 会议室下往上拖动排序
     *
     * @param areaId        区域ID
     * @param nowRoomId     当前拖动的会议室ID
     * @param purposeRoomId 需要替换的会议室ID
     * @return
     */
    boolean updateMeetingRoomSorting(String areaId, String nowRoomId, String purposeRoomId, String userId);

    /**
     * 会议室下往上拖动排序
     *
     * @param nowRoomId     当前拖动的会议室ID
     * @param purposeRoomId 需要替换的会议室ID
     * @return
     */
    boolean updateMeetingRoomAreaSorting(String nowRoomId, String purposeRoomId, String userId, String moveType);


   /* *//**
     * 会议室上往下拖动排序
     * @param areaId 区域ID
     * @param nowRoomId 当前拖动的会议室ID
     * @param purposeRoomId 需要替换的会议室ID
     * @return
     *//*
    boolean updateMeetingRoomSortingDown(String areaId,String nowRoomId,String purposeRoomId);
*/

    /**
     * 查看会议室预定
     *
     * @return
     */
    YuncmRoomReserveConf selectYuncmRoomReserveConf();

    /**
     * 获取会议室预定权限
     * @param meetingId
     * @return
     */
    String selectMeetingRoomReservation(String meetingId);


    /**
     * 查看单个会议室 会议室详情//用于删除图片
     *
     * @return
     */
    YuncmMeetingRoom selectByYuncmMeetingRoom(String id);

    /**
     * 查看租户所创建的会议室
     * @return
     */
    List<YuncmMeetingRoom> selectTenantMeetingRoomCount();

    /**
     * 查询某个时间段某个会议室的占用情况
     * @param meetingRoomId 会议室Id
     * @param begin 开始时间
     * @param end 结束时间
     * @param state 状态
     * @return
     */
    Boolean findMeetingRoomTakeInfo(String meetingRoomId,String begin,String end,String state);
    /**
     * 查询某个时间段某个会议室的占用情况
     * 会议修改时调用
     * @return
     */
    Boolean findMeetingRoomTakeInfo(Map<String,Object> map);
    /**
     * 查询某个用户对某个会议室的权限
     * @param meetingRoomId
     * @param userId
     * @return
     */
    Boolean findMeetingRoomAndUser(String meetingRoomId,String userId);

    /**
     * 查询某个角色对某个会议室的权限
     * @param meetingRoomId
     * @param roleId
     * @return
     */
    Boolean findMeetingRoomAndRole(String meetingRoomId,String roleId);

    /**
     * 判断该会议室预订权限是否全体人员
     * @param meetingRoomId
     * @return
     */
    Boolean findMeetingRoomId(String meetingRoomId);

    /**
     * 查询会议室
     * @param meetingRoomId
     * @return
     */
    YuncmMeetingRoom findMeetingRoom(String meetingRoomId);


    /**
     * 查询某个时间段某个会议室的占用情况
     * @param meetingRoomId 会议室Id
     * @param begin 开始时间
     * @param end 结束时间
     * @param state 状态
     * @return
     */
    Boolean findMeetingRoomTakeInfo(String meetingRoomId,String begin,String end);

    /**
     * 获取用户回复数量
     * @return
     */
    public Integer getUserReply();
    /**
     * 获取用户回复数量(根据会议id查询)
     * @return
     */
    public Integer getUserReply(List<String> meetingIds);

    /**
     * 获取用户签到数量
     * @return
     */
    public Integer getUserSign();
    /**
     * 获取用户签到数量(根据会议id查询)
     * @return
     */
    public Integer getUserSign(List<String> meetingIds);

   /* *//**
     * 获取租户下全部会议室
     * @return
     *//*
    List<YuncmMeetingRoom> selectYuncmMeetingRoomAll();*/

}
