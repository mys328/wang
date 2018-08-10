package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.YunmeetingDynamics;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface YunmeetingDynamicsMapper extends Mapper<YunmeetingDynamics> {

    /**
     * 方法名：selectMeetingDynamicByTime</br>
     * 描述：根据会议id和时间段查询会议动态</br>
     * 参数：map 时间段条件 (startTime 要查询的时间段开始  endTime 要查询的时间段结束) </br>
     * 返回值：</br>
     */
    List<YunmeetingDynamics> selectMeetingDynamicByTime(Map map);
    /**
     * 方法名：selectMeetingDynamicSearch</br>
     * 描述：查询会议动态搜索</br>
     * 参数：search 搜索状态</br>
     * 返回值：</br>
     */
    List<YunmeetingDynamics> selectMeetingDynamicSearch(Map<String,Object> map);
    /**
     * 方法名：selectDynaicOrderBy</br>
     * 描述：会议动态排序</br>
     * 参数：</br>
     * 返回值：</br>
     */
    List<YunmeetingDynamics> selectDynaicOrderBy(Map<String,Object> map);

    /**
     * 根据会议id获取会议动态创建者id集合
     * @param map
     * @return
     */
    public Integer selectMeetingDynamicCreateIdsByMeetingId(Map map);
}