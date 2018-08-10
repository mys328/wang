package com.thinkwin.auth.service.impl;

import com.thinkwin.auth.mapper.db.SysRoleMapper;
import com.thinkwin.auth.mapper.db.SysUserMapper;
import com.thinkwin.auth.mapper.db.SysUserRoleMapper;
import com.thinkwin.auth.service.UserRoleService;
import com.thinkwin.common.model.db.SysRole;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.model.db.SysUserRole;
import com.thinkwin.yunmeeting.framework.datasource.dynamicdatasource.DBContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: yinchunlei
 * Date: 2017/5/19.
 * Company: thinkwin
 */
@Service("userRoleService")
public class UserRoleServiceImpl implements UserRoleService {
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysUserRoleMapper userRoleMapper;
    @Autowired
    private SysRoleMapper roleMapper;
    @Override
    //根据用户名获取用户的角色列表
    public List<SysRole> findUserRolesByUserId(String userId) {
        List<SysRole> roleList = new ArrayList<>();
        String dbType = DBContextHolder.getDBType();
        if(dbType == null ||"0".equals(dbType)){
            DBContextHolder.setDBType("plantform_init_yunmeeting_db");
        }
        if(null != userId){
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            List<SysUserRole> userRoles = userRoleMapper.select(userRole);
            if(null != userRoles && userRoles.size() > 0){
                for (SysUserRole userRolee:userRoles) {
                    if(null != userRolee){
                        SysRole role1 = roleMapper.selectByPrimaryKey(userRolee.getRoleId());
                        if(null != role1){
                            roleList.add(role1);
                        }
                    }
                }
            }
        }
        return roleList;
    }

    /**
     * 根据角色主键id集合获取用户主键集合功能
     * @param roleIdList
     * @return
     */
    public List<String> selectUserIdsByRoleIds(List<String> roleIdList){
        if(null != roleIdList && roleIdList.size() > 0) {
            Map map = new HashMap<>();
            map.put("roleIdList",roleIdList);
            return userRoleMapper.selectUserIdsByRoleIds(map);
        }else{
            return null;
        }
    }
}
