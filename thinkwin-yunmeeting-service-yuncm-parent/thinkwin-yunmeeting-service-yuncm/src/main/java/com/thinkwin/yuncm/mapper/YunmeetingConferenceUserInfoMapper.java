package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.YunmeetingConferenceUserInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface YunmeetingConferenceUserInfoMapper extends Mapper<YunmeetingConferenceUserInfo> {

    /**
     * 方法名：selectParticipateMeeting</br>
     * 描述：根据用户Id和参与会议信息表Id
     * 查询个人本月参加多少会议(组织机构)</br>
     * 参数：participateInfoId  参与会议信息表Id</br>
     * 参数：userId  用户Id</br>
     * 返回值：</br>
     */
    List<YunmeetingConferenceUserInfo> selectParticipateMeetingByOrg(Map<String,Object> map);
    /**
     * 方法名：selectMeetingAllPerson</br>
     * 描述：查询所有人员 带搜索功能</br>
     * 参数：</br>
     * 返回值：</br>
     */
    List<YunmeetingConferenceUserInfo> selectMeetingAllPerson(Map<String,Object> map);
}