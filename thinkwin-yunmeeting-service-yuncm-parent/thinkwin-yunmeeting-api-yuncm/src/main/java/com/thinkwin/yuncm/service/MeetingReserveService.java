package com.thinkwin.yuncm.service;

import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.vo.meetingVo.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类名: MeetingReserveService </br>
 * 描述: 会议预定接口</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/7/31 </br>
 */
public interface MeetingReserveService {

    /**
     * 方法名：selectMeetingByMeetingId</br>
     * 描述：根据会议Id查询会议</br>
     * 参数：meetingId 会议Id</br>
     * 返回值：YunmeetingConference 会议对象</br>
     */
    YunmeetingConference selectMeetingByMeetingId(String meetingId);

    /**
     * 方法名：selectMeeting</br>
     * 描述：查询会议 根据预定者Id 或者组织者Id</br>
     * 参数：rPersonId 预定人Id </br>
     * 参数：organizerId 组织人Id </br>
     * 返回值：List<YunmeetingConference> 会议对象集合 </br>
     */
    List<YunmeetingConference> selectMeeting(String rPersonId, String organizerId);

    /**
     * 方法名：selectMeeting</br>
     * 描述：查询会议 根据会议表对象不为空的查询</br>
     * 参数：yunmeetingConference 对象</br>
     * 返回值：List<YunmeetingConference> 会议对象集合 </br>
     */
    List<YunmeetingConference> selectMeeting(YunmeetingConference yunmeetingConference);


    /**
     * 方法名：selectTenantTotalMeeting</br>
     * 描述：查询当月企业总会议信息 </br>
     * 参数：userId  用户Id （可以为空 不为空是查询该用户当月预定的会议）</br>
     * 返回值：List<YunmeetingConference> 企业当月总会议集合</br>
     */
    List<YunmeetingConference> selectTenantTotalMeeting(String userId, Date time);

    /**
     * 方法名：selectPersonJoinMeeting</br>
     * 描述：根据用户Id查询个人当月参加会议数(不为组织机构)</br>
     * 参数：conferenceId  会议Id </br>
     * 参数：userId  用户Id</br>
     * 参数：type  参会人员类型  0：只查用户 1：只查组织机构 2：会议分组参会</br>
     * 返回值：Integer 参加次数</br>
     */
    List<YunmeetingParticipantsInfo> selectPersonParticipateMeetingByUserNum(String conferenceId, String userId, String type);

    /**
     * 方法名：selectPersonJoinMeeting</br>
     * 描述：根据用户Id查询个人当月参加会议数（为组织机构）</br>
     * 参数：participateInfoId  参与会议信息表Id</br>
     * 参数：userId  用户Id</br>
     * 返回值：Integer 参加次数</br>
     */
    List<YunmeetingConferenceUserInfo> selectPersonParticipateMeetingByOrgNum(String participateInfoId, String userId);

    /**
     * 方法名：selectParticipateMeetingByMeetingId</br>
     * 描述：根据会议Id查询参会人员表信息</br>
     * 参数：meetingId 会议Id</br>
     * 返回值：</br>
     */
    List<YunmeetingParticipantsInfo> selectParticipateMeetingByMeetingId(Map<String, Object> map);

    /**
     * 方法名：insertMeeting</br>
     * 描述：增加会议表（预定会议）</br>
     * 参数：yunmeetingConference 会议表实体</br>
     * 返回值：</br>
     */
    boolean insertMeeting(YunmeetingConference yunmeetingConference);

    /**
     * 方法名：insertMeetingMessageInform</br>
     * 描述：增加会议消息通知表</br>
     * 参数：yunmeetingMessageInform 消息通知表实体表实体</br>
     * 返回值：</br>
     */
    boolean insertMeetingMessageInform(YunmeetingMessageInform yunmeetingMessageInform);

    /**
     * 方法名：insertYummeetingConferenceRoomMiddle</br>
     * 描述：增加会议和会议室中间表</br>
     * 参数：yummeetingConferenceRoomMiddle 会议和会议室中间表实体</br>
     * 返回值：</br>
     */
    boolean insertYummeetingConferenceRoomMiddle(YummeetingConferenceRoomMiddle yummeetingConferenceRoomMiddle);

    /**
     * 方法名：updateMeeting</br>
     * 描述：修改会议表</br>
     * 参数：yunmeetingConference 会议表实体</br>
     * 返回值：</br>
     */
    boolean updateMeeting(YunmeetingConference yunmeetingConference);

