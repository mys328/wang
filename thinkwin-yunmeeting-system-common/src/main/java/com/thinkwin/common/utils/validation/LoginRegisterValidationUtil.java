package com.thinkwin.common.utils.validation;

import com.alibaba.fastjson.JSON;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.core.*;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.utils.redis.RedisUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名: LoginRegisterValidationUtil </br>
 * 描述:用户登录注册校验工具类</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/6/8 </br>
 */
public class LoginRegisterValidationUtil {
    /**
     * 方法名：registerValidation</br>
     * 描述：注册校验</br>
     * 参数：[rep]</br>
     * 返回值：com.thinkwin.common.response.ResponseResult</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/8  </br>
     */
    public static Map<String, Object> registerValidation(HttpServletRequest rep) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();
        //获取前端请求参数
        String tenantName = rep.getParameter("tenantName");//公司名称
        String userName = rep.getParameter("userName");//用户名
        String phoneNumber = rep.getParameter("phoneNumber");//用户手机号码
        String password = rep.getParameter("password");//密码
        String verifyCode = rep.getParameter("verifyCode");//验证码
        String deviceToken = rep.getParameter("deviceToken");//设备token
        String deviceType = rep.getParameter("deviceType");//设备类型
        String unionId = rep.getParameter("uId");//用户第三方唯一Id
        //参数校验
        responseResult.setIfSuc(0);
        //参数是否为空
        map.put("paramState", "isBlank");
        //校验参数不能为空
        if (StringUtils.isBlank(tenantName)) {
            responseResult.setMsg("公司名称不能为空！");
            map.put("param", "tenantName");
        } else if (StringUtils.isBlank(userName)) {
            responseResult.setMsg("用户名不能为空！");
            map.put("param", "userName");
        } else if (StringUtils.isBlank(phoneNumber)) {
            responseResult.setMsg("手机号码不能为空！");
            map.put("param", "phoneNumber");
        } else if (StringUtils.isBlank(password)) {
            responseResult.setMsg("密码不能为空！");
            map.put("param", "password");
        } else if (StringUtils.isBlank(verifyCode)) {
            responseResult.setMsg("验证码不能为空！");
            map.put("param", "verifyCode");
        } else {
            //校验格式不正确
            boolean b = ValidatorUtil.isPassword(password);
            boolean mobile = ValidatorUtil.isMobile(phoneNumber);
            map.put("paramState", "isNotFormat");
            if (!b) {
                responseResult.setMsg("密码格式不正确！");
                //参数状态
                map.put("param", "password");
            } else if (!mobile) {
                responseResult.setMsg("手机号码格式不正确！");
                map.put("param", "phoneNumber");
            } else {
                //校验通过
                responseResult.setIfSuc(1);
                responseResult.setMsg("success");
                map.put("paramState", "");
                map.put("param", "");
            }
        }
        responseResult.setData(map);
        Map<String, Object> map1 = new HashMap<>();
        //用户信息表实体  给实体赋值
        SysUser sysUser = new SysUser();
        sysUser.setUserName(userName);
        sysUser.setDeviceToken(deviceToken);
        sysUser.setPhoneNumber(phoneNumber);
        int deviceTypeNum = 0;
        if (null != deviceType && !deviceType.equals("")) {
            deviceTypeNum = Integer.parseInt(deviceType);
        }
        sysUser.setDeviceType(deviceTypeNum);
        //用户登录表实体
        SaasUserWeb saasUserWeb = new SaasUserWeb();
        saasUserWeb.setAccount(phoneNumber);
        password = SHA1Util.SHA1(password);
        saasUserWeb.setPassword(password);
        //公司表实体
        SaasTenant saasTenant = new SaasTenant();
        saasTenant.setTenantName(tenantName);
        //公司信息表实体
        SaasTenantInfo saasTenantInfo = new SaasTenantInfo();
        saasTenantInfo.setTenantName(tenantName);
        saasTenantInfo.setCompanyInvitationCode(CreateRandomNumber.createSixByteRandomNumber());
        //用户第三方登录表实体
        if (StringUtils.isNotBlank(unionId)) {
            SaasUserOauth userOauth = new SaasUserOauth();
            userOauth.setOauthUnionId(unionId);
            map1 = wechatLoginHandle(userOauth);
        }
        map1.put("responseResult", responseResult);
        map1.put("sysUser", sysUser);
        map1.put("saasUserWeb", saasUserWeb);
        map1.put("saasTenant", saasTenant);
        map1.put("saasTenantInfo", saasTenantInfo);
        map1.put("verifyCode", verifyCode);
        map1.put("phoneNumber", phoneNumber);
        return map1;
    }

    /**
     * 方法名：forgetPasswordValidation</br>
     * 描述：忘记密码校验</br>
     * 参数：[request]</br>
     * 返回值：com.thinkwin.common.response.ResponseResult</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/9  </br>
     */
    public static ResponseResult forgetPasswordValidation(HttpServletRequest request) {
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();
        //获取前台传参
        String phoneNumber = request.getParameter("phoneNumber");
        String verifyCode = request.getParameter("verifyCode");
        String password = request.getParameter("password");
        String againPassword = request.getParameter("againPassword");
        responseResult.setIfSuc(0);
        //判断参数是否为空
        map.put("paramState", "isBlank");
        if (StringUtils.isBlank(phoneNumber)) {
            map.put("param", "phoneNumber");
        } else if (StringUtils.isBlank(verifyCode)) {
            map.put("param", "verifyCode");
        } else if (StringUtils.isBlank(password)) {
            map.put("param", "password");
        } else if (StringUtils.isBlank(againPassword)) {
            map.put("param", "againPassword");
        } else {
            //判断参数格式是否正确
            boolean mobile = ValidatorUtil.isMobile(phoneNumber);
            boolean password1 = ValidatorUtil.isPassword(password);
            boolean password2 = ValidatorUtil.isPassword(againPassword);
            map.put("paramState", "isNotFormat");
            if (!mobile) {
                map.put("param", "phoneNumber");
            }
            if (!password1) {
                map.put("param", "password");
            } else if (!password2) {
                map.put("param", "againPassword");
            } else if (!againPassword.equals(password)) {
                map.put("param", "againPassword");
                map.put("paramState", "error");
            } else {
                responseResult.setIfSuc(1);
                responseResult.setMsg("success");
                map.put("paramState", "");
                map.put("param", "");
            }
        }
        responseResult.setData(map);
        return responseResult;
    }

    /**
     * 方法名：wechatLoginHandle</br>
     * 描述：微信登录参数处理</br>
     * 参数：[userOauth]</br>
     * 返回值：com.thinkwin.common.model.core.SaasUserOauth</br>
     */
    public static Map<String, Object> wechatLoginHandle(SaasUserOauth saasUserOauth) {
        SaasUserOauthInfo saasUserOauthInfo = new SaasUserOauthInfo();
        String uId = saasUserOauth.getOauthUnionId();
        if (StringUtils.isNotBlank(uId)) {
            //获取redis里面的第三方信息
            String wechatUser = RedisUtil.get("WeChat" + uId);
            Map<String, Object> jsonObject = (Map) JSON.parseObject(wechatUser);
            Map snsUserInfo = (Map) jsonObject.get("snsUserInfo");
            //给第三方赋值
            saasUserOauth.setOauthUserName((String) snsUserInfo.get("nickname"));
            saasUserOauth.setOauthUnionId((String) snsUserInfo.get("unionid"));
            saasUserOauth.setOauthOpenId((String) snsUserInfo.get("openId"));
            saasUserOauth.setOauthPhoto((String) snsUserInfo.get("headImgUrl"));
            saasUserOauthInfo.setNickName((String) snsUserInfo.get("nickname"));
            saasUserOauthInfo.setSex((Integer) snsUserInfo.get("sex"));
            saasUserOauthInfo.setCity((String) snsUserInfo.get("city"));
            saasUserOauthInfo.setProvince((String) snsUserInfo.get("province"));
            saasUserOauthInfo.setCountry((String) snsUserInfo.get("country"));

            Map wechatAccessTokenVo = (Map) jsonObject.get("wechatAccessTokenVo");
            saasUserOauth.setOauthAccessToken((String) wechatAccessTokenVo.get("accessToken"));
            Integer expiresIn = (Integer) wechatAccessTokenVo.get("expiresIn");
            saasUserOauth.setOauthExpires(expiresIn + "");
            saasUserOauth.setOauthRefreshToken((String) wechatAccessTokenVo.get("refreshToken"));
            saasUserOauth.setPassword(SHA1Util.SHA1(uId));
        }
        saasUserOauth.setOauthType(2);
        saasUserOauth.setIsBind(1);
        //增加第三方登录信息表实体
        saasUserOauthInfo.setOauthType(2);
        Map<String, Object> map = new HashMap<>();
        map.put("saasUserOauth",saasUserOauth);
        map.put("saasUserOauthInfo",saasUserOauthInfo);
        return map;
    }

    public static Map<String, Object> addPeopleValidat(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        ResponseResult responseResult = new ResponseResult();
        String userName = request.getParameter("name");
        String phoneNumber = request.getParameter("phone");
        String email = request.getParameter("email");
        String position = request.getParameter("position");
        String sex = request.getParameter("sex");
        Integer sexs = null;
        if (StringUtils.isNotBlank(sex)) {
            sexs = Integer.valueOf(sex);
        }
        String orgId = request.getParameter("orgId");
        String orgName = request.getParameter("department");
        String userId = request.getParameter("id");
        String fileId = request.getParameter("fileId");
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(phoneNumber) || StringUtils.isBlank(orgId) || StringUtils.isBlank(orgName)) {
            responseResult.setIfSuc(0);
            responseResult.setCode(BusinessExceptionStatusEnum.ParameterIsNull.getCode());
            responseResult.setMsg(BusinessExceptionStatusEnum.ParameterIsNull.getDescription());
            map.put("responseResult", responseResult);
            return map;
        }
        boolean username = ValidatorUtil.isUsername(userName);
        boolean mobile = ValidatorUtil.isMobile(phoneNumber);
        boolean emails = ValidatorUtil.isEmail(email);
        if (!username) {
            responseResult.setIfSuc(0);
            responseResult.setCode(BusinessExceptionStatusEnum.ParamErr.getCode());
            responseResult.setMsg("用户名格式不正确");
            map.put("responseResult", responseResult);
            return map;
        }
        if (!mobile) {
            responseResult.setIfSuc(0);
            responseResult.setCode(BusinessExceptionStatusEnum.ParamErr.getCode());
            responseResult.setMsg("手机号格式不正确");
            map.put("responseResult", responseResult);
            return map;
        }
        if (!emails) {
            responseResult.setIfSuc(0);
            responseResult.setCode(BusinessExceptionStatusEnum.ParamErr.getCode());
            responseResult.setMsg("邮箱格式不正确");
            map.put("responseResult", responseResult);
            return map;
        }
        if(userName.length()>10){
            responseResult.setIfSuc(0);
            responseResult.setCode(BusinessExceptionStatusEnum.ParamErr.getCode());
            responseResult.setMsg("用户名长度受限");
            map.put("responseResult", responseResult);
            return map;
        }
        if(StringUtils.isNotBlank(position)&&position.length()>20){
            responseResult.setIfSuc(0);
            responseResult.setCode(BusinessExceptionStatusEnum.ParamErr.getCode());
            responseResult.setMsg("职位长度受限");
            map.put("responseResult", responseResult);
            return map;
        }
        if(StringUtils.isNotBlank(email)&&email.length()>50){
            responseResult.setIfSuc(0);
            responseResult.setCode(BusinessExceptionStatusEnum.ParamErr.getCode());
            responseResult.setMsg("邮箱长度受限");
            map.put("responseResult", responseResult);
            return map;
        }
        //生成用户id
        String state = "update";
        if(StringUtils.isBlank(userId)){
            userId = CreateUUIdUtil.Uuid();
            state = "save";
        }
        //用户
        SysUser sysUser = new SysUser();
        sysUser.setOrgName(orgName);
        sysUser.setOrgId(orgId);
        sysUser.setPhoneNumber(phoneNumber);
        if(state.equals("save")){
            sysUser.setStatus(2);
        }
        sysUser.setEmail(email);
        sysUser.setPosition(position);
        sysUser.setSex(sexs);
        sysUser.setUserNamePinyin(PingYinUtil.getPingYin(userName));
        sysUser.setCreateTime(new Date());
        sysUser.setId(userId);
        sysUser.setPhoto(fileId);
        sysUser.setUserName(userName);
        //用户登录
        SaasUserWeb saasUserWeb = new SaasUserWeb();
        saasUserWeb.setId(CreateUUIdUtil.Uuid());
        saasUserWeb.setUserId(userId);
        saasUserWeb.setAccount(phoneNumber);
        saasUserWeb.setStatus(1);
        //用户租户中间表
        SaasUserTenantMiddle saasUserTenantMiddle = new SaasUserTenantMiddle();
        saasUserTenantMiddle.setId(CreateUUIdUtil.Uuid());
        saasUserTenantMiddle.setUserId(userId);
        saasUserTenantMiddle.setCreateTime(new Date());
        map.put("sysUser", sysUser);
        map.put("saasUserWeb", saasUserWeb);
        map.put("saasUserTenantMiddle", saasUserTenantMiddle);
        map.put("state",state);
        return map;
    }
    /**
     * 方法名：validationName</br>
     * 描述：校验用户名称和公司名称方法</br>
     * 参数：[userName, tenantName]</br>
     * 返回值：java.util.Map<java.lang.String,java.lang.Object></br>
     */
    public static Map<String,Object> validationName(String userName,String tenantName){
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isNotBlank(userName)){
            //校验公司名称格式和长度
            if(StringUtils.isBlank(tenantName)){
                map.put("ResponseResult",ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"公司名称不能为空",""));
                return map;
            }
            if(!ValidatorUtil.isTenantName(tenantName)){
                map.put("ResponseResult" ,ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"公司名称格式不正确",""));
                return map;
            }
            if(tenantName.length()>30){
                map.put("ResponseResult" ,ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"公司名称长度受限",""));
                return map;
            }
        }
         if(StringUtils.isNotBlank(userName)) {
             //校验用户名称
             if (StringUtils.isBlank(userName)) {
                 map.put("ResponseResult", ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "用户名不能为空", ""));
                 return map;
             }
             if (!ValidatorUtil.isUsername(userName)) {
                 map.put("ResponseResult", ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "用户名格式不正确", ""));
                 return map;
             }
             if (userName.length() > 10) {
                 map.put("ResponseResult", ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "用户名长度受限", ""));
                 return map;
             }
         }
        return map;
    }
}
