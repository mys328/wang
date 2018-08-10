package com.thinkwin.yunmeeting.weixin.controller;

import com.thinkwin.SMSsender.service.SMSsenderService;
import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.SaasTenantInfoService;
import com.thinkwin.auth.service.SaasTenantService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.promotion.CapacityConfig;
import com.thinkwin.common.dto.promotion.PricingConfigDto;
import com.thinkwin.common.model.core.*;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.oauth.OauthEnum;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.promotion.service.PricingConfigService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yunmeeting.weixin.service.WxUserService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 微信端通过扫描企业二维码加入企业
 *
 * @author LiNing
 *         微信用户
 */
@Controller
@RequestMapping("/wechat/wxRegister")
public class WxRegisterController {

    private final Logger log = LoggerFactory.getLogger(WxRegisterController.class);

    @Resource
    private UserService userService;
    @Resource
    private SaasTenantService saasTenantService;
    @Resource
    private SaasTenantInfoService saasTenantInfoService;
    @Resource
    private WxUserService wxUserService;
    @Resource
    private LoginRegisterService loginRegisterService;
    @Resource
    SMSsenderService sMSsenderService;
    @Resource
    PricingConfigService pricingConfigService;
    /**
     * Step 1
     * 显示邀请码页面
     *
     * @param tenantId
     * @param openId
     * @return
     */
    @RequestMapping("/inviteCodePage")
    @ResponseBody
    public ModelAndView inviteCodePage(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId) {
        ModelAndView mv = new ModelAndView();

        //封装要显示到视图的数据
        mv.addObject("tenantId", tenantId);
        mv.addObject("openId", openId);

        //视图名
        mv.setViewName("register/inviteCode");
        return mv;
    }

    /**
     * Step 2
     * 注册表单页面
     *
     * @param request
     * @param response
     * @param tenantId
     * @param openId
     * @return
     */
    @RequestMapping("/invitePage")
    @ResponseBody
    public ModelAndView invitePage(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId) {
        ModelAndView mv = new ModelAndView();

        SaasTenantInfo temp = new SaasTenantInfo();
        temp.setTenantId(tenantId);
        TenantContext.setTenantId("0");
        SaasTenantInfo tenantInfo = this.saasTenantInfoService.selectSaasTenantInfo(temp);

        //封装要显示到视图的数据
        mv.addObject("tenantId", tenantId);
        mv.addObject("openId", openId);
        mv.addObject("tenantName", tenantInfo.getTenantName());

        //视图名
        mv.setViewName("register/invite");
        return mv;
    }

    /**
     * Step 3
     * 注册成功返回
     *
     * @param request
     * @param response
     * @param tenantId
     * @param openId
     * @return
     */
    @RequestMapping("/inviteSuccessPage")
    @ResponseBody
    public ModelAndView inviteResult(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId) {
        ModelAndView mv = new ModelAndView();

        //封装要显示到视图的数据
        mv.addObject("tenantId", tenantId);
        mv.addObject("openId", openId);

        //视图名
        mv.setViewName("register/inviteSuccess");
        return mv;
    }

    /**
     * 查检邀请码
     *
     * @param tenantId
     * @param openId
     * @param code
     * @return
     */
    @RequestMapping("/checkInviteCode")
    @ResponseBody
    public ResponseResult checkInviteCode(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId, String code) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<String, Object>();

        SaasTenantInfo temp = new SaasTenantInfo();
        temp.setTenantId(tenantId);
        TenantContext.setTenantId("0");
        SaasTenantInfo tenantInfo = this.saasTenantInfoService.selectSaasTenantInfo(temp);

        map.put("openId", openId);
        map.put("tenantId", tenantId);
        responseResult.setData(map);

