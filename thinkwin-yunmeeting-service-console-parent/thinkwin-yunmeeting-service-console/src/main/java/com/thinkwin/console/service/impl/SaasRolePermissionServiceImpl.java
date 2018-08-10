package com.thinkwin.console.service.impl;


import com.thinkwin.common.model.console.SaasPermission;
import com.thinkwin.common.model.console.SaasRolePermission;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.console.mapper.SaasPermissionMapper;
import com.thinkwin.console.mapper.SaasRolePermissionMapper;
import com.thinkwin.console.service.SaasRolePermissionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("saasRolePermissionService")
public class SaasRolePermissionServiceImpl implements SaasRolePermissionService {

    @Autowired
    private SaasRolePermissionMapper saasRolePermissionMapper;
    @Autowired
    private SaasPermissionMapper saasPermissionMapper;

    @Override
    public List<SaasPermission> findSaasResourcesByRoleId(String roleId) {
        List<SaasPermission> permissionList = new ArrayList<>();
        Example example = new Example(SaasRolePermission.class,true,true);
        example.createCriteria().andEqualTo("roleId",roleId);
        List<SaasRolePermission> rolePermissions = saasRolePermissionMapper.selectByExample(example);
        if(rolePermissions != null && rolePermissions.size() > 0) {
            for (SaasRolePermission rolePermissionn : rolePermissions) {
                String permissionId = rolePermissionn.getPermissionId();
                if(permissionId != null){
                    SaasPermission permission =  saasPermissionMapper.selectByPrimaryKey(permissionId);
                    if(null != permission){
                        permissionList.add(permission);
                    }
                }
            }
        }
        return permissionList;
    }

    @Override
    public boolean saveSaasRolePermission(String roleId,List<String> permissionIds){
        if(null != roleId && null != permissionIds && permissionIds.size() > 0) {
            int number = 0;
            for (String permissionId :permissionIds) {
                SaasRolePermission sysRolePermission = new SaasRolePermission();
                sysRolePermission.setRoleId(roleId);
                sysRolePermission.setPermissionId(permissionId);
                List<SaasRolePermission> sysRolePermissionList = saasRolePermissionMapper.select(sysRolePermission);
               //如果sysRolePermissionList为空说明库中没有当前权限ID和该角色的关联关系
                if(sysRolePermissionList.size()<=0){
                    SaasRolePermission sysRolePermission1 = new SaasRolePermission();
                    sysRolePermission1.setRoleId(roleId);
                    sysRolePermission1.setPermissionId(permissionId);
                    sysRolePermission1.setId(CreateUUIdUtil.Uuid());
                    sysRolePermission1.setCreateTime(new Date());
                    int num = saasRolePermissionMapper.insertSelective(sysRolePermission1);
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

    @Override
    public boolean updateSaasRolePermission(String roleId,List<String> permissionIds){
        if(null != roleId && !"".equals(roleId) && permissionIds.size() >0){
            int num = 0;
            boolean status = false;
            SaasRolePermission sysRolePermission = new SaasRolePermission();
            sysRolePermission.setRoleId(roleId);
            List<SaasRolePermission> sysRolePermissionList = saasRolePermissionMapper.select(sysRolePermission);
            for (SaasRolePermission sysRolePermission1: sysRolePermissionList) {
                String permissionId = sysRolePermission1.getPermissionId();
                for (String permissionIdd:permissionIds) {
                    if(permissionId.equals(permissionIdd)){
                        status = true;
                    }
                }
                if(status==false){
                    SaasRolePermission sysRolePermission2 = new SaasRolePermission();
                    sysRolePermission2.setRoleId(roleId);
                    sysRolePermission2.setPermissionId(permissionId);
                    int deletete = saasRolePermissionMapper.delete(sysRolePermission2);
                    if(deletete<=0){
                        return false;
                    }
                }else {
                    status = false;
                }
            }
            for(String permissionIddd:permissionIds) {
                SaasRolePermission sysRolePermission3 = new SaasRolePermission();
                sysRolePermission3.setRoleId(roleId);
                sysRolePermission3.setPermissionId(permissionIddd);
                List<SaasRolePermission> sysRolePermissionListtt = saasRolePermissionMapper.select(sysRolePermission3);
                if(null != sysRolePermissionListtt && sysRolePermissionListtt.size() <= 0){
                    sysRolePermission.setId(CreateUUIdUtil.Uuid());
                    sysRolePermission.setPermissionId(permissionIddd);
                    sysRolePermission.setCreateTime(new Date());
                    num = saasRolePermissionMapper.insertSelective(sysRolePermission);
                }else {
                    num = sysRolePermissionListtt.size();
                }
            }
            if (num != 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteSaasRolePermission(String roleId,List<String> permissionIds){
        boolean flag = false;
        if(null != roleId && !"".equals(roleId) && permissionIds.size() > 0){
            for (String permissionId:permissionIds) {
                SaasRolePermission sysRolePermission = new SaasRolePermission();
                sysRolePermission.setRoleId(roleId);
                sysRolePermission.setPermissionId(permissionId);
                int deleteNumber = saasRolePermissionMapper.delete(sysRolePermission);
                if(deleteNumber > 0){
                    flag = true;
                }else {
                    return false;
                }
            }
        }
        return flag;
    }

    @Override
    public boolean deleteSaasRolePermission(String roleId) {
        boolean flag = false;
        if(StringUtils.isNotBlank(roleId)){
            SaasRolePermission saasRolePermission = new SaasRolePermission();
            saasRolePermission.setRoleId(roleId);
            int delete = saasRolePermissionMapper.delete(saasRolePermission);
            if(delete > 0){
                flag = true;
            }else {
                return false;
            }
        }
        return flag;
    }

    @Override
    public List<SaasPermission> selectSaasPermissionsByRoleId(String roleId){
        List<SaasPermission> list = new ArrayList<>();
        SaasRolePermission saasRolePermission = new SaasRolePermission();
        saasRolePermission.setRoleId(roleId);
        List<SaasRolePermission> sysRolePermissionList = saasRolePermissionMapper.select(saasRolePermission);
        if(null != sysRolePermissionList && sysRolePermissionList.size() > 0){
            for (SaasRolePermission sysRolePermission1:sysRolePermissionList) {
                String permissionId = sysRolePermission1.getPermissionId();
                if(null != permissionId && !"".equals(permissionId)){
                    SaasPermission sysPermission = saasPermissionMapper.selectByPrimaryKey(permissionId);
                    list.add(sysPermission);
                }
            }
            return list;
        }
        return null;
    }

    @Override
    public List<SaasPermission> findSaasPermissionsByParentId(String parentId) {
        if(StringUtils.isNotBlank(parentId)){
//            SaasPermission sysPermission = new SaasPermission();
//            sysPermission.setParentId(parentId);
            Example ex = new Example(SaasPermission.class,true,true);
            ex.createCriteria().andEqualTo("parentId",parentId);
            ex.setOrderByClause("org_code");
            return saasPermissionMapper.selectByExample(ex);
        }
        return null;
    }

}
