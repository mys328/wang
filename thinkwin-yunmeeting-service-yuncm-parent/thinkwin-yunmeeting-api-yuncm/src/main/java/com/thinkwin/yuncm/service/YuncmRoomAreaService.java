package com.thinkwin.yuncm.service;

import com.thinkwin.common.model.db.*;

import java.util.List;

/**
 * 会议室接口
 * 开发人员:daipengkai
 * 创建时间:2017/7/11
 */
public interface YuncmRoomAreaService {

    /**
     * 创建会议室
     *
     * @param name          会议室名称
     * @param areaId        所在区域id
     * @param persionNumber 会议室可容纳人数
     * @param location      会议室所在位置
     * @param deviceService 会议室设备，用,号隔开
     * @param isAudit       预定审核，0:无须审核 ；1:需要审核
     * @param roleId        预定权限，0，全部人员，1：会议室管理员2：预定专员。多参数用逗号隔开
     * @param imageUrl      会议室图片 id
     * @return
     */
    public boolean insertYuncmMeetingRoom(YuncmMeetingRoom room, String roleId, String userId);

    /**
     * 方法名：selectMeetingRoomRole</br>
     * 描述：查询会议室预定权限根据会议Id</br>
     * 参数：roomId 会议室Id</br>
     * 返回值：</br>
     */
    List<String> selectMeetingRoomRole(String roomId);

    /**
     * 修改会议室
     *
     * @param name          会议室名称
     * @param areaId        所在区域id
     * @param persionNumber 会议室可容纳人数
     * @param location      会议室所在位置
     * @param deviceService 会议室设备，用,号隔开
     * @param isAudit       预定审核，0:无须审核 ；1:需要审核
     * @param roleId        预定权限，0，全部人员，1：会议室管理员2：预定专员。多参数用逗号隔开
     * @param imageUrl      会议室图片 id
     * @return
     */
    public boolean updateYuncmMeetingRoom(String id, String name, String areaId, String persionNumber, String location, String deviceService, String isAudit, String roleId, String imageUrl, String userId);


    /**
     * 会议室停用
     *
     * @param id         会议室ID
     * @param startTime  临时停用开始时间
     * @param endTime    临时停用结束时间
     * @param state      1－永久关闭;2－正常开启;;3－临时关闭
     * @param operReason 停用原因
     * @return
     */
    public boolean updateStopYuncmMeetingRoom(String id, String startTime, String endTime, String state, String operReason, String userId);

    /**
     * 会议室启用
     *
     * @param id 会议室ID
     * @return
     */
    public boolean updateStartYuncmMeetingRoom(String id, String userId);

    /**
     * 会议室预定设置
     *
     * @param reserveTimeStart 可预订时间开始
     * @param reserveTimeEnd   可预定时间结束
     * @param meetingMaximum   单次会议最大时长，0为不限制，单位小时
     * @param meetingMinimum   单次会议最小时长，0为不限制，单位分钟
     * @param reserveCycle     会议室可预订周期，单位天
     * @param qrDuration   二维码有效时长
     * @return
     */
    public YuncmRoomReserveConf updateMeetingRoomReserve(String reserveTimeStart, String reserveTimeEnd, String meetingMaximum, String meetingMinimum, String reserveCycle, String userId,String qrDuration,String signSet);

    /**
     * 方法名：selectMCRoomMiddleByMRoomId</br>
     * 描述：根据会议室Id查询会议和会议室中间表</br>
     * 参数：meetingRoomId  会议室Id</br>
     * 返回值：List<YummeetingConferenceRoomMiddle> 返回list集合</br>
     */
    public List<YummeetingConferenceRoomMiddle> selectMCRoomMiddleByMRoomId(String meetingRoomId);

    /**
     * 方法名：selectMCRoomMiddleByMId</br>
     * 描述：根据会议Id查询会议和会议室中间表</br>
     * 参数：meetingId  会议Id</br>
     * 返回值：YummeetingConferenceRoomMiddle 返回对象</br>
     */
    public YummeetingConferenceRoomMiddle selectMCRoomMiddleByMId(String meetingId);

    /**
     * 方法名：selectMCRoomMiddleByMId</br>
     * 描述：根据会议Id和会议室Id查询会议和会议室中间表</br>
     * 参数：meetingId  会议Id</br>
     * 参数：roomId  会议室Id</br>
     * 返回值：YummeetingConferenceRoomMiddle 返回对象</br>
     */
    public YummeetingConferenceRoomMiddle selectMCRoomMiddleByMId(String meetingId,String roomId);

    /**
     * 方法名：deleteYuncmMeetingRoom</br>
     * 描述：根据会议室Id删除会议室</br>
     * 参数：meetingRoomId 会议室id deleteState 删除状态 物理时删除该参数为空</br>
     * 返回值：boolean</br>
     */
    public boolean deleteYuncmMeetingRoom(String meetingRoomId, String deleteState);

    /**
     * 方法名：deleteMCRoomMiddleByMId</br>
     * 描述：根据会议Id删除会议和会议室中间表</br>
     * 参数：meetingId 会议id </br>
     * 返回值：boolean</br>
     */
    public boolean deleteMCRoomMiddleByMId(String meetingId);

    public boolean deleteYuncmRooArea(String areaId, boolean success);

    public List<YuncmRoomArea> getRoomAreaAll();

    public List<YuncmDeviceService> getDeviceAll();

    /**
     * 方法名：selectRoomAreaByAreaId</br>
     * 描述：根据区域Id查询会议室区域</br>
     * 参数：areaId 区域Id</br>
     * 返回值：</br>
     */
    public YuncmRoomArea selectRoomAreaByAreaId(String areaId);

    /**
     * 方法名：selectRoomReserveConf</br>
     * 描述：查询会议是设置表</br>
     * 参数：</br>
     * 返回值：</br>
     */
    public YuncmRoomReserveConf selectRoomReserveConf();
}
