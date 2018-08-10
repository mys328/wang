package com.thinkwin.web.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.thinkwin.SMSsender.service.SMSsenderService;
import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.OrganizationService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.promotion.CapacityConfig;
import com.thinkwin.common.dto.promotion.PricingConfigDto;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.core.*;
import com.thinkwin.common.model.db.SysOrganization;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.utils.validation.LoginRegisterValidationUtil;
import com.thinkwin.common.utils.wechat.WechatCommonUtil;
import com.thinkwin.common.utils.wechat.WechatConfig;
import com.thinkwin.common.utils.wechat.WechatOAuthProcessUtil;
import com.thinkwin.common.vo.WechatAccessTokenVo;
import com.thinkwin.common.vo.WechatSNSUserInfoVo;
import com.thinkwin.core.service.LoginRegisterCoreService;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.core.service.SaasUserOauthInfoService;
import com.thinkwin.core.service.SaasUserOauthService;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.promotion.service.PricingConfigService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static com.thinkwin.common.log.LocalIpUtil.getIpAddr;

/**
 * 类名: LoginRegisterController </br>
 * 描述: 登录注册Controller</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/5/19 </br>
 */
@Controller
@RequestMapping("/system")
public class LoginRegisterController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(LoginRegisterController.class);
    @Resource
    private LoginRegisterService loginRegisterService;
    @Resource
    private UserService userService;
    @Resource
    private SMSsenderService sMSsenderService;
    @Resource
    private SysLogService sysLogService;
    @Resource
    private OrganizationService organizationService;
    @Resource
    private SaasTenantService saasTenantCoreService;
    @Resource
    private LoginRegisterCoreService loginRegisterCoreService;
    @Resource
    private SaasUserOauthInfoService saasUserOauthInfoService;
    @Resource
    private SaasUserOauthService saasUserOauthService;
    @Resource
    PricingConfigService pricingConfigService;

    @RequestMapping("/login")
    @ResponseBody
    public Object login(String account, String password) throws Exception {
        // 调用 service 层验证登录
        SaasUserWeb saasUserWeb = new SaasUserWeb();
        saasUserWeb.setAccount(account);
        saasUserWeb.setPassword(password);
        SaasUserWeb saasUser = this.saasTenantCoreService.selectUserLoginInfo(saasUserWeb);
        if (null != saasUser) {
            //登录成功修改最后一次登录时间
            saasUser.setLastLoginTime(new Date());
            this.saasTenantCoreService.updateUserLoginInfo(saasUser);
            Map<String, Object> model = new HashMap<>();
            model.put("data", saasUser);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), model);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) throws Exception {
        // Invalidates this session then unbinds any objects bound to it.
        session.invalidate();
        // 重定向到登录页面 或 首页等

        return "redirect:/items/query_items";
    }

    /**
     * 确认请求来自微信服务器  微信的回调
     */
    @RequestMapping(value = "/oauthtest", method = RequestMethod.GET)
    public String OAuthTest(HttpServletRequest request, HttpServletResponse response, Model model) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        // 用户同意授权后，能获取到code
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        // 用户同意授权
        if (!"authdeny".equals(code)) {
            // 获取网页授权access_token
            WechatAccessTokenVo wechatAccessTokenVo = WechatOAuthProcessUtil.getOauthAccessToken(code);
            if (null == wechatAccessTokenVo) {
                if (state.equals("register")) {
                    return "redirect:/system/wechatregisterpage";
                }
                return "redirect:/system/loginpage";
            }
            // 网页授权接口访问凭证
            String accessToken = wechatAccessTokenVo.getAccessToken();
            // 用户标识
            String openId = wechatAccessTokenVo.getOpenId();
            // 获取用户信息
            WechatSNSUserInfoVo snsUserInfo = WechatOAuthProcessUtil.getSNSUserInfo(accessToken, openId);
            Map<String, Object> map = new HashMap<>();
            map.put("wechatAccessTokenVo", wechatAccessTokenVo);
            map.put("snsUserInfo", snsUserInfo);
            String s = JSON.toJSONString(map);
            // 用户unionid
            String unionId = snsUserInfo.getUnionid();
            //把字符串存redis里面
            RedisUtil.set("WeChat" + unionId, s);

            request.setAttribute("uId", unionId);
            request.setAttribute("photo", snsUserInfo.getHeadImgUrl());
            request.setAttribute("name", snsUserInfo.getNickname());
            //登录之前查询微信信息是否修改
            Map<String, Object> map1 = saasTenantCoreService.checkWechatInfoChange(snsUserInfo);
            SaasUserOauthInfo saasUserOauthInfo = (SaasUserOauthInfo) map1.get("saasUserOauthInfo");
            List<SaasUserOauth> userOauth = (List<SaasUserOauth>) map1.get("saasUserOauth");
            boolean isWechatUpdate = false;
            //修改微信登录表
            if (null != userOauth && userOauth.size()>0) {
                isWechatUpdate = true;
                for(SaasUserOauth userOauth1:userOauth) {
                    saasTenantCoreService.updateOAuthLoginInfo(userOauth1);
                }
            }
            if (null != saasUserOauthInfo) {
                saasTenantCoreService.updateOAuthUserInfo(saasUserOauthInfo);
            }
            //根据微信unionId 查询用户是否绑定微信
            SaasUserOauth saasUserOauth = new SaasUserOauth();
            saasUserOauth.setOauthUnionId(unionId);
            List<SaasUserOauth> saasUserOauth1 = saasTenantCoreService.selectOAuthLoginInfo(saasUserOauth);
            //判断该请求是注册请求
            if (state.equals("register")) {
                if (null == saasUserOauth1) {
                    //跳转到微信注册页面
                    request.getRequestDispatcher("/system/registerpage").forward(request, response);
                    return null;
                } else {
                    for(SaasUserOauth saasUserOauth2:saasUserOauth1){
                        Integer oauthType = saasUserOauth2.getOauthType();
                        if((oauthType==1&&saasUserOauth2.getIsBind()==1)||(oauthType==2&&saasUserOauth2.getIsBind()==1)){
                            model.addAttribute("info", "你已经是企云会的注册用户，请返回登录");
                            WechatConfig wechatConfig = new WechatConfig();
                            model.addAttribute("appId",wechatConfig.getAppId());
                            model.addAttribute("callBackUrl",wechatConfig.getCallBackUrl());
                            model.addAttribute("webSiteDomain",wechatConfig.getWebSiteDomain());
                            return "/login-register/wechat-register";
                        }
                    }
                    //跳转到微信注册页面
                    request.getRequestDispatcher("/system/registerpage").forward(request, response);
                    return null;
                }
            } else {
                //用户未绑定  跳转到绑定微信 或者开通企业页面
                if (null == saasUserOauth1) {
                    request.getRequestDispatcher("/system/corporateaccount").forward(request, response);
                    return null;
                } else {
                    boolean b = false;
                    //根据用户Id查询用户  判断用户状态是否正常
                    for(SaasUserOauth saasUserOauth2:saasUserOauth1){
                        Integer oauthType = saasUserOauth2.getOauthType();
                        if((oauthType==1&&saasUserOauth2.getIsBind()==1)||(oauthType==2&&saasUserOauth2.getIsBind()==1)){
                            String userId = saasUserOauth2.getUserId();
                            String tenantId = saasUserOauth2.getTenantId();
                            if(StringUtils.isBlank(tenantId)){
                                request.getRequestDispatcher("/system/corporateaccount").forward(request, response);
                                return null;
                            }
                            SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
                            if(null == saasTenant){
                                request.getRequestDispatcher("/system/corporateaccount").forward(request, response);
                                return null;
                            }
                            Integer status1 = saasTenant.getStatus();
                            if(2 != status1){
                                //切换数据源
                                TenantContext.setTenantId(tenantId);
                                SysUser sysUser = userService.selectUserByUserId(userId);
                                if(null!=sysUser) {
                                    Integer status = sysUser.getStatus();
                                    //判断用户为离职状态  跳转到注册新企业
                                    if (89 == status) {
                                        request.getRequestDispatcher("/system/corporateaccount").forward(request, response);
                                        return null;
                                    }
                                    if(isWechatUpdate){
                                        if(StringUtils.isNotBlank(sysUser.getWechat())){
                                            //修改微信名
                                            sysUser.setWechat(userOauth.get(0).getOauthUserName());
                                            userService.updateUserByUserId(sysUser);
                                        }
                                    }
                                }
                                b = true;
                            }else{
                                request.getRequestDispatcher("/system/createenterprispage?uId="+unionId+"&userId="+userId).forward(request, response);
                                return null;
                            }
                        }
                    }
                    if(!b){
                        request.getRequestDispatcher("/system/corporateaccount").forward(request, response);
                        return null;
                    }
                }
                //直接登录
                //return "redirect:/system/newgotologin?username=" + unionId;
                request.getRequestDispatcher("/system/newgotologin?username="+unionId).forward(request, response);
                return null;
            }

        }
        return null;
    }

    /**
     * 方法名：newGoToLogin</br>
     * 描述：微信请求登录接口</br>
     */
    @RequestMapping("/newgotologin")
    public String newGoToLogin(String username, Model model) {
        model.addAttribute("userName", username);
        return "index3";
    }

    //微信登录
    @RequestMapping(value = "/thirdpartylogin", method = RequestMethod.GET)
    @ResponseBody
    public void thirdPartyLogin(HttpServletRequest req, HttpServletResponse resp) {
        try {
            WechatOAuthProcessUtil.getOAuthCode(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法名：getVerifyCode</br>
     * 描述：获取验证码接口</br>
     * 参数：[phoneNumber]  手机号码</br>
     * 返回值：java.lang.Object 返回json对象</br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/24  </br>
     */
    @RequestMapping(value = "/getverifycode", method = RequestMethod.POST)
    @ResponseBody
    public Object getVerifyCode(String phoneNumber, Integer type) {
        Map<String, Object> map = new HashMap<>();
        Integer isRegist = null;
        boolean mobile = ValidatorUtil.isMobile(phoneNumber);
        if (null != phoneNumber && null != type && mobile) {
            boolean isSysUserWeb = this.saasTenantCoreService.checkPhoneNumber(phoneNumber);
            //查询该手机号是否存在
            isRegist = !isSysUserWeb ? 0 : 1;
            map.put("isRegist", isRegist);
            //忘记密码 用户不存在  获取验证码失败
            if (isRegist == 0 && type == 2) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberNotRegister.getDescription(), map, BusinessExceptionStatusEnum.PhoneNumberNotRegister.getCode());
            }
            //注册 用户存在 注册不成功
            if (isRegist == 1 && type == 1) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberRegister.getDescription(), map, BusinessExceptionStatusEnum.PhoneNumberRegister.getCode());
            }
            //新增邀请人员用户已存在判断是否是离职状态
            if(isRegist == 1 && type == 4){
                SaasUserWeb saasUserWeb = saasTenantCoreService.selectUserLoginInfo(null, phoneNumber);
                if(null!=saasUserWeb) {
                    String tenantId = saasUserWeb.getTenantId();
                    SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
                    if (null != saasTenant && saasTenant.getStatus() != 2){
                        String userId = saasUserWeb.getUserId();
                    //切换数据源
                    TenantContext.setTenantId(tenantId);
                    SysUser sysUser = userService.selectUserByUserId(userId);
                    if (null != sysUser) {
                        Integer status = sysUser.getStatus();
                        if (status != 89) {
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberRegister.getDescription(), map, BusinessExceptionStatusEnum.PhoneNumberRegister.getCode());
                        }
                    }
                }
                }
            }
            //变更管理员
            if (type == 3) {
                if (isRegist == 0) {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberNotRegister.getDescription(), map, BusinessExceptionStatusEnum.PhoneNumberNotRegister.getCode());
                }
                SaasUserWeb saasUserWeb = saasTenantCoreService.selectUserLoginInfo(null, phoneNumber);
                if (null == saasUserWeb) {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberNotTenant.getDescription(), map, BusinessExceptionStatusEnum.PhoneNumberNotTenant.getCode());
                }
                String userId = saasUserWeb.getUserId();
                String tenantId = saasUserWeb.getTenantId();
                if(StringUtils.isBlank(tenantId)) {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(),map,BusinessExceptionStatusEnum.ParamErr.getCode());
                }
                SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
                if(null == saasTenant){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(),map,BusinessExceptionStatusEnum.ParamErr.getCode());
                }
                if(saasTenant.getStatus() == 2){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberNotTenant.getDescription(), map, BusinessExceptionStatusEnum.PhoneNumberNotTenant.getCode());
                }
                TenantContext.setTenantId(tenantId);
                SysUser sysUser = userService.selectUserByUserId(userId);
                if(null!=sysUser){
                    Integer status = sysUser.getStatus();
                    if(status == 89){
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberNotTenant.getDescription(), map, BusinessExceptionStatusEnum.PhoneNumberNotTenant.getCode());
                    }
                    if(status == 3){
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.UserWasDisabled.getDescription(), map, BusinessExceptionStatusEnum.UserWasDisabled.getCode());
                    }
                }
                //查询用户权限表  如果为系统管理员则返回不能修改成自己
                /*List<String> list = userService.selectUserRole(userId);
                if (null!=list&&list.size() > 0) {
                    for (String role : list) {
                        if (role.equals("1")) {
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PermissionDenied.getDescription(), map, BusinessExceptionStatusEnum.PermissionDenied.getCode());
                        }
                    }
                } else {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberNotTenant.getDescription(), map, BusinessExceptionStatusEnum.PhoneNumberNotTenant.getCode());
                }*/
            }
            String code = sMSsenderService.SMSsender(phoneNumber, SMSCode.SENDTEMPLATE_YUMEETING);
            //验证码存redis里面
            RedisUtil.set("QYH_SMS_" + phoneNumber, code);
            //设置验证码过期时间
            RedisUtil.expire("QYH_SMS_" + phoneNumber, 600);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 方法名：regist</br>
     * 描述：用户注册接口</br>
     * 参数：[rep] </br>
     * 返回值：org.springframework.web.servlet.ModelAndView</br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/24  </br>
     */
    @RequestMapping(value = "/regist", method = RequestMethod.POST)
    @ResponseBody
    public Object regist(HttpServletRequest rep) {
        Map<String, Object> map = new HashMap<>();
        //校验前台传参
        Map<String, Object> map2 = registerParam(rep);
        Map<String, Object> infoMap = loginRegisterCoreService.registerValidation(map2);
        //获取校验结果
        ResponseResult responseResult = (ResponseResult) infoMap.get("responseResult");
        //校验不通过
        if (responseResult.getIfSuc() == 0) {
            return responseResult;
        }
        //获取租户信息
        SaasTenant saasTenant = (SaasTenant) infoMap.get("saasTenant");
        //获取登录用户信息
        SaasUserWeb saasUserWeb = (SaasUserWeb) infoMap.get("saasUserWeb");
        String phoneNumber = saasUserWeb.getAccount();
        //获取redis里面的验证码
        String code = RedisUtil.get("QYH_SMS_" + phoneNumber);
        if (StringUtils.isBlank(code)) {
            //参数状态
            map.put("paramState", "error");
            map.put("param", "verifyCode");
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), map, BusinessExceptionStatusEnum.VerifyCodeError.getCode());
        }
        //增加日志接口2个参数
        String ua = ((HttpServletRequest) rep).getHeader("user-agent");
