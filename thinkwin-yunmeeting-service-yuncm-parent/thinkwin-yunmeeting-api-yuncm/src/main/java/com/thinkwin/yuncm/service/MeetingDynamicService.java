package com.thinkwin.yuncm.service;

import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.YunmeetingDynamics;
import com.thinkwin.common.model.db.YunmeetingDynamicsClickRecord;
import com.thinkwin.common.vo.meetingVo.DynamicVo;
import com.thinkwin.common.vo.meetingVo.MeetingDynamicVo;
import com.thinkwin.common.vo.meetingVo.MeetingParticipantsVo;

import java.util.List;
import java.util.Map;

/**
 * 类名: MeetingDynamicService </br>
 * 描述:</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/8/9 </br>
 */
public interface MeetingDynamicService {

    /**
     * 方法名：selectRecentMeetingDynamic</br>
     * 描述：查询近期会议动态（带搜索功能）</br>
     * 参数：Map 条件</br>
     * 返回值：</br>
     */
    List<DynamicVo> selectRecentMeetingDynamic(Map map);

    /**
     * 方法名：insertMeetingDynamic</br>
     * 描述：增加会议动态消息</br>
     * 参数：yunmeetingDynamics 会议动态表</br>
     * 返回值：</br>
     */
    boolean insertMeetingDynamic(YunmeetingDynamics yunmeetingDynamics,String dynamicType,String userId);

    /**
     * 方法名：updateMeetingDynamic</br>
     * 描述：修改会议动态消息</br>
     * 参数：yunmeetingDynamics 会议动态表</br>
     * 返回值：</br>
     */
    boolean updateMeetingDynamic(YunmeetingDynamics yunmeetingDynamics);

    /**
     * 方法名：selectMeetingDynamicById</br>
     * 描述：查询会议动态  根据会议动态Id</br>
     * 参数：dynamicId 动态Id</br>
     * 返回值：</br>
     */
    YunmeetingDynamics selectMeetingDynamicById(String dynamicId);

    /**
     * 方法名：selectMeetingDynamic</br>
     * 描述：查询会议动态信息  根据会议动态表实体</br>
     * 参数：yunmeetingDynamics 会议动态表</br>
     * 返回值：</br>
     */
    List<YunmeetingDynamics> selectMeetingDynamic(YunmeetingDynamics yunmeetingDynamics);

    /**
     * 方法名：selectMeetingDynamic</br>
     * 描述：查询会议动态信息  根据会议Id</br>
     * 参数：meetingId 会议Id</br>
     * 返回值：</br>
     */
    List<YunmeetingDynamics> selectMeetingDynamic(String meetingId);
    /**
     * 方法名：selectMeetingDynamic</br>
     * 描述：查询会议动态排序</br>
     * 参数：</br>
     * 返回值：</br>
     */
    List<String> selectMeetingDynamic(BasePageEntity pageEntity,Map<String,Object> map);
    /**
     * 方法名：insertMeetingDynamicByMeetingId</br>
     * 描述：根据会议Id增加所有参会人动态表</br>
     * 参数：meetingId 会议Id</br>
     * 参数：content 动态内容</br>
     * 返回值：参会人员Id（绑定微信的人）</br>
     */
    List<MeetingParticipantsVo> insertMeetingDynamicByMeetingId(Map<String,Object> map);

    /**
     * 方法名：selectDynamicClick</br>
     * 描述：查询动态已读信息表</br>
     * 参数：dynamicId 动态Id</br>
     * 参数：userId 用户Id</br>
     * 返回值：</br>
     */
    YunmeetingDynamicsClickRecord selectDynamicClickInfo(String dynamicId,String userId);
    /**
     * 方法名：insertDynamicClickInfo</br>
     * 描述：增加动态已读信息</br>
     * 参数：</br>
     * 返回值：</br>
     */
    boolean insertDynamicClickInfo(YunmeetingDynamicsClickRecord yunmeetingDynamicsClickRecord);

    /**
     *
     * @param yunmeetingConferenceId
     * @return
     */
    public Integer selectMeetingDynamicCreateIdsByMeetingId(String yunmeetingConferenceId,String userId);
}
