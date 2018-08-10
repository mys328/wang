package com.thinkwin.auth.service.impl;

import com.github.pagehelper.Page;
import com.thinkwin.auth.mapper.db.SysRoleMapper;
import com.thinkwin.auth.service.RoleService;
import com.thinkwin.common.model.db.SysRole;
import com.thinkwin.yunmeeting.framework.datasource.dynamicdatasource.DBContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: yinchunlei
 * Date: 2017/6/1.
 * Company: thinkwin
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService{
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Override
    public boolean saveRole(SysRole sysRole) {
        //DBContextHolder.setDBType("de17fa76b32c4967bb4d141ef0e7933c");
        sysRole.setCreateTime(new Date());
        int status = sysRoleMapper.insertSelective(sysRole);
        if(status > 0){
            return true;
        }
        return false;
    }
    //根据角色主键ID删除角色功能接口
    public boolean deleteRole(String roleId){
       // DBContextHolder.setDBType("de17fa76b32c4967bb4d141ef0e7933c");
        if(null != roleId && !"".equals(roleId)){
            int status = sysRoleMapper.deleteByPrimaryKey(roleId);
            if(status > 0){
                return true;
            }
        }
        return false;
    }
    //修改角色的功能
    public boolean updateRole(SysRole sysRole){
      //  DBContextHolder.setDBType("de17fa76b32c4967bb4d141ef0e7933c");
       String roleId = sysRole.getRoleId();
        if(null != roleId && !"".equals(roleId)) {
            int status = sysRoleMapper.updateByPrimaryKeySelective(sysRole);
            if (status > 0) {
                return true;
            }
        }
        return false;
    }

    //查询所有的角色功能（带分页【未添加】）
    public List<SysRole> selectRoles(Page page){
      //  DBContextHolder.setDBType("de17fa76b32c4967bb4d141ef0e7933c");
        return sysRoleMapper.selectAll();
    }

    //根据角色主键ID获取角色详细信息
    public SysRole selectRoles(String roleId){
       // DBContextHolder.setDBType("de17fa76b32c4967bb4d141ef0e7933c");
        if(null != roleId && !"".equals(roleId)) {
            return sysRoleMapper.selectByPrimaryKey(roleId);
        }
        return null;
    }

    /**
     *   获取所有角色详细信息
     */
    public List<SysRole> selectRolesInfo(){
       return sysRoleMapper.selectAll();
    }
}
