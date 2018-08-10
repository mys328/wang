package com.thinkwin.auth.localService;

import com.thinkwin.common.model.db.SysRoleMenu;

import java.util.List;

/**
 * User: yinchunlei
 * Date: 2017/7/14.
 * Company: thinkwin
 */
public interface LocalRoleMenuService {

    /**
     * 根据角色id获取角色和菜单的关联表集合
     * @param roleId
     * @return
     */
    public List<SysRoleMenu> selectMenuIdByRoleId(String roleId);
}
