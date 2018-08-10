package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.YuncmRoomReserveAuth;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface YuncmRoomReserveAuthMapper extends Mapper<YuncmRoomReserveAuth> {


    List<YuncmRoomReserveAuth> findMeetingRoomId(Map map);
}