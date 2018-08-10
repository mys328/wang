package com.thinkwin.web.controller;

import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.*;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.core.*;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.utils.FileUploadUtil;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.utils.validation.LoginRegisterValidationUtil;
import com.thinkwin.common.vo.FastdfsVo;
import com.thinkwin.common.vo.FileVo;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.web.service.FileUploadCommonService;
import com.thinkwin.yunmeeting.weixin.service.WxQrcodeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 类名: PeopleManagementController </br>
 * 描述: 通讯录人员管理controller层</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/7/13 </br>
 */
@RestController
@RequestMapping("/people")
public class PeopleManagementController {

    @Resource
    UserService userService;
    @Resource
    LoginRegisterService loginRegisterService;
    @Resource
    SaasTenantInfoService saasTenantInfoService;
    @Resource
    WxQrcodeService wxQrcodeService;
    @Resource
    SysLogService sysLogService;
    @Resource
    RoleService roleService;
    @Resource
    FileUploadCommonService fileUploadCommonService;
    @Resource
    OrganizationService organizationService;
    @Resource
    SaasTenantService saasTenantCoreService;
    @Resource
    FileUploadService fileUploadService;


    /**
     * 方法名：invitePeople</br>
     * 描述：邀请人员接口</br>
     * 参数：[tenantId] 租户Id</br>
     * 返回值：java.lang.String</br>
     */
    @RequestMapping(value = "/invitepeople", method = RequestMethod.POST)
    public Object invitePeople() {
        //获取租户Id
        String tenantId = TenantContext.getTenantId();
        //判断租户Id是否为空
        if (StringUtils.isBlank(tenantId)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "", "6011");
        }
        SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
        if(null == saasTenant){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
        }
        SaasTenantInfo saasTenantInfo = saasTenantCoreService.selectSaasTenantInfo(tenantId);
        if (null == saasTenantInfo) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
        }
        Map<String, Object> map = new HashMap<>();
        String qrcodePath = saasTenantInfo.getQrcodePath();
        if (StringUtils.isBlank(qrcodePath)) {
            //根据租户Id 获取二维码然后保存库里
            qrcodePath = wxQrcodeService.getPermanentQrcode(tenantId);
            saasTenantInfo.setQrcodePath(qrcodePath);
            //把二维码存库
            saasTenantCoreService.updateSaasTenantInfo(saasTenantInfo);
        }
        //切换数据源
       // TenantContext.setTenantId(tenantId);
        map.put("invitationCode", saasTenantInfo.getCompanyInvitationCode());
        map.put("qrcodePath", qrcodePath);
        map.put("companyName",saasTenantInfo.getTenantName());
        map.put("tenantId",tenantId);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", map);
    }

    @Resource
    private UserRoleService userRoleService;
    /**
     * 方法名：removePeople</br>
     * 描述：删除人员角色</br>
     * 参数：[userIds, userId]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/removepeople", method = RequestMethod.POST)
    public Object removePeople(String[] userIds) {

        //获取用户Id
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        String tenantId = userInfo.getTenantId();
        String userRoleId = null;
        //判断userIds是否为空
        if (null == userIds || userIds.length <= 0) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        List<String> peopleIds = Arrays.asList(userIds);
        //判断用户是否具有删除人员权限
        boolean handlePeoplePermission = userService.isHandlePeoplePermission(userId);
        if (!handlePeoplePermission) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "", "6019");
        }
        List<SysUserRole> SysUserRoles = userService.getCurrentUserRoleIds(userId);
        if(null != SysUserRoles && SysUserRoles.size() > 0){
            List list = new ArrayList();
            for (SysUserRole sysUserRole:SysUserRoles) {
                if(null != sysUserRole){
                    list.add(sysUserRole.getRoleId());
                }
            }
            userRoleId =  Collections.min(list).toString();
        }else{
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(3,"当前用户角色不可为空！！！");
        }
        List<SysRole> sysRoles1 = roleService.selectRolesInfo();
        List<String> roleIdList = new ArrayList();
        if(null != sysRoles1 && sysRoles1.size() > 0){
            for (SysRole sysRole:sysRoles1) {
                if(null != sysRole){
                    String roleId = sysRole.getRoleId();
                    if(StringUtils.isNotBlank(roleId)){
                        int result = userRoleId.compareTo(roleId);
                        if(result == 1 || result == 0){
                            roleIdList.add(roleId);
                        }
                    }
                }
            }
        }
        List<String> userIdss = userRoleService.selectUserIdsByRoleIds(roleIdList);
        if(null == userIdss || userIdss.size() <= 0){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(3,"公司管理员信息有误！！！");
        }
        if(!userIdss.contains(userId)){
            userIdss.add(userId);
        }
        for (String peopleIdd:peopleIds) {
            if(userIdss.contains(peopleIdd)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(3,"您没有此操作权限");
            }
        }
        int nn = 0;
        String content = "";
        //循环删除用户
        for (String peopleId : peopleIds) {
            if (StringUtils.isNotBlank(peopleId)) {
                if (!peopleId.equals(userId)) {
                    SysUser sysUser = new SysUser();
                    sysUser.setId(peopleId);
                    sysUser.setStatus(89);
                    sysUser.setWechat("");
                    sysUser.setOpenId("");
                    sysUser.setIsSubscribe("");
                    boolean user = userService.updateUserByUserId(sysUser);
                    if (user) {
                        //查询用户所有角色Id
                        List<String> sysRoles = userService.selectUserRole(peopleId);
                        //删除用户所有角色信息
                        userService.deleteUserRole(peopleId, sysRoles);
                        //增加用户普通成员角色
                        sysRoles = new ArrayList<>();
                        sysRoles.add("99");
                        userService.saveUserRole(peopleId, sysRoles);
                        //修改已绑定微信的人员
                        SaasUserOauth saasUserOauth = new SaasUserOauth();
                        saasUserOauth.setUserId(peopleId);
                        saasUserOauth.setTenantId(tenantId);
                        List<SaasUserOauth> saasUserOauths = saasTenantCoreService.selectOAuthLoginInfo(saasUserOauth);
                        if (null != saasUserOauths && saasUserOauths.size() > 0) {
                            for (SaasUserOauth userOauth : saasUserOauths) {
                                Integer oauthType = userOauth.getOauthType();
                                userOauth.setIsBind(0);
                                userOauth.setTicket("");
                                userOauth.setTicketTime(null);
                                userOauth.setTenantId("");
                                boolean flag = false;
                                if (oauthType == 1) {
                                    flag =  saasTenantCoreService.updateOAuthLoginInfo(userOauth);
                                }
                                if (oauthType == 2) {
                                    //修改第三方开放平台信息表
                                    flag = saasTenantCoreService.updateOAuthLoginInfo(userOauth);
                                }
                                if(flag){
                                    //删除redis用户缓存
                                    RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_"+userOauth.getUserId());
                                }
                            }
                        }
                    }
                    SysUser sysUser1 = userService.selectUserByUserId(peopleId);
                    if (null != sysUser1) {
                        content += sysUser1.getUserName() + "、";
                    }
                } else {
                    nn = 1;
                }
           // }
        }
        }
        if(nn == 0) {
            //增加操作日志
            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.del_person.toString(), content.substring(0,content.length()-1) + "被移除企业", "", Loglevel.info.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "");
        }else{
            //增加操作日志
            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.del_person.toString(), content.substring(0,content.length()-1) +  "移除企业失败", "企业管理员不可删除或禁用", Loglevel.info.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(2, "企业管理员不可删除或禁用！！！");
        }
    }

    /**
     * 校验操作者的权限功能
     * @return
     */
    public boolean validateUserInfo(String userId,List<String> peopleIds,int flag){
        String userRoleId = null;
        List<SysUserRole> SysUserRoles = userService.getCurrentUserRoleIds(userId);
        if(null != SysUserRoles && SysUserRoles.size() > 0){
            List list = new ArrayList();
            for (SysUserRole sysUserRole:SysUserRoles) {
                if(null != sysUserRole){
                    list.add(sysUserRole.getRoleId());
                }
            }
            userRoleId =  Collections.min(list).toString();
        }else{
            return false;
        }
        List<SysRole> sysRoles1 = roleService.selectRolesInfo();
        List<String> roleIdList = new ArrayList();
        if(null != sysRoles1 && sysRoles1.size() > 0){
            for (SysRole sysRole:sysRoles1) {
                if(null != sysRole){
                    String roleId = sysRole.getRoleId();
                    if(StringUtils.isNotBlank(roleId)){
                        int result = userRoleId.compareTo(roleId);
                        if(result == 1 || result == 0){
                            roleIdList.add(roleId);
                        }
                    }
                }
            }
        }
        List<String> userIdss = userRoleService.selectUserIdsByRoleIds(roleIdList);
        if(null == userIdss || userIdss.size() <= 0){
            return false;
        }
        if(flag != 2) {
            if (!userIdss.contains(userId)) {
                userIdss.add(userId);
            }
        }else {
           for (String s : userIdss) {
                if (s.equals(userId)) {
                    userIdss.remove(s);
                    break;
                }
            }
        }
        for (String peopleIdd:peopleIds) {
            if(userIdss.contains(peopleIdd)){
                return false;
            }
        }
        return true;
    }

    /**
     * 方法名：movePeople</br>
     * 描述：移动用户到某个部门下</br>
     * 参数：[userIds, orgId, userId]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/movepeople")
    public Object movePeople(String[] userIds, String orgId, String orgName) {
        //获取用户Id
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        //判断前台传参是否为空
        if (null == userIds || userIds.length <= 0 || StringUtils.isBlank(orgId)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "", "6001");
        }
        List<String> peopleIds = Arrays.asList(userIds);
        //判断用户是否具有移动人员权限
        boolean handlePeoplePermission = userService.isHandlePeoplePermission(userId);
        if (!handlePeoplePermission) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "", "6019");
        }
        //此处判断组织机构是否存在
        SysOrganization sysOrganization = organizationService.selectOrganiztionById(orgId);
        if (null != sysOrganization) {
            if(null != peopleIds && peopleIds.size()> 0){
                boolean b = validateUserInfo(userId, peopleIds,2);
                if(!b){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(3,"您没有此操作权限");
                }
                //循环修改
                for (String peopleId : peopleIds) {
                    SysUser sysUser = userService.selectUserByUserId(peopleId);
                    if (null != sysUser) {
                        sysUser.setOrgId(orgId);
                        sysUser.setOrgName(orgName);
                        userService.updateUserByUserId(sysUser);
                    }
                }
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "");
        } else {
            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person.toString(), "移动人员时组织机构参数错误!", "", Loglevel.error.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "组织机构不存在！", false);
        }
    }

    /**
     * 方法名：changePeopleState</br>
     * 描述：更换用户状态：启用或禁用</br>
     * 参数：[userIds, userId, state]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/changepeoplestate", method = RequestMethod.POST)
    public Object changePeopleState(String[] userIds, Integer state,String orgId) {
        //获取用户Id
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        //判断前台传参是否为空
        if (null == userIds || userIds.length <= 0 || null == state) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "", "6001");
        }
        List<String> peopleIds = Arrays.asList(userIds);
        //判断用户是否具有移动人员权限
        boolean handlePeoplePermission = userService.isHandlePeoplePermission(userId);
        if (!handlePeoplePermission) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PermissionDenied.getDescription(), BusinessExceptionStatusEnum.PermissionDenied.getCode());
        }
        int nn = 0;
        if(null != peopleIds && peopleIds.size() > 0){
            boolean b = validateUserInfo(userId, peopleIds,1);
            if(!b){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(3,"您没有此操作权限");
            }
            //循环批量修改
        for (String peopleId : peopleIds) {
            if (!peopleId.equals(userId)) {
                SysUser sysUser = userService.selectUserByUserId(peopleId);
                if (null != sysUser) {
                    sysUser.setStatus(state);
                    userService.updateUserByUserId(sysUser);
                }
            } else {
                nn = 1;
            }
        }
    }
        if(nn == 0) {
            if(StringUtils.isBlank(orgId)){
                orgId = "0";
            }
            Map userNumInfo1 = userService.getUserNumInfo1(orgId);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),userNumInfo1,BusinessExceptionStatusEnum.Success.getCode());
        }else{
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(2, "企业管理员不可删除或禁用！！！");
        }
    }

    /**
     * 方法名：addPeople</br>
     * 描述：增加用户</br>
     * 参数：[request, userId]</br>
     * 返回值：java.lang.Object</br>
     */
   // @RequestMapping(value = "/addpeople", method = RequestMethod.POST) (2018/3/7 该方法已被修改为addpeopleModify)
    public Object addPeople(HttpServletRequest request, String img, String fileName, String size) {
        TenantUserVo userInfo = TenantContext.getUserInfo();
        //获取租户Id
        String tenantId = userInfo.getTenantId();
        //获取用户Id
        String userId = userInfo.getUserId();
        try {
            //判断租户Id是否为空
            if (StringUtils.isBlank(tenantId)) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.NotNullTenantId.getDescription(), BusinessExceptionStatusEnum.NotNullTenantId.getCode());
            }
            //判断用户是否具有增加人员权限
            boolean handlePeoplePermission = userService.isHandlePeoplePermission(userId);
            if (!handlePeoplePermission) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PermissionDenied.getDescription(), BusinessExceptionStatusEnum.PermissionDenied.getCode());
            }
            //校验前台传参以及实体赋值
            Map<String, Object> map = LoginRegisterValidationUtil.addPeopleValidat(request);
            if (null != map.get("responseResult")) {
                return map.get("responseResult");
            }
            if (null != map.get("sysUser") && "save".equals(map.get("state"))) {
                //获取sysUser实体对象
                SysUser sysUser = (SysUser) map.get("sysUser");
                //获取saasUserWeb实体对象
                SaasUserWeb userWeb = (SaasUserWeb) map.get("saasUserWeb");
                //增加租户用户中间表
                SaasUserTenantMiddle saasUserTenantMiddle = (SaasUserTenantMiddle) map.get("saasUserTenantMiddle");
                //校验用户是否已存在
                String phoneNumber = sysUser.getPhoneNumber();
                SaasUserWeb saasUserWeb = saasTenantCoreService.selectUserLoginInfo(null, phoneNumber);
                SysUser sysUser1 = new SysUser();
                if (null != saasUserWeb) {
                    String tenantId2 = saasUserWeb.getTenantId();
                    String userId1 = saasUserWeb.getUserId();
                    if(!saasUserWeb.getTenantId().equals(tenantId)){
                        SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId2);
                        if(null != saasTenant){
                            Integer status = saasTenant.getStatus();
                            if(status == 2){
                                Dissolutionuserinfo dissolutionuserinfo = saasTenantCoreService.selectDissolutionUserInfo(userId1);
                                if(null != dissolutionuserinfo){
                                    sysUser1.setId(dissolutionuserinfo.getUserId());
                                    sysUser1.setUserNamePinyin(dissolutionuserinfo.getUserNamePinyin());
                                    sysUser1.setUserName(dissolutionuserinfo.getUserName());
                                    sysUser1.setStatus(89);
                                    sysUser1.setPhoneNumber(dissolutionuserinfo.getPhoneNumber());
                                }
                            }else{
                                //切换数据库
                                TenantContext.setTenantId(tenantId2);
                                sysUser1 = userService.selectUserByUserId(userId1);
                            }
                        }
                    }else{
                        sysUser1 = userService.selectUserByUserId(userId1);
                    }
                    //切回数据库
                    TenantContext.setTenantId(tenantId);
                    if (null != sysUser1 && sysUser1.getStatus() != 89) {
                        sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.add_person.toString(), "添加人员失败", BusinessExceptionStatusEnum.PhoneNumberRegister.getDescription(), Loglevel.error.toString());
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberRegister.getDescription(), BusinessExceptionStatusEnum.PhoneNumberRegister.getCode());
                    } else if(null != sysUser1&& sysUser1.getStatus() == 89){//判断状态为离职人员
                        if(!tenantId2.equals(tenantId)){
                            //String tenantId1 = saasUserWeb.getTenantId();
                            //修改用户登录表
                            saasUserWeb.setTenantId(tenantId);
                            boolean userLoginInfo = saasTenantCoreService.updateUserLoginInfo(saasUserWeb);
                            if(userLoginInfo){
                                //修改用户租户中间表
                                List<SaasUserTenantMiddle> saasUserTenantMiddles = saasTenantCoreService.selectSaasUserTenantMiddle(saasUserWeb.getUserId(), tenantId2);
                                SaasUserTenantMiddle saasUserTenantMiddle1 = saasUserTenantMiddles.get(0);
                                saasUserTenantMiddle1.setTenantId(tenantId);
                                saasTenantCoreService.updateSysUserTenantMiddle(saasUserTenantMiddle1);
                            }
                            sysUser.setId(saasUserWeb.getUserId());
                        }else{
                            //为离职状态时直接修改
                            sysUser.setId(sysUser1.getId());
                            sysUser.setStatus(2);
                            boolean userByUserId = userService.updateUserByUserId(sysUser);
                            if (userByUserId) {
                                //增加操作日志
                                sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.add_person.toString(), sysUser1.getUserName() + "加入企业", "", Loglevel.info.toString());
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
                            }
                        }
                    }
                }else {
                    userWeb.setTenantId(tenantId);
                    //增加用户登录表
                    boolean saveUserLoginInfo = saasTenantCoreService.saveUserLoginInfo(userWeb);
                    if (saveUserLoginInfo) {
                        //增加用户租户中间表
                        saasUserTenantMiddle.setTenantId(tenantId);
                        saasUserTenantMiddle.setCreateId(userId);
                        boolean b = saasTenantCoreService.saveSysUserTenantMiddle(saasUserTenantMiddle);
                        if (!b) {
                            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.add_person.toString(), "添加人员失败", BusinessExceptionStatusEnum.OperateDBError.getDescription(), Loglevel.error.toString());
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
                        }
                    }
                }
                sysUser.setCreater(userId);
                sysUser.setTenantId(tenantId);
                FastdfsVo fastdfsVo = null;
                //调用图片上传
                if (StringUtils.isNotBlank(img)) {
                    String fileId = this.fileUploadCommonService.fileUploadCommon(fastdfsVo, img, fileName, size, null, userId,tenantId);
                    //储存空间计算
                    if(BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription().equals(fileId)){
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription() , BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
                    }
                    sysUser.setPhoto(fileId);
                }
                List<String> list = new ArrayList<>();
                list.add("99");
                boolean saveUser = userService.saveUser(sysUser, list);
                if (saveUser) {
                    //增加操作日志
                    sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.add_person.toString(), sysUser.getUserName() + "加入企业", "", Loglevel.info.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //增加操作日志
        sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.add_person.toString(), "添加人员失败", BusinessExceptionStatusEnum.OperateDBError.getDescription(), Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
    }
    /**
     * 方法名：updatePeople</br>
     * 描述：修改用户</br>
     * 参数：[request]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/updatepeople", method = RequestMethod.POST)
    public Object updatePeople(HttpServletRequest request) {
        TenantUserVo userInfo = TenantContext.getUserInfo();
        //获取租户Id
        String tenantId = userInfo.getTenantId();
        //获取用户Id
        String currentUserId = userInfo.getUserId();
        //判断租户Id是否为空
        if (StringUtils.isBlank(tenantId)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.NotNullTenantId.getDescription(), BusinessExceptionStatusEnum.NotNullTenantId.getCode());
        }
        //判断用户是否具有增加人员权限
        boolean handlePeoplePermission = userService.isHandlePeoplePermission(currentUserId);
        if (!handlePeoplePermission) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PermissionDenied.getDescription(), BusinessExceptionStatusEnum.PermissionDenied.getCode());
        }
        //校验前台传参以及实体赋值
        Map<String, Object> map = LoginRegisterValidationUtil.addPeopleValidat(request);
        if (null != map.get("responseResult")) {
            return map.get("responseResult");
        }
        if (null != map.get("sysUser") && "update".equals(map.get("state"))) {
            //获取sysUser实体对象
            SysUser sysUser = (SysUser) map.get("sysUser");
            //获取用户Id
            String userId = sysUser.getId();
            //校验用户权限
            List<String> peopleIds = new ArrayList<>();
            peopleIds.add(userId);
            boolean validateUserInfo = validateUserInfo(currentUserId, peopleIds, 2);
            if(!validateUserInfo){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(3,"您没有此操作权限");
            }
            SysUser user = userService.selectUserByUserId(userId);
            if (null == user) {
                //增加操作日志
                sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person.toString(), user.getUserName() + "信息修改失败", BusinessExceptionStatusEnum.PhoneNumberNotRegister.getDescription(), Loglevel.error.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberNotRegister.getDescription(), BusinessExceptionStatusEnum.PhoneNumberNotRegister.getCode());
            }
            //修改用户信息
            boolean b = userService.updateUserByUserId(sysUser);
            if (b) {
                //修改租户信息
                List<SysUserRole> userRoleByUserIdAndRoleId = userService.getUserRoleByUserIdAndRoleId(userId, "1");
                if(null!=userRoleByUserIdAndRoleId&&userRoleByUserIdAndRoleId.size()>0){
                    SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
                    if(null!=saasTenant){
                        String userName = sysUser.getUserName();
                        String contacts = saasTenant.getContacts();
                        if(StringUtils.isNotBlank(contacts)&&contacts.equals(userName)){
                        }else{
                            //修改租户表
                            saasTenant.setContacts(userName);
                            saasTenantCoreService.updateSaasTenantService(saasTenant);
                        }
                    }
                }
                //增加操作日志
                String s = comparisonPersonInfo(user);
                if(StringUtils.isNotBlank(s)){
                    s = sysUser.getUserName()+"信息修改成功";
                }
                RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_" + userId);
                sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person.toString(), s, "", Loglevel.info.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
            }else {
                //增加操作日志
                sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person.toString(), sysUser.getUserName() + "修改人员失败", BusinessExceptionStatusEnum.SysError.getDescription(), Loglevel.error.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SysError.getDescription(), BusinessExceptionStatusEnum.SysError.getCode());
            }
        }
        //增加操作日志
        //sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person.toString(), sysUser.getUserName() + "修改人员失败", BusinessExceptionStatusEnum.SysError.getDescription(), Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SysError.getDescription(), BusinessExceptionStatusEnum.SysError.getCode());
    }

    /**
     * 方法名：comparisonPersonInfo</br>
     * 描述：比对修改用户信息</br>
     * 参数：</br>
     * 返回值：</br>
     */
    private String comparisonPersonInfo(SysUser sysUserBefore){
        if(null!=sysUserBefore){
            String userId = sysUserBefore.getId();
            SysUser sysUserAfter = userService.selectUserByUserId(userId);
            String content = "";
            boolean flag = false;
            if(null!=sysUserAfter){
                if(!sysUserAfter.getUserName().equals(sysUserBefore.getUserName())){
                    flag = true;
                    content = sysUserBefore.getUserName()+"信息修改为："+sysUserAfter.getUserName();
                }
                if(!sysUserAfter.getEmail().equals(sysUserBefore.getEmail())&&flag){
                    content = content+"等";
                }else{
                    content = sysUserAfter.getUserName()+"信息修改为："+sysUserAfter.getEmail();
                }
            }
            return content;
        }
        return null;
    }

    /**
     * 方法名：addpeopleModify</br>
     * 描述：增加用户(使用中)</br>
     * 参数：[request, userId]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/addpeopleModify", method = RequestMethod.POST)
    public Object addpeopleModify (HttpServletRequest request, String img, String fileName, String size,MultipartFile file) {
        TenantUserVo userInfo = TenantContext.getUserInfo();
        //获取租户Id
       // String tenantId = userInfo.getTenantId();
        String tenantId = TenantContext.getTenantId();
                //获取用户Id
        String userId = userInfo.getUserId();
        try {
            //判断租户Id是否为空
            if (StringUtils.isBlank(tenantId)) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.NotNullTenantId.getDescription(), BusinessExceptionStatusEnum.NotNullTenantId.getCode());
            }
            //判断用户是否具有增加人员权限
            boolean handlePeoplePermission = userService.isHandlePeoplePermission(userId);
            if (!handlePeoplePermission) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PermissionDenied.getDescription(), BusinessExceptionStatusEnum.PermissionDenied.getCode());
            }
            //校验前台传参以及实体赋值
            Map<String, Object> map = LoginRegisterValidationUtil.addPeopleValidat(request);
            if (null != map.get("responseResult")) {
                return map.get("responseResult");
            }
            if (null != map.get("sysUser") && "save".equals(map.get("state"))) {
                //获取sysUser实体对象
                SysUser sysUser = (SysUser) map.get("sysUser");
                //获取saasUserWeb实体对象
                SaasUserWeb userWeb = (SaasUserWeb) map.get("saasUserWeb");
                //增加租户用户中间表
                SaasUserTenantMiddle saasUserTenantMiddle = (SaasUserTenantMiddle) map.get("saasUserTenantMiddle");

                //校验用户是否已存在
                String phoneNumber = sysUser.getPhoneNumber();
                SaasUserWeb saasUserWeb = saasTenantCoreService.selectUserLoginInfo(null, phoneNumber);
               // SysUser sysUser1 = new SysUser();
                if (null != saasUserWeb) {
                    String userId1 = saasUserWeb.getUserId();
                    sysUser.setId(userId1);
                    sysUser.setCreater(userId);
                    sysUser.setTenantId(tenantId);
                    String tenantId2 = saasUserWeb.getTenantId();
                    if(!tenantId2.equals(tenantId)){
                        SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId2);
                        if(null != saasTenant){
                            Integer status = saasTenant.getStatus();
                            if(2 == status){
                                saasTenantCoreService.deleteDissolutionUserInfoByUserId(userId1);
                                sysUser.setStatus(89);
                            }else{
                                //切换数据库
                                TenantContext.setTenantId(tenantId2);
                                SysUser sysUser2 = userService.selectUserByUserId(userId1);
                                if(null != sysUser2){
                                    sysUser.setStatus(sysUser2.getStatus());
                                }
                            }
                        }
                    }else{
                        SysUser sysUser2 = userService.selectUserByUserId(saasUserWeb.getUserId());
                        if(null != sysUser2){
                            sysUser.setStatus(sysUser2.getStatus());
                        }
                    }
                    //切回数据库
                    TenantContext.setTenantId(tenantId);
                    if (null != sysUser && sysUser.getStatus() != 89) {
                        sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.add_person.toString(), "添加人员失败", BusinessExceptionStatusEnum.PhoneNumberRegister.getDescription(), Loglevel.error.toString());
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberRegister.getDescription(), BusinessExceptionStatusEnum.PhoneNumberRegister.getCode());
                    } else if(null != sysUser&& sysUser.getStatus() == 89){//判断状态为离职人员
                        if(!saasUserWeb.getTenantId().equals(tenantId)){
                            String tenantId1 = saasUserWeb.getTenantId();
                            //修改用户登录表
                            saasUserWeb.setTenantId(tenantId);
                            boolean userLoginInfo = saasTenantCoreService.updateUserLoginInfo(saasUserWeb);
                            if(userLoginInfo){
                                //修改用户租户中间表
                                List<SaasUserTenantMiddle> saasUserTenantMiddles = saasTenantCoreService.selectSaasUserTenantMiddle(saasUserWeb.getUserId(), tenantId1);
                                if(null != saasUserTenantMiddles && saasUserTenantMiddles.size()>0){
                                    SaasUserTenantMiddle saasUserTenantMiddle1 = saasUserTenantMiddles.get(0);
                                    saasUserTenantMiddle1.setTenantId(tenantId);
                                    saasTenantCoreService.updateSysUserTenantMiddle(saasUserTenantMiddle1);
                                }
                            }
                            sysUser.setId(saasUserWeb.getUserId());
                        }else{
                            //为离职状态时直接修改
                            sysUser.setId(sysUser.getId());
                            sysUser.setStatus(2);
                            boolean userByUserId = userService.updateUserByUserId(sysUser);
                            if (userByUserId) {
                                //增加操作日志
                                sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.add_person.toString(), sysUser.getUserName() + "加入企业", "", Loglevel.info.toString());
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
                            }
                        }
                    }
                }else {
                    userWeb.setTenantId(tenantId);
                    //增加用户登录表
                    boolean saveUserLoginInfo = saasTenantCoreService.saveUserLoginInfo(userWeb);
                    if (saveUserLoginInfo) {
                        //增加用户租户中间表
                        saasUserTenantMiddle.setTenantId(tenantId);
                        saasUserTenantMiddle.setCreateId(userId);
                        boolean b = saasTenantCoreService.saveSysUserTenantMiddle(saasUserTenantMiddle);
                        if (!b) {
                            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.add_person.toString(), "添加人员失败", BusinessExceptionStatusEnum.OperateDBError.getDescription(), Loglevel.error.toString());
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
                        }
                    }
                }
                sysUser.setCreater(userId);
                sysUser.setTenantId(tenantId);
                sysUser.setStatus(2);
                FastdfsVo fastdfsVo = null;
                FileVo vo = null;
                //图片是否为空
                if(file != null) {
                    //调用图片上传
                    String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                    Map<String, String> maps = new HashMap<>();
                    maps.put("big", "100_100");
                    maps.put("in", "64_64");
                    maps.put("small", "32_32");
                    //处理图片
                    //Map<String, BufferedImage> imageMap = ResizeImage.resizeImage(file.getBytes(), maps);
                    //fileId = fileUploadCommonService.uploadFileCommon(imageMap, ext, file.getBytes(), tenantId, userId);
                    SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
                    List<FastdfsVo> vos =  FileUploadUtil.fileUpload(maps,file.getBytes(),"png");
                    vo = fileUploadService.insertFileUpload("3",userId,tenantId,saasTenant.getBasePackageSpaceNum(),"png",vos);
                    if (!"0".equals(vo.getId())) {
                        if (!"".equals(vo.getId())) {
                            sysUser.setPhoto(vo.getId());
                        } else {
                            sysLogService.createLog(BusinessType.meetingOp.toString(), EventType.add_meeting_room.toString(), "修改会议室失败", BusinessExceptionStatusEnum.Failure.getDescription(), Loglevel.error.toString());
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
                        }
                    } else {
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription(), BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
                    }
                }else{
                    sysUser.setPhoto("");
                }
                List<String> list = new ArrayList<>();
                list.add("99");
                boolean saveUser = userService.saveUser(sysUser, list);
                if (saveUser) {
                   // Map<String, String> imgMap = fileUploadCommonService.selectFileCommon(fileId);
                    if(null != vo) {
                        Map map1 = new HashMap();
                        String primary = vo.getPrimary();
                        String big = vo.getBig();
                        String in = vo.getIn();
                        String small = vo.getSmall();
                        map1.put("primary", primary);
                        map1.put("big", big);
                        map1.put("in", in);
                        map1.put("small", small);
                        userService.saveImageUrl(userId, map1, vo.getId());
                    }
                    //增加操作日志
                    sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.add_person.toString(), sysUser.getUserName() + "加入企业", "", Loglevel.info.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //增加操作日志
        sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.add_person.toString(), "添加人员失败", BusinessExceptionStatusEnum.OperateDBError.getDescription(), Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
    }












}
