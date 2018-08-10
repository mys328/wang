package com.thinkwin.auth.mapper.db;

import com.thinkwin.common.model.db.SysPermission;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SysPermissionMapper extends Mapper<SysPermission> {
    /**
     * 根据条件集合查询资源集合信息
     * @param map
     * @return
     */
    public List<String> selectPermissionsByIds(Map map);
}