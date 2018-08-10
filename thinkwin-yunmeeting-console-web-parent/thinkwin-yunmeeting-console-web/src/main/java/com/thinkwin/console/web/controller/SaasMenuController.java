package com.thinkwin.console.web.controller;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasMenu;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.console.service.SaasMenuService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 菜单管理控制层
 * User: wangxilei
 * Date: 2017/9/15
 * Company: thinkwin
 */

@Controller
@RequestMapping("/saasMenu")
public class SaasMenuController {
    @Resource
    private SaasMenuService saasMenuService;

    /**
     * 添加菜单功能
     * @param saasMenu 菜单对象
     * @param roleIds
     * @return
     */
    @RequestMapping("/saveSaasMenu")
    @ResponseBody
    public Object saveSaasMenu(SaasMenu saasMenu, String[] roleIds){
        if (saasMenu!=null){
            boolean b = saasMenuService.saveSaasMenu(saasMenu, Arrays.asList(roleIds));
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 根据菜单的主键删除相应的信息
     * @param menuId 菜单Id
     * @return
     */
    @RequestMapping("/deleteSaasMenuByMenuId")
    @ResponseBody
    public Object deleteSaasMenuByMenuId(String menuId){
        if (StringUtils.isNotBlank(menuId)){
            Integer integer = saasMenuService.deleteSaasMenuByMenuId(menuId);
            if (integer>0){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 修改菜单功能
     * @param saasMenu
     * @return
     */
    @RequestMapping("/updateSaasMenu")
    @ResponseBody
    public Object updateSaasMenu(SaasMenu saasMenu){
        if (saasMenu!=null){
            boolean b = saasMenuService.updateSaasMenu(saasMenu);
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 不带分页查询全部菜单功能
     * @param parentId
     * @return
     */
    @RequestMapping("/selectSaasMenusByUserId")
    @ResponseBody
    public Object selectSaasMenusByUserId(String parentId){
        String userId = TenantContext.getUserInfo().getUserId();
        List<SaasMenu> saasMenus = saasMenuService.selectSaasMenusByUserId(userId, parentId);
        if (saasMenus.size()>0){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasMenus);
        }else{
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
        }
    }

    /**
     * 带分页查询全部菜单功能
     * @param pageEntity
     * @return
     */
    @RequestMapping("/selectSaasMenus")
    @ResponseBody
    public Object selectSaasMenus(BasePageEntity pageEntity){
        PageInfo pageInfo = saasMenuService.selectSaasMenus(pageEntity);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), pageInfo);
    }

    /**
     * 带条件查询菜单集合
     * @param saasMenu
     * @return
     */
    @RequestMapping("/selectSaasMenuByCondition")
    @ResponseBody
    public Object selectSaasMenuByCondition(SaasMenu saasMenu){
        if (saasMenu!=null){
            List<SaasMenu> saasMenus = saasMenuService.selectSaasMenuByCondition(saasMenu);
            if (saasMenus.size()>0){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasMenus);
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 批量删除菜单功能
     * @param menuIds 菜单Id列表
     * @return
     */
    @RequestMapping("/deleteSaasMenu")
    @ResponseBody
    public Object deleteSaasMenu(String[] menuIds){
        if (menuIds!=null && menuIds.length >0){
            boolean b = saasMenuService.deleteSaasMenu(Arrays.asList(menuIds));
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 根据菜单主键id获取菜单详细信息
     * @param menuId
     * @return
     */
    @RequestMapping("/selectSaasMenuByMenuId")
    @ResponseBody
    public Object selectSaasMenuByMenuId(String menuId){
        if (StringUtils.isNotBlank(menuId)){
            SaasMenu saasMenu = saasMenuService.selectSaasMenuByMenuId(menuId);
            if (saasMenu != null){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasMenu);
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

}
