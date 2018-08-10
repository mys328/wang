package com.thinkwin.console.web.controller;

import com.thinkwin.SMSsender.service.SMSsenderService;
import com.thinkwin.TenantUserVo;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.console.SaasUser;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.SHA1Util;
import com.thinkwin.common.utils.SMSCode;
import com.thinkwin.common.utils.ValidatorUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.utils.validation.LoginRegisterValidationUtil;
import com.thinkwin.console.service.SaasUserService;
import com.thinkwin.service.TenantContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录
 */
@Controller
@RequestMapping("/system")
public class LoginController {

    @Autowired
    private SaasUserService saasUserService;

    @Autowired
    private SMSsenderService sMSsenderService;

    /**
     * 方法名：loginPage</br>
     * 描述：请求此接口跳转到登录页面</br>
     */
    @RequestMapping("/loginpage")
    public String loginPage() {
        return "login-register/login";
    }

    /**
     * 方法名：forgatpasswordpage</br>
     * 描述：请求此接口跳转到忘记密码页面</br>
     */
    @RequestMapping("/forgetpasswordpage")
    public String forgetPasswordPage(String phoneNumber, Model model) {
        model.addAttribute("phoneNumber", phoneNumber);
        return "login-register/forget";
    }

    /**
     * 方法名：getVerifyCode</br>
     * 描述：获取验证码接口</br>
     */
    @RequestMapping(value = "/getverifycode", method = RequestMethod.POST)
    @ResponseBody
    public Object getVerifyCode(String phoneNumber, Integer type) {

        Map<String, Object> map = new HashMap<>();
        Integer isRegist = null;
        boolean mobile = ValidatorUtil.isMobile(phoneNumber);
        if (null != phoneNumber && null != type && mobile) {
            SaasUser saasUser = new SaasUser();
            saasUser.setId(TenantContext.getUserInfo().getUserId());
            //查询该手机号是否存在
            SaasUser saasUser1 = this.saasUserService.selectSaasUser(saasUser);
            if (saasUser1 != null) {
                isRegist = 1;
            } else {
                isRegist = 0;
            }

            map.put("isRegist", isRegist);
            //忘记密码 用户不存在  获取验证码失败
            if (isRegist == 0 && type == 2) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "账号不存在", map, BusinessExceptionStatusEnum.Failure.getCode());
            }

            //变更管理员
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
     * 方法名：changePassword</br>
     * 描述：忘记密码接口实现</br>
     */
    @RequestMapping(value = "/changepassword", method = RequestMethod.POST)
    @ResponseBody
    public Object changePassword(HttpServletRequest request) {
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
        SaasUser saasUser = new SaasUser();
        saasUser.setPhoneNumber(phoneNumber);
        //查询该手机号是否存在
        SaasUser saasUser1 = this.saasUserService.selectSaasUser(saasUser);

        if (saasUser1 == null) {
            //参数状态
            map.put("paramState", "isNotRegist");
            map.put("param", "phoneNumber");
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberNotRegister.getDescription(), map, BusinessExceptionStatusEnum.PhoneNumberNotRegister.getCode());
        }
        //获取redis里面的参数
        String code = RedisUtil.get("QYH_SMS_" + phoneNumber);
        if (StringUtils.isBlank(code)) {
            //参数状态
            map.put("paramState", "error");
            map.put("param", "verifyCode");
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), map, BusinessExceptionStatusEnum.VerifyCodeError.getCode());
        }

        if (code.equals(verifyCode)) {
            //删除redis里面的验证码
            RedisUtil.remove("QYH_SMS_" + phoneNumber);

            saasUser1.setPassword(SHA1Util.SHA1(password));
            //修改用户登录信息表
            boolean b = this.saasUserService.updateSaasUser(saasUser1);
            if (b) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
            }
        } else {
            //参数状态
            map.put("paramState", "error");
            map.put("param", "verifyCode");
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), map, BusinessExceptionStatusEnum.VerifyCodeError.getCode());
        }
    }

    /**
     * 方法名：successpage</br>
     * 描述：请求此接口跳转到操作成功页面</br>
     */
    @RequestMapping("/successpage")
    public String successPage(Integer businessType, Model model) {
        if (null == businessType || "".equals(businessType)) {
            return null;
        }
        String hintMessage = "";
        String buttonMessage = "";
        //根据业务类型返回不同的提示
        switch (businessType) {
            case 2:
                hintMessage = "密码找回成功，请用新密码登录！";
                buttonMessage = "返回登录";
                break;
        }
        model.addAttribute("hintMessage", hintMessage);
        model.addAttribute("buttonMessage", buttonMessage);
        return "login-register/succeed";
    }

}
