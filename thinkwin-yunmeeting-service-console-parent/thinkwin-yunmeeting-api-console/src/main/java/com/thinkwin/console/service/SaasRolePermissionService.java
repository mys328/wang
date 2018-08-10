package com.thinkwin.console.service;

import com.thinkwin.common.model.console.SaasPermission;

import java.util.List;

/**
 * 角色与权限业务类
 * User: wangxilei
 * Date: 2017/9/13
 * Company: thinkwin
 */
public interface SaasRolePermissionService {
    /**
     * 根据角色ID获取该角色拥有的所有权限
     * @param roleId 角色ID
     * @return
     */
    List<SaasPermission> findSaasResourcesByRoleId(String roleId);

    /**
     * 添加角色权限的功能
     * @param roleId 角色Id
     * @param permissionIds 权限Id列表
     * @return
     */
    boolean saveSaasRolePermission(String roleId, List<String> permissionIds);

    /**
     * 修改角色权限的功能
     * @param roleId 角色Id
     * @param permissionIds 权限Id列表
     * @return
     */
    boolean updateSaasRolePermission(String roleId, List<String> permissionIds);

    /**
     * 删除某个角色的权限功能
     * @param roleId 角色Id
     * @param permissionIds 权限Id列表
     * @return
     */
    boolean deleteSaasRolePermission(String roleId, List<String> permissionIds);

    /**
     * 清除该角色的权限
     * @param roleId 角色Id
     * @return
     */
    boolean deleteSaasRolePermission(String roleId);

    /**
     * 根据某个角色的主键ID获取该角色的所有权限信息
     * @param roleId 角色Id
     * @return
     */
    List<SaasPermission> selectSaasPermissionsByRoleId(String roleId);

    /**
     * 根据权限父ID获取权限列表
     * @param parentId 权限父Id
     * @return
     */
    List<SaasPermission> findSaasPermissionsByParentId(String parentId);

}
