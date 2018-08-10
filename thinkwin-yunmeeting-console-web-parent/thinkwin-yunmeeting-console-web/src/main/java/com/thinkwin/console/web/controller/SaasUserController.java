package com.thinkwin.console.web.controller;

import com.github.pagehelper.PageInfo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.console.SaasRole;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.SHA1Util;
import com.thinkwin.common.vo.consoleVo.SaasUserVo;
import com.thinkwin.console.service.SaasUserRoleService;
import com.thinkwin.console.service.SaasUserService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管控制层
 * User: wangxilei
 * Date: 2017/9/14
 * Company: thinkwin
 */

@Controller
@RequestMapping("/saasUser")
public class SaasUserController {

    @Resource
    private SaasUserService saasUserService;
    @Resource
    private SaasUserRoleService saasUserRoleService;

    /**
     * 菜单跳转到用户页面
     */
    @RequestMapping("/gotoUserPage")
    public String gotoInvoicePage() {
        return "console/user";
    }


    /**
     * 根据条件查询用户列表功能
     * @param condition 查询条件
     * @return
     */
    @RequestMapping("/selectSaasUserList")
    @ResponseBody
    public Object selectSaasUserList(String condition,BasePageEntity page) throws InvocationTargetException, IllegalAccessException {
        PageInfo<SaasUser> saasUsers = saasUserService.selectSaasUserList(condition,page);
        List<SaasUserVo> saasUserVos = new ArrayList<>();
        for (SaasUser su:saasUsers.getList()) {
            List<SaasRole> roles = saasUserRoleService.findSaasUserRolesByUserId(su.getId());
            List<Map<String,String>> roleNames = new ArrayList<>();
            SaasUserVo saasUserVo = new SaasUserVo();
            for (SaasRole role:roles) {
                Map<String,String> map = new HashMap<>();
                map.put("code",role.getOrgCode());
                map.put("name",role.getRoleName());
                roleNames.add(map);
            }
            saasUserVo.setRolesName(roleNames);
            ConvertUtils.register(new org.apache.commons.beanutils.converters.DateConverter(null), java.util.Date.class);
            BeanUtils.copyProperties(saasUserVo,su);
            saasUserVos.add(saasUserVo);
        }
        PageInfo<SaasUserVo> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo,saasUsers);
        pageInfo.setList(saasUserVos);
        if(saasUserVos.size()>0){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), pageInfo);
        }else{
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
        }
    }

    /**
     * 根据用户名或手机号判断用户是否存在
     * @param userName 用户名/手机号
     * @return
     */
    @RequestMapping("/selectSaasUserByUserName")
    @ResponseBody
    public Object selectSaasUserByUserName(String userName){
        if (StringUtils.isNotBlank(userName)){
            boolean b = saasUserService.selectSaasUserByUserName(userName);
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 添加新用户功能
     * @return
     */
    @RequestMapping(value = "/saveSaasUser", method = RequestMethod.POST)
    @ResponseBody
    public Object saveSaasUser(String passwordConfirm,String password,String userName,String email,String position){
        if (StringUtils.isNotBlank(userName)&&StringUtils.isNotBlank(email)&&StringUtils.isNotBlank(password)&&StringUtils.isNotBlank(passwordConfirm)){
            if (password.equals(passwordConfirm)){

                SaasUser saasUser = new SaasUser();
                saasUser.setEmail(email);
                SaasUser user = saasUserService.selectSaasUser(saasUser);
                if(user!=null){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "用户名已存在！", BusinessExceptionStatusEnum.Failure.getCode());
                }else{
                    String uuid = CreateUUIdUtil.Uuid();
                    saasUser.setId(uuid);
                    String pw = SHA1Util.SHA1(password);
                    saasUser.setPassword(pw);
                    saasUser.setUserName(userName);
                    saasUser.setPosition(position);
                    saasUser.setCreater(TenantContext.getUserInfo().getUserId());
                    boolean b = saasUserService.saveSaasUser(saasUser);
                    if (b){
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
//                        List<String> list = new ArrayList<>();
//                        list.add("4"); //默认初始角色为 运营人员
//                        boolean b1 = saasUserRoleService.addSaasUserRole(uuid, list);
//                        if(b1){
//                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
//                        }else{
//                            saasUserService.deleteSaasUserByUserId(uuid);
//                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
//                        }
                    }else{
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
                    }
                }
            }else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "两次密码输入不一致", BusinessExceptionStatusEnum.Failure.getCode());
            }
        }else {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
    }


    /**
     * 根据用户的主键ID删除用户功能
     * @param userId 用户Id
     * @return
     */
    @RequestMapping("/deleteSaasUserByUserId")
    @ResponseBody
    public Object deleteSaasUserByUserId(String userId){
        if (StringUtils.isNotBlank(userId)){
            boolean b = saasUserService.deleteSaasUserByUserId(userId);
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 根据用户的主键ID获取用户的信息功能
     * @return
     */
    @RequestMapping("/selectSaasUserByUserId")
    @ResponseBody
    public Object selectSaasUserByUserId(){
        String userId = TenantContext.getUserInfo().getUserId();
        if (StringUtils.isNotBlank(userId)){
            SaasUser saasUser = saasUserService.selectSaasUserByUserId(userId);
            if (saasUser!=null){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasUser);
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }


    /**
     * 更新用户（用户Id不可为空）
     * @return
     */
    @RequestMapping(value = "/updateSaasUser", method = RequestMethod.POST)
    @ResponseBody
    public Object updateSaasUser(String userId,String userName,String email,String position){
        if (StringUtils.isNotBlank(userId)){
            SaasUser saasUser = new SaasUser();
            saasUser.setId(userId);
            saasUser.setEmail(email);
            saasUser.setUserName(userName);
            saasUser.setPosition(position);
            saasUser.setModifyer(TenantContext.getUserInfo().getUserId());
            boolean b = saasUserService.updateSaasUser(saasUser);
            if (b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }

    /**
     * 重置密码
     * @return
     */
    @RequestMapping(value = "/updateSaasUserPassword", method = RequestMethod.POST)
    @ResponseBody
    public Object updateSaasUserPassword(String userId,String passwordConfirm,String password){
        if (StringUtils.isNotBlank(userId)&&StringUtils.isNotBlank(password)&&StringUtils.isNotBlank(passwordConfirm)){
            if (password.equals(passwordConfirm)) {
                SaasUser saasUser = new SaasUser();
                saasUser.setId(userId);
                String pw = SHA1Util.SHA1(password);
                saasUser.setPassword(pw);
                boolean b = saasUserService.updateSaasUser(saasUser);
                if (b) {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
                } else {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
                }
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
    }
}
