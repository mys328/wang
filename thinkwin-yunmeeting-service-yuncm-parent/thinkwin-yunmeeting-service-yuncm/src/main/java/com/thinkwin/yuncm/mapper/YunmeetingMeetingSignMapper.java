package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.YunmeetingMeetingSign;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface YunmeetingMeetingSignMapper extends Mapper<YunmeetingMeetingSign> {

    /**
     * 根据会议id获取签到记录
     * @param map
     * @return
     */
    public List<YunmeetingMeetingSign> selectAllSignByMeetingIds(Map map);
}