        //验证码不符返回
        if (null != tenantInfo && StringUtils.isNotBlank(code)) {
            if (!code.equals(tenantInfo.getCompanyInvitationCode())) {
                responseResult.setIfSuc(0);
                responseResult.setMsg(BusinessExceptionStatusEnum.CheckTenantInvitCode.getDescription());
                responseResult.setCode(BusinessExceptionStatusEnum.CheckTenantInvitCode.getCode());
                return responseResult;
            }
        }
        //成功返回
        responseResult.setIfSuc(1);
        return responseResult;
    }

    @Resource
    private com.thinkwin.core.service.SaasTenantService saasTenantCoreService;
    /**
     * 注册租户下的用户
     *
     * @param tenantId
     * @param openId
     * @param
     * @return
     */
    @RequestMapping("/invite")
    @ResponseBody
    public ResponseResult invite(HttpServletRequest request, HttpServletResponse response, String tenantId, String openId, String tenantName, String phoneNumber, String code, String userName,String password) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();

        map.put("openId", openId);
        map.put("tenantId", tenantId);
        responseResult.setData(map);

        try {
            //校验密码格式是否正确
            if(StringUtils.isNotBlank(password)&&!ValidatorUtil.isPassword(password)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"密码格式不正确","");
            }
            //获取redis里面的验证码
            String smscode = RedisUtil.get("QYH_WX_SMS_" + phoneNumber);
            if (StringUtils.isBlank(smscode) && StringUtils.isNotBlank(code)) {
                responseResult.setIfSuc(0);
                responseResult.setMsg(BusinessExceptionStatusEnum.VerifyCodeError.getDescription());
                responseResult.setCode(BusinessExceptionStatusEnum.VerifyCodeError.getCode());
                return responseResult;
            }

            if (smscode.equals(code)) {

                //****************************************************************************
                //增加租户人员上线判断
                String tenId =tenantId;
                SaasTenant saas = saasTenantCoreService.selectByIdSaasTenantInfo(tenId);
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

                //****************************************************************************
                //获取用户总人数
                TenantContext.setTenantId(tenantId);
                List<SysUser> sysUsers = userService.selectNotDimissionPerson();
                if (saas.getExpectNumber() <= sysUsers.size()) {
                    responseResult.setIfSuc(0);
                    responseResult.setMsg("企业当前已达到最大授权限制，请联系管理员升级成员容量");
                    responseResult.setCode("");
                    return responseResult;
                }


                //添加登陆账户
                SaasUserWeb saasUserWeb = new SaasUserWeb();
                //OAuthInfo详情
                SaasUserOauthInfo userOauthInfo = new SaasUserOauthInfo();
                //OAuth登陆账户
                SaasUserOauth userOauth = new SaasUserOauth();



                //租户下用户Id
                String sysUserId = CreateUUIdUtil.Uuid();
                //登陆账户Id
                String userWebId = CreateUUIdUtil.Uuid();
                //OAuthUserId
                String userOauthId = CreateUUIdUtil.Uuid();
                //UserOauthInfoId
                String userOauthInfoId = CreateUUIdUtil.Uuid();

                //获取微信详情
                WxMpUser wxMpUser = this.wxUserService.getWxUserInfo(openId);


                //设置数据源
                TenantContext.setTenantId("0");
                //
                SaasUserWeb tempUW = new SaasUserWeb();
                tempUW.setAccount(phoneNumber);
                SaasUserWeb temp = this.loginRegisterService.selectUserLoginInfo(tempUW);
                SysUser user=new SysUser();
                if(null!=temp){
                    String tenantId1 = temp.getTenantId();
                    String userIdd = temp.getUserId();
                    if(org.apache.commons.lang.StringUtils.isBlank(tenantId1)) {
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

                //*******************************************************************************
                if (null == temp) {   //新增用户
                    //log.info("**********add User1**********");
                    //添加登陆账户
                    saasUserWeb.setId(userWebId);
                    saasUserWeb.setAccount(phoneNumber);
                    saasUserWeb.setTenantId(tenantId);
                    saasUserWeb.setUserId(sysUserId);
                    saasUserWeb.setStatus(1);
                    saasUserWeb.setPassword(SHA1Util.SHA1(password));

                    //OAuth登陆账户
                    userOauth.setId(userOauthId);
                    userOauth.setUserId(sysUserId);
                    userOauth.setIsBind(1);
                    userOauth.setTenantId(tenantId);
                    userOauth.setOauthOpenId(openId);
                    userOauth.setOauthUnionId(wxMpUser.getUnionId());
                    userOauth.setPassword(SHA1Util.SHA1(wxMpUser.getUnionId()));
                    userOauth.setOauthUserName(wxMpUser.getNickname());
                    userOauth.setOauthPhoto(wxMpUser.getHeadImgUrl());
                    userOauth.setOauthType(OauthEnum.WxService.getCode());
                    userOauth.setStatus(1);
                    userOauth.setCreateTime(new Date());

                    //OAuthInfo详情
                    userOauthInfo.setId(userOauthInfoId);
                    userOauthInfo.setUserOauthId(userOauthId);
                    userOauthInfo.setNickName(wxMpUser.getNickname());
                    userOauthInfo.setOauthType(OauthEnum.WxService.getCode());
                    int sex = ("男".equals(wxMpUser.getSex()) ? 1 : 0);
                    userOauthInfo.setSex(sex);
                    userOauthInfo.setLanguage(wxMpUser.getLanguage());
                    userOauthInfo.setCity(wxMpUser.getCity());
                    userOauthInfo.setProvince(wxMpUser.getProvince());
                    userOauthInfo.setCountry(wxMpUser.getCountry());
                    userOauthInfo.setSubscribe(wxMpUser.getSubscribe() ? 1 : 0);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String d = format.format(wxMpUser.getSubscribeTime());
                    Date date = format.parse(d);
                    userOauthInfo.setSubscribeTime(date);
                    userOauthInfo.setRemark(wxMpUser.getRemark());
                    userOauthInfo.setCreateTime(new Date());

                    //设置数据源
                    TenantContext.setTenantId("0");
                    //保存登陆账户
                    this.loginRegisterService.saveUserLoginInfo(saasUserWeb);
                    //保存Oauth登陆账户
                    this.loginRegisterService.saveOAuthLoginInfo(userOauth);
                    //保存userOauthInfo
                    this.loginRegisterService.saveOAuthUserInfo(userOauthInfo);

                    //添加租户用户
                    SysUser sysUser = new SysUser();
                    sysUser.setPhoneNumber(phoneNumber);
                    sysUser.setUserName(userName);
                    sysUser.setTenantId(tenantId);
                    sysUser.setOrgId("1");
                    sysUser.setOrgName(tenantName);
                    sysUser.setWechat(wxMpUser.getNickname());
                    sysUser.setOpenId(wxMpUser.getOpenId());
                    sysUser.setIsSubscribe(wxMpUser.getSubscribe() ? "1" : "0");
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

                    responseResult.setIfSuc(1);
                }else if (null != temp && user.getStatus()==89) {  //已离职用户
                    //log.info("**********add User2**********");

                    String oauthUserId=temp.getUserId();

                    //设置数据源
                    TenantContext.setTenantId("0");


                    //更新公众号绑定的租户
                    SaasUserOauth userOauth1=new SaasUserOauth();
                    userOauth1.setUserId(temp.getUserId());
                    //userOauth1.setTenantId(tenantId);
                    userOauth1.setOauthType(OauthEnum.WxService.getCode());
                    List<SaasUserOauth> wxService=this.loginRegisterService.selectOAuthLoginInfo(userOauth1);
                    if(null!=wxService && wxService.size()>0){
                        //wxService.get(0).setUserId(oauthUserId); //恢复原来的用户不创建新用户
                        wxService.get(0).setOauthOpenId(openId);
                        wxService.get(0).setOauthUnionId(wxMpUser.getUnionId());
                        wxService.get(0).setTenantId(tenantId);
                        wxService.get(0).setIsBind(1);
                        this.loginRegisterService.updateOAuthLoginInfo(wxService.get(0));
                    }

                    //更新开放平台绑定租户
                    SaasUserOauth userOauth2=new SaasUserOauth();
                    userOauth2.setUserId(temp.getUserId());
                    //userOauth2.setTenantId(tenantId);
                    userOauth2.setOauthType(OauthEnum.WxOpen.getCode());
                    List<SaasUserOauth> wxOpen=this.loginRegisterService.selectOAuthLoginInfo(userOauth2);
                    //恢复原来的用户不创建新用户
                    if(null!=wxOpen){
                        //wxOpen.get(0).setUserId(oauthUserId);
                        wxOpen.get(0).setOauthUnionId(wxMpUser.getUnionId());
                        wxOpen.get(0).setTenantId(tenantId);
                        wxService.get(0).setIsBind(1);
                        this.loginRegisterService.updateOAuthLoginInfo(wxOpen.get(0));
                    }

                    // 未绑定第三方用户
                    if(null==wxService && null==wxOpen){
                        userOauth.setId(userOauthId);
                        userOauth.setUserId(oauthUserId);
                        userOauth.setIsBind(1);
                        userOauth.setTenantId(tenantId);
                        userOauth.setOauthOpenId(openId);
                        userOauth.setOauthUnionId(wxMpUser.getUnionId());
                        userOauth.setPassword(SHA1Util.SHA1(wxMpUser.getUnionId()));
                        userOauth.setOauthUserName(wxMpUser.getNickname());
                        userOauth.setOauthPhoto(wxMpUser.getHeadImgUrl());
                        userOauth.setOauthType(OauthEnum.WxService.getCode());
                        userOauth.setStatus(1);
                        userOauth.setCreateTime(new Date());

                        //保存Oauth登陆账户
                        this.loginRegisterService.saveOAuthLoginInfo(userOauth);
                    }


                    //更新账户密码表绑定的租户
                    temp.setStatus(1);
                    //temp.setUserId(oauthUserId);
                    temp.setTenantId(tenantId);
                    temp.setPassword(SHA1Util.SHA1(password));
                    this.loginRegisterService.updateUserLoginInfo(temp);


                    //添加租户用户
                    user.setPhoneNumber(phoneNumber);
                    user.setUserName(userName);
                    user.setOrgId("1");
                    user.setOrgName(tenantName);
                    user.setWechat(wxMpUser.getNickname());
                    user.setOpenId(wxMpUser.getOpenId());
                    user.setIsSubscribe(wxMpUser.getSubscribe() ? "1" : "0");
                    user.setStatus(1);
                    user.setCreater(user.getId()); //创建者自己
                    user.setCreateTime(new Date());
                    user.setDeviceType(0);

                    //切换数据源
                    TenantContext.setTenantId(tenantId);
                    if(StringUtils.isNotBlank(user.getId()) && tenantId.equals(user.getTenantId())){ //在本企业离职重新加入本企业的情况
                        this.userService.updateUserByUserId(user);
                    }else{  //在本企业离职加入其它企业
                        //添加租户下的用户
                        List<String> releds = new ArrayList<String>();
                        //普通员工
                        releds.add("99");
                        user.setId(oauthUserId);
                        user.setTenantId(tenantId);
                        this.userService.saveUser(user,releds);
                    }
                    responseResult.setIfSuc(1);
                }else{  //已存在该用户
                    responseResult.setIfSuc(0);
                    responseResult.setMsg(BusinessExceptionStatusEnum.PhoneNumberRegister.getDescription());
                    responseResult.setCode(BusinessExceptionStatusEnum.PhoneNumberRegister.getCode());
                    return responseResult;
                }

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
            responseResult.setIfSuc(0);
            responseResult.setMsg(BusinessExceptionStatusEnum.Failure.getDescription());
            responseResult.setCode(BusinessExceptionStatusEnum.Failure.getCode());
        }

        return responseResult;
    }


    /**
     * 返回手机验证码
     *
     * @param phoneNumber 手机号
     * @param type        类型
     * @return
     */
    @RequestMapping(value = "/getverifycode")
    @ResponseBody
    public Object getVerifyCode(String phoneNumber, Integer type) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();
        Integer isRegist = null;
        if (null != phoneNumber && null != type) {
            //设置数据源
            TenantContext.setTenantId("0");
            SaasUserWeb t = new SaasUserWeb();
            t.setAccount(phoneNumber);
            SaasUserWeb isSysUserWeb = this.loginRegisterService.selectUserLoginInfo(t);
            SysUser sysUser=null;
            if(null!=isSysUserWeb){
                SaasTenant saasTenant=this.saasTenantCoreService.selectByIdSaasTenantInfo(isSysUserWeb.getTenantId());
                if(saasTenant.getStatus()!=2){
                    TenantContext.setTenantId(isSysUserWeb.getTenantId());
                    sysUser=this.userService.selectUserByUserId(isSysUserWeb.getUserId());
                }
            }

            //查询该手机号是否存在
            if (null != isSysUserWeb && null!=sysUser) {
                if(sysUser.getStatus()!=89) {
                    responseResult.setIfSuc(0);
                    responseResult.setMsg(BusinessExceptionStatusEnum.PhoneNumberRegister.getDescription());
                    responseResult.setCode(BusinessExceptionStatusEnum.PhoneNumberRegister.getCode());
                    return responseResult;
                }
            }
            String code = sMSsenderService.SMSsender(phoneNumber, SMSCode.SENDTEMPLATE_YUMEETING);
            //验证码存redis里面
            RedisUtil.set("QYH_WX_SMS_" + phoneNumber, code);
            //设置验证码过期时间
            RedisUtil.expire("QYH_WX_SMS_" + phoneNumber, 600);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }


}
