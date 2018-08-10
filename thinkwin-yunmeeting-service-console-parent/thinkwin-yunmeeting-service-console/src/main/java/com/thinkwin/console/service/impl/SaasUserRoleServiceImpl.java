package com.thinkwin.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasRole;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.common.model.console.SaasUserRole;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.console.mapper.SaasRoleMapper;
import com.thinkwin.console.mapper.SaasUserMapper;
import com.thinkwin.console.mapper.SaasUserRoleMapper;
import com.thinkwin.console.service.SaasUserRoleService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("saasUserRoleService")
public class SaasUserRoleServiceImpl implements SaasUserRoleService {
    @Autowired
    private SaasUserRoleMapper saasUserRoleMapper;
    @Autowired
    private SaasRoleMapper saasRoleMapper;
    @Autowired
    private SaasUserMapper saasUserMapper;
    @Override
    //根据用户名获取用户的角色列表
    public List<SaasRole> findSaasUserRolesByUserId(String userId) {
        List<SaasRole> roleList = new ArrayList<>();
        if(null != userId){
            SaasUserRole userRole = new SaasUserRole();
            userRole.setUserId(userId);
            List<SaasUserRole> userRoles = saasUserRoleMapper.select(userRole);
            if(null != userRoles && userRoles.size() > 0){
                for (SaasUserRole userRolee:userRoles) {
                    if(null != userRolee){
                        SaasRole role1 = saasRoleMapper.selectByPrimaryKey(userRolee.getRoleId());
                        if(null != role1){
                            roleList.add(role1);
                        }
                    }
                }
            }
        }
        return roleList;
    }

    @Override
    public List<SaasUser> findSaasUsersByRoleId(String roleId) {
        List<SaasUser> userList = new ArrayList<>();
        SaasUserRole role = new SaasUserRole();
        role.setRoleId(roleId);
        List<SaasUserRole> userRoles = saasUserRoleMapper.select(role);
        if(null != userRoles && userRoles.size() > 0){
            for (SaasUserRole userRolee:userRoles) {
                if(null != userRolee){
                    SaasUser user = saasUserMapper.selectByPrimaryKey(userRolee.getUserId());
                    if(null != user){
                        userList.add(user);
                    }
                }
            }
        }
        return userList;
    }

    @Override
    public boolean saveSaasUserRole(String userId, List<String> roleIds) {
        if(null != userId && null != roleIds && roleIds.size() > 0) {
            int num = 0;
            String creater = userId;
            if(TenantContext.getUserInfo() != null){
                creater = TenantContext.getUserInfo().getUserId();
            }
            boolean flag = false;
            SaasUserRole saasUserRole = new SaasUserRole();
            saasUserRole.setUserId(userId);
            List<SaasUserRole> saasUserRoleList = saasUserRoleMapper.select(saasUserRole);
            for (SaasUserRole sr:saasUserRoleList) {
                for (String roleId :roleIds) {
                    if(sr.getRoleId().equals(roleId)){
                        flag = true;
                    }
                }
                if(flag==false){
                    SaasUserRole saasUserRole1 = new SaasUserRole();
                    saasUserRole1.setUserId(userId);
                    saasUserRole1.setRoleId(sr.getRoleId());
                    num = saasUserRoleMapper.delete(saasUserRole1);
                }else{
                    flag = false;
                }
            }
            for (String roleId :roleIds) {
                SaasUserRole s = new SaasUserRole();
                s.setUserId(userId);
                s.setRoleId(roleId);
                List<SaasUserRole> saasUserRoleList1 = saasUserRoleMapper.select(s);
                if(saasUserRoleList1.size()<=0){
                    s.setId(CreateUUIdUtil.Uuid());
                    s.setCreaterId(creater);
                    s.setCreateTime(new Date());
                    num = saasUserRoleMapper.insertSelective(s);
                }else{
                    num = saasUserRoleList1.size();
                }
            }
            if(num>0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addSaasUserRole(String userId, List<String> roleIds) {
        if(null != userId && null != roleIds && roleIds.size() > 0) {
            int num = 0;
            for (String roleId : roleIds) {
                SaasUserRole s = new SaasUserRole();
                s.setUserId(userId);
                s.setRoleId(roleId);
                List<SaasUserRole> saasUserRoleList1 = saasUserRoleMapper.select(s);
                if (saasUserRoleList1.size() <= 0) {
                    s.setId(CreateUUIdUtil.Uuid());
                    s.setCreateTime(new Date());
                    num = saasUserRoleMapper.insertSelective(s);
                }
            }
            if(num>0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteSaasUserRole(String userId, List<String> roleIds) {
        boolean flag = false;
        if(null != userId && !"".equals(userId) && null != roleIds && roleIds.size() > 0){
            for (String roleId:roleIds) {
                SaasUserRole saasUserRole = new SaasUserRole();
                saasUserRole.setUserId(userId);
                saasUserRole.setRoleId(roleId);
                int deleteNumber = saasUserRoleMapper.delete(saasUserRole);
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
    public boolean deleteSaasUserRole(String userId) {
        boolean flag = false;
        if(null != userId && !"".equals(userId)){
            SaasUserRole saasUserRole = new SaasUserRole();
            saasUserRole.setUserId(userId);
            int deleteNumber = saasUserRoleMapper.delete(saasUserRole);
            if(deleteNumber > 0){
                flag = true;
            }else {
                return false;
            }
        }
        return flag;
    }

    @Override
    public List<SaasRole> selectSaasUserRole(String userId, BasePageEntity page) {
        List<SaasRole> list = new ArrayList<>();
        SaasUserRole saasUserRole = new SaasUserRole();
        saasUserRole.setUserId(userId);
        PageHelper.startPage(page.getCurrentPage(),page.getPageSize());
        List<SaasUserRole> sysUserRoleList = saasUserRoleMapper.select(saasUserRole);
        if(null != sysUserRoleList && sysUserRoleList.size() > 0){
            for (SaasUserRole s:sysUserRoleList) {
                String roleId = s.getRoleId();
                if(null != roleId && !"".equals(roleId)){
                    SaasRole sysRole = saasRoleMapper.selectByPrimaryKey(roleId);
                    list.add(sysRole);
                }
            }
            return list;
        }
        return null;
    }

    @Override
    public List<String> selectSaasUserRoleIds(String userId) {
        List<String> list = new ArrayList<>();
        SaasUserRole saasUserRole = new SaasUserRole();
        saasUserRole.setUserId(userId);
        List<SaasUserRole> sysUserRoleList = saasUserRoleMapper.select(saasUserRole);
        if(null != sysUserRoleList && sysUserRoleList.size() > 0){
            for (SaasUserRole userRole:sysUserRoleList) {
                String roleId = userRole.getRoleId();
                list.add(roleId);
            }
            return list;
        }
        return null;
    }

    @Override
    public List<SaasUserRole> selectSaasUserRole(String userId) {
        SaasUserRole saasUserRole = new SaasUserRole();
        saasUserRole.setUserId(userId);
        return saasUserRoleMapper.select(saasUserRole);
    }

    @Override
    public List<SaasUserRole> getSaasUserRoleByUserIdAndRoleId(String userId, String roleId) {
        if(StringUtils.isNotBlank(userId)){
            String r = roleId;
            if(StringUtils.isBlank(roleId)){
                r = "1";
            }
            SaasUserRole saasUserRole = new SaasUserRole();
            saasUserRole.setUserId(userId);
            saasUserRole.setRoleId(r);
            return saasUserRoleMapper.select(saasUserRole);
        }
        return null;
    }
}
