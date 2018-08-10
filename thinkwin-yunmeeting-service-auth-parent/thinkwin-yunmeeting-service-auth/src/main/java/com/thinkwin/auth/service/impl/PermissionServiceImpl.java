package com.thinkwin.auth.service.impl;

import com.github.pagehelper.Page;
import com.thinkwin.auth.localService.LocalUserService;
import com.thinkwin.auth.mapper.db.SysPermissionMapper;
import com.thinkwin.auth.mapper.db.SysRolePermissionMapper;
import com.thinkwin.auth.mapper.db.SysUserPermissionMapper;
import com.thinkwin.auth.service.PermissionService;
import com.thinkwin.common.model.db.SysPermission;
import com.thinkwin.common.model.db.SysRolePermission;
import com.thinkwin.common.model.db.SysUserPermission;
import com.thinkwin.common.model.db.SysUserRole;
import com.thinkwin.yunmeeting.framework.datasource.dynamicdatasource.DBContextHolder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * User: yinchunlei
 * Date: 2017/5/19.
 * Company: thinkwin
 */
@Service("permissionService")
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    @Autowired
    private SysUserPermissionMapper sysUserPermissionMapper;

    @Override
    public SysPermission getPermissionById(String permissionId) {
        String dbType = DBContextHolder.getDBType();
        if(dbType == null ||"0".equals(dbType)){
            DBContextHolder.setDBType("plantform_init_yunmeeting_db");
        }
        return sysPermissionMapper.selectByPrimaryKey(permissionId);
    }

    //根据用户的主键ID获取用户的所有权限功能
    public List<SysPermission> selectUserPermissionByUserId(String userId){
        List list = new ArrayList();
        SysUserPermission sysUserPermission = new SysUserPermission();
        sysUserPermission.setUserId(userId);
        List<SysUserPermission> sysUserPermissionList = sysUserPermissionMapper.select(sysUserPermission);
        if(null != sysUserPermissionList && sysUserPermissionList.size() > 0){
            for (SysUserPermission sysUserPermissionn:sysUserPermissionList) {
                String permissionId = sysUserPermissionn.getPermissionId();
                if(null != permissionId && !"".equals(permissionId)){
                    SysPermission sysPermission = sysPermissionMapper.selectByPrimaryKey(permissionId);
                    list.add(sysPermission);
                }
            }
            return list;
        }
        return null;
    }

    //添加新的权限信息
    public boolean savePermission(SysPermission sysPermission){
        String permissionId = sysPermission.getPermissionId();
        if(null != permissionId && !"".equals(permissionId)) {
            sysPermission.setCreateTime(new Date());
            int status = sysPermissionMapper.insertSelective(sysPermission);
            if(status > 0){
                return true;
            }
        }
        return false;
    }

    //权限的批量删除功能
    public boolean deletePermission(List<String> permissionIds){
        if(null != permissionIds && permissionIds.size() > 0){
            int num = 0;
            for (String permissionId:permissionIds) {
                if(null != permissionId && !"".equals(permissionId)){
                    int status = sysPermissionMapper.deleteByPrimaryKey(permissionId);
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

    //根据权限的主键id删除相应的权限信息
    public boolean deletePermissionById(String permissionId){
        if(null != permissionId && !"".equals(permissionId)){
            int num = sysPermissionMapper.deleteByPrimaryKey(permissionId);
            if(num > 0){
                return true;
            }
        }
        return false;
    }

    //权限的修改功能
    public boolean updatePermission(SysPermission permission){
        String permissionId = permission.getPermissionId();
        if(null != permissionId && !"".equals(permissionId)){
            permission.setModifyTime(new Date());
            int num = sysPermissionMapper.updateByPrimaryKeySelective(permission);
            if(num > 0){
                return true;
            }
        }
        return false;
    }

    //带分页功能查询全部权限
    public List<SysPermission> selectPermission(Page page){
        //  DBContextHolder.setDBType("de17fa76b32c4967bb4d141ef0e7933c");
        return sysPermissionMapper.selectAll();
    }

    @Autowired
    private LocalUserService localUserService;
    /**
     * 判断某个用户是否具有访问某个路径的权限功能
     * @param userId
     * @param requestUrl
     * @return
     */
    public boolean getUserJurisdiction(String userId,String requestUrl){
        if(StringUtils.isNotBlank(userId)) {
            List list = new ArrayList();
            List<SysUserRole> SysUserRoles = localUserService.getCurrentUserRoleIds(userId);
            if(null != SysUserRoles && SysUserRoles.size() > 0){
                for (SysUserRole sysUserRole:SysUserRoles) {
                    if(null != sysUserRole){
                        list.add(sysUserRole.getRoleId());
                    }
                }
            }
            if(null != list && list.size() >0){
                //根据角色id获取资源列表功能
                List<String> permissionUrls = selectPermissionUrlsByRoleId(list);
                if(null != permissionUrls && permissionUrls.size() > 0){
                    for (String permissionUrl:permissionUrls) {
                        if(requestUrl.equals(permissionUrl)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;
    /**
     * 根据角色主键获取资源路径集合功能
     * @param userHighestRoleId
     * @return
     */
    public List<String> selectPermissionUrlsByRoleId(String userHighestRoleId){
        List<String> list = new ArrayList<>();
        Example example = new Example(SysRolePermission.class);
        example.createCriteria().andEqualTo("roleId",userHighestRoleId);
        List<SysRolePermission> sysRolePermissions = sysRolePermissionMapper.selectByExample(example);
        if(null != sysRolePermissions){
            List<String> list1 = new ArrayList<>();
            for (SysRolePermission sysRolePermission:sysRolePermissions) {
                if(null != sysRolePermission){
                    String permissionId = sysRolePermission.getPermissionId();
                    if(StringUtils.isNotBlank(permissionId)){
                        list1.add(permissionId);
                    }
                }
            }
            if(null != list1 && list1.size() > 0){
                Map map = new HashMap<>();
                map.put("list1",list1);
                List<String> permissions = sysPermissionMapper.selectPermissionsByIds(map);
                if(null != permissions && permissions.size() > 0){
                    list.removeAll(permissions);
                    list.addAll(permissions);
                }
            }
        }
        return list;
    }

    /**
     * 根据角色主键获取资源路径集合功能(新)
     * @param list2
     * @return
     */
    public List<String> selectPermissionUrlsByRoleId(List<String> list2){
        List<String> list = new ArrayList<>();
        Example example = new Example(SysRolePermission.class);
        example.createCriteria().andIn("roleId",list2);
        List<SysRolePermission> sysRolePermissions = sysRolePermissionMapper.selectByExample(example);
        if(null != sysRolePermissions){
            List<String> list1 = new ArrayList<>();
            for (SysRolePermission sysRolePermission:sysRolePermissions) {
                if(null != sysRolePermission){
                    String permissionId = sysRolePermission.getPermissionId();
                    if(StringUtils.isNotBlank(permissionId)){
                        list1.add(permissionId);
                    }
                }
            }
            if(null != list1 && list1.size() > 0){
                Map map = new HashMap<>();
                map.put("list1",list1);
                List<String> permissions = sysPermissionMapper.selectPermissionsByIds(map);
                if(null != permissions && permissions.size() > 0){
                    list.removeAll(permissions);
                    list.addAll(permissions);
                }
            }
        }
        return list;
    }
}
