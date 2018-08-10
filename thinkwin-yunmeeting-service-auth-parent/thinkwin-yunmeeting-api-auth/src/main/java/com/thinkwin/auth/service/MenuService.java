package com.thinkwin.auth.service;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.SysMenu;

import java.util.List;

/**
 * 菜单部分业务接口层
 * User: yinchunlei
 * Date: 2017/6/2.
 * Company: thinkwin
 */
public interface MenuService {
    //添加菜单功能
    public boolean saveMenu(SysMenu sysMenu,List<String> list);

    //根据菜单的主键删除相应的信息
    public Integer deleteMenu(String menuId);

    //批量删除菜单功能
    public boolean deleteMenu(List<String> menuIds);

    //修改菜单功能
    public boolean updateMenu(SysMenu sysMenu);

    //不带分页查询全部菜单功能
    public List<SysMenu> selectMenus(String userId,String parentId,String tenantType);

    //带分页查询全部菜单功能
    public PageInfo selectMenus(BasePageEntity pageEntity);

    //带条件查询菜单集合
    public List<SysMenu> selectMenuByCondition(SysMenu sysMnu);

    //根据菜单主键id获取菜单详细信息
    public SysMenu selectMenuByMenuId(String menuId);
}
