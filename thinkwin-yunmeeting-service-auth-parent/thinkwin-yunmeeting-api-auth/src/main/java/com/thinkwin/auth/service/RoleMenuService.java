package com.thinkwin.auth.service;

import com.github.pagehelper.Page;
import com.thinkwin.common.model.db.SysMenu;

import java.util.List;

/**
 * 角色菜单业务层
 * User: yinchunlei
 * Date: 2017/6/2.
 * Company: thinkwin
 */
public interface RoleMenuService {
    //角色菜单的添加功能
    public boolean saveRoleAndMenu(String roleId,List<String> menuIds);

    //角色菜单的批量删除功能
    public boolean deleteRoleAndMenu(String roleId,List<String> menuIds);

    //角色菜单的单个删除
    public boolean deleteRoleAndMenu(String roleId,String menuId);

    //角色菜单修改关联关系功能
    public boolean updateRoleAndMenu(String roleId,List<String> menuIds);

    //根据角色id获取该id拥有哪些菜单的对象集合
    public List<SysMenu> selectRoleAndMenuByRoleId(String roleId);
    //根据角色id获取该id拥有哪些菜单的对象集合(加分页功能)
    public List<String> selectRoleAndMenuByRoleId(String roleId,Page page);
}