    /**
     * 方法名：updateMeetingMessageInform</br>
     * 描述：修改会议消息通知表</br>
     * 参数：yunmeetingMessageInform 消息通知表实体表实体</br>
     * 返回值：</br>
     */
    boolean updateMeetingMessageInform(YunmeetingMessageInform yunmeetingMessageInform);
    /**
     * 方法名：deleteMeetingMessageInform</br>
     * 描述：删除会议消息通知表根据会议Id</br>
     * 参数：meetingId 会议Id</br>
     * 返回值：</br>
     */
    boolean deleteMeetingMessageInform(String meetingId);

    /**
     * 方法名：insertYunmeetingParticipantsInfo</br>
     * 描述：增加参会人员信息表</br>
     * 参数：yunmeetingParticipantsInfo 参会人员实体</br>
     * 返回值：</br>
     */
    boolean insertYunmeetingParticipantsInfo(YunmeetingParticipantsInfo yunmeetingParticipantsInfo);

    /**
     * 方法名：insertYunmeetingConferenceUserInfo</br>
     * 描述：增加参会人员组织机构信息表</br>
     * 参数：yunmeetingConferenceUserInfo 参会组织人员信息实体</br>
     * 返回值：</br>
     */
    boolean insertYunmeetingConferenceUserInfo(YunmeetingConferenceUserInfo yunmeetingConferenceUserInfo);

    /**
     * 方法名：insertMeetingPeople</br>
     * 描述：增加会议人员</br>
     * 参数：peopleIds 参会人员Id </br>
     * 参数：meetingId 会议Id </br>
     * * 参数：userId 当前用户Id </br>
     * 返回值：</br>
     */
    boolean insertMeetingPeople(List<MeetingParticipantsVo> peopleIds, String meetingId, String userId);

    /**
     * 方法名：deleteMeetingPeople</br>
     * 描述：根据会议Id删除所有参会人员</br>
     * 参数：</br>
     * 返回值：</br>
     */
    boolean deleteMeetingPeople(String meetingId);

    /**
     * 方法名：selectDayViewByTime</br>
     * 描述：根据时间查询会议日视图</br>
     * 参数：time 当月时间（格式：2017-08）</br>
     * 返回值：返回时间List</br>
     */
    List<YunmeetingConference> selectMeetingDayViewByTime(Map<String, Object> map);

    /**
     * 方法名：selectSetTimeMeeting</br>
     * 描述：根据时间查询指定日期会议信息</br>
     * 参数：time 当月时间（格式：2017-08-07）</br>
     * 返回值：返回时间List</br>
     */
    List<YunmeetingConference> selectSetTimeMeeting(Map<String, Object> map);

    /**
     * 方法名：selectMeetingRoomByMeetingId</br>
     * 描述：根据会议Id查询会议室信息</br>
     * 参数：meetingId 会议Id</br>
     * 返回值：</br>
     */
    List<YuncmMeetingRoom> selectMeetingRoomByMeetingId(String meetingId);

    /**
     * 方法名：selectMeetingMessageeInform</br>
     * 描述：查询会议消息通知根据会议Id</br>
     * 参数：meetingId 会议Id</br>
     * 返回值：</br>
     */
    List<YunmeetingMessageInform> selectMeetingMessageeInform(String meetingId);

    /**
     * 方法名：selectMeetingDetails</br>
     * 描述：查询会议详情</br>
     * 参数：meetingId 会议Id</br>
     * 返回值：</br>
     */
    MeetingDetailsVo selectMeetingDetails(String meetingId);

    /**
     * 方法名：selectParticipantsReply</br>
     * 描述：查询会议回复表根据会议Id和用户Id</br>
     * 参数：meetingId 会议Id</br>
     * 参数：userId 用户Id
     * 返回值：</br>
     */
    YunmeetingParticipantsReply selectParticipantsReply(String meetingId, String userId);

    /**
     * 方法名：selectParticipantsReply</br>
     * 描述：查询会议回复表根据会议Id和用户Id</br>
     * 参数：meetingId 会议Id</br>
     * 参数：userId 用户Id
     * 返回值：</br>
     */
    List<YunmeetingParticipantsReply> selectParticipantsReply(String meetingId);

    /**
     * 方法名：selectAllParticipantByMeetingId</br>
     * 描述：根据会议Id查询所有参会人</br>
     * 参数：meetingId 会议Id</br>
     * 返回值：</br>
     */
    List<PersonsVo> selectAllParticipantByMeetingId(String meetingId);

    /**
     * 方法名：insertMeetingAuditInfo</br>
     * 描述：增加会议审核接口</br>
     * 参数：yunmeetingConferenceAudit 会议审核表实体</br>
     * 返回值：</br>
     */
    boolean insertMeetingAuditInfo(YunmeetingConferenceAudit yunmeetingConferenceAudit);

