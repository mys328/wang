package com.thinkwin.web.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.thinkwin.auth.service.MenuService;
import com.thinkwin.auth.service.SaasTenantService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.SysMenu;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 菜单的controller层
 * User: yinchunlei
 * Date: 2017/6/12.
 * Company: thinkwin
 */
@RestController
public class MenuController {
    @Resource
    private MenuService menuService;
    @Resource
    private SaasTenantService saasTenantService;
    @Resource
    private FileUploadService fileUploadService;
    @Resource
    private com.thinkwin.core.service.SaasTenantService saasTenantCoreService;

    /**
     * 菜单的添加功能
     * @param sysMenu
     * @return
     */
    @RequestMapping("/saveMenu")
    public ResponseResult addMenu(SysMenu sysMenu,String[] roleIds){
        if(null != sysMenu) {
            boolean status;
            if(null != roleIds && roleIds.length > 0) {
                List<String> list = Arrays.asList(roleIds);
                status = menuService.saveMenu(sysMenu,list);
            }else {
                status = menuService.saveMenu(sysMenu,null);
            }
            if(status) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),true);
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.Failure.getDescription(),false);
    }


    /**
     * 根据菜单主键id删除菜单功能
     * @param menuId
     * @return
     */
    @RequestMapping("/deleteMenuById")
    public ResponseResult deleteMenuById(String menuId){
        if(null != menuId && !"".equals(menuId)){
            int status = menuService.deleteMenu(menuId);
            if(status == 1) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),true);
            }else if(status == 2){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.HaveSon.getDescription(),BusinessExceptionStatusEnum.HaveSon.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.Failure.getDescription(),false);
    }

    /**
     * 菜单的批量删除功能
     * @param menuIds
     * @return
     */
    @RequestMapping("/deleteMenuByIds")
    public ResponseResult deleteMenuByIds(String[] menuIds){
        if(null != menuIds || menuIds.length >0) {
            List menuIds1 = Arrays.asList(menuIds);
            if (null != menuIds1 && menuIds1.size() > 0) {
                boolean b = menuService.deleteMenu(menuIds1);
                if (b) {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), true);
                } else {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), false);
                }
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.Failure.getDescription(),false);
    }


    /**
     * 修改菜单功能接口
     * @param sysMenu
     * @return
     */
    @RequestMapping("/updateMenu")
    public ResponseResult updateMenu(SysMenu sysMenu){
        if(null != sysMenu){
            boolean b = menuService.updateMenu(sysMenu);
            if(b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),true);
            }else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Failure.getDescription(),false);
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.Failure.getDescription(),false);
    }

    /**
     * 根据菜单id获取菜单详情
     * @param menuId
     * @return
     */
    @RequestMapping("/selectMenuById")
    public ResponseResult selectMenuById(String menuId){
        if(null != menuId && !"".equals(menuId)) {
            SysMenu sysMenu = menuService.selectMenuByMenuId(menuId);
            if (null != sysMenu) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), sysMenu);
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(),BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }else {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());

        }
    }

    /**
     * 带分页查询所有的菜单列表功能
     * @param pageEntity
     * @return
     */
    @RequestMapping("/selectMenusByPage")
   public ResponseResult selectMenusByPage(BasePageEntity pageEntity){
        PageInfo pageInfo = menuService.selectMenus(pageEntity);
        if(null != pageInfo){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),pageInfo);
        }else {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.DataNull.getDescription(),pageInfo,BusinessExceptionStatusEnum.DataNull.getCode());
        }
    }

    /**
     * 不分页 查询所有菜单功能列表
     * @param parentId
     * @return
     */
    @RequestMapping("/selectMenus")
    public ResponseResult selectMenus(String  parentId){
        String userId = TenantContext.getUserInfo().getUserId();
        String tenantId = TenantContext.getTenantId();
        if(StringUtils.isNotBlank(userId)) {
           // TenantContext.setTenantId("0");
            SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
            /*SaasTenant saasTenant = saasTenantService.selectSaasTenantServcie(tenantId);*/
            String tenantType = "0";//0免费 1收费
            if(null != saasTenant){
                String tenantType1 = saasTenant.getTenantType();
                if(StringUtils.isNotBlank(tenantType1)){
                    tenantType = tenantType1;
                }
            }
           // TenantContext.setTenantId(tenantId);
            List<SysMenu> sysMenus = new ArrayList<>();
            String s1 = RedisUtil.get(tenantId + "_Menus_" + userId);
            if(StringUtils.isNotBlank(s1)) {
                sysMenus = JSON.parseArray(s1, SysMenu.class);
            }else {
                sysMenus = menuService.selectMenus(userId, parentId, tenantType);
                String s = JSON.toJSONString(sysMenus);
                //把字符串存redis里面
                RedisUtil.set(tenantId + "_Menus_" + userId, s);
                RedisUtil.expire(tenantId + "_Menus_" + userId, 1200);
            }
           if (null != sysMenus && sysMenus.size() > 0) {
               return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), sysMenus);
           } else {
               return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(),sysMenus,BusinessExceptionStatusEnum.DataNull.getCode());
           }
       }
       return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 菜单查询功能接口（不分页带条件查询）
     * @param sysMenu
     * @return
     */
    @RequestMapping("/selectMenusByCondition")
    public ResponseResult selectMenusByCondition(SysMenu sysMenu){
        if(null != sysMenu){
            List<SysMenu> sysMenus = menuService.selectMenuByCondition(sysMenu);
            if(null != sysMenus && sysMenus.size() > 0){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),sysMenus);
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.DataNull.getDescription(),sysMenus,BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.Failure.getDescription());
    }

    /**
     * 带条件加分页功能
     * @param sysMenu
     * @param pageEntity
     * @return
     */
    @RequestMapping("/selectMenusByConditionAndPage")
    public ResponseResult selectMenusByConditionAndPage(SysMenu sysMenu,BasePageEntity pageEntity){
        if(null != sysMenu){
            List<SysMenu> sysMenus = menuService.selectMenuByCondition(sysMenu);
            if(null != sysMenus && sysMenus.size() > 0){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),sysMenus);
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(),sysMenus,BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.ParameterIsNull.getDescription(),BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

}
