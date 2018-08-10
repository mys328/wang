package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.YuncmDeviceService;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface YuncmDeviceServiceMapper extends Mapper<YuncmDeviceService> {

    /**
     *  查询会议室设备
     * @param id 会议ID
     * @return
     */
    List<YuncmDeviceService> selectYuncmDeviceService(String id);
}