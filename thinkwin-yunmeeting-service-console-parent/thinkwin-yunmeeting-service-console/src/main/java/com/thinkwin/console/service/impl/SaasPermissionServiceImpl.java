package com.thinkwin.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasPermission;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.common.model.console.SaasUserPermission;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.console.mapper.SaasPermissionMapper;
import com.thinkwin.console.mapper.SaasUserPermissionMapper;
import com.thinkwin.console.service.SaasPermissionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("saasPermissionService")
public class SaasPermissionServiceImpl implements SaasPermissionService {
    @Autowired
    private SaasPermissionMapper saasPermissionMapper;
    @Autowired
    private SaasUserPermissionMapper saasUserPermissionMapper;

    @Override
    public SaasPermission getSaasPermissionById(String permissionId) {
        return saasPermissionMapper.selectByPrimaryKey(permissionId);
    }

    @Override
    public List<SaasPermission> selectSaasUserPermissionByUserId(String userId){
        List<SaasPermission> list = new ArrayList<>();
        SaasUserPermission saasUserPermission = new SaasUserPermission();
        saasUserPermission.setUserId(userId);
        List<SaasUserPermission> saasUserPermissionList = saasUserPermissionMapper.select(saasUserPermission);
        if(null != saasUserPermissionList && saasUserPermissionList.size() > 0){
            for (SaasUserPermission saasUserPermissionn:saasUserPermissionList) {
                String permissionId = saasUserPermissionn.getPermissionId();
                if(null != permissionId && !"".equals(permissionId)){
                    SaasPermission saasPermission = saasPermissionMapper.selectByPrimaryKey(permissionId);
                    list.add(saasPermission);
                }
            }
            return list;
        }
        return null;
    }

    @Override
    public boolean saveSaasPermission(SaasPermission saasPermission){
        if(saasPermission!=null) {
            saasPermission.setPermissionId(CreateUUIdUtil.Uuid());
            saasPermission.setCreateTime(new Date());
            int status = saasPermissionMapper.insertSelective(saasPermission);
            if(status > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteSaasPermission(List<String> permissionIds){
        if(null != permissionIds && permissionIds.size() > 0){
            int num = 0;
            for (String permissionId:permissionIds) {
                if(null != permissionId && !"".equals(permissionId)){
                    int status = saasPermissionMapper.deleteByPrimaryKey(permissionId);
                    if(status <= 0){
                        num = 1;
                    }
                }
            }
            if(num == 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteSaasPermissionById(String permissionId){
        if(null != permissionId && !"".equals(permissionId)){
            int num = saasPermissionMapper.deleteByPrimaryKey(permissionId);
            if(num > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateSaasPermission(SaasPermission permission){
        String permissionId = permission.getPermissionId();
        if(null != permissionId && !"".equals(permissionId)){
            permission.setModifyTime(new Date());
            int num = saasPermissionMapper.updateByPrimaryKeySelective(permission);
            if(num > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public PageInfo<SaasPermission> selectSaasPermission(String condition, BasePageEntity page){
        Example ex = new Example(SaasUser.class, true, true);
        if (StringUtils.isNotBlank(condition)) {
            String cre = "'%" + condition + "%'";
            ex.or().andCondition("org_name like " + cre);
            ex.or().andCondition("url like " + cre);
        }
        ex.setOrderByClause("org_code");
        if(page!=null){
            PageHelper.startPage(page.getCurrentPage(),page.getPageSize());
        }
        List<SaasPermission> saasPermissions = saasPermissionMapper.selectByExample(ex);
        return new PageInfo<>(saasPermissions);
    }

    @Override
    public List<SaasPermission> getSaasPermission(SaasPermission saasPermission) {
        return saasPermissionMapper.select(saasPermission);
    }
}
