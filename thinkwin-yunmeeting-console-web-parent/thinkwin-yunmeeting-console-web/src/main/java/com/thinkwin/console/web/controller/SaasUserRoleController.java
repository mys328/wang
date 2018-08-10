package com.thinkwin.console.web.controller;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.console.SaasRole;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.console.service.SaasRoleService;
import com.thinkwin.console.service.SaasUserRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 用户角色管理控制层
 * User: wangxilei
 * Date: 2017/9/14
 * Company: thinkwin
 */

@Controller
@RequestMapping("/saasUserRole")
public class SaasUserRoleController {

    @Resource
    private SaasUserRoleService saasUserRoleService;

    @Resource
    private SaasRoleService saasRoleService;


    /**
     * 根据用户id 查询(未授权)角色列表
     * @return
     */
    @RequestMapping("/findUserRolesUnAuth")
    @ResponseBody
    public Object findUserRolesUnAuth(String userId){
        if (StringUtils.isNotBlank(userId)) {
            PageInfo<SaasRole> saasUsersAll = saasRoleService.findAllSaasRoles(null,null);
            List<SaasRole> saasUsers = saasRoleService.findUserRolesByUserId(userId);
            List<SaasRole> list = saasUsersAll.getList();
            Iterator<SaasRole> it = list.iterator();
            while (it.hasNext()){
                SaasRole next = it.next();
                for (SaasRole sa:saasUsers) {
                    if(next.getRoleId().equals(sa.getRoleId())){
                        it.remove();
                    }
                }
            }
            for (SaasRole saasRole:list) {
                if(saasRole.getOrgCode().equals("0")){
                    list.remove(saasRole);
                    break;
                }
            }
            if (list.size() > 0) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), list);
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 根据用户id 查询(已授权)角色列表
     * @return
     */
    @RequestMapping("/findUserRolesByUserId")
    @ResponseBody
    public Object findUserRolesByUserId(String userId){
        if (StringUtils.isNotBlank(userId)) {
            List<SaasRole> saasUsers = saasRoleService.findUserRolesByUserId(userId);
            if (saasUsers != null && saasUsers.size() > 0) {
                for (SaasRole saasRole:saasUsers) {
                    if(saasRole.getOrgCode().equals("0")){
                        saasUsers.remove(saasRole);
                        break;
                    }
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasUsers);
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 获取用户的角色列表
     * @return
     */
    @RequestMapping("/findSaasUserRolesByUserId")
    @ResponseBody
    public Object findSaasUserRolesByUserId(String userId){
        if (StringUtils.isNotBlank(userId)){
            List<SaasRole> saasUsers = saasUserRoleService.findSaasUserRolesByUserId(userId);
            if (saasUsers!=null && saasUsers.size() > 0){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasUsers);
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 用户角色的添加功能
     * @param roleIds 角色Id列表
     * @return
     */
    @RequestMapping("/saveSaasUserRole")
    @ResponseBody
    public Object saveSaasUserRole(String[] roleIds,String userId){
        if (StringUtils.isNotBlank(userId)){
            boolean b = false;
            List<SaasRole> userRoles = saasUserRoleService.findSaasUserRolesByUserId(userId);
            boolean flag = false;
            if(userRoles!=null){
                for (SaasRole sr:userRoles){
                    if(sr.getOrgCode()!=null&&sr.getOrgCode().equals("0")){
                        flag = true;
                        break;
                    }
                }
            }
            if (roleIds!=null&&roleIds.length>0){
                List<String> str = new ArrayList<>();
                str.addAll(Arrays.asList(roleIds));
                if(flag){
                    str.add("0");
                }
                b = saasUserRoleService.saveSaasUserRole(userId, str);
            }else{
                if(flag) {
                    if(userRoles.size()==1){
                        b = true;
                    }else{
                        List<String> ids = new ArrayList<>();
                        for (SaasRole saasRole:userRoles){
                            if(!saasRole.getOrgCode().equals("0")){
                                ids.add(saasRole.getRoleId());
                            }
                        }
                        b = saasUserRoleService.deleteSaasUserRole(userId,ids);
                    }
                }else{
                    b = saasUserRoleService.deleteSaasUserRole(userId);
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
     * 删除用户的角色功能
     * @param userId 用户Id
     * @param roleIds 角色Id列表
     * @return
     */
    @RequestMapping("/deleteSaasUserRole")
    @ResponseBody
    public Object deleteSaasUserRole(String userId,String[] roleIds){
        if (StringUtils.isNotBlank(userId)&&roleIds.length>0){
            boolean b = saasUserRoleService.deleteSaasUserRole(userId,Arrays.asList(roleIds));
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }


}
