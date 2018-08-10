package com.thinkwin.core.mapper;

import com.thinkwin.common.model.core.SaasTenant;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SaasTenantMapper extends Mapper<SaasTenant> {

    List<SaasTenant> selectSaasTenantListByPage(Map map);

    List<SaasTenant> selectSaasTenantListBySeachKey(Map map);
}