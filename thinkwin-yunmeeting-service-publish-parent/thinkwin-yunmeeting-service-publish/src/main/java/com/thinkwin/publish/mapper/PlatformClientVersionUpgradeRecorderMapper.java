package com.thinkwin.publish.mapper;

import com.thinkwin.common.model.publish.PlatformClientVersionUpgradeRecorder;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface PlatformClientVersionUpgradeRecorderMapper extends Mapper<PlatformClientVersionUpgradeRecorder> {

    List<PlatformClientVersionUpgradeRecorder> findByClientVersionIdAndTenantName(Map<String,String> map);
}