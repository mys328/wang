package com.thinkwin.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasRole;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.common.model.console.SaasUserRole;
import com.thinkwin.console.mapper.SaasRoleMapper;
import com.thinkwin.console.mapper.SaasUserRoleMapper;
import com.thinkwin.console.service.SaasRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("saasRoleService")
public class SaasRoleServiceImpl implements SaasRoleService {

    @Autowired
    SaasUserRoleMapper saasUserRoleMapper;

    @Autowired
    SaasRoleMapper saasRoleMapper;

    @Override
    public PageInfo<SaasRole> findAllSaasRoles(String condition, BasePageEntity pageEntity) {
        Example ex = new Example(SaasUser.class, true, true);
        if (StringUtils.isNotBlank(condition)) {
            String cre = "'%" + condition + "%'";
            ex.or().andCondition("role_name like " + cre);
        }
        ex.setOrderByClause("create_time desc");
        if(pageEntity!=null){
            PageHelper.startPage(pageEntity.getCurrentPage(),pageEntity.getPageSize());
        }
        List<SaasRole> saasRoles = saasRoleMapper.selectByExample(ex);
        return new PageInfo<>(saasRoles);
    }

    @Override
    public List<SaasRole> findUserRolesByUserId(String userId) {
        List<SaasRole> roleList = new ArrayList<>();

        if (null != userId) {
            SaasUserRole userRole = new SaasUserRole();
            userRole.setUserId(userId);
            List<SaasUserRole> userRoles = this.saasUserRoleMapper.select(userRole);
            if (null != userRoles && userRoles.size() > 0) {
                for (SaasUserRole userRolee : userRoles) {
                    if (null != userRolee) {
                        SaasRole role1 = this.saasRoleMapper.selectByPrimaryKey(userRolee.getRoleId());
                        if (null != role1) {
                            roleList.add(role1);
                        }
                    }
                }
            }
        }
        return roleList;
    }

    @Override
    public boolean saveSaasRole(SaasRole saasRole) {
        if (saasRole!=null) {
//            saasRole.setRoleId(CreateUUIdUtil.Uuid());
            saasRole.setCreateTime(new Date());
            int status = saasRoleMapper.insertSelective(saasRole);
            if(status > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteSaasRole(String roleId) {
        if(roleId !=null && !"".equals(roleId)){
            int i = saasRoleMapper.deleteByPrimaryKey(roleId);
            if (i > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateSaasRole(SaasRole saasRole) {
        if(saasRole!=null){
            int i =saasRoleMapper.updateByPrimaryKeySelective(saasRole);
            if (i > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<SaasRole> selectSaasRoles(BasePageEntity pageEntity) {
        PageHelper.startPage(pageEntity.getCurrentPage(),pageEntity.getPageSize());
        return saasRoleMapper.selectAll();
    }

    @Override
    public SaasRole selectSaasRoleById(String roleId) {
        if(roleId !=null && !"".equals(roleId)){
            return saasRoleMapper.selectByPrimaryKey(roleId);
        }
        return null;
    }

    @Override
    public List<SaasRole> selectSaasRole(SaasRole saasRole) {
        if(saasRole !=null ){
            return saasRoleMapper.select(saasRole);
        }
        return null;
    }
}
