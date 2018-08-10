package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.YunmeetingParticipantsReply;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface YunmeetingParticipantsReplyMapper extends Mapper<YunmeetingParticipantsReply> {

    /**
     * 根据会议获取回复记录
     * @param map
     * @return
     */
    public List<YunmeetingParticipantsReply> selectAllReplyByMeetingIds(Map map);
}