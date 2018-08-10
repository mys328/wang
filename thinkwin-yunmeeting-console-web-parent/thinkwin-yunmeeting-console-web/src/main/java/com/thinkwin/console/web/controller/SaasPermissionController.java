package com.thinkwin.console.web.controller;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasPermission;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.console.service.SaasPermissionService;
import com.thinkwin.console.service.SaasUserService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 权限管理控制层
 * User: wangxilei
 * Date: 2017/9/14
 * Company: thinkwin
 */

@Controller
@RequestMapping("/saasPermission")
public class SaasPermissionController {
    @Resource
    private SaasPermissionService saasPermissionService;
    @Resource
    private SaasUserService saasUserService;

    /**
     * 菜单跳转到权限页面
     */
    @RequestMapping("/gotoPermissionPage")
    public String gotoInvoicePage() {
        return "console/authority";
    }

    /**
     * 查询全部权限
     * @return
     */
    @RequestMapping("/selectSaasPermission")
    @ResponseBody
    public Object selectSaasPermission(String condition,BasePageEntity page){
        PageInfo<SaasPermission> saasPermissions = saasPermissionService.selectSaasPermission(condition,page);
        PageInfo<SaasUser> saasUsers = saasUserService.selectSaasUserList(null,null);
        List<SaasPermission> saasPermissionsList = saasPermissions.getList();
        List<SaasUser> saasUsersList = saasUsers.getList();
        if (saasPermissionsList.size() > 0 &&saasUsersList.size() > 0){
            for (SaasPermission saasPermission:saasPermissionsList) {
                for (SaasUser saasUser:saasUsersList) {
                    if(saasPermission.getCreaterId()!=null){
                        if(saasPermission.getCreaterId().equals(saasUser.getId())){
                            saasPermission.setCreaterId(saasUser.getUserName());
                        }
                    }
                    if(saasPermission.getModifyerId()!=null){
                        if(saasPermission.getModifyerId().equals(saasUser.getId())){
                            saasPermission.setModifyerId(saasUser.getUserName());
                        }
                    }
                }
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasPermissions);
        }else{
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
        }
    }


    /**
     * 根据权限主键的id获取权限信息
     * @param permissionId 权限Id
     * @return
     */
    @RequestMapping("/getSaasPermissionById")
    @ResponseBody
    public Object getSaasPermissionById(String permissionId){
        if (StringUtils.isNotBlank(permissionId)){
            SaasPermission permission = saasPermissionService.getSaasPermissionById(permissionId);
            if (null != permission){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), permission);
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 根据用户的主键ID获取用户的所有权限功能
     * @param userId 用户Id
     * @return
     */
    @RequestMapping("/selectSaasUserPermissionByUserId")
    @ResponseBody
    public Object selectSaasUserPermissionByUserId(String userId){
        if (StringUtils.isNotBlank(userId)){
            List<SaasPermission> permissions = saasPermissionService.selectSaasUserPermissionByUserId(userId);
            if (null != permissions && permissions.size() > 0){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), permissions);
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 添加新的权限信息
     * @return
     */
    @RequestMapping(value = "/saveSaasPermission", method = RequestMethod.POST)
    @ResponseBody
    public Object saveSaasPermission(String permissionCode,String permissionName,String permissionUrl){
        if (StringUtils.isNotBlank(permissionCode)&&StringUtils.isNotBlank(permissionUrl)&&StringUtils.isNotBlank(permissionName)){
            SaasPermission saasPermission = new SaasPermission();
            saasPermission.setOrgCode(permissionCode);
            List<SaasPermission> list = saasPermissionService.getSaasPermission(saasPermission);
            if(null != list && list.size()>0){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "该权限标识已存在", BusinessExceptionStatusEnum.Failure.getCode());
            }
            saasPermission.setUrl(permissionUrl);
            saasPermission.setOrgName(permissionName);
            saasPermission.setCreaterId(TenantContext.getUserInfo().getUserId());
            boolean b = saasPermissionService.saveSaasPermission(saasPermission);
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 权限的批量删除功能
     * @param permissionIds 权限Id列表
     * @return
     */
    @RequestMapping("/deleteSaasPermission")
    @ResponseBody
    public Object deleteSaasPermission(String[] permissionIds){
        if (permissionIds!=null && permissionIds.length>0){
            boolean b = saasPermissionService.deleteSaasPermission(Arrays.asList(permissionIds));
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 根据权限的主键id删除相应的权限信息
     * @param permissionId 权限Id
     * @return
     */
    @RequestMapping("/deleteSaasPermissionById")
    @ResponseBody
    public Object deleteSaasPermissionById(String permissionId){
        if (StringUtils.isNotBlank(permissionId)){
            boolean b = saasPermissionService.deleteSaasPermissionById(permissionId);
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 权限的修改功能
     * @return
     */
    @RequestMapping(value = "/updateSaasPermission", method = RequestMethod.POST)
    @ResponseBody
    public Object updateSaasPermission(String permissionCode,String permissionId,String permissionName,String permissionUrl){
        if (StringUtils.isNotBlank(permissionCode)&&StringUtils.isNotBlank(permissionId)&&StringUtils.isNotBlank(permissionUrl)&StringUtils.isNotBlank(permissionName)) {
            SaasPermission saasPermission = new SaasPermission();
            saasPermission.setPermissionId(permissionId);
            saasPermission.setOrgCode(permissionCode);
            saasPermission.setUrl(permissionUrl);
            saasPermission.setOrgName(permissionName);
            saasPermission.setModifyerId(TenantContext.getUserInfo().getUserId());
            boolean b = saasPermissionService.updateSaasPermission(saasPermission);
            if (b) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

}
