package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.YuncmReserveConfig;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface YuncmReserveConfigMapper extends Mapper<YuncmReserveConfig> {

    /**
     * 查看会议室预定权限
     * @param meetingId
     * @return
     */
   List<YuncmReserveConfig> selectYuncmReserveConfig(String meetingId);
}