package com.thinkwin.auth.service.impl;


import com.thinkwin.auth.mapper.db.SysPermissionMapper;
import com.thinkwin.auth.mapper.db.SysRolePermissionMapper;
import com.thinkwin.auth.service.RolePermissionService;
import com.thinkwin.common.model.db.SysPermission;
import com.thinkwin.common.model.db.SysRolePermission;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.yunmeeting.framework.datasource.dynamicdatasource.DBContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: yinchunlei
 * Date: 2017/5/19.
 * Company: thinkwin
 */
@Service("rolePermissionService")
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public List<SysRolePermission> findPermsByRoleId(String roleId) {
       // DBContextHolder.setDBType("de17fa76b32c4967bb4d141ef0e7933c");
        String dbType = DBContextHolder.getDBType();
        if(dbType == null ||"0".equals(dbType)){
            DBContextHolder.setDBType("plantform_init_yunmeeting_db");
        }
        SysRolePermission rolePermission = new SysRolePermission();
        rolePermission.setRoleId(roleId);
        return sysRolePermissionMapper.select(rolePermission);
    }

    @Override
    public List<SysPermission> findResourcesByRoleName(String roleName) {
        return null;
    }

    @Override
    public List<SysPermission> findResourcesByRoleId(String roleId) {
       // DBContextHolder.setDBType("de17fa76b32c4967bb4d141ef0e7933c");

        String dbType = DBContextHolder.getDBType();
        if(dbType == null ||"0".equals(dbType)){
            DBContextHolder.setDBType("plantform_init_yunmeeting_db");
        }
        List<SysPermission> permissionList = new ArrayList<>();
        Example example = new Example(SysRolePermission.class,true,true);
        example.createCriteria().andEqualTo("roleId",roleId);
        List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectByExample(example);
        if(rolePermissions != null && rolePermissions.size() > 0) {
           for (SysRolePermission rolePermissionn : rolePermissions) {
                String permissionId = rolePermissionn.getPermissionId();
                if(permissionId != null){
                   SysPermission permission =  sysPermissionMapper.selectByPrimaryKey(permissionId);
                   if(null != permission){
                       permissionList.add(permission);
                   }
                }
           }
       }
        return permissionList;
    }

    //添加角色权限的功能
    public boolean saveRolePermission(String roleId,List<String> permissionIds){
        //DBContextHolder.setDBType("de17fa76b32c4967bb4d141ef0e7933c");
        if(null != roleId && null != permissionIds && permissionIds.size() > 0) {
            int number = 0;
            for (String permissionId :permissionIds) {
                SysRolePermission sysRolePermission = new SysRolePermission();
                sysRolePermission.setRoleId(roleId);
                sysRolePermission.setPermissionId(permissionId);
                List<SysRolePermission> sysRolePermissionList = sysRolePermissionMapper.select(sysRolePermission);
               //如果sysRolePermissionList为空说明库中没有当前权限ID和该角色的关联关系
                if(sysRolePermissionList.size()<=0){
                    SysRolePermission sysRolePermission1 = new SysRolePermission();
                    sysRolePermission1.setRoleId(roleId);
                    sysRolePermission1.setPermissionId(permissionId);
                    sysRolePermission1.setId(CreateUUIdUtil.Uuid());
                    sysRolePermission1.setCreateTime(new Date());
                    int num = sysRolePermissionMapper.insertSelective(sysRolePermission1);
                    if(num > 0){
                        number = num;
                    }else {
                        return false;
                    }
                }else {
                    number = sysRolePermissionList.size();
                }
            }
            if(number > 0){
                return true;
            }
        }
        return false;
    }

    //修改角色权限的功能
    public boolean updateRolePermission(String roleId,List<String> permissionIds){
        //DBContextHolder.setDBType("de17fa76b32c4967bb4d141ef0e7933c");
        if(null != roleId && !"".equals(roleId) && permissionIds.size() >0){
            int num = 0;
            boolean status = false;
            SysRolePermission sysRolePermission = new SysRolePermission();
            sysRolePermission.setRoleId(roleId);
            List<SysRolePermission> sysRolePermissionList = sysRolePermissionMapper.select(sysRolePermission);
            for (SysRolePermission sysRolePermission1: sysRolePermissionList) {
                String permissionId = sysRolePermission1.getId();
                for (String permissionIdd:permissionIds) {
                    if(permissionId.equals(permissionIdd)){
                        status = true;
                    }
                }
                if(status==false){
                    SysRolePermission sysRolePermission2 = new SysRolePermission();
                    sysRolePermission2.setRoleId(roleId);
                    sysRolePermission2.setPermissionId(permissionId);
                    int deletete = sysRolePermissionMapper.delete(sysRolePermission2);
                    if(deletete<=0){
                        return false;
                    }
                }else {
                    status = false;
                }
            }
            for(String permissionIddd:permissionIds) {
                SysRolePermission sysRolePermission3 = new SysRolePermission();
                sysRolePermission3.setRoleId(roleId);
                sysRolePermission3.setPermissionId(permissionIddd);
                List<SysRolePermission> sysRolePermissionListtt = sysRolePermissionMapper.select(sysRolePermission3);
                if(null != sysRolePermissionListtt && sysRolePermissionListtt.size() <= 0){
                    sysRolePermission.setId(CreateUUIdUtil.Uuid());
                    sysRolePermission.setPermissionId(permissionIddd);
                    sysRolePermission.setCreateTime(new Date());
                    num = sysRolePermissionMapper.insertSelective(sysRolePermission);
                }else {
                    if(null != sysRolePermissionListtt) {
                        num = sysRolePermissionListtt.size();
                    }
                }
            }
            if (num != 0){
                return true;
            }
        }
        return false;
    }

    //删除某个角色的权限功能
    public boolean deleteRolePermission(String roleId,List<String> permissionIds){
        //DBContextHolder.setDBType("de17fa76b32c4967bb4d141ef0e7933c");
        if(null != roleId && !"".equals(roleId) && permissionIds.size() > 0){
            int deleteNum = 0;
            for (String permissionId:permissionIds) {
                SysRolePermission sysRolePermission = new SysRolePermission();
                sysRolePermission.setRoleId(roleId);
                sysRolePermission.setPermissionId(permissionId);
                int deleteNumber = sysRolePermissionMapper.delete(sysRolePermission);
                if(deleteNumber > 0){
                    deleteNum = deleteNumber;
                }else {
                    return false;
                }
            }
            if(deleteNum != 0) {
                return true;
            }
        }
        return false;
    }

    //根据某个角色的主键ID获取该角色的所有权限信息
    public List<SysPermission> selectPermissionsByRoleId(String roleId){
       // DBContextHolder.setDBType("de17fa76b32c4967bb4d141ef0e7933c");
        List list = new ArrayList();
        SysRolePermission sysRolePermission = new SysRolePermission();
        sysRolePermission.setRoleId(roleId);
        List<SysRolePermission> sysRolePermissionList = sysRolePermissionMapper.select(sysRolePermission);
        if(null != sysRolePermissionList && sysRolePermissionList.size() > 0){
            for (SysRolePermission sysRolePermission1:sysRolePermissionList) {
                String permissionId = sysRolePermission1.getId();
                if(null != permissionId && !"".equals(permissionId)){
                    SysPermission sysPermission = sysPermissionMapper.selectByPrimaryKey(permissionId);
                    list.add(sysPermission);
                }
            }
            return list;
        }
        return null;
    }

}
