package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.YunmeetingParticipantsInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface YunmeetingParticipantsInfoMapper extends Mapper<YunmeetingParticipantsInfo> {

    /**
     * 方法名：selectParticipateMeeting</br>
     * 描述：根据会议Id和用户Id查询个人本月参加多少会议(不包含组织机构)</br>
     * 参数：conferenceId  会议Id</br>
     * 参数：userId  用户Id</br>
     * 参数：type  参会人员类型  0：只查用户 1：只查组织机构 2：会议分组参会</br>
     * 返回值：</br>
     */
    List<YunmeetingParticipantsInfo> selectParticipateMeetingByUser(Map<String,Object> map);
    /**
     * 方法名：selectMeetingReplay</br>
     * 描述：查询会议回复状态（带搜索）</br>
     * 参数：map</br>
     * 返回值：</br>
     */
    List<YunmeetingParticipantsInfo> selectMeetingReplay(Map<String,Object> map);
}