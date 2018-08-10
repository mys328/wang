package com.thinkwin.auth.service;

import com.github.pagehelper.Page;
import com.thinkwin.common.model.db.SysPermission;

import java.util.List;

/**
 * User: yinchunlei
 * Date: 2017/5/27.
 * Company: thinkwin
 */
public interface PermissionService {
    //根据权限主键的id获取权限信息
    public SysPermission getPermissionById(String permissionId);

    //根据用户的主键ID获取用户的所有权限功能
    public List<SysPermission> selectUserPermissionByUserId(String userId);

    //添加新的权限信息
    public boolean savePermission(SysPermission sysPermission);

    //权限的批量删除功能
    public boolean deletePermission(List<String> permissionIds);

    //根据权限的主键id删除相应的权限信息
    public boolean deletePermissionById(String permissionId);

    //权限的修改功能
    public boolean updatePermission(SysPermission permission);

    //带分页功能查询全部权限
    public List<SysPermission> selectPermission(Page page);

    /**
     * 判断某个用户是否具有访问某个路径的权限功能
     * @param userId
     * @param requestUrl
     * @return
     */
    public boolean getUserJurisdiction(String userId,String requestUrl);

    /**
     * 根据角色主键获取资源路径集合功能
     * @param userHighestRoleId
     * @return
     */
    public List<String> selectPermissionUrlsByRoleId(String userHighestRoleId);
}
