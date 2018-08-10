package com.thinkwin.console.service;


import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasMenu;

import java.util.List;

/**
 * 菜单接口
 * User: wangxilei
 * Date: 2017/9/12
 * Company: thinkwin
 */
public interface SaasMenuService {
    /**
     * 添加菜单功能
     * @param saasMenu 菜单对象
     * @param roleIds 角色Id列表
     * @return
     */
    boolean saveSaasMenu(SaasMenu saasMenu, List<String> roleIds);

    /**
     * 添加菜单
     * @param saasMenu 菜单对象
     * @return
     */
    boolean saveSaasMenu(SaasMenu saasMenu);

    /**
     * 根据菜单的主键删除相应的信息
     * @param menuId 菜单Id
     * @return
     */
    Integer deleteSaasMenuByMenuId(String menuId);

    /**
     * 批量删除菜单功能
     * @param menuIds 菜单Id列表
     * @return
     */
    boolean deleteSaasMenu(List<String> menuIds);

    /**
     * 修改菜单功能
     * @param saasMenu 菜单对象
     * @return
     */
    boolean updateSaasMenu(SaasMenu saasMenu);

    /**
     * 不带分页查询全部菜单功能
     * @param userId
     * @param parentId
     * @return
     */
    List<SaasMenu> selectSaasMenusByUserId(String userId,String parentId);

    /**
     * 带分页查询全部菜单功能
     * @param pageEntity
     * @return
     */
    PageInfo selectSaasMenus(BasePageEntity pageEntity);

    /**
     * 带条件查询菜单集合
     * @param saasMenu 菜单对象
     * @return
     */
    List<SaasMenu> selectSaasMenuByCondition(SaasMenu saasMenu);

    /**
     * 根据菜单主键id获取菜单详细信息
     * @param menuId 菜单Id
     * @return
     */
    SaasMenu selectSaasMenuByMenuId(String menuId);
}