    /**
     * 方法名：insertMeetingAuditInfo</br>
     * 描述：增加会议审核接口</br>
     * 参数：yunmeetingConferenceAudit 会议审核表实体</br>
     * 返回值：</br>
     */
    boolean insertH5MeetingAuditInfo(YunmeetingConferenceAudit yunmeetingConferenceAudit, String userId);

    /**
     * 方法名：reserveMeeting</br>
     * 描述：预定会议中的增加会议和会议室中间表</br>
     * 参数：yummeetingConferenceRoomMiddle 会议和会议室中间表实体</br>
     * 参数：yunmeetingConference 会议实体
     * 返回值：</br>
     */
    YunmeetingConference reserveMeeting(YummeetingConferenceRoomMiddle yummeetingConferenceRoomMiddle, YunmeetingConference yunmeetingConference);


    /**
     * 查询某时间段某个会议室的所有会议
     * @param meetingRoomId 会议室Id
     * @param meetingBegin 开始时间
     * @param meetingEnd 结束时间
     * @return 会议列表
     */
    List<YunmeetingConference> findByMeetingRoomIdAndMeetingtakeStartDate(String meetingRoomId, String meetingBegin, String meetingEnd);

    /**
     * 方法名：insertMeetingReply</br>
     * 描述：增加会议回复表</br>
     * 参数：yunmeetingParticipantsReply 回复实体表</br>
     * 返回值：</br>
     */
    boolean insertMeetingReply(YunmeetingParticipantsReply yunmeetingParticipantsReply);

    /**
     * 方法名：updateMeetingReply</br>
     * 描述：修改会议回复表</br>
     * 参数：yunmeetingParticipantsReply 回复实体表</br>
     * 返回值：</br>
     */
    boolean updateMeetingReply(YunmeetingParticipantsReply yunmeetingParticipantsReply);

    /**
     * 方法名：insertMeetingSign</br>
     * 描述：增加会议签到信息</br>
     * 参数：yunmeetingMeetingSign 会议签到</br>
     * 返回值：</br>
     */
    boolean insertMeetingSign(YunmeetingMeetingSign yunmeetingMeetingSign);

    /**
     * 方法名：selectSignInfo</br>
     * 描述：查询签到信息根据会议Id和用户Id</br>
     * 参数：meetingId 会议id</br>
     * 参数：userId  用户Id
     * 返回值：</br>
     */
    YunmeetingMeetingSign selectSignInfo(String meetingId, String userId);
    /**
     * 方法名：selectSignInfo</br>
     * 描述：查询签到信息根据会议Id和用户Id</br>
     * 参数：meetingId 会议id</br>
     * 参数：userId  用户Id
     * 返回值：</br>
     */
    List<YunmeetingMeetingSign> selectSignInfo(String meetingId);

    /**
     * 方法名：selectRecentMeeting</br>
     * 描述：查询近期所有会议</br>
     * 参数：</br>
     * 返回值：</br>
     */
    List<RecentMeetingVo> selectRecentMeeting(BasePageEntity basePageEntity);

    /**
     * 方法名：selectAuthstrNum</br>
     * 描述：查询未审核用户人数</br>
     * 参数：</br>
     * 返回值：</br>
     */
    Integer selectAuthstrNum();

    /**
     * 查询七天前所有我参与的会议的信息
     * @return
     */
    public Integer selectUnreadMessage(String userId);

    /**
     * 方法名：updateMeetingStatus</br>
     * 描述：修改会议状态根据会议Id</br>
     * 参数：meetingId 会议Id</br>
     * 参数：status 状态</br>
     * 返回值：</br>
     */
    public void updateMeetingStatus(String meetingId, String status);

    /**
     * 微信端-我组织的会议
     * @param userId 用户id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageBegin 开始页码
     * @param step 每页数量
     * @return
     */
    public List<YunmeetingConference> organizeMeeting(String userId, String startTime, String endTime, String pageBegin, String step);

    /**
     * 微信端-我参与的会议
     * @param userId 用户id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageBegin 开始页码
     * @param step 每页数量
     * @return
     */
    public List<YunmeetingConference> aboutMeeting(String userId, String startTime, String endTime, String pageBegin, String step);

    /**
     * 查询与我相关的会议
     * @param userId
     * @param meetingName 会议名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param status 状态
     * @return
     */
    public List<YunmeetingConference> findByMyMeeting(String userId, String meetingName, String startTime, String endTime, String status);

