package com.thinkwin.auth.service;

import com.thinkwin.common.model.db.SysRole;

import java.util.List;

/**
 * User: yinchunlei
 * Date: 2017/5/27.
 * Company: thinkwin
 */
public interface UserRoleService {
    /**
     * 根据用户的主键id获取用户的角色信息列表功能
     * @param userId
     * @return
     */
    public List<SysRole> findUserRolesByUserId(String userId);

    /**
     * 根据角色主键id集合获取用户主键集合功能
     * @param roleIdList
     * @return
     */
    public List<String> selectUserIdsByRoleIds(List<String> roleIdList);
}
