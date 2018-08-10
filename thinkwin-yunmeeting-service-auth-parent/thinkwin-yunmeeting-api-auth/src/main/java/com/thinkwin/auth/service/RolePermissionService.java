package com.thinkwin.auth.service;
import com.thinkwin.common.model.db.SysPermission;
import com.thinkwin.common.model.db.SysRolePermission;

import java.util.List;

/**
 * 角色与权限业务类
 * User: yinchunlei
 * Date: 2017/5/27.
 * Company: thinkwin
 */
public interface RolePermissionService {
    public List<SysRolePermission> findPermsByRoleId(String roleId);

    public List<SysPermission> findResourcesByRoleName(String roleName);

    //根据角色ID获取该角色拥有的所有权限
    public List<SysPermission> findResourcesByRoleId(String roleId);
    //添加角色权限的功能
    public boolean saveRolePermission(String roleId,List<String> permissionIds);

    //修改角色权限的功能
    public boolean updateRolePermission(String roleId,List<String> permissionIds);

    //删除某个角色的权限功能
    public boolean deleteRolePermission(String roleId,List<String> permissionIds);

    //根据某个角色的主键ID获取该角色的所有权限信息
    public List<SysPermission> selectPermissionsByRoleId(String roleId);

}