//        String source = "PC";
//        if (StringUtils.containsIgnoreCase(ua, "micromessenger")) {
//            source = "Weixin";
//        } else if (StringUtils.containsIgnoreCase(ua, "iPhone")) {
//            source = "iPhone";
//        } else if (StringUtils.containsIgnoreCase(ua, "Android")) {
//            source = "Android";
//        }
        String source = RequestSourceUtil.getRequestSource(ua);
//      String ip = rep.getRemoteAddr();
        String ip = getIpAddr(rep);

        //获取前台验证码
        String verifyCode = (String) infoMap.get("verifyCode");
        //校验验证码
        if (code.equals(verifyCode)) {
            //删除redis里面的验证码
            RedisUtil.remove("QYH_SMS_" + phoneNumber);
            //获取用户信息
            SysUser sysUser = (SysUser) infoMap.get("sysUser");
            //获取租户信息表
            SaasTenantInfo saasTenantInfo = (SaasTenantInfo) infoMap.get("saasTenantInfo");
            //获取第三方登录信息表
            SaasUserOauth saasUserOauth = null;
            SaasUserOauthInfo saasUserOauthInfo = null;
            if (null != infoMap.get("saasUserOauth")) {
                saasUserOauth = (SaasUserOauth) infoMap.get("saasUserOauth");
                saasUserOauthInfo = (SaasUserOauthInfo) infoMap.get("saasUserOauthInfo");
            }
            SysUser sysUser1 = loginRegisterCoreService.saveUserLoginAndTenantInfo(saasUserWeb, saasTenant, sysUser, saasTenantInfo, saasUserOauth, saasUserOauthInfo);
            if (null!=sysUser1) {
                List<String> sysRoleIds = new ArrayList<>();
                String tenantId = sysUser1.getTenantId();
                //切换数据源
                TenantContext.setTenantId(tenantId);
                String orgId = "1";
                sysUser1.setOrgId(orgId);
                sysUser1.setOrgName(saasTenant.getTenantName());
                boolean saveUser = userService.saveUser(sysUser1, sysRoleIds, "1");
                if (saveUser) {
                    //增加组织表
                    SysOrganization sysOrganization = new SysOrganization();
                    sysOrganization.setCreateTime(new Date());
                    sysOrganization.setId(orgId);
                    sysOrganization.setStatus(1);
                    sysOrganization.setOrgName(saasTenant.getTenantName());
                    sysOrganization.setOrgNamePinyin(PingYinUtil.getPingYin(saasTenant.getTenantName()));
                    sysOrganization.setParentId("0");
                    sysOrganization.setCompositor(1);
                    boolean b = organizationService.saveOrganization(sysOrganization);
                    if (b) {
                        //增加登录注册日志
                        sysLogService.createLog(BusinessType.registerOp.toString(), EventType.platform_register.toString(),sysUser1.getUserName()+ "创建"+saasTenant.getTenantName()+"注册成功", "", Loglevel.info.toString(), ip, source);
                        //删除redis用户缓存
                        RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_"+sysUser1.getId());
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
                    }
                }
            }
        } else {
            //参数状态
            map.put("paramState", "error");
            map.put("param", "verifyCode");
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), map);
        }
        //增加登录注册日志
        sysLogService.createLog(BusinessType.registerOp.toString(), EventType.platform_register.toString(), "创建"+saasTenant.getTenantName()+"注册失败", BusinessExceptionStatusEnum.OperateDBError.getDescription(), Loglevel.error.toString(), ip, source);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
    }

    private Map<String,Object> registerParam(HttpServletRequest rep){
        String tenantName = rep.getParameter("tenantName");//公司名称
        String userName = rep.getParameter("userName");//用户名
        String phoneNumber = rep.getParameter("phoneNumber");//用户手机号码
        String password = rep.getParameter("password");//密码
        String verifyCode = rep.getParameter("verifyCode");//验证码
        String deviceToken = rep.getParameter("deviceToken");//设备token
        String deviceType = rep.getParameter("deviceType");//设备类型
        String unionId = rep.getParameter("uId");//用户第三方唯一Id
        Map<String,Object> map = new HashMap<>();
        map.put("tenantName",tenantName);
        map.put("userName",userName);
        map.put("phoneNumber",phoneNumber);
        map.put("password",password);
        map.put("verifyCode",verifyCode);
        map.put("deviceToken",deviceToken);
        map.put("deviceType",deviceType);
        map.put("uId",unionId);
        return map;
    }
    /**
     * 查检邀请码
     * @param tenantId
     * @param code
     * @return
     */
    @RequestMapping("/checkInviteCode")
    public String checkInviteCode(HttpServletRequest request, HttpServletResponse response, String tenantId, String code,Model model) {
        if(StringUtils.isBlank(tenantId)){
            model.addAttribute("isSuc",0);
            return "invitePerson/checkInviteCode";
        }
        SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
        if(null == saasTenant){
            model.addAttribute("isSuc",0);
            return "invitePerson/checkInviteCode";
        }
        Integer status = saasTenant.getStatus();
        if(2 == status){
            model.addAttribute("isSuc",0);
            return "invitePerson/checkInviteCode";
        }
        SaasTenantInfo tenantInfo = this.saasTenantCoreService.selectSaasTenantInfo(tenantId);
        //验证码不符返回
        if (null != tenantInfo && StringUtils.isNotBlank(code)) {
            String codes = tenantInfo.getCompanyInvitationCode();
            String s = MD5Utils.MD5(codes);
            if (!code.equals(s)) {
                model.addAttribute("isSuc",0);
                return "invitePerson/checkInviteCode";
            }
            model.addAttribute("isSuc",1);
            model.addAttribute("tenantName",tenantInfo.getTenantName());
            model.addAttribute("tenantId",tenantInfo.getTenantId());
            return "invitePerson/checkInviteCode";
        }
        model.addAttribute("isSuc",0);
        return "invitePerson/checkInviteCode";
    }

    /**
     * 注册租户下的用户(pc端邀请注册)
     * @param tenantId
     * @param
     * @return
     */
    @RequestMapping("/invite")
    @ResponseBody
    public ResponseResult invite(HttpServletRequest request, HttpServletResponse response,
                                 String tenantId, String tenantName, String phoneNumber,
                                 String code, String userName,String password) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();
        map.put("tenantId", tenantId);
        responseResult.setData(map);

        try {
            //校验密码格式是否正确
            if(StringUtils.isBlank(password)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"密码不能为空","");
            }
            if(!ValidatorUtil.isPassword(password)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"密码格式不正确","");
            }
            //校验用户名和公司名
            Map<String, Object> map1 = LoginRegisterValidationUtil.validationName(userName, tenantName);
            if(map1.containsKey("ResponseResult")){
                return (ResponseResult) map1.get("ResponseResult");
            }
            //获取redis里面的验证码
            String smscode = RedisUtil.get("QYH_SMS_" + phoneNumber);
            if (StringUtils.isBlank(smscode) && StringUtils.isNotBlank(code)) {
                responseResult.setIfSuc(0);
                responseResult.setMsg(BusinessExceptionStatusEnum.VerifyCodeError.getDescription());
                responseResult.setCode(BusinessExceptionStatusEnum.VerifyCodeError.getCode());
                return responseResult;
            }

            if (smscode.equals(code)) {
                RedisUtil.remove("QYH_SMS_" + phoneNumber);
                //增加租户人员上线判断
                SaasTenant saas = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
                if(null==saas){
                    responseResult.setIfSuc(0);
                    responseResult.setMsg("必填参数不能为空");
                    return responseResult;
                }
                //增加企业解散时判断
                if(saas.getStatus()==2){
                    responseResult.setIfSuc(0);
                    responseResult.setMsg("企业已解散");
                    responseResult.setCode("");
                    return responseResult;
                }

                //是否是免费用户，免费用户读取用户配置
                if("0".equals(saas.getTenantType())){
                    //获取定价配置
                    PricingConfigDto configDto = pricingConfigService.getPricingConfig();
                    //获取免费价格
                    List<CapacityConfig> configs = configDto.getFreeAccountConfig();
                    for(CapacityConfig config : configs){
                        //员工人数
                        if("102".equals(config.getSku())){
                            saas.setExpectNumber(config.getQty());
                            break;
                        }
                    }
                }

                //获取用户总人数
                TenantContext.setTenantId(tenantId);
                List<SysUser> sysUsers = userService.selectNotDimissionPerson();
                if (saas.getExpectNumber() <= sysUsers.size()) {
                    responseResult.setIfSuc(0);
                    responseResult.setMsg("企业当前已有"+sysUsers.size()+"名成员，达到最大授权限制，请联系管理员升级成员容量");
                    responseResult.setCode("");
                    return responseResult;
                }
                //添加登陆账户
                SaasUserWeb saasUserWeb = new SaasUserWeb();
                //租户下用户Id
                String sysUserId = CreateUUIdUtil.Uuid();
                //登陆账户Id
                String userWebId = CreateUUIdUtil.Uuid();
                SaasUserWeb tempUW = new SaasUserWeb();
                tempUW.setAccount(phoneNumber);
                SaasUserWeb temp = this.saasTenantCoreService.selectUserLoginInfo(tempUW);
                SysUser user=new SysUser();
                if(null!=temp){
                    String tenantId1 = temp.getTenantId();
                    String userIdd = temp.getUserId();
                    if(StringUtils.isBlank(tenantId1)) {
                        responseResult.setIfSuc(0);
                        responseResult.setMsg("必填参数不能为空");
                        return responseResult;
                    }
                        SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId1);
                        if(null == saasTenant){
                            responseResult.setIfSuc(0);
                            responseResult.setMsg("必填参数不能为空");
                            return responseResult;
                        }
                    Integer status = saasTenant.getStatus();
                        if(2 == status){
                            saasTenantCoreService.deleteDissolutionUserInfoByUserId(userIdd);
                            user.setStatus(89);
                        }else{
                            TenantContext.setTenantId(tenantId1);
                            user=this.userService.selectUserByUserId(userIdd);
                        }
                }
                if (null != temp && null!=user && user.getStatus()!=89) {  //已存在该用户
                    responseResult.setIfSuc(0);
                    responseResult.setMsg(BusinessExceptionStatusEnum.PhoneNumberRegister.getDescription());
                    responseResult.setCode(BusinessExceptionStatusEnum.PhoneNumberRegister.getCode());
                    return responseResult;
                } else if (null == temp) {   //新增用户
                    //添加登陆账户
                    saasUserWeb.setId(userWebId);
                    saasUserWeb.setAccount(phoneNumber);
                    saasUserWeb.setTenantId(tenantId);
                    saasUserWeb.setUserId(sysUserId);
                    saasUserWeb.setStatus(1);
                    saasUserWeb.setPassword(SHA1Util.SHA1(password));
                    //保存登陆账户
                    this.saasTenantCoreService.saveUserLoginInfo(saasUserWeb);
                } else if (null!=user && user.getStatus()==89) {  //已离职用户
                    //更新开放平台绑定租户
                    SaasUserOauth userOauth2=new SaasUserOauth();
                    userOauth2.setUserId(temp.getUserId());
                    userOauth2.setTenantId(tenantId);
                    userOauth2.setOauthType(2);
                    List<SaasUserOauth> wxOpen=this.saasTenantCoreService.selectOAuthLoginInfo(userOauth2);
                    if(null!=wxOpen){
                        wxOpen.get(0).setUserId(sysUserId);
                        wxOpen.get(0).setTenantId(tenantId);
                        saasTenantCoreService.updateOAuthLoginInfo(wxOpen.get(0));
                    }
                    //更新账户密码表绑定的租户
                    temp.setStatus(1);
                    temp.setUserId(sysUserId);
                    temp.setTenantId(tenantId);
                    temp.setPassword(SHA1Util.SHA1(password));
                    this.saasTenantCoreService.updateUserLoginInfo(temp);

                }
                //添加租户用户
                SysUser sysUser = new SysUser();
                sysUser.setPhoneNumber(phoneNumber);
                sysUser.setUserName(userName);
                sysUser.setTenantId(tenantId);
                sysUser.setOrgId("1");
                sysUser.setOrgName(tenantName);
                sysUser.setStatus(1);
                sysUser.setId(sysUserId);
                sysUser.setCreater(sysUserId); //创建者自己
                sysUser.setCreateTime(new Date());
                sysUser.setDeviceType(0);
                List<String> releds = new ArrayList<String>();
                //普通员工
                releds.add("99");
                //切换数据源
                TenantContext.setTenantId(tenantId);
                //添加租户下的用户
                this.userService.saveUser(sysUser, releds);

                //删除redis里面的验证码
                RedisUtil.remove("QYH_WX_SMS_" + phoneNumber);
            } else {
                responseResult.setIfSuc(0);
                responseResult.setMsg(BusinessExceptionStatusEnum.VerifyCodeError.getDescription());
                responseResult.setCode(BusinessExceptionStatusEnum.VerifyCodeError.getCode());
                return responseResult;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        responseResult.setIfSuc(1);
        return responseResult;
    }

    /**
     * 方法名：createEterprise</br>
     * 描述：创建新企业</br>
     * 参数：[tenantName, uId]</br>
     * 返回值：java.lang.Object</br>
     * 开发人员：weining</br>
     * 创建时间：2017/7/7  </br>
     */
    @RequestMapping(value = "/createeterprise", method = RequestMethod.POST)
    @ResponseBody
    public Object createEterprise(String tenantName, String uId,String userId,HttpServletRequest rep) {
        //增加日志接口2个参数
        String ua = ((HttpServletRequest) rep).getHeader("user-agent");
        String source = RequestSourceUtil.getRequestSource(ua);
//      String ip = rep.getRemoteAddr();
        String ip = getIpAddr(rep);

        String oldTenantId = "";
        if(tenantName.length() > 30){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "公司名称长度受限！", BusinessExceptionStatusEnum.OperateDBError.getCode());
        }
        if(StringUtils.isNotBlank(userId)){
            SaasUserWeb saasUserWeb = new SaasUserWeb();
            saasUserWeb.setUserId(userId);
            SaasUserWeb saasUserWeb1 = saasTenantCoreService.selectUserLoginInfo(saasUserWeb);
            if(null!=saasUserWeb1){
                oldTenantId = saasUserWeb1.getTenantId();
                if(StringUtils.isNotBlank(oldTenantId)){
                    SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(oldTenantId);
                    if(null != saasTenant){
                        Integer status = saasTenant.getStatus();
                        if(2 != status){
                            TenantContext.setTenantId(oldTenantId);
                            SysUser sysUser22 = userService.selectUserByUserId(userId);
                            if(null != sysUser22) {
                                Integer status1 = sysUser22.getStatus();
                                if(89 != status1) {
                                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "您已有注册公司，请返回登录");
                                }
                                }
                            }
                    }
                }
            }
        }
        //校验公司名格式是否正确
        Map<String, Object> map = LoginRegisterValidationUtil.validationName(null, tenantName);
        if (map.containsKey("ResponseResult")) {
            return map;
        }
        //校验公司名称是否存在
        boolean b = saasTenantCoreService.checkTenantName(tenantName);
        if (b) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.TenantNameExist.getDescription(), BusinessExceptionStatusEnum.TenantNameExist.getCode());
        }

        if(StringUtils.isNotBlank(uId)){
            SaasUserOauth saasUserOauth = new SaasUserOauth();
            saasUserOauth.setOauthUnionId(uId);
            saasUserOauth.setOauthType(2);
            List<SaasUserOauth> saasUserOauth1 = saasTenantCoreService.selectOAuthLoginInfo(saasUserOauth);
            //判断第三方用户表不能为空
            if (null == saasUserOauth1) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
            }
            oldTenantId = saasUserOauth1.get(0).getTenantId();
        }