    /**
     * 方法名：findMeetingReplay</br>
     * 描述：查询会议回复接口微信H5页面带搜索功能</br>
     * 参数：type 类型：0全部，1接受，2暂定，3谢绝，4未回复</br>
     * 参数：search 搜索内容 </br>
     * 参数：meetingId 会议Id </br>
     * 返回值：</br>
     */
    public Map<String,Object> findMeetingReplay(String type, String search, String meetingId, String reservationPersonId);

    /**
     * 方法名：findAllMeetingPerson</br>
     * 描述：查询所有参会人员 带搜索功能</br>
     * 参数：meetingId 会议Id</br>
     * 参数：search 搜索内容</br>
     * 返回值：</br>
     */
    public List<PersonsVo> findAllMeetingPerson(String meetingId, String search);
    /**
     * 方法名：countMeetingReplyNum</br>
     * 描述：计算参会回复人数</br>
     * 参数：</br>
     * 返回值：</br>
     */
    public List<Integer> countMeetingReplyNum(String meetingId, String reservationPersonId);

    /**
     * 方法名：findMeetingSign</br>
     * 描述：查询人员签到信息 带搜索</br>
     * 参数：meetingId 会议Id</br>
     * 参数：search 搜索内容</br>
     * 参数：type 根据类型显示</br>
     * 返回值：</br>
     */
    public List<MeetingSignVo> findMeetingSign(String type, String meetingId, String search);

    /**
     * 方法名：countMeetingReplyNum</br>
     * 描述：计算签到人数</br>
     * 参数：</br>
     * 返回值：</br>
     */
    public List<Integer> countMeetingSignNum(String meetingId);

    /**
     * 方法名：selectMeetingByUserId</br>
     * 描述：根据用户Id和会议Id查询会议信息</br>
     * 参数：userId 用户Id</br>
     * 参数：meetingId 会议Id</br>
     * 返回值：</br>
     */
    public YunmeetingConference selectMeetingByUserId(Map<String, Object> map);

    /**
     * 查询某个时间段某个会议室有多少待审核会议
     * 会议修改时调用
     * @return
     */
    List<YunmeetingConference> findMeetingTakeInfo(Map<String, Object> map);

    /**
     * 方法名：countMeetingReplyNum</br>
     * 描述：计算签到人数</br>
     * 参数：</br>
     * 返回值：</br>
     */
    public Map<String,Integer> getCountMeetingSignNum(String meetingId);

    /**
     * 根据起始时间获取会议列表
     * @param startTime
     * @param endTime
     * @return
     */
    public List<YunmeetingConference> findMeetingConferenceByTime(String startTime, String endTime);
    /**
     * 根据起始时间获取会议列表
     * @param startTime
     * @param endTime
     * @return
     */
    public List<YunmeetingConference> findMeetingConferenceByTimeNew(String startTime, String endTime);

    /**
     * 根据起始时间获取会议列表(新)
     * @param startTime
     * @param endTime
     * @return
     */
    public List<YunmeetingConference> findMeetingConferenceByTimeNew(String startTime, String endTime, List<String> ll);

    /**
     * 查询某人在某个时间段会议占用状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userId  用户Id
     * @return
     */
    public List<YunmeetingConference> findByUserByOccupy(String startTime, String endTime, String userId);

    public YunmeetingConference findUserParticipantsStatus(Map<String,Object> map);

    /**
     * 根据需求查询公司的所有会议列表
     * @return
     */
    public List<YunmeetingConference> selectMeetingConferenceNum(String status);

    //查询会议室可预订时间获取会议室预定时长
    public YuncmRoomReserveConf selectYuncmRoomReserveConf();

    /**
     * 根据会议id回去会议室id
     * @param meetingId
     * @return
     */
    public String  getMeetingIdAndRoomId(String meetingId);

    /**
     * 方法名：isShowSignButton</br>
     * 描述：查询是否显示签到按钮接口</br>
     * 参数：[meetingDetailsVo, userId]</br>
     * 返回值：com.thinkwin.common.vo.meetingVo.MeetingDetailsVo</br>
     */
    public MeetingDetailsVo isShowSignButton(MeetingDetailsVo meetingDetailsVo,String userId);

    /**
     * 方法名：delectSignInfoByMeetingId</br>
     * 描述：根据会议Id删除所有签到信息</br>
     * 参数：[meetingId]</br>
     * 返回值：boolean</br>
     */
    public boolean delectSignInfoByMeetingId(String meetingId);

    /**
     * 方法名：selectMeetingByMeetingIdAndUserId</br>
     * 描述：根据会议Id和用户Id查询会议</br>
     * 参数：[map]</br>
     * 返回值：com.thinkwin.common.model.db.YunmeetingConference</br>
     */
    public YunmeetingConference selectMeetingByMeetingIdAndUserId(Map<String,Object> map);
}
