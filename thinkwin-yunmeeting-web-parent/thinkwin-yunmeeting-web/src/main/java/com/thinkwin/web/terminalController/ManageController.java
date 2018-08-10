package com.thinkwin.web.terminalController;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.util.UriEncoder;
import com.thinkwin.SMSsender.service.SMSsenderService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.dto.publish.*;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.InfoReleaseTerminal;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.SHA1Util;
import com.thinkwin.common.utils.SMSCode;
import com.thinkwin.common.utils.ValidatorUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.publish.TerminalMobileVo;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.publish.service.PublishService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.InfoReleaseTerminalService;
import com.thinkwin.yuncm.service.TerminalService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User:wangxilei
 * Date:2018/5/3
 * Company:thinkwin
 */
@Controller
@RequestMapping("/manage")
public class ManageController {
    @Resource
    private SaasTenantService saasTenantCoreService;
    @Resource
    private SMSsenderService sMSsenderService;
    @Resource
    private TerminalService terminalService;
    @Resource
    private InfoReleaseTerminalService infoReleaseTerminalService;
    @Resource
    private PublishService publishService;
    @Resource
    private UserService userService;
    /**
     * 设置管理密码
     * @param newPass
     * @param newPassRepeat
     * @return
     */
    @RequestMapping(value = "/setPass",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Object setPass(String newPass,String newPassRepeat){
        if(StringUtils.isNotBlank(newPass)&&StringUtils.isNotBlank(newPassRepeat)){
            if(newPass.equals(newPassRepeat)){
                SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
                if(saasTenant!=null){
                    String passwd = saasTenant.getTerminalManagerPasswd();
                    if(StringUtils.isNotBlank(passwd)){
                        if(SHA1Util.SHA1(newPass).equals(passwd)){
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"原密码和新密码不能相同");
                        }
                        saasTenant.setTerminalManagerPasswd(SHA1Util.SHA1(newPass));
                        boolean b = saasTenantCoreService.updateSaasTenantService(saasTenant);
                        if (b) {
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
                        } else {
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
                        }
                    }else {
                        saasTenant.setTerminalManagerPasswd(SHA1Util.SHA1(newPass));
                        boolean b = saasTenantCoreService.updateSaasTenantService(saasTenant);
                        if (b) {
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
                        } else {
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
                        }
                    }
                }
            }else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"两次输入的密码不一致", BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 修改管理密码
     * @param oldPass
     * @param newPass
     * @param newPassRepeat
     * @return
     */
    @RequestMapping(value = "/updateManagerPass",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Object updateManagerPass(String oldPass,String newPass,String newPassRepeat){
        if(StringUtils.isNotBlank(oldPass)&&StringUtils.isNotBlank(newPass)&&StringUtils.isNotBlank(newPassRepeat)){
            SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
            if(saasTenant!=null){
                String terminalManagerPasswd = saasTenant.getTerminalManagerPasswd();
                if(SHA1Util.SHA1(oldPass).equals(terminalManagerPasswd)){
                    if(newPass.equals(newPassRepeat)){
                        if(newPass.equals(oldPass)){
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"原密码和新密码不能相同");
                        }else {
                            saasTenant.setTerminalManagerPasswd(SHA1Util.SHA1(newPass));
                            boolean b = saasTenantCoreService.updateSaasTenantService(saasTenant);
                            if (b) {
                                //向租户下所有终端推送修改密码指令
                                List<InfoReleaseTerminal> terminals = infoReleaseTerminalService.selectInfoReleaseTerminalByTenantId(TenantContext.getTenantId());
                                CommandMessage message = new CommandMessage();
                                message.setCmd("20016");
                                message.setTenantId(TenantContext.getTenantId());
                                message.setTerminals(terminals.stream().map(InfoReleaseTerminal::getId).collect(Collectors.toList()));
                                message.setData(SHA1Util.SHA1(newPass));
                                message.setTimestamp(new Date().getTime());
                                publishService.pushCommand(message);
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
                            } else {
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
                            }
                        }
                    }else {
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"两次输入的密码不一致");
                    }
                }else{
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"原密码不正确","7111");
                }
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 发送验证码
     * @param tel
     * @return
     */
    @RequestMapping(value = "/sendCode",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Object sendCode(String tel){
        if(StringUtils.isNotBlank(tel)) {
            Map<String, Object> map = new HashMap<>();
            Integer isRegist = null;
            boolean mobile = ValidatorUtil.isMobile(tel);
            if (null != tel && mobile) {
                //查询该手机号是否存在
                SysUser sysUser = new SysUser();
                sysUser.setPhoneNumber(tel);
                List<SysUser> sysUsers = userService.selectUser(sysUser);
                if (sysUsers != null &&sysUsers.size()>0) {
                    isRegist = 1;
                } else {
                    isRegist = 0;
                }

                map.put("isRegist", isRegist);
                //忘记密码 用户不存在  获取验证码失败
                if (isRegist == 0) {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "手机号码未注册", map, BusinessExceptionStatusEnum.Failure.getCode());
                }
                List<String> roles = userService.selectUserRole(sysUsers.get(0));
                map.put("roles",roles);
                if(null == roles && roles.size() <= 0){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"您没有权限");
                }
                if(!roles.contains("1")){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"请输入主管理员手机号");
                }
                //变更管理员
                String code = sMSsenderService.SMSsender(tel, SMSCode.SENDTEMPLATE_YUMEETING);
                //验证码存redis里面
                RedisUtil.set("QYH_SMS_" + tel, code);
                //设置验证码过期时间
                RedisUtil.expire("QYH_SMS_" + tel, 600);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 校验验证码
     * @param tel
     * @param code
     * @return
     */
    @RequestMapping(value = "/verifyCode",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Object verifyCode(String tel,String code){
        if(StringUtils.isNotBlank(tel)&&StringUtils.isNotBlank(code)) {
            Map<String, Object> map = new HashMap<>();
            SaasTenant saasTenant = new SaasTenant();
            saasTenant.setContactsTel(tel);
            //查询该手机号是否存在
            SaasTenant tenant = this.saasTenantCoreService.selectSaasTenantServcie(saasTenant);
            if (tenant == null) {
                //参数状态
                map.put("paramState", "isNotRegist");
                map.put("param", "tel");
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberNotRegister.getDescription(), map, BusinessExceptionStatusEnum.PhoneNumberNotRegister.getCode());
            }
            //获取redis里面的参数
            String verifyCode = RedisUtil.get("QYH_SMS_" + tel);
            if (StringUtils.isBlank(verifyCode)) {
                //参数状态
                map.put("paramState", "error");
                map.put("param", "code");
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), map, BusinessExceptionStatusEnum.VerifyCodeError.getCode());
            }

            if (code.equals(verifyCode)) {
                //删除redis里面的验证码
                RedisUtil.remove("QYH_SMS_" + tel);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
            } else {
                //参数状态
                map.put("paramState", "error");
                map.put("param", "code");
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), map, BusinessExceptionStatusEnum.VerifyCodeError.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 获取终端列表
     * @return
     */
    @RequestMapping(value = "/queryTerminals", method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Object queryTerminals(@ModelAttribute("request") SearchTerminalRequest request) throws UnsupportedEncodingException {
        /*if(StringUtils.isNotBlank(request.getWord())) {
            String str = new String(request.getWord().getBytes("iso8859-1"), "utf-8");
            request.setWord(str);
        }*/
        Map<String,Object> map = new HashMap<>();
        request.setType("app");
        SearchTerminalResponse response = terminalService.queryTerminals(request);
        if(response!=null){
            List<TerminalDto> terminals = response.getTerminals();
            List<TerminalMobileVo> terminalMobileVoList = null;
            if(terminals!=null&&terminals.size()>0){
                terminalMobileVoList = new ArrayList<>();
                for (TerminalDto dto : terminals){
                    TerminalMobileVo vo = new TerminalMobileVo();
                    vo.setId(dto.getId());
                    vo.setName(dto.getName());
                    vo.setBackgroundUrl(dto.getBackgroundUrl());
                    vo.setStatus(dto.getStatus());
                    terminalMobileVoList.add(vo);
                }
            }
            map.put("terminals",terminalMobileVoList);
            map.put("status",response.getStats());
            map.put("pageInfo",response.getPageInfo());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
    }

    /**
     * 修改音量亮度
     * @param terminalId 终端Id
     * @param voice 音量
     * @param light 亮度
     * @return
     */
    @RequestMapping(value = "/updateVoiceAndLight", method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseResult updateVoiceAndLight(String terminalId, String voice, String light){
        if(StringUtils.isNotBlank(terminalId)&&StringUtils.isNotBlank(voice)&&StringUtils.isNotBlank(light)){
            InfoReleaseTerminal terminal = new InfoReleaseTerminal();
            terminal.setId(terminalId);
            terminal.setTerminalVolume(Integer.valueOf(voice));
            terminal.setTerminalBrightness(Integer.valueOf(light));
            boolean b = infoReleaseTerminalService.updateInfoReleaseTerminal(terminal);
            if(b){
                Map<String,Object> map = new HashMap<>();
                map.put("light",light);
                map.put("voice",voice);
                CommandMessage message = new CommandMessage();
                message.setCmd("20012");
                message.setTenantId(TenantContext.getTenantId());
                message.addTerminal(terminalId);
                message.setData(map);
                message.setTimestamp(new Date().getTime());
                publishService.pushCommand(message);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), BusinessExceptionStatusEnum.Success.getCode());
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription(), BusinessExceptionStatusEnum.Failure.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.ParamErr.getDescription(),BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 获取终端详情
     * @return
     */
    @RequestMapping(value = "/getTerminalDetail")
    @ResponseBody
    public Object getTerminalDetail(@RequestParam("terminalId")String terminalId) {
        BusinessExceptionStatusEnum errMsg = BusinessExceptionStatusEnum.Success;
        Object result = null;

        if(StringUtils.isEmpty(terminalId)){
            errMsg = BusinessExceptionStatusEnum.TerminalNotExists;
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, errMsg.getDescription(), result);
        }

        TerminalDto dto = terminalService.getTerminalById4Mobile(terminalId);

        if(null == dto){
            errMsg = BusinessExceptionStatusEnum.TerminalNotExists;
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, errMsg.getDescription(), result);
        }

        result = dto;

        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, errMsg.getDescription(), result);
    }

    /**
     * 远程重启
     * @return
     */
    @RequestMapping(value = "/rebootTerminal")
    @ResponseBody
    public Object rebootTerminal(@RequestParam("terminals")List<String> terminals) {
        terminalService.rebootTerminal(terminals);
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, "", null);
    }
}