/*        if(StringUtils.isNotBlank(userId)){
            SaasUserWeb saasUserWeb = new SaasUserWeb();
            saasUserWeb.setUserId(userId);
            SaasUserWeb saasUserWeb1 = saasTenantCoreService.selectUserLoginInfo(saasUserWeb);
            if(null!=saasUserWeb1){
                oldTenantId = saasUserWeb1.getTenantId();
            }
        }*/
        SysUser sysUser = new SysUser();
        if(StringUtils.isNotBlank(oldTenantId)){
            SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(oldTenantId);
            if(null != saasTenant &&saasTenant.getStatus()!=2) {
                TenantContext.setTenantId(oldTenantId);
                SysUser sysUser1 = userService.selectUserByUserId(userId);
                if(null != sysUser1) {
                    sysUser.setUserName(sysUser1.getUserName());
                    sysUser.setUserNamePinyin(sysUser1.getUserNamePinyin());
                    sysUser.setPhoneNumber(sysUser1.getPhoneNumber());
                }
            }else{
                Dissolutionuserinfo dissolutionuserinfo = saasTenantCoreService.selectDissolutionUserInfo(userId);
                if(null != dissolutionuserinfo) {
                        sysUser.setUserName(dissolutionuserinfo.getUserName());
                        sysUser.setUserNamePinyin(dissolutionuserinfo.getUserNamePinyin());
                        sysUser.setPhoneNumber(dissolutionuserinfo.getPhoneNumber());
                }
            }
        }else{
            Dissolutionuserinfo dissolutionuserinfo = saasTenantCoreService.selectDissolutionUserInfo(userId);
            if(null != dissolutionuserinfo) {
                sysUser.setUserName(dissolutionuserinfo.getUserName());
                sysUser.setUserNamePinyin(dissolutionuserinfo.getUserNamePinyin());
                sysUser.setPhoneNumber(dissolutionuserinfo.getPhoneNumber());
            }
        }
        sysUser.setId(userId);
        SysUser sysUser1 = loginRegisterCoreService.saveCreateEterprise(tenantName, sysUser, uId);
        if (null!=sysUser1) {
            String tenantId = sysUser1.getTenantId();
            //切换数据源
            TenantContext.setTenantId(tenantId);
            String orgId = "1";
            sysUser1.setOrgId(orgId);
            sysUser1.setOrgName(tenantName);
            List<String> sysRoleIds = new ArrayList<>();
            boolean saveUser = userService.saveUser(sysUser1, sysRoleIds, "1");
            if (saveUser) {
                //增加组织表
                SysOrganization sysOrganization = new SysOrganization();
                sysOrganization.setCreateTime(new Date());
                sysOrganization.setId(orgId);
                sysOrganization.setStatus(1);
                sysOrganization.setOrgName(tenantName);
                sysOrganization.setOrgNamePinyin(PingYinUtil.getPingYin(tenantName));
                sysOrganization.setParentId("0");
                sysOrganization.setCompositor(1);
                b = organizationService.saveOrganization(sysOrganization);
                if(b){
                    saasTenantCoreService.deleteDissolutionUserInfoByUserId(userId);
                }
                //删除redis用户缓存
                RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_"+sysUser1.getId());
                //增加日志
                //sysLogService.createLog(BusinessType.companyOp.toString(),EventType.company_information.toString(),"创建新企业成功！","","info");
                //增加日志
                sysLogService.createLog(BusinessType.registerOp.toString(), EventType.platform_register.toString(),sysUser1.getUserName()+ "创建新企业成功！", "", Loglevel.info.toString(), ip, source);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
            }
        }
        //增加日志
        //sysLogService.createLog(BusinessType.companyOp.toString(),EventType.company_information_fail.toString(),"创建新企业失败！",BusinessExceptionStatusEnum.OperateDBError.getDescription());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
    }

    /**
     * 方法名：checkphonenumbertenantname</br>
     * 描述：校验手机号和公司名是否存在接口</br>
     * 参数：[phoneNumber,tenantName] 手机号码,公司名称</br>
     * 返回值：java.lang.Object</br>
     * 开发人员：weining</br>
     * 创建时间：2017/5/25  </br>
     */
    @RequestMapping(value = "/checkphonenumbertenantname", method = RequestMethod.POST)
    @ResponseBody
    public Object checkPhoneNumberTenantName(String phoneNumber, String tenantName) {
        //获取租户Id
        String tenantId = TenantContext.getTenantId();
        Map<String, Object> map = new HashMap<>();
        Integer isRegist = null, isExist = null;
        if (StringUtils.isNotBlank(phoneNumber)) {
            //切换数据源
            TenantContext.setTenantId("0");
            //查询手机号是否存在
            boolean b = this.loginRegisterService.checkPhoneNumber(phoneNumber);
            isRegist = b ? 1 : 0;
            map.put("isRegist", isRegist);
        } else if (StringUtils.isNotBlank(tenantName)) {
            //查询公司名是否存在
            boolean b = this.loginRegisterService.checkTenantName(tenantName);
            isExist = b ? 1 : 0;
            map.put("isExist", isExist);
        } else {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
        }
        if (StringUtils.isNotBlank(tenantId)) {
            //数据源切换
            TenantContext.setTenantId(tenantId);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
    }

    /**
     * 方法名：changePassword</br>
     * 描述：忘记密码接口实现</br>
     * 参数：[]</br>
     * 返回值：org.springframework.web.servlet.ModelAndView</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/2  </br>
     */
    @RequestMapping(value = "/changepassword", method = RequestMethod.POST)
    @ResponseBody
    public Object changePassword(HttpServletRequest request,HttpServletRequest rep) {
        //增加日志接口2个参数
        String ua = ((HttpServletRequest) rep).getHeader("user-agent");
//        String source = "PC";
//        if (StringUtils.containsIgnoreCase(ua, "micromessenger")) {
//            source = "Weixin";
//        } else if (StringUtils.containsIgnoreCase(ua, "iPhone")) {
//            source = "iPhone";
//        } else if (StringUtils.containsIgnoreCase(ua, "Android")) {
//            source = "Android";
//        }
        String source = RequestSourceUtil.getRequestSource(ua);
//      String ip = rep.getRemoteAddr();
        String ip = getIpAddr(rep);


        Map<String, Object> map = new HashMap<>();
        //获取前台传参
        String phoneNumber = request.getParameter("phoneNumber");
        String verifyCode = request.getParameter("verifyCode");
        String password = request.getParameter("password");
        String againPassword = request.getParameter("againPassword");
        //校验前台传参是否为空或者格式
        ResponseResult responseResult = LoginRegisterValidationUtil.forgetPasswordValidation(request);
        if (responseResult.getIfSuc() == 0) {
            return responseResult;
        }
        //查询手机号码是否存在
        boolean checkPhoneNumber = this.saasTenantCoreService.checkPhoneNumber(phoneNumber);
        if (!checkPhoneNumber) {
            //参数状态
            map.put("paramState", "isNotRegist");
            map.put("param", "phoneNumber");
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberNotRegister.getDescription(), map, BusinessExceptionStatusEnum.PhoneNumberNotRegister.getCode());
        }
        SaasUserWeb saasUserWeb = saasTenantCoreService.selectUserLoginInfo(null, phoneNumber);
        String tenantId = saasUserWeb.getTenantId();
        String userName = "";
        if(StringUtils.isNotBlank(tenantId)){
            SaasTenant saasTenant1 = saasTenantCoreService.selectSaasTenantServcie(tenantId);
            if(null!=saasTenant1&&saasTenant1.getStatus()==1){
                //切换数据库
                TenantContext.setTenantId(tenantId);
                SysUser sysUser = new SysUser();
                sysUser.setPhoneNumber(phoneNumber);
                List<SysUser> sysUsers = userService.selectUser(sysUser);
                if(null!=sysUsers && sysUsers.size()>0){
                    userName = sysUsers.get(0).getUserName();
                }
            }else {
                Dissolutionuserinfo dissolutionuserinfo = saasTenantCoreService.selectDissolutionUserInfo(saasUserWeb.getUserId());
                if(null!=dissolutionuserinfo){
                    userName = dissolutionuserinfo.getUserName();
                }else{
                    userName = phoneNumber;
                }
            }
        }
        //获取redis里面的参数
        String code = RedisUtil.get("QYH_SMS_" + phoneNumber);
        if (StringUtils.isBlank(code)) {
            //参数状态
            map.put("paramState", "error");
            map.put("param", "verifyCode");
            //增加忘记密码日志
            sysLogService.createLog(BusinessType.loginOp.toString(),EventType.forget_password_access.toString(),userName+"找回密码失败",BusinessExceptionStatusEnum.VerifyCodeError.getDescription(),Loglevel.error.toString(),ip,source);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), map, BusinessExceptionStatusEnum.VerifyCodeError.getCode());
        }

        if (code.equals(verifyCode)) {
            //删除redis里面的验证码
            RedisUtil.remove("QYH_SMS_" + phoneNumber);
            saasUserWeb.setPassword(SHA1Util.SHA1(password));
            //修改用户登录信息表
            boolean b = saasTenantCoreService.updateUserLoginInfo(saasUserWeb);
            if (b) {
                //增加忘记密码日志
                sysLogService.createLog(BusinessType.loginOp.toString(),EventType.forget_password_access.toString(),userName+"成功找回密码",null,Loglevel.info.toString(),ip,source);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
            } else {
                //增加忘记密码日志
                sysLogService.createLog(BusinessType.loginOp.toString(),EventType.forget_password_access.toString(),userName+"找回密码失败",BusinessExceptionStatusEnum.OperateDBError.getDescription(),Loglevel.error.toString(),ip,source);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
            }
        } else {
            //参数状态
            map.put("paramState", "error");
            map.put("param", "verifyCode");
            //增加忘记密码日志
            sysLogService.createLog(BusinessType.loginOp.toString(),EventType.forget_password_access.toString(),userName+"找回密码失败",BusinessExceptionStatusEnum.VerifyCodeError.getDescription(),Loglevel.error.toString(),ip,source);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), map, BusinessExceptionStatusEnum.VerifyCodeError.getCode());
        }
    }

    /**
     * 方法名：bindaccount</br>
     * 描述：绑定用户接口实现</br>
     * 参数：[]</br>
     * 返回值：org.springframework.web.servlet.ModelAndView</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/2  </br>
     */
    @RequestMapping(value = "/bindaccount", method = RequestMethod.POST)
    @ResponseBody
    public Object bindAccount(String phoneNumber, String verifyCode, String uId) {
        SaasUserWeb saasUserWeb = new SaasUserWeb();
        SaasUserOauth saasUserOauth = new SaasUserOauth();
        //获取redis里面的验证码
        String code = RedisUtil.get("QYH_SMS_" + phoneNumber);
        if (StringUtils.isBlank(code)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), BusinessExceptionStatusEnum.VerifyCodeError.getCode());
        }
        if (code.equals(verifyCode)) {
            //删除redis里面的验证码
            RedisUtil.remove("QYH_SMS_" + phoneNumber);
            //查询用户登录信息表
            saasUserWeb.setAccount(phoneNumber);
            SaasUserWeb userWeb = this.saasTenantCoreService.selectUserLoginInfo(saasUserWeb);
            if(null == userWeb){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "手机号未注册");
            }
            //获取用户Id
            String userId = userWeb.getUserId();
            //获取tenantId
            String tenantId = userWeb.getTenantId();
            if(StringUtils.isNotBlank(tenantId)){
                SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
                if(null != saasTenant){
                    Integer status = saasTenant.getStatus();
                    if(null != status && status == 2){
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "手机号不在任何企业下");
                    }
                }
                //切换数据源  查询用户是不是离职状态
                TenantContext.setTenantId(tenantId);
                SysUser sysUser = userService.selectUserByUserId(userId);
                if(null!=sysUser){
                    Integer status = sysUser.getStatus();
                    if(null!=status && status ==89){
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "手机号不在任何企业下");
                    }
                }
            }

            //查询第三方用户登录信息表
            //saasUserOauth.setUserId(userId);
            saasUserOauth.setOauthUnionId(uId);
            saasUserOauth.setOauthType(2);
            List<SaasUserOauth> userOauth = this.saasTenantCoreService.selectOAuthLoginInfo(saasUserOauth);
            boolean b = false;
            //获取SaasUserOauth实体
            saasUserOauth.setTenantId(tenantId);
            Map<String, Object> map = LoginRegisterValidationUtil.wechatLoginHandle(saasUserOauth);
            SaasUserOauth wechatLoginHandle = (SaasUserOauth) map.get("saasUserOauth");
            if (null == userOauth) {
                String userOauthId = CreateUUIdUtil.Uuid();
                wechatLoginHandle.setId(userOauthId);
                wechatLoginHandle.setCreateTime(new Date());
                wechatLoginHandle.setUserId(userId);
                //给userOauth赋值
                b = this.saasTenantCoreService.saveOAuthLoginInfo(wechatLoginHandle);
                if (b) {
                    SaasUserOauthInfo saasUserOauthInfo = (SaasUserOauthInfo) map.get("saasUserOauthInfo");
                    saasUserOauthInfo.setId(CreateUUIdUtil.Uuid());
                    saasUserOauthInfo.setUserOauthId(userOauthId);
                    saasUserOauthInfo.setCreateTime(new Date());
                    //增加第三方用户信息
                    b = this.saasTenantCoreService.saveOAuthUserInfo(saasUserOauthInfo);
                }
            } else {
                String userOauthId = userOauth.get(0).getId();
                wechatLoginHandle.setId(userOauthId);
                wechatLoginHandle.setUpdateTime(new Date());
                wechatLoginHandle.setUserId(userId);
                b = this.saasTenantCoreService.updateOAuthLoginInfo(wechatLoginHandle);
                if (b) {
                    SaasUserOauthInfo saasUserOauthInfo = (SaasUserOauthInfo) map.get("saasUserOauthInfo");
                    saasUserOauthInfo.setUserOauthId(userOauthId);
                    //查询第三方登录信息表
                    SaasUserOauthInfo saasUserOauthInfo1 = saasTenantCoreService.selectUserOauthInfoByOauthUserId(userOauthId, "3");
                    if (null != saasUserOauthInfo1) {
                        saasUserOauthInfo.setId(saasUserOauthInfo1.getId());
                        saasUserOauthInfo.setUpdateTime(new Date());
                        //修改第三方用户信息
                        b = this.saasTenantCoreService.updateOAuthUserInfo(saasUserOauthInfo);
                    } else {
                        saasUserOauthInfo.setId(CreateUUIdUtil.Uuid());
                        saasUserOauthInfo.setCreateTime(new Date());
                        //增加第三方用户信息
                        b = this.saasTenantCoreService.saveOAuthUserInfo(saasUserOauthInfo);
                    }
                }
            }

            TenantContext.setTenantId(tenantId);
            //关联已关注的微信服务号start
            SysUser user=this.userService.selectUserByUserId(userId);
            //查询第三方用户登录信息表
            SaasUserOauth userOauth1=new SaasUserOauth();
            //userOauth1.setUserId(userId);
            userOauth1.setOauthUnionId(uId);
            userOauth1.setOauthType(1);
            List<SaasUserOauth> userOauth2 = this.saasTenantCoreService.selectOAuthLoginInfo(userOauth1);
            if(null!=userOauth2 &&userOauth2.size()>0){ //解散企业或离职的恢复
                SaasUserOauth uo=userOauth2.get(0);
                uo.setTenantId(tenantId);
                uo.setStatus(1);
                uo.setIsBind(1);
                uo.setOauthType(1);
                uo.setUserId(userId);
                this.saasUserOauthService.update(uo);

                //修改租户库用户服务信息
                user.setOpenId(uo.getOauthOpenId());
                user.setIsSubscribe("1");

            }else{ //未关联的关联公众号


                SaasUserOauthInfo tempUserOauthInfo=new SaasUserOauthInfo();
                tempUserOauthInfo.setOauthUnionId(uId);
                tempUserOauthInfo.setOauthType(1);
                List<SaasUserOauthInfo> oauthInfos=this.saasUserOauthInfoService.findByUserOauthInfo(tempUserOauthInfo);
                if(null!=oauthInfos && oauthInfos.size()>0){
                    SaasUserOauthInfo info=oauthInfos.get(0);

                    String oauthId=CreateUUIdUtil.Uuid();
                    userOauth1.setId(oauthId);
                    userOauth1.setUserId(userId);
                    userOauth1.setTenantId(tenantId);
                    userOauth1.setOauthOpenId(info.getOauthOpenId());
                    userOauth1.setOauthUnionId(info.getOauthUnionId());
                    userOauth1.setStatus(1);
                    userOauth1.setIsBind(1);
                    userOauth1.setOauthType(1);
                    userOauth1.setOauthPhoto(wechatLoginHandle.getOauthPhoto());
                    userOauth1.setOauthUserName(wechatLoginHandle.getOauthUserName());
                    userOauth1.setPassword(SHA1Util.SHA1(uId));
                    //userOauth1.setOauthPhoto();
                    this.saasUserOauthService.save(userOauth1);

                    //修改租户库用户服务信息
                    user.setOpenId(info.getOauthOpenId());
                    user.setIsSubscribe("1");
                }
            }
            String oauthUserName = wechatLoginHandle.getOauthUserName();
            user.setWechat(oauthUserName);
            this.userService.updateUserByUserId(user);
            //关联已关注的微信服务号end




            if (b) {
                //删除redis用户缓存
                RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_"+userId);
                //删除redis微信缓存
                RedisUtil.remove("WeChat" + uId);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), BusinessExceptionStatusEnum.VerifyCodeError.getCode());
    }

    @RequestMapping("/checkverifycode")
    @ResponseBody
    public Object checkVerifyCode(String phoneNumber, String verifyCode) {
        //获取redis里面的参数
        String code = RedisUtil.get("QYH_SMS_" + phoneNumber);
        if (StringUtils.isBlank(code)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), BusinessExceptionStatusEnum.VerifyCodeError.getCode());
        }
        if (verifyCode.equals(code)) {
            RedisUtil.remove("QYH_SMS_" + phoneNumber);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), BusinessExceptionStatusEnum.VerifyCodeError.getCode());
    }

    /**
     * 方法名：loginPage</br>
     * 描述：请求此接口跳转到登录页面</br>
     * 参数：[]</br>
     * 返回值：org.springframework.web.servlet.ModelAndView</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/2  </br>
     */
    @RequestMapping("/loginpage")
    public String loginPage(Model model) {
        WechatConfig wechatConfig = new WechatConfig();
        String appId = wechatConfig.getAppId();
        String appSecret = wechatConfig.getAppSecret();
        String callBackUrl = wechatConfig.getCallBackUrl();
        String webSiteDomain = wechatConfig.getWebSiteDomain();
        model.addAttribute("appId",appId);
        model.addAttribute("appSecret",appSecret);
        model.addAttribute("callBackUrl",callBackUrl);
        model.addAttribute("webSiteDomain",webSiteDomain);
        return "login-register/login";
    }

    /**
     * 方法名：registePage</br>
     * 描述：请求此接口跳转到注册页面</br>
     * 参数：[]</br>
     * 返回值：org.springframework.web.servlet.ModelAndView</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/2  </br>
     */
    @RequestMapping("/registerpage")
    public String registePage(HttpServletRequest request, Model model) {
        String uId = null;
        uId = request.getParameter("uId");
        if (StringUtils.isBlank(uId))
            uId = (String) request.getAttribute("uId");
        //获取redis里面的第三方信息
        String wechatUser = RedisUtil.get("WeChat" + uId);
        Map<String, Object> jsonObject = (Map) JSON.parseObject(wechatUser);
        if (null != jsonObject) {
            Map snsUserInfo = (Map) jsonObject.get("snsUserInfo");
            model.addAttribute("uId", uId);
            model.addAttribute("name", snsUserInfo.get("nickname"));
            model.addAttribute("photo", snsUserInfo.get("headImgUrl"));
        }
        return "login-register/register";
    }

    /**
     * 方法名：wechatregistePage</br>
     * 描述：请求此接口跳转到微信注册页面</br>
     * 参数：[]</br>
     * 返回值：org.springframework.web.servlet.ModelAndView</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/2  </br>
     */
    @RequestMapping("/wechatregisterpage")
    public String wechatRegistePage(Model model) {
        WechatConfig wechatConfig = new WechatConfig();
        String appId = wechatConfig.getAppId();
        String appSecret = wechatConfig.getAppSecret();
        String callBackUrl = wechatConfig.getCallBackUrl();
        String webSiteDomain = wechatConfig.getWebSiteDomain();
        model.addAttribute("appId",appId);
        model.addAttribute("appSecret",appSecret);
        model.addAttribute("callBackUrl",callBackUrl);
        model.addAttribute("webSiteDomain",webSiteDomain);
        return "login-register/wechat-register";
    }

    /**
     * 方法名：tospage</br>
     * 描述：请求此接口跳转到服务条款页面</br>
     * 参数：[]</br>
     * 返回值：org.springframework.web.servlet.ModelAndView</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/2  </br>
     */
    @RequestMapping("/termspage")
    public String termsPage() {
        return "login-register/terms";
    }

    /**
     * 方法名：forgatpasswordpage</br>
     * 描述：请求此接口跳转到忘记密码页面</br>
     * 参数：[]</br>
     * 返回值：org.springframework.web.servlet.ModelAndView</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/2  </br>
     */
    @RequestMapping("/forgetpasswordpage")
    public String forgetPasswordPage(String phoneNumber, Model model) {
        model.addAttribute("phoneNumber", phoneNumber);
        return "login-register/forget";
    }

    /**
     * 方法名：corporateAccount</br>
     * 描述：创建企业或者开通企业页面</br>
     * 创建时间：2017/7/10  </br>
     */
    @RequestMapping("/corporateaccount")
    public String corporateAccount(HttpServletRequest request, Model model) {
        String uId = (String) request.getAttribute("uId");
        String photo = (String) request.getAttribute("photo");
        String name = (String) request.getAttribute("name");
        String isRegist = request.getParameter("isRegist");
        model.addAttribute("uId", uId);
        model.addAttribute("name", name);
        model.addAttribute("photo", photo);
        model.addAttribute("isRegist", isRegist);
        return "login-register/corporate-account";
    }

    /**
     * 方法名：successpage</br>
     * 描述：请求此接口跳转到操作成功页面</br>
     * 参数：[]</br>
     * 返回值：org.springframework.web.servlet.ModelAndView</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/2  </br>
     */
    @RequestMapping("/successpage")
    public String successPage(Integer businessType, Model model) {
        if (null == businessType || businessType <= 0) {
            return null;
        }
        String hintMessage;
        String buttonMessage;
        //根据业务类型返回不同的提示
        switch (businessType) {
            case 1:
                hintMessage = "恭喜您，注册成功！";
                buttonMessage = "立即体验";
                break;
            case 2:
                hintMessage = "密码找回成功，请用新密码登录！";
                buttonMessage = "返回登录";
                break;
            case 3:
                hintMessage = "恭喜您，企业创建成功！";
                buttonMessage = "立即体验";
                break;
            case 4:
                hintMessage = "绑定成功，今后可以用微信快速登录！";
                buttonMessage = "立即体验";
                break;
                default:
                    hintMessage = "";
                    buttonMessage = "";
        }
        model.addAttribute("hintMessage", hintMessage);
        model.addAttribute("buttonMessage", buttonMessage);
        return "login-register/succeed";
    }

    /**
     * 方法名：bindAccountPage</br>
     * 描述：请求此接口跳转到绑定账户页面</br>
     * 参数：[]</br>
     * 返回值：org.springframework.web.servlet.ModelAndView</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/2  </br>
     */
    @RequestMapping("/bindaccountpage")
    public String bindAccountPage(String uId, Model model) {
        model.addAttribute("uId", uId);
        return "login-register/bind-account";
    }

    /**
     * 方法名：createEnterprisPage</br>
     * 描述：请求此接口跳转到创建新企业页面</br>
     * 参数：[]</br>
     * 返回值：org.springframework.web.servlet.ModelAndView</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/2  </br>
     */
    @RequestMapping("/createenterprispage")
    public String createEnterprisPage(String uId,String userId, Model model) {
        model.addAttribute("uId", uId);
        model.addAttribute("userId",userId);
        return "login-register/create-enterpris";
    }

    /**
     * 验证用户token  微信对接测试接口
     * 确认请求来自微信服务器
     */
    /*@RequestMapping(value = "/wechattest", method = RequestMethod.GET)
    public void wechatTest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = "weiningtestweixin";
        // 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");

        Map map = new HashMap();
        PrintWriter out = response.getWriter();
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (WechatCommonUtil.checkSingature(signature, token, timestamp, nonce)) {
            map.put("echostr", echostr);
            out.print(echostr);
        }
        out.close();
        out = null;
    }*/

    @RequestMapping("/page")
    @ResponseBody
    public Object page(BasePageEntity basePageEntity, SaasUserTenantMiddle saasUserTenantMiddle) {
        PageInfo saasUserTenantMiddles = loginRegisterService.selectSaasUserTenantMiddleByPage(basePageEntity, saasUserTenantMiddle);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", saasUserTenantMiddles);
    }
}
