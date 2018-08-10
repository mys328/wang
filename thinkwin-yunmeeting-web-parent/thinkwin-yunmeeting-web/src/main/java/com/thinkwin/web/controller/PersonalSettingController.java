package com.thinkwin.web.controller;

import com.alibaba.fastjson.JSON;
import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessException;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.core.SaasUserOauth;
import com.thinkwin.common.model.core.SaasUserOauthInfo;
import com.thinkwin.common.model.core.SaasUserWeb;
import com.thinkwin.common.model.db.SysAttachment;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.model.db.SysUserRole;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.utils.validation.LoginRegisterValidationUtil;
import com.thinkwin.common.vo.FastdfsVo;
import com.thinkwin.common.vo.FileVo;
import com.thinkwin.common.vo.SysUserVo;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.web.service.FileUploadCommonService;
import com.thinkwin.yunmeeting.weixin.service.WxQrcodeService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 类名: PersonalSettingController </br>
 * 描述: 个人设置页面控制层</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/6/16 </br>
 */
@RequestMapping("/setting")
@Controller
public class PersonalSettingController {
    @Resource
    LoginRegisterService loginRegisterService;
    @Resource
    UserService userService;
    @Resource
    FileUploadService fileUploadService;
    @Resource
    WxQrcodeService wxQrcodeService;
    @Resource
    private SysLogService sysLogService;
    @Resource
    FileUploadCommonService fileUploadCommonService;
    @Resource
    SaasTenantService saasTenantCoreService;

    /**
     * 方法名：changePhoneNumber</br>
     * 描述：更换手机号</br>
     * 参数：[phoneNumber, verCode, userId]</br>
     * 返回值：java.lang.Object</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/19  </br>
     */
    @RequestMapping(value = "/changephonenumber", method = RequestMethod.POST)
    @ResponseBody
    public Object changePhoneNumber(String phoneNumber, String verCode, String userId) {
        //获取租户id
        String tenantId = TenantContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        //判断参数为空
        if (StringUtils.isBlank(phoneNumber) || StringUtils.isBlank(verCode) || StringUtils.isBlank(userId)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        //获取redis里面的验证码
        String code = RedisUtil.get("QYH_SMS_" + phoneNumber);
        if (verCode.equals(code)) {
            //删除redis里面的验证码
            RedisUtil.remove("QYH_SMS_" + phoneNumber);
            //查询用户
            SysUser sysUser = userService.selectUserByUserId(userId);
            sysUser.setPhoneNumber(phoneNumber);
            sysUser.setModifyer(userId);
            sysUser.setModifyTime(new Date());
            //修改用户
            boolean b = userService.updateUserByUserId(sysUser);
            if (b) {
                String photo = sysUser.getPhoto();
                if (StringUtils.isNotBlank(photo)) {
                    sysUser.setPhoto(fileUploadService.selectTenementByFile(photo));
                }
                String s = JSON.toJSONString(sysUser);
                //把字符串存redis里面
                RedisUtil.set(tenantId+"_yunmeeting_SysUserInfo_" + userId, s);
                RedisUtil.expire(tenantId+"_yunmeeting_SysUserInfo_" + userId, 1200);
                //切换数据源
                TenantContext.setTenantId("0");
                SaasUserWeb saasUserWeb = new SaasUserWeb();
                saasUserWeb.setUserId(userId);
                //查询登录用户信息
                SaasUserWeb saasUserWeb1 = loginRegisterService.selectUserLoginInfo(saasUserWeb);
                saasUserWeb1.setAccount(phoneNumber);
                //修改用户账号
                boolean b1 = loginRegisterService.updateUserLoginInfo(saasUserWeb1);
                //切换数据源
                TenantContext.setTenantId(tenantId);
                if (!b1) {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
                }
                Map<String, Object> map = new HashMap<>();
                map.put("phoneNumber", phoneNumber);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), BusinessExceptionStatusEnum.VerifyCodeError.getCode());
    }

    /**
     * 方法名：removeBinding</br>
     * 描述：解除绑定第三方账号信息</br>
     * 参数：[phoneNumber, verCode, userId, wechatAccount]</br>
     * 返回值：java.lang.Object</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/19  </br>
     */
    @RequestMapping(value = "/removebinding", method = RequestMethod.POST)
    @ResponseBody
    public Object removeBinding(String phoneNumber, String verifyCode, String userId, String wechatAccount) {
        //获取tenantId
        String tenantId = TenantContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.NotNullTenantId.getDescription(), BusinessExceptionStatusEnum.NotNullTenantId.getCode());
        }
        //判断参数为空
        if (StringUtils.isBlank(phoneNumber) || StringUtils.isBlank(verifyCode) || StringUtils.isBlank(userId) || StringUtils.isBlank(wechatAccount)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        //获取redis里面的验证码
        String code = RedisUtil.get("QYH_SMS_" + phoneNumber);
        if (verifyCode.equals(code)) {
            //删除redis里面的验证码
            RedisUtil.remove("QYH_SMS_" + phoneNumber);
            SaasUserOauth saasUserOauth = new SaasUserOauth();
            saasUserOauth.setUserId(userId);
            saasUserOauth.setOauthUserName(wechatAccount);
            saasUserOauth.setTenantId(tenantId);
            //查询用户第三方登录表
            List<SaasUserOauth> saasUserOauths = saasTenantCoreService.selectOAuthLoginInfo(saasUserOauth);
            if(null!=saasUserOauths && saasUserOauths.size()>0) {
                for(SaasUserOauth userOauth:saasUserOauths){
                    Integer oauthType = userOauth.getOauthType();
                    userOauth.setIsBind(0);
                    //修改第三方公众平台信息表
                    if(oauthType==1) {
                        saasTenantCoreService.updateOAuthLoginInfo(userOauth);
                    }
                    //修改第三方开放平台信息表
                    if(oauthType==2) {
                        saasTenantCoreService.updateOAuthLoginInfo(userOauth);
                    }
                }
            }
            //修改个人用户表
            SysUser sysUser = userService.selectUserByUserId(userId);
            sysUser.setWechat("");
            sysUser.setOpenId("");
            sysUser.setModifyer(userId);
            sysUser.setModifyTime(new Date());
            boolean b1 = userService.updateUserByUserId(sysUser);
            if (!b1) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
            }
            //删除redis用户缓存
            RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_"+userId);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), BusinessExceptionStatusEnum.VerifyCodeError.getCode());
    }

