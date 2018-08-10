package com.thinkwin.auth.mapper.db;

import com.thinkwin.common.model.db.SysUserRole;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SysUserRoleMapper extends Mapper<SysUserRole> {
    /**
     * 根据角色主键id集合获取用户主键集合功能
     * @param map
     * @return
     */
    public List<String> selectUserIdsByRoleIds(Map map);
}