package com.thinkwin.console.web.controller;

import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasMenu;
import com.thinkwin.common.model.console.SaasRoleMenu;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.console.service.SaasRoleMenuService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 角色菜单管理控制层
 * User: wangxilei
 * Date: 2017/9/15
 * Company: thinkwin
 */

@Controller
@RequestMapping("/saasRoleMenu")
public class SaasRoleMenuController {
    @Resource
    private SaasRoleMenuService saasRoleMenuService;

    /**
     * 根据角色id获取角色和菜单的关联表集合
     * @param roleId 角色Id
     * @return
     */
    @RequestMapping("/selectMenuIdByRoleId")
    @ResponseBody
    public Object selectMenuIdByRoleId(String roleId){
        if (StringUtils.isNotBlank(roleId)){
            List<SaasRoleMenu> saasRoleMenuList = saasRoleMenuService.selectMenuIdByRoleId(roleId);
            if (saasRoleMenuList != null){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasRoleMenuList);
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 角色菜单的添加功能
     * @param roleId 角色Id
     * @param menuIds 菜单Id列表
     * @return
     */
    @RequestMapping("/saveSaasRoleMenu")
    @ResponseBody
    public Object saveSaasRoleMenu(String roleId,String[] menuIds){
        if (StringUtils.isNotBlank(roleId)&& menuIds!=null && menuIds.length > 0){
            boolean b = saasRoleMenuService.saveSaasRoleMenu(roleId,Arrays.asList(menuIds));
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 角色菜单的批量删除功能
     * @param roleId 角色Id
     * @param menuIds 菜单Id列表
     * @return
     */
    @RequestMapping("/deleteSaasRoleMenu")
    @ResponseBody
    public Object deleteSaasRoleMenu(String roleId,String[] menuIds){
        if (StringUtils.isNotBlank(roleId)&& menuIds!=null && menuIds.length > 0){
            boolean b = saasRoleMenuService.deleteSaasRoleMenu(roleId,Arrays.asList(menuIds));
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 角色菜单的单个删除
     * @param roleId 角色Id
     * @param menuId 菜单Id
     * @return
     */
    @RequestMapping("/deleteSaasRoleMenuOne")
    @ResponseBody
    public Object deleteSaasRoleMenuOne(String roleId,String menuId){
        if (StringUtils.isNotBlank(roleId)&& StringUtils.isNotBlank(menuId)){
            boolean b = saasRoleMenuService.deleteSaasRoleMenuOne(roleId,menuId);
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 角色菜单修改关联关系功能
     * @param roleId 角色Id
     * @param menuIds 菜单Id列表
     * @return
     */
    @RequestMapping("/updateSaasRoleMenu")
    @ResponseBody
    public Object updateSaasRoleMenu(String roleId,String[] menuIds){
        if (StringUtils.isNotBlank(roleId)&& menuIds!=null && menuIds.length > 0){
            boolean b = saasRoleMenuService.updateSaasRoleMenu(roleId,Arrays.asList(menuIds));
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 根据角色id获取该id拥有哪些菜单的对象集合
     * @param roleId 角色Id
     * @param page 分页实体类
     * @return
     */
    @RequestMapping("/selectSaasRoleMenuByRoleId")
    @ResponseBody
    public Object selectSaasRoleMenuByRoleId(String roleId, BasePageEntity page){
        if (StringUtils.isNotBlank(roleId)){
            List<SaasMenu> saasMenus = saasRoleMenuService.selectSaasRoleMenuByRoleId(roleId,page);
            if (saasMenus != null){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasMenus);
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

}
