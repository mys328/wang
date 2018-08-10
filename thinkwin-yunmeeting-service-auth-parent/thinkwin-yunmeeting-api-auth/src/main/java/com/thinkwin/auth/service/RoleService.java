package com.thinkwin.auth.service;

import com.github.pagehelper.Page;
import com.thinkwin.common.model.db.SysRole;

import java.util.List;

/**
 * 角色业务类
 * User: yinchunlei
 * Date: 2017/6/1.
 * Company: thinkwin
 */
public interface RoleService {
    //添加角色功能接口
    public boolean saveRole(SysRole sysRole);

    //根据角色主键ID删除角色功能接口
    public boolean deleteRole(String roleId);

    //修改角色的功能
    public boolean updateRole(SysRole sysRole);

    //查询所有的角色功能（带分页【未添加】）
    public List<SysRole> selectRoles(Page page);

    //根据角色主键ID获取角色详细信息
    public SysRole selectRoles(String roleId);

    //获取所有角色详细信息
    public List<SysRole> selectRolesInfo();
}
