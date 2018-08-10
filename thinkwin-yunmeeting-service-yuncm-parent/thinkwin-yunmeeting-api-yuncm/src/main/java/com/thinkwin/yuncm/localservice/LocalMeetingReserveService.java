package com.thinkwin.yuncm.localservice;

import com.thinkwin.common.model.db.*;
import com.thinkwin.common.vo.meetingVo.PersonsVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类名: LocalMeetingRoomReserveService </br>
 * 描述:</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/7/28 </br>
 */
public interface LocalMeetingReserveService {
    /**
     * 查看会议室预定
     *
     * @return
     */
    YuncmRoomReserveConf selectYuncmRoomReserveConf();

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
     * 查看单个会议室 会议室详情
     *
     * @return
     */
    YuncmMeetingRoom selectByidYuncmMeetingRoom(String id);

    /**
     * 方法名：selectMeetingDynamic</br>
     * 描述：查询会议动态信息  根据会议Id</br>
     * 参数：meetingId 会议Id</br>
     * 返回值：</br>
     */
    List<YunmeetingDynamics> selectMeetingDynamic(Map<String,Object> map);

    /**
     * 方法名：selectParticipateMeetingByMeetingId</br>
     * 描述：根据会议Id查询参会人员表信息</br>
     * 参数：meetingId 会议Id</br>
     * 返回值：</br>
     */
    List<YunmeetingParticipantsInfo> selectParticipateMeetingByMeetingId(Map<String,Object> map);

    /**
     * 方法名：selectAllParticipantByMeetingId</br>
     * 描述：根据会议Id查询所有参会人</br>
     * 参数：meetingId 会议Id</br>
     * 返回值：</br>
     */
    List<PersonsVo> selectAllParticipantByMeetingId(String meetingId);

    /**
     * 方法名：selectMCRoomMiddleByMId</br>
     * 描述：根据会议Id查询会议和会议室中间表</br>
     * 参数：meetingId  会议Id</br>
     * 返回值：YummeetingConferenceRoomMiddle 返回对象</br>
     */
    public YummeetingConferenceRoomMiddle selectMCRoomMiddleByMId(String meetingId);

    /**
     * 方法名：deleteMCRoomMiddleByMId</br>
     * 描述：根据会议Id删除会议和会议室中间表</br>
     * 参数：meetingId 会议id </br>
     * 返回值：boolean</br>
     */
    public boolean deleteMCRoomMiddleByMId(String meetingId);


}
