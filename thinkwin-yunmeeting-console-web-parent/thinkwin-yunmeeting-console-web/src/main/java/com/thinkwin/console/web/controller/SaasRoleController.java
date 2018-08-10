package com.thinkwin.console.web.controller;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasRole;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.console.service.SaasRoleService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 角色管理控制层
 * User: wangxilei
 * Date: 2017/9/14
 * Company: thinkwin
 */

@Controller
@RequestMapping("/saasRole")
public class SaasRoleController {
    @Resource
    private SaasRoleService saasRoleService;

    /**
     * 菜单跳转到角色页面
     */
    @RequestMapping("/gotoRolePage")
    public String gotoInvoicePage() {
        return "console/role";
    }

    /**
     * 获取所有角色列表
     * @return
     */
    @RequestMapping("/findAllSaasRoles")
    @ResponseBody
    public Object findAllSaasRoles(String condition,BasePageEntity page){
        PageInfo<SaasRole> saasUsers = saasRoleService.findAllSaasRoles(condition,page);
        if (saasUsers!=null && saasUsers.getList().size() >0){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasUsers);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
    }



    /**
     * 添加角色功能接口
     * @return
     */
    @RequestMapping(value = "/saveSaasRole", method = RequestMethod.POST)
    @ResponseBody
    public Object saveSaasRole(String roleCode,String roleName){
        if (StringUtils.isNotBlank(roleCode)&&StringUtils.isNotBlank(roleName)){
            SaasRole saasRole = new SaasRole();
            saasRole.setOrgCode(roleCode);
            List<SaasRole> saasRoles = saasRoleService.selectSaasRole(saasRole);
            if(saasRoles!=null&&saasRoles.size()>0){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "该角色Id已存在", BusinessExceptionStatusEnum.Failure.getCode());
            }else {
                saasRole.setRoleId(CreateUUIdUtil.Uuid());
                saasRole.setRoleName(roleName);
                saasRole.setCreaterId(TenantContext.getUserInfo().getUserId());
                boolean b = saasRoleService.saveSaasRole(saasRole);
                if (b){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasRole);
                }else{
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
                }
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
    }

    /**
     * 根据角色主键ID删除角色功能接口
     * @param roleId 角色Id
     * @return
     */
    @RequestMapping("/deleteSaasRole")
    @ResponseBody
    public Object deleteSaasRole(String roleId){
        if (StringUtils.isNotBlank(roleId)){
            boolean b = saasRoleService.deleteSaasRole(roleId);
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
    }

    /**
     * 修改角色的功能
     * @return
     */
    @RequestMapping(value = "/updateSaasRole", method = RequestMethod.POST)
    @ResponseBody
    public Object updateSaasRole(String roleId,String roleCode,String roleName){
        if (StringUtils.isNotBlank(roleId)&&StringUtils.isNotBlank(roleCode)&&StringUtils.isNotBlank(roleName)){
            SaasRole saasRole = new SaasRole();
            saasRole.setRoleId(roleId);
            saasRole.setRoleName(roleName);
            saasRole.setOrgCode(roleCode);
            saasRole.setModifyer(TenantContext.getUserInfo().getUserId());
            saasRole.setModifyTime(new Date());
            boolean b = saasRoleService.updateSaasRole(saasRole);
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
    }

    /**
     * 查询所有的角色功能
     * @param page 分页实体类
     * @return
     */
    @RequestMapping("/selectSaasRoles")
    @ResponseBody
    public Object selectSaasRoles(BasePageEntity page){
        if (null != page){
            List<SaasRole> saasRoles = saasRoleService.selectSaasRoles(page);
            if (null != saasRoles && saasRoles.size()>0){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasRoles);
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
    }

    /**
     * 根据角色主键ID获取角色详细信息
     * @param roleId 角色Id
     * @return
     */
    @RequestMapping("/selectSaasRoleById")
    @ResponseBody
    public Object selectSaasRoleById(String roleId){
        if (StringUtils.isNotBlank(roleId)){
            SaasRole saasRoles = saasRoleService.selectSaasRoleById(roleId);
            if (null != saasRoles){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
    }


}
