package com.thinkwin.console.web.controller;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.console.SaasPermission;
import com.thinkwin.common.model.console.SaasRole;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.console.service.SaasPermissionService;
import com.thinkwin.console.service.SaasRolePermissionService;
import com.thinkwin.console.service.SaasUserRoleService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * 角色权限管理控制层
 * User: wangxilei
 * Date: 2017/9/14
 * Company: thinkwin
 */

@Controller
@RequestMapping("/saasRolePermission")
public class SaasRolePermissionController {
    @Resource
    private SaasRolePermissionService saasRolePermissionService;

    @Resource
    private SaasPermissionService saasPermissionService;

    @Resource
    private SaasUserRoleService saasUserRoleService;

    /**
     * 根据角色ID获取该角色拥有的（已授权）权限列表
     * @param roleId 角色ID
     * @return
     */
    @RequestMapping("/selectSaasPermissionsByRoleId")
    @ResponseBody
    public Object selectSaasPermissionsByRoleId(String roleId){
        if (StringUtils.isNotBlank(roleId)){
            List<SaasPermission> saasPermissions = saasRolePermissionService.findSaasResourcesByRoleId(roleId);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasPermissions);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 根据角色ID获取该角色（未授权）权限列表
     * @param roleId 角色ID
     * @return
     */
    @RequestMapping("/findSaasPermissionsUnAuth")
    @ResponseBody
    public Object findSaasResourcesByRoleId(String roleId){
        if (StringUtils.isNotBlank(roleId)){
            PageInfo<SaasPermission> saasPermissionsAll = saasPermissionService.selectSaasPermission(null,null);
            List<SaasPermission> saasPermissions = saasRolePermissionService.findSaasResourcesByRoleId(roleId);
            List<SaasPermission> list = saasPermissionsAll.getList();
            Iterator<SaasPermission> it = list.iterator();
            while (it.hasNext()){
                SaasPermission next = it.next();
                for (SaasPermission sa:saasPermissions) {
                    if(next.getPermissionId().equals(sa.getPermissionId())){
                        it.remove();
                    }
                }
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), list);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 根据权限父ID获取权限列表
     * @param parentId 角色ID
     * @return
     */
    @RequestMapping("/findSaasPermissionsByParentId")
    @ResponseBody
    public Object findSaasPermissionsByParentId(String parentId){
        if (StringUtils.isNotBlank(parentId)){
            List<SaasPermission> l2 = new ArrayList<>();
            List<SaasPermission> l1 = saasRolePermissionService.findSaasPermissionsByParentId(parentId); //获取该父id下的权限列表
            if(l1.size()>0) {
                String userId = TenantContext.getUserInfo().getUserId();
                List<SaasRole> roleList = saasUserRoleService.findSaasUserRolesByUserId(userId);
                List<SaasPermission> list = new ArrayList<>();
                if (roleList.size() > 0) {
                    for (SaasRole role : roleList) {
                        List<SaasPermission> rolePermissionList = saasRolePermissionService.findSaasResourcesByRoleId(role.getRoleId());
                        list.addAll(rolePermissionList);
                    }
                }
                if(list.size()>0) {
                    //去重
                    Set<SaasPermission> set = new TreeSet<>((o1, o2) -> o1.getPermissionId().compareTo(o2.getPermissionId()));
                    set.addAll(list);
                    List<SaasPermission> saasPermissions = new ArrayList<>(set);
                    for (int i = 0; i < l1.size(); i++) {
                        for (int j = 0; j < saasPermissions.size(); j++) {
                            if (l1.get(i).getPermissionId().equals(saasPermissions.get(j).getPermissionId())) {
                                l2.add(l1.get(i));
                            }
                        }
                    }
                }
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), l2);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 添加角色权限的功能
     * @param roleId 角色Id
     * @param permissionIds 权限Id列表
     * @return
     */
    @RequestMapping("/saveSaasRolePermission")
    @ResponseBody
    public Object saveSaasRolePermission(String roleId, String[] permissionIds){
        if (StringUtils.isNotBlank(roleId)){
            boolean b = false;
            if(permissionIds!=null&&permissionIds.length>0){
                b = saasRolePermissionService.saveSaasRolePermission(roleId, Arrays.asList(permissionIds));
            }else {
                b = saasRolePermissionService.deleteSaasRolePermission(roleId);
            }
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 修改角色权限的功能
     * @param roleId 角色Id
     * @param permissionIds 权限Id列表
     * @return
     */
    @RequestMapping("/updateSaasRolePermission")
    @ResponseBody
    public Object updateSaasRolePermission(String roleId, String[] permissionIds){
        if (StringUtils.isNotBlank(roleId)){
            boolean b = false;
            if(permissionIds!=null&&permissionIds.length>0){
                b = saasRolePermissionService.updateSaasRolePermission(roleId, Arrays.asList(permissionIds));
            }else {
                List<SaasPermission> saasPermissions = saasRolePermissionService.selectSaasPermissionsByRoleId(roleId);
                if(saasPermissions!=null&&saasPermissions.size()>0){
                    b = saasRolePermissionService.deleteSaasRolePermission(roleId);
                }else{
                    b = true;
                }
            }
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 删除某个角色的权限功能
     * @param roleId 角色Id
     * @param permissionIds 权限Id列表
     * @return
     */
    @RequestMapping("/deleteSaasRolePermission")
    @ResponseBody
    public Object deleteSaasRolePermission(String roleId, String[] permissionIds){
        if (StringUtils.isNotBlank(roleId)){
            boolean b = saasRolePermissionService.deleteSaasRolePermission(roleId, Arrays.asList(permissionIds));
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

}