    /**
     * 方法名：bindingOauth</br>
     * 描述：个人资料页面绑定第三方账号信息</br>
     * 参数：[phoneNumber]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/bindingoauth", method = RequestMethod.POST)
    @ResponseBody
    public Object bindingOauth(long phoneNumber, String userId) {
        //获取租户Id
        String tenantId = TenantContext.getTenantId();
        SaasUserOauth saasUserOauth = new SaasUserOauth();
        saasUserOauth.setUserId(userId);
        //saasUserOauth.setTenantId(tenantId);
        saasUserOauth.setOauthType(1);
        List<SaasUserOauth> saasUserOauths = saasTenantCoreService.selectOAuthLoginInfo(saasUserOauth);
        //获取SaasUserOauth实体
        Map<String, Object> map1 = LoginRegisterValidationUtil.wechatLoginHandle(saasUserOauth);
        SaasUserOauth wechatLoginHandle = (SaasUserOauth) map1.get("saasUserOauth");
        if (null == saasUserOauths) {
            //根据手机号生成ticket
            WxMpQrCodeTicket temporaryQrcodeTicket = wxQrcodeService.getTemporaryQrcodeTicket(phoneNumber, 2592000);
            if(null!=temporaryQrcodeTicket) {
                String ticket = temporaryQrcodeTicket.getTicket();
                wechatLoginHandle.setId(CreateUUIdUtil.Uuid());
                wechatLoginHandle.setTicket(ticket);
                wechatLoginHandle.setTicketTime(ticketTimeHandle());
                wechatLoginHandle.setCreateTime(new Date());
                wechatLoginHandle.setOauthType(1);
                wechatLoginHandle.setIsBind(0);
                wechatLoginHandle.setTenantId(tenantId);
                //给userOauth赋值
                boolean b = this.saasTenantCoreService.saveOAuthLoginInfo(wechatLoginHandle);
                //切换数据源
                TenantContext.setTenantId(tenantId);
                if (b) {
                    String qrCodePictureUrl = wxQrcodeService.getQrCodePictureUrl(ticket);
                    Map<String, Object> map = new HashMap<>();
                    map.put("qrCodePath", qrCodePictureUrl);
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
                }
            }
        } else {
            String ticket = saasUserOauths.get(0).getTicket();
            Date ticketTime = saasUserOauths.get(0).getTicketTime();
            //ticket过期的时候  重新生成ticket
            if (null == ticketTime || StringUtils.isBlank(ticket)  || (new Date().after(ticketTime))) {
                //根据手机号生成ticket
                WxMpQrCodeTicket temporaryQrcodeTicket = wxQrcodeService.getTemporaryQrcodeTicket(phoneNumber, 2592000);
                ticket =temporaryQrcodeTicket.getTicket();
                saasUserOauths.get(0).setTicket(ticket);
                saasUserOauths.get(0).setTicketTime(ticketTimeHandle());
                saasUserOauths.get(0).setUpdateTime(new Date());
                if(!tenantId.equals(saasUserOauths.get(0).getTenantId())){
                    saasUserOauths.get(0).setTenantId(tenantId);
                }
                //修改userOauth表
                boolean b = saasTenantCoreService.updateOAuthLoginInfo(saasUserOauths.get(0));
                if (!b)
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
            }
            String qrCodePictureUrl = wxQrcodeService.getQrCodePictureUrl(ticket);
            Map<String, Object> map = new HashMap<>();
            map.put("qrCodePath", qrCodePictureUrl);
            //删除redis用户缓存
            RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_"+userId);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
    }
    /**
     * 方法名：queryWechatBindingStatus</br>
     * 描述：查询用户是否绑定微信接口</br>
     * 参数：[]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/querywechatbindingstatus", method = RequestMethod.POST)
    @ResponseBody
    public Object queryWechatBindingStatus(){
        //获取当前用户信息
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        String tenantId = userInfo.getTenantId();
        //查询第三方绑定表
        SaasUserOauth saasUserOauth = new SaasUserOauth();
        saasUserOauth.setOauthType(1);
        saasUserOauth.setTenantId(tenantId);
        saasUserOauth.setUserId(userId);
        List<SaasUserOauth> saasUserOauths = saasTenantCoreService.selectOAuthLoginInfo(saasUserOauth);

        if(null!=saasUserOauths&&saasUserOauths.size()>0){
            SaasUserOauth userOauth = saasUserOauths.get(0);
            Integer isBind = userOauth.getIsBind();
            if(isBind==1) {
                String oauthUserName = userOauth.getOauthUserName();
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),oauthUserName, BusinessExceptionStatusEnum.Success.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),0, BusinessExceptionStatusEnum.Success.getCode());
    }
    /**
     * 方法名：updatePersonalInfo</br>
     * 描述：修改用户名和邮箱</br>
     * 参数：[userId, userName, email]</br>
     * 返回值：java.lang.Object</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/19  </br>
     */
    @RequestMapping(value = "/updatepersonalinfo", method = RequestMethod.POST)
    @ResponseBody
    public Object updatePersonalInfo(String userId, String userName, String email) {

        SysUser sysUser = userService.selectUserByUserId(userId);
        String tenantId = TenantContext.getTenantId();
        if(StringUtils.isBlank(tenantId)){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParamErr.getDescription(), BusinessExceptionStatusEnum.ParamErr.getCode());
        }
        if (null == sysUser) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
        }
        boolean username = ValidatorUtil.isUsername(userName);
        boolean emails = ValidatorUtil.isEmail(email);
        if (!username) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "用户名格式不正确");
        }
        if (!emails) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "邮箱格式不正确");
        }
        if(StringUtils.isNotBlank(userName)&&userName.length()>10){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "用户名称长度受限");
        }
        if(StringUtils.isNotBlank(email)&&email.length()>50){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "邮箱长度受限");
        }
        sysUser.setId(userId);
        sysUser.setUserName(userName);
        sysUser.setEmail(email);
        sysUser.setModifyer(userId);
        sysUser.setModifyTime(new Date());
        boolean b = this.userService.updateUserByUserId(sysUser);
        if (b) {
            //修改租户信息
            List<SysUserRole> userRoleByUserIdAndRoleId = userService.getUserRoleByUserIdAndRoleId(userId, "1");
            if(null!=userRoleByUserIdAndRoleId&&userRoleByUserIdAndRoleId.size()>0){
                SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
                if(null!=saasTenant){
                    String userName1 = sysUser.getUserName();
                    String contacts = saasTenant.getContacts();
                    if(StringUtils.isNotBlank(contacts)&&contacts.equals(userName1)){
                    }else{
                        //修改租户表
                        saasTenant.setContacts(userName1);
                        saasTenantCoreService.updateSaasTenantService(saasTenant);
                    }
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("userName", userName);
            map.put("email", email);
            //String photo = sysUser.getPhoto();
            /*String s = "";
            if (StringUtils.isNotBlank(photo)) {
                SysUserVo sysUserVo = new SysUserVo();
                BeanUtils.copyProperties(sysUser,sysUserVo);
                Map<String, String> photos = userService.getUploadInfo(photo);
                if(null != photos) {
                    sysUserVo.setPhoto(photos.get("primary"));
                    sysUserVo.setBigPicture(photos.get("big"));
                    sysUserVo.setInPicture(photos.get("in"));
                    sysUserVo.setSmallPicture(photos.get("small"));
                }
                s = JSON.toJSONString(sysUserVo);
            }else{
                s = JSON.toJSONString(sysUser);
            }*/

            //把字符串存redis里面
            RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_" + userId);
//            sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person_success.toString(), "用户名和邮箱修改成功!", "", Loglevel.info.toString());
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
        }
//        sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person_fail.toString(), "用户名和邮箱修改失败!", "", Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
    }

    /**
     * 方法名：changePhoto</br>
     * 描述：更换头像</br>
     * 参数：[request]</br>
     * 返回值：java.lang.Object</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/19  </br>
     */
    @RequestMapping(value = "/changephoto", method = RequestMethod.POST)
    @ResponseBody
    public Object changePhoto(HttpServletRequest request, String img, String fileName, String size) {
        try {
            //获取租户id
            String tenantId = TenantContext.getTenantId();
            String userId = request.getParameter("userId");
            SysUser sysUser = this.userService.selectUserByUserId(userId);
            //解密获取图片地址
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(fileName.replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,", ""));
            String ext = "png";
            Map<String,String> maps = new HashMap<>();
            maps.put("big","100_100");
            maps.put("in","64_64");
            maps.put("small","32_32");
            //处理图片
            //Map<String,BufferedImage> imageMap = ResizeImage.resizeImage(bytes,maps);
            //调用图片上传
            SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
            List<FastdfsVo> vos =  FileUploadUtil.fileUpload(maps,bytes,"png");
            FileVo vo = fileUploadService.insertFileUpload("3",userId,tenantId,saasTenant.getBasePackageSpaceNum(),"png",vos);
            if (!"0".equals(vo.getId())) {
                if (!"".equals(vo.getId())) {
                    //删除原图片
                    if(StringUtils.isNotBlank(sysUser.getPhoto())){
                        List<SysAttachment> sysAttachments = this.fileUploadService.deleteFileUpload(sysUser.getPhoto(),tenantId);
                        boolean b = FileUploadUtil.deleteFile(sysAttachments);
                        if(b){
                            userService.deleteImageUrl(sysUser.getPhoto());
                        }
                    }
                    if (null == sysUser) {
//                    sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person_fail.toString(), "更换头像失败!", "", Loglevel.error.toString());
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
                    }
                /**
                 修改用户新图Id
                 **/
                    sysUser.setPhoto(vo.getId());
                    sysUser.setModifyer(userId);
                    sysUser.setModifyTime(new Date());
                    boolean b = this.userService.updateUserByUserId(sysUser);
                    SysUserVo sysUserVo = new SysUserVo();
                    if (b) {
                        BeanUtils.copyProperties(sysUser,sysUserVo);
                        if (vo != null) {
                            //Map<String,String> imgMap = this.fileUploadCommonService.selectFileCommon(fileId);
                           // Map<String, String> imgMap = userService.getUploadInfo(fileId);*//*
                            Map map1 = new HashMap();
                            String primary = vo.getPrimary();
                            String big = vo.getBig();
                            String in = vo.getIn();
                            String small = vo.getSmall();
                            map1.put("primary",primary);
                            map1.put("big",big);
                            map1.put("in",in);
                            map1.put("small",small);
                            userService.saveImageUrl(userId,map1,vo.getId());
                            sysUserVo.setPhoto(primary);
                            sysUserVo.setBigPicture(big);
                            sysUserVo.setInPicture(in);
                            sysUserVo.setSmallPicture(small);
                        }
                        //把字符串存redis里面
                        RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_" + userId);
//                    sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person_success.toString(), "更换头像成功!", "", Loglevel.info.toString());
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), sysUserVo);
                    } else {
//                    sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person_fail.toString(), "更换头像失败!", "", Loglevel.info.toString());
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
                    }
                }
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription(), BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        sysLogService.createLog(BusinessType.contactsOp.toString(), EventType.update_person_fail.toString(), "更换头像失败!", "", Loglevel.error.toString());
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.UploadResultError.getDescription(), BusinessExceptionStatusEnum.UploadResultError.getCode());
    }

    /**
     * 方法名：deletePhoto</br>
     * 描述：删除头像</br>
     * 参数：[request]</br>
     * 返回值：java.lang.Object</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/19  </br>
     */
    @RequestMapping(value = "/deletephoto", method = RequestMethod.POST)
    @ResponseBody
    public Object deletePhoto() {
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = userInfo.getUserId();
        String tenantId = userInfo.getTenantId();
        //查询用户信息
        SysUser sysUser = userService.selectUserByUserId(userId);
        if(null!=sysUser){
            String photo = sysUser.getPhoto();
            //删除图片
            /*SysAttachment sys = fileUploadService.downloadFlie(photo);
            boolean success = false;
            if(sys != null){
                success = FileManager.deleteFileUpload(sys.getGroup(), sys.getAttachmentPath() + sys.getFileName());
            }
            success = fileUploadService.deleteFileUpload(photo, tenantId);*/
            List<SysAttachment> sysAttachments = this.fileUploadService.deleteFileUpload(sysUser.getPhoto(),tenantId);
            boolean success = FileUploadUtil.deleteFile(sysAttachments);
            if(success){
                sysUser.setPhoto("");
                userService.updateUserByUserId(sysUser);
                RedisUtil.remove(tenantId+"_yunmeeting_SysUserInfo_" + userId);
                userService.deleteImageUrl(photo);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.Success.getDescription(),BusinessExceptionStatusEnum.Success.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.DataNull.getDescription(),"",BusinessExceptionStatusEnum.DataNull.getCode());
    }

    /**
     * 方法名：queryPersonalInfo</br>
     * 描述：查询用户信息表</br>
     * 参数：[userId]</br>
     * 返回值：java.lang.Object</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/19  </br>
     */
    @RequestMapping(value = "/querypersonalinfo", method = RequestMethod.POST)
    @ResponseBody
    public Object queryPersonalInfo(String userId) {
        if (StringUtils.isBlank(userId)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
        }
        //根据userId查询用户信息表
        SysUser userInfo = this.userService.selectUserByUserId(userId);
        if (null != userInfo) {
            SysUserVo  sysUserVo = new SysUserVo();
            BeanUtils.copyProperties(userInfo,sysUserVo);
            if (StringUtils.isNotBlank(userInfo.getPhoto())) {
                //根据图片id 查询 图片链接
               /* Map<String, String> photos = this.fileUploadService.selectFileCommon(sysUserVo.getPhoto());*/
                Map<String, String> photos = userService.getUploadInfo(userInfo.getPhoto());
                if(null != photos) {
                    sysUserVo.setPhoto(photos.get("primary"));
                    sysUserVo.setBigPicture(photos.get("big"));
                    sysUserVo.setInPicture(photos.get("in"));
                    sysUserVo.setSmallPicture(photos.get("small"));
                }
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), sysUserVo);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
    }

    /*
    * 跳转到个人设置页面
    * */
    @RequestMapping("/personalpage")
    public String personalPage() {
        return "person_app/user";
    }

    @RequestMapping(value = "/companypage", method = RequestMethod.POST)
    public String companyPage() {
        return "login-register/companyPage";
    }


    private Date ticketTimeHandle() {
        //ticket过期时间处理
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(calendar.DATE, 30);//把日期往后增加30天
        Date date = calendar.getTime();
        return date;
    }
}
