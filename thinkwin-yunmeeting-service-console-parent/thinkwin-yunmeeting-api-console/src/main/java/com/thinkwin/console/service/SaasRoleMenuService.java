package com.thinkwin.console.service;

import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasMenu;
import com.thinkwin.common.model.console.SaasRoleMenu;

import java.util.List;

/**
 * 角色菜单接口
 * User: wangxilei
 * Date: 2017/9/11
 * Company: thinkwin
 */
public interface SaasRoleMenuService {
    /**
     * 根据角色id获取角色和菜单的关联表集合
     * @param roleId 角色Id
     * @return
     */
    List<SaasRoleMenu> selectMenuIdByRoleId(String roleId);

    /**
     * 角色菜单的添加功能
     * @param roleId 角色Id
     * @param menuIds 菜单Id列表
     * @return
     */
    boolean saveSaasRoleMenu(String roleId,List<String> menuIds);

    /**
     * 角色菜单的批量删除功能
     * @param roleId 角色Id
     * @param menuIds 菜单Id列表
     * @return
     */
    boolean deleteSaasRoleMenu(String roleId,List<String> menuIds);

    /**
     * 角色菜单的单个删除
     * @param roleId 角色Id
     * @param menuId 菜单Id
     * @return
     */
    boolean deleteSaasRoleMenuOne(String roleId,String menuId);

    /**
     * 角色菜单修改关联关系功能
     * @param roleId 角色Id
     * @param menuIds 菜单Id列表
     * @return
     */
    boolean updateSaasRoleMenu(String roleId,List<String> menuIds);

    /**
     * 根据角色id获取该id拥有哪些菜单的对象集合
     * @param roleId 角色Id
     * @param page 分页实体类
     * @return
     */
    List<SaasMenu> selectSaasRoleMenuByRoleId(String roleId, BasePageEntity page);
}
