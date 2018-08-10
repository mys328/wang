package com.thinkwin.web.controller;

import com.alibaba.fastjson.JSON;
import com.thinkwin.TenantUserVo;

import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.MenuService;
import com.thinkwin.auth.service.OrganizationService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.log.Loglevel;
import com.thinkwin.common.model.core.*;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.model.log.TerminalLog;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.*;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.utils.validation.LoginRegisterValidationUtil;
import com.thinkwin.common.vo.ChangeAdminVo;
import com.thinkwin.common.vo.FastdfsVo;
import com.thinkwin.common.vo.FileVo;
import com.thinkwin.common.vo.SaasTenantInfoVo;
import com.thinkwin.common.vo.meetingVo.PersonsVo;
import com.thinkwin.core.service.LoginRegisterCoreService;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.dictionary.service.DictionaryService;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.goodscenter.service.ProductOrderService;
import com.thinkwin.log.service.SysLogService;
import com.thinkwin.log.service.TerminalLogService;
import com.thinkwin.publish.service.PlatformClientVersionUpgradeRecorderService;
import com.thinkwin.publish.service.PlatformProgramTenantMiddleService;
import com.thinkwin.publish.service.PlatformTenantTerminalMiddleService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.web.service.FileUploadCommonService;
import com.thinkwin.yuncm.service.InfoReleaseTerminalService;
import com.thinkwin.yuncm.service.MeetingReserveService;
import com.thinkwin.yuncm.service.SearchMeetingRoomService;
import com.thinkwin.yuncm.service.YuncmMeetingService;
import com.thinkwin.yunmeeting.weixin.service.WxQrcodeService;
import com.thinkwin.yunmeeting.weixin.service.WxTemplateMsgService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 类名: CompanySettingController </br>
 * 描述: 企业设置页面控制层</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/6/19 </br>
 */
@RequestMapping("/setting")
@Controller
@CrossOrigin
public class CompanySettingController {

    private static final Logger log = LoggerFactory.getLogger(CompanySettingController.class);
    @Resource
    LoginRegisterService loginRegisterService;
    @Resource
    FileUploadService fileUploadService;
    @Resource
    UserService userService;
    @Resource
    WxQrcodeService wxQrcodeService;
    @Resource
    DictionaryService dictionaryService;
    @Resource
    FileUploadCommonService fileUploadCommonService;
    @Resource
    OrganizationService organizationService;
    @Resource
    SysLogService sysLogService;
    @Resource
    SaasTenantService saasTenantCoreService;

    @Resource
    TerminalLogService terminalLogService;

    @Resource
    InfoReleaseTerminalService infoReleaseTerminalService;

    @Resource
    PlatformClientVersionUpgradeRecorderService platformClientVersionUpgradeRecorderService;

    @Resource
    PlatformTenantTerminalMiddleService platformTenantTerminalMiddleService;

    /**
     * 方法名：updateCompanyinfo</br>
     * 描述：修改企业设置信息</br>
     * 参数：[tenantName, companyType, companyAddress, companyDescription]</br>
     * 返回值：java.lang.Object</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/19  </br>
     */
    @RequestMapping(value = "/updatecompanyinfo", method = RequestMethod.POST)
    @ResponseBody
    public Object updateCompanyInfo(String tenantName, String companyType, String companyAddress, String companyDescription) {
        TenantUserVo userInfo = TenantContext.getUserInfo();
        //获取tenantId
        String tenantId = userInfo.getTenantId();
        String userId = userInfo.getUserId();
        if (StringUtils.isBlank(tenantId)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.NotNullTenantId.getDescription(), BusinessExceptionStatusEnum.NotNullTenantId.getCode());
        }
        //校验公司名称
        Map<String, Object> map = LoginRegisterValidationUtil.validationName(null, tenantName);
        if (map.containsKey("ResponseResult")) {
            return map.get("ResponseResult");
        }
        //校验详细地址和公司简介长度
        if(StringUtils.isNotBlank(companyAddress)&&companyAddress.length()>200){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "公司详细地址长度受限");
        }
        if(StringUtils.isNotBlank(companyDescription)&&companyDescription.length()>200){
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "公司简介长度受限");
        }
        //根据tenantId查询租户信息表
        SaasTenantInfo tenantInfo = saasTenantCoreService.selectSaasTenantInfo(tenantId);
        if (null != tenantInfo) {
            if(StringUtils.isNotBlank(tenantName)){
                //校验公司名
                boolean b = this.saasTenantCoreService.checkTenantName(tenantName);
                if (b) {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.TenantNameExist.getDescription(), BusinessExceptionStatusEnum.TenantNameExist.getCode());
                }
                tenantInfo.setTenantName(tenantName);
            }
            tenantInfo.setCompanyAddress(companyAddress);
            tenantInfo.setCompanyType(companyType);
            tenantInfo.setCompanyDescription(companyDescription);
            //修改之前查询租户信息
            SaasTenantInfo tenantInfo1 = saasTenantCoreService.selectSaasTenantInfo(tenantId);
            boolean b1 = this.saasTenantCoreService.updateSaasTenantInfo(tenantInfo);
            if (b1) {
                if(StringUtils.isNotBlank(tenantName)){
                    //修改租户表
                    SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantInfo.getTenantId());
                    saasTenant.setTenantName(tenantName);
                    this.saasTenantCoreService.updateSaasTenantService(saasTenant);
                    //查询组织父Id
                    SysOrganization sysOrganization = organizationService.selectOrganiztionById("1");
                    //修改组织里面的父Id
                    sysOrganization.setOrgName(tenantName);
                    organizationService.updateOrganization(sysOrganization);
                    //查询用户为父组织的人
                    SysUser sysUser = new SysUser();
                    sysUser.setOrgId("1");
                    List<SysUser> sysUsers = userService.selectUser(sysUser);
                    for(SysUser sysUser1 : sysUsers){
                        sysUser1.setOrgName(tenantName);
                        userService.updateUserByUserId(sysUser1);
                    }
                }
                SaasTenantInfoVo saasTenantInfoVo = new SaasTenantInfoVo();
                BeanUtils.copyProperties(tenantInfo,saasTenantInfoVo);
                String companyLogo = saasTenantInfoVo.getCompanyLogo();
                if (StringUtils.isNotBlank(companyLogo)) {
                    /*Map<String, String> logos = this.fileUploadService.selectFileCommon(companyLogo);*/
                    Map<String, String> logos = userService.getUploadInfo(companyLogo);
                    if(null != logos) {
                        saasTenantInfoVo.setCompanyLogo(logos.get("primary"));
                        saasTenantInfoVo.setBigPicture(logos.get("big"));
                        saasTenantInfoVo.setInPicture(logos.get("in"));
                        saasTenantInfoVo.setSmallPicture(logos.get("small"));
                    }
                }
                String s = JSON.toJSONString(saasTenantInfoVo);
                //把字符串存redis里面
                //把字符串存redis里面
                RedisUtil.set(tenantId+"_SaasTenantInfo", s);
                RedisUtil.expire(tenantId+"_SaasTenantInfo", 1200);
                //比对修改企业信息
                String s1 = comparisonCompanySetting(tenantInfo1);
                sysLogService.createLog(BusinessType.companyOp.toString(), EventType.company_information.toString(),s1,"", Loglevel.info.toString());
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasTenantInfoVo);
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
    }

    /**
     * 方法名：refreshInviteCode</br>
     * 描述：刷新企业邀请码</br>
     * 参数：[tenantId]</br>
     * 返回值：java.lang.Object</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/19  </br>
     */
    @RequestMapping(value = "/refreshinvitecode", method = RequestMethod.POST)
    @ResponseBody
    public Object refreshInviteCode() {
        TenantUserVo userInfo = TenantContext.getUserInfo();
        String userId = null;
        if(null != userInfo){
            userId = userInfo.getUserId();
        }
        String tenantId = TenantContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.NotNullTenantId.getDescription(), BusinessExceptionStatusEnum.NotNullTenantId.getCode());
        }
        //生成六位随机数
        String sixByteRandomNumber = CreateRandomNumber.createSixByteRandomNumber();
        //根据tenantId查询租户信息表
        SaasTenantInfo tenantInfo = this.saasTenantCoreService.selectSaasTenantInfo(tenantId);
        if (null != tenantInfo) {
            tenantInfo.setCompanyInvitationCode(sixByteRandomNumber);
            boolean b = this.saasTenantCoreService.updateSaasTenantInfo(tenantInfo);
            if (b) {
                SaasTenantInfoVo saasTenantInfoVo = new SaasTenantInfoVo();
                BeanUtils.copyProperties(tenantInfo,saasTenantInfoVo);
                String companyLogo = tenantInfo.getCompanyLogo();
                if (StringUtils.isNotBlank(companyLogo)) {
                  /*  Map<String, String> logos = this.fileUploadService.selectFileCommon(companyLogo);*/
                    Map<String, String> logos = userService.getUploadInfo(companyLogo);
                    if(null != logos) {
                        saasTenantInfoVo.setCompanyLogo(logos.get("primary"));
                        saasTenantInfoVo.setBigPicture(logos.get("big"));
                        saasTenantInfoVo.setInPicture(logos.get("in"));
                        saasTenantInfoVo.setSmallPicture(logos.get("small"));
                    }
                }
                String s = JSON.toJSONString(saasTenantInfoVo);
                //把字符串存redis里面
                RedisUtil.set( tenantId+"_SaasTenantInfo", s);
                RedisUtil.expire( tenantId+"_SaasTenantInfo", 1200);
                //返回邀请码
                Map<String, Object> map = new HashMap<>();
                map.put("inviteCode", sixByteRandomNumber);
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map);
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
            }
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
    }

    @Resource
    private ProductOrderService productOrderService;
    /**
     * 方法名：queryCompanyInfo</br>
     * 描述：查询租户信息表</br>
     * 参数：[tenantId]</br>
     * 返回值：java.lang.Object</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/19  </br>
     */
    @RequestMapping(value = "/querycompanyinfo", method = RequestMethod.POST)
    @ResponseBody
    public Object queryCompanyInfo() {
        String tenantId = TenantContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.NotNullTenantId.getDescription(), BusinessExceptionStatusEnum.NotNullTenantId.getCode());
        }
        //根据tenantId查询租户信息表
        SaasTenantInfo tenantInfo = this.saasTenantCoreService.selectSaasTenantInfo(tenantId);
        if (null != tenantInfo) {
            //查询公司二维码
            String qrcodePath = tenantInfo.getQrcodePath();
            if (StringUtils.isBlank(qrcodePath)) {
                //根据租户Id 获取二维码然后保存库里
                qrcodePath = wxQrcodeService.getPermanentQrcode(tenantId);
                tenantInfo.setQrcodePath(qrcodePath);
                //把二维码存库
                saasTenantCoreService.updateSaasTenantInfo(tenantInfo);
            }
            //查询公司logo
            String fileId = tenantInfo.getCompanyLogo();
            SaasTenantInfoVo saasTenantInfoVo = new SaasTenantInfoVo();
            BeanUtils.copyProperties(tenantInfo,saasTenantInfoVo);
            if (StringUtils.isNotBlank(fileId)) {
               /* Map<String, String> logos = this.fileUploadService.selectFileCommon(fileId);*/
                Map<String, String> logos = userService.getUploadInfo(fileId);
                if(null != logos) {
                    saasTenantInfoVo.setCompanyLogo(logos.get("primary"));
                    saasTenantInfoVo.setBigPicture(logos.get("big"));
                    saasTenantInfoVo.setInPicture(logos.get("in"));
                    saasTenantInfoVo.setSmallPicture(logos.get("small"));
                }
            }
            String adminId = saasTenantInfoVo.getAdminId();
            SysUser sysUser = userService.selectUserByUserId(adminId);
            if(null != sysUser){
                saasTenantInfoVo.setAdminPhone(sysUser.getPhoneNumber());

            }
            SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
            if(null != saasTenant){
                saasTenantInfoVo.setExpectNumber(saasTenant.getExpectNumber());
                saasTenantInfoVo.setBuyRoomNumTotal(saasTenant.getBuyRoomNumTotal());
                if(saasTenant.getBasePackageExpir() != null){
                    saasTenantInfoVo.setBasePackageExpir(DateUtils.addDays(DateUtils.truncate(saasTenant.getBasePackageExpir(), Calendar.DAY_OF_MONTH), -1));//如果saasTenant.getBasePachageExpir为空就是永久
                }else{
                    saasTenantInfoVo.setBasePackageExpir(null);//如果saasTenant.getBasePachageExpir为空就是永久
                }
                String basePackageType = saasTenant.getBasePackageType();
               /* if(StringUtils.isNotBlank(basePackageType)) {
                    ProductPackSku ppsku = productOrderService.selectProductPackSkuBySku(basePackageType);
                    if(null != ppsku){
                        saasTenantInfoVo.setProductName(ppsku.getSkuDesc());
                    }
                }*/
               if("0".equals(saasTenant.getTenantType())){
                   saasTenantInfoVo.setProductName("免费版");
               }else {
                   saasTenantInfoVo.setProductName("专业版");
               }
               saasTenantInfoVo.setSpaceTotal(saasTenant.getBuySpaceNumTotal());
            }
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasTenantInfoVo);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
    }

    public SaasTenantInfoVo saasTenantInfoToSaasTenantInfoVo(SaasTenantInfo tenantInfo){
        if(null != tenantInfo) {
            SaasTenantInfoVo saasTenantInfoVo = new SaasTenantInfoVo();
            saasTenantInfoVo.setId(tenantInfo.getId());
            saasTenantInfoVo.setAdminId(tenantInfo.getAdminId());
            saasTenantInfoVo.setCompanyAddress(tenantInfo.getCompanyAddress());
            saasTenantInfoVo.setCompanyDescription(tenantInfo.getCompanyDescription());
            saasTenantInfoVo.setCompanyInvitationCode(tenantInfo.getCompanyInvitationCode());
            saasTenantInfoVo.setCompanyLogo(tenantInfo.getCompanyLogo());
            saasTenantInfoVo.setCompanyType(tenantInfo.getCompanyType());
            saasTenantInfoVo.setQrcodePath(tenantInfo.getQrcodePath());
            saasTenantInfoVo.setTenantId(tenantInfo.getTenantId());
            saasTenantInfoVo.setTenantName(tenantInfo.getTenantName());
            return saasTenantInfoVo;
        }
        return null;
    }

    /**
     * 方法名：changeLogo</br>
     * 描述：更换企业设置logo</br>
     * 参数：[request]</br>
     * 返回值：java.lang.Object</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/19  </br>
     */
    @RequestMapping(value = "/changelogo", method = RequestMethod.POST)
    @ResponseBody
    public Object changeLogo(HttpServletRequest request, String img, String fileName, String size) {
        try {
            String userId = request.getParameter("userId");
            String tenantId = TenantContext.getTenantId();
            //查询租户信息
            SaasTenantInfo saasTenantInfo = this.saasTenantCoreService.selectSaasTenantInfo(tenantId);

            //解密获取图片地址
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(fileName.replace("data:image/png;base64,", "").replace("data:image/jpeg;base64,", ""));
            //调用图片上传
            String ext = "png";
            Map<String,String> map = new HashMap<>();
            map.put("big","100_100");
            map.put("in","80_80");
            map.put("small","64_64");
            //处理图片
            //Map<String,BufferedImage> imageMap = ResizeImage.resizeImage(bytes,map);
            //String fileId = fileUploadCommonService.uploadFileCommon(imageMap,ext,bytes,tenantId,userId);
            SaasTenant saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
            List<FastdfsVo> vos = FileUploadUtil.fileUpload(map,bytes,"png");
            FileVo vo = this.fileUploadService.insertFileUpload("3",userId,tenantId,saasTenant.getBasePackageSpaceNum(),"png",vos);
            if (!"0".equals(vo.getId())) {
                if (!"".equals(vo.getId())) {
                    //删除原图片
                    if(StringUtils.isNotBlank(saasTenantInfo.getCompanyLogo())){
                        String companyLogo1 = saasTenantInfo.getCompanyLogo();
                        List<SysAttachment> sysAttachments = this.fileUploadService.deleteFileUpload(companyLogo1,tenantId);
                        boolean b = FileUploadUtil.deleteFile(sysAttachments);
                        if(b){
                            userService.deleteImageUrl(companyLogo1);
                        }

                    }

                    //获取tenantId
                    if (StringUtils.isBlank(tenantId)) {
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.NotNullTenantId.getDescription(), BusinessExceptionStatusEnum.NotNullTenantId.getCode());
                    }
                    if (null == saasTenantInfo) {
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
                    }
                    saasTenantInfo.setCompanyLogo(vo.getId());
                    boolean b = this.saasTenantCoreService.updateSaasTenantInfo(saasTenantInfo);
                    SaasTenantInfoVo saasTenantInfoVo = new SaasTenantInfoVo();
                    BeanUtils.copyProperties(saasTenantInfo,saasTenantInfoVo);
                    if (b) {
                       /* Map<String,String> imgMap = this.fileUploadCommonService.selectFileCommon(fileId);*/
                        //Map<String, String> imgMap = fileUploadCommonService.selectFileCommon(vo.getId());
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
                        String companyLogo = saasTenantInfoVo.getCompanyLogo();
                        if (StringUtils.isNotBlank(companyLogo)) {
                            saasTenantInfoVo.setCompanyLogo(primary);
                            saasTenantInfoVo.setBigPicture(big);
                            saasTenantInfoVo.setInPicture(in);
                            saasTenantInfoVo.setSmallPicture(small);
                        }
                        String s = JSON.toJSONString(saasTenantInfoVo);
                        //把字符串存redis里面
                        RedisUtil.set(tenantId+"_SaasTenantInfo", s);
                        RedisUtil.expire( tenantId+"_SaasTenantInfo", 1200);
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), saasTenantInfoVo);
                    } else {
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
                    }
                } else {
                    sysLogService.createLog(BusinessType.meetingOp.toString(),EventType.add_meeting_room.toString(), "创建会议室失败", BusinessExceptionStatusEnum.Failure.getDescription(), Loglevel.error.toString());
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
                }
            } else {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.SpaceIsNotEnough.getDescription(), BusinessExceptionStatusEnum.SpaceIsNotEnough.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
    }

    @Resource
    private MenuService menuService;
    @Resource
    private WxTemplateMsgService wxTemplateMsgService;
    /**
     * 方法名：changeRootAdmin</br>
     * 描述：修改主管理员接口</br>
     * 参数：[phoneNumber,code]</br>
     * 返回值：java.lang.Object</br>
     */
    @RequestMapping(value = "/changerootadmin", method = RequestMethod.POST)
    @ResponseBody
    public Object changeRootAdmin(String phoneNumber, String verifyCode) {
        //获取redis里面的验证码
        String code = RedisUtil.get("QYH_SMS_" + phoneNumber);
        if (StringUtils.isBlank(code)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), BusinessExceptionStatusEnum.VerifyCodeError.getCode());
        }
        TenantUserVo userInfo = TenantContext.getUserInfo();
        //获取租户Id
        String tenantId = userInfo.getTenantId();
        //获取用户Id
        String userId = userInfo.getUserId();
        if (StringUtils.isBlank(tenantId)) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.NotNullTenantId.getDescription(), BusinessExceptionStatusEnum.NotNullTenantId.getCode());
        }
        if (code.equals(verifyCode)) {
            //删除redis里面的验证码
            RedisUtil.remove("QYH_SMS_" + phoneNumber);
            SaasUserWeb saasUserWeb = new SaasUserWeb();
            saasUserWeb.setAccount(phoneNumber);
            SaasUserWeb userWeb = saasTenantCoreService.selectUserLoginInfo(saasUserWeb);
            if(null == userWeb){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberNotTenant.getDescription(), BusinessExceptionStatusEnum.PhoneNumberNotTenant.getCode());
            }
            String userIdd = userWeb.getUserId();
            if(StringUtils.isBlank(userIdd)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "用户信息错误！");
            }
            if (!tenantId.equals(userWeb.getTenantId())) {
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PhoneNumberNotTenant.getDescription(), BusinessExceptionStatusEnum.PhoneNumberNotTenant.getCode());
            }
            if(userId.equals(userIdd)){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PermissionDenied.getDescription(), BusinessExceptionStatusEnum.PermissionDenied.getCode());
            }
            //查询当前用户权限
            List<SysUserRole> sysUserRoles = userService.getUserRoleByUserIdAndRoleId(userId, "1");
            if (null!=sysUserRoles&&sysUserRoles.size() > 0) {
                SysUserRole sysUserRole = sysUserRoles.get(0);
                sysUserRole.setModifyerId(sysUserRole.getUserId());
                sysUserRole.setUserId(userId);
                sysUserRole.setRoleId("99");
                //修改当前用户权限
                boolean b1 = userService.updateUserRole(sysUserRole);
                if (b1) {
                    List<String> list = new ArrayList<>();
                    list.add("1");
                    //增加被修改这权限
                    boolean b2 = userService.saveUserRole(userIdd, list);
                    //查询租户信息表
                    SaasTenantInfo saasTenantInfo = saasTenantCoreService.selectSaasTenantInfo(tenantId);
                    if (null != saasTenantInfo) {
                        saasTenantInfo.setAdminId(userIdd);
                        //修改租户信息表的主管理员Id
                        boolean b = saasTenantCoreService.updateSaasTenantInfo(saasTenantInfo);
                        if (b) {
                            String companyLogo = saasTenantInfo.getCompanyLogo();
                            if (StringUtils.isNotBlank(companyLogo)) {
                                saasTenantInfo.setCompanyLogo(fileUploadService.selectTenementByFile(companyLogo));
                            }
                            String s = JSON.toJSONString(saasTenantInfo);
                            //把字符串存redis里面
                            RedisUtil.set(tenantId+"_SaasTenantInfo", s);
                            RedisUtil.expire( tenantId+"_SaasTenantInfo", 1200);
                            SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
                            String tenantType = "0";//0免费 1收费
                            if(null != saasTenant){
                                String tenantType1 = saasTenant.getTenantType();
                                if(StringUtils.isNotBlank(tenantType1)){
                                    tenantType = tenantType1;
                                }
                            }
                            //查询用户状态
                            SysUser sysUser = userService.selectUserByUserId(userIdd);
                            saasTenant.setContactsTel(sysUser.getPhoneNumber());
                            saasTenant.setContacts(sysUser.getUserName());
                            saasTenant.setContactsEmail(sysUser.getEmail());
                            //修改saasTenant表
                            saasTenantCoreService.updateSaasTenantService(saasTenant);
                            List<SysMenu> sysMenus = new ArrayList<>();
                            sysMenus = menuService.selectMenus(userIdd, null, tenantType);
                            String s1 = JSON.toJSONString(sysMenus);
                            //把字符串存redis里面
                            RedisUtil.set(tenantId + "_Menus_" + userIdd, s1);
                            RedisUtil.expire(tenantId + "_Menus_" + userIdd, 1200);
                            RedisUtil.remove(tenantId +"_Menus_" + userId);
                            Integer status = sysUser.getStatus();
                            if(status == 2 || status == 3){
                                //修改用户状态
                                sysUser.setStatus(1);
                                userService.updateUserByUserId(sysUser);
                            }
                            //发送微信通知
                            ChangeAdminVo changeAdminVo = new ChangeAdminVo();
                            changeAdminVo.setTenantName(saasTenantInfo.getTenantName());
                            //查询新用户信息
                            SysUser newSysUser = userService.selectUserByUserId(userIdd);
                            changeAdminVo.setNewAdmin(newSysUser);
                            //查询老用户信息
                            SysUser oldSysUser = userService.selectUserByUserId(userId);
                            changeAdminVo.setOldAdmin(oldSysUser);
                            wxTemplateMsgService.changeAdmin(changeAdminVo);
                            //加入日志
                            sysLogService.createLog(BusinessType.companyOp.toString(),EventType.change_admin.toString(),saasTenantInfo.getTenantName()+"主管理员变更为"+sysUser.getUserName(),"",Loglevel.info.toString());
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
                        }
                    }
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.OperateDBError.getDescription(), BusinessExceptionStatusEnum.OperateDBError.getCode());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.PermissionDenied.getDescription(), BusinessExceptionStatusEnum.PermissionDenied.getCode());
            }
        } else {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), BusinessExceptionStatusEnum.VerifyCodeError.getCode());
        }
    }

    /**
     * 方法名：selectTenantType</br>
     * 描述：查询行业类型</br>
     * 参数：</br>
     * 返回值：</br>
     */
    @RequestMapping("/selecttenanttype")
    @ResponseBody
    public Object selectTenantType() {
        List<SysDictionary> sysDictionaries = dictionaryService.selectSysDictionaryByParentId("6500");
        if (null != sysDictionaries && sysDictionaries.size() > 0) {
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), sysDictionaries);
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.DataNull.getDescription(), BusinessExceptionStatusEnum.DataNull.getCode());
    }

    /**
     * 方法名：comparisonCompanySetting</br>
     * 描述：比对修改企业设置</br>
     * 参数：tenantInfoBefore 修改前的企业设置</br>
     * 返回值：</br>
     */
    private String comparisonCompanySetting(SaasTenantInfo tenantInfoBefore){
        if(null!=tenantInfoBefore){
            //查询修改后的企业设置信息
            SaasTenantInfo tenantInfoAfter = saasTenantCoreService.selectSaasTenantInfo(tenantInfoBefore.getTenantId());
            if(!tenantInfoAfter.getTenantName().equals(tenantInfoBefore.getTenantName())){
                return "企业信息修改为："+tenantInfoAfter.getTenantName();
            }
        }
        return null;
    }

    SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " );
    @Resource
    private LoginRegisterCoreService loginRegisterCoreService;
    @Resource
    private YuncmMeetingService yuncmMeetingService;
    @Resource
    private SearchMeetingRoomService searchMeetingRoomService;
    @Resource
    private MeetingReserveService meetingReserveService;
    @Resource
    PlatformProgramTenantMiddleService platformProgramTenantMiddleService;
    /**
     * 企业解散功能逻辑controller层
     * @return
     */
    @RequestMapping("/businessBankruptcy")
    @ResponseBody
    public ResponseResult businessBankruptcy(HttpServletRequest request, String phone, String code) {
        String tenantId = TenantContext.getTenantId();
        TenantUserVo userInfo = TenantContext.getUserInfo();
        Map map = new HashMap();
        if (StringUtils.isNotBlank(tenantId) && null != userInfo){
            if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(code)) {
                //获取redis里面的验证码
                String code1 = RedisUtil.get("QYH_SMS_" + phone);
                if (StringUtils.isNotBlank(code1)) {
                    if (code.equals(code1)) {
                        RedisUtil.remove("QYH_SMS_" + phone);
                        List<SysUserRole> userRoleByUserIdAndRoleId = userService.getUserRoleByUserIdAndRoleId(userInfo.getUserId(), "1");
                        if(null == userRoleByUserIdAndRoleId || userRoleByUserIdAndRoleId.size() <= 0 ){
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1,BusinessExceptionStatusEnum.PermissionDenied.getDescription(),BusinessExceptionStatusEnum.PermissionDenied.getCode());
                        }
                        String dataBaseName = null;
                        SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
                        if(null == saasTenant){
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.ParameterIsNull.getDescription(), BusinessExceptionStatusEnum.ParameterIsNull.getCode());
                        }
                        dataBaseName = saasTenant.getTenantCode() + "_" + tenantId;
                        //在执行delDataBaseByName的方法前需要确认那些信息需要备份先来，确定好之后再执行删除数据库操作
                        //根据条件查询用户列表功能
                        List<SysUser> userList = userService.selectUser(new SysUser());
                        if(null != userList && userList.size() > 0){
                            for (SysUser sysU:userList) {
                                if(null != sysU) {
                                    String id = sysU.getId();
                                    if (sysU.getStatus() != 89) {
                                        Dissolutionuserinfo dissolutionuserinfo = new Dissolutionuserinfo();
                                        dissolutionuserinfo.setId(CreateUUIdUtil.Uuid());
                                        dissolutionuserinfo.setTenantId(tenantId);
                                        dissolutionuserinfo.setUserId(id);
                                        dissolutionuserinfo.setCreateTime(new Date());
                                        dissolutionuserinfo.setPhoneNumber(sysU.getPhoneNumber());
                                        dissolutionuserinfo.setUserName(sysU.getUserName());
                                        dissolutionuserinfo.setUserNamePinyin(sysU.getUserNamePinyin());
                                        boolean b = saasTenantCoreService.saveDissolutionuserinfo(dissolutionuserinfo);
                                        if (!b) {
                                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据备份失败！");
                                        }
                                        //该处是删除该租户下的用户的 token信息
                                        RedisUtil.remove(tenantId + "_yunmeeting_WEB_token_" + id);
                                        RedisUtil.remove(tenantId + "_yunmeeting_APP_token_" + id);
                                        RedisUtil.remove(tenantId + "_yunmeeting_wx_token_" + id);
                                    }else{
                                        //根据用户的主键id获取saasUserWeb信息
                                        SaasUserWeb saasUserWeb = saasTenantCoreService.selectSaasUserWebByUserId(id);
                                        if(null != saasUserWeb){
                                            String tenantId1 = saasUserWeb.getTenantId();
                                            if(tenantId.equals(tenantId1)){
                                                Dissolutionuserinfo dissolutionuserinfo = new Dissolutionuserinfo();
                                                dissolutionuserinfo.setId(CreateUUIdUtil.Uuid());
                                                dissolutionuserinfo.setTenantId(tenantId);
                                                dissolutionuserinfo.setUserId(id);
                                                dissolutionuserinfo.setCreateTime(new Date());
                                                dissolutionuserinfo.setPhoneNumber(sysU.getPhoneNumber());
                                                dissolutionuserinfo.setUserName(sysU.getUserName());
                                                dissolutionuserinfo.setUserNamePinyin(sysU.getUserNamePinyin());
                                                boolean b = saasTenantCoreService.saveDissolutionuserinfo(dissolutionuserinfo);
                                                if (!b) {
                                                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "数据备份失败！");
                                                }
                                                //该处是删除该租户下的用户的 token信息
                                               // RedisUtil.remove(tenantId + "_yunmeeting_token_" + id);
                                                RedisUtil.remove(tenantId + "_yunmeeting_WEB_token_" + id);
                                                RedisUtil.remove(tenantId + "_yunmeeting_APP_token_" + id);
                                                RedisUtil.remove(tenantId + "_yunmeeting_wx_token_" + id);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Datasnapshot datasnapshot = new Datasnapshot();
                        datasnapshot.setId(CreateUUIdUtil.Uuid());
                        //此处需要备份信息
                        // 获取用户数量(除离职和移除人员外的总数)
                        int userNum = userService.selectUserTotalNum();
                        datasnapshot.setTotalUserNum(userNum);
                        //获取所有组织机构数量
                        int organizationNum = organizationService.getOrganizationNum();
                        datasnapshot.setDepNum(organizationNum);
                        //获取所有会议室区域的数量
                        List<YuncmRoomArea> yuncmRoomAreas=this.yuncmMeetingService.selectAllListYuncmRoomArea();
                        int yuncmRoomAreaNum = yuncmRoomAreas.size();
                        datasnapshot.setTotalConferenceRoomAreaNum(yuncmRoomAreaNum);
                        //获取会议室数量
                        List<YuncmMeetingRoom> yuncmMeetingRoomList = searchMeetingRoomService.selectMeetingRoomNum();
                        int meetingRoomNum = yuncmMeetingRoomList.size();
                        datasnapshot.setTotalConferenceRoomNum(meetingRoomNum);
                        //获取总会议数量(需求定为只取以结束的)
                        List<YunmeetingConference> yunmeetingConferenceList = meetingReserveService.selectMeetingConferenceNum("4");
                        int conferenceNum= 0;
                        if(null != yunmeetingConferenceList) {
                            conferenceNum=yunmeetingConferenceList.size();
                        }
                        datasnapshot.setTotalConferenceNum(conferenceNum);
                        //已取消会议数量
                        List<YunmeetingConference> yunmeetingConferenceList1 = meetingReserveService.selectMeetingConferenceNum("5");
                        int cancelConferenceNum = 0;
                        if(null != yunmeetingConferenceList1) {
                            cancelConferenceNum = yunmeetingConferenceList1.size();
                        }
                        datasnapshot.setCancelConferenceNum(cancelConferenceNum);
                        int attendeeNum = 0;//总会议参会人数
                        List<String> endMeetingIds = new ArrayList<>();//结束会议的主键id集合
                        if(null != yunmeetingConferenceList && yunmeetingConferenceList.size() > 0){
                            for (YunmeetingConference yunmeetingConferencee:yunmeetingConferenceList) {
                                if(null != yunmeetingConferencee) {
                                    String id = yunmeetingConferencee.getId();
                                    if(StringUtils.isNotBlank(id)) {
                                        endMeetingIds.add(id);
                                        //根据会议id获取参会人员
                                        List<PersonsVo> personsVos = meetingReserveService.selectAllParticipantByMeetingId(id);
                                        if (null != personsVos && personsVos.size() > 0) {
                                            attendeeNum += personsVos.size();
                                        }
                                    }
                                }
                            }
                        }
                        //会议响应率
                        int replyNum = 0;
                        if(endMeetingIds.size() >0) {
                            replyNum = yuncmMeetingService.getUserReply(endMeetingIds);
                        }
                        log.info("会议相应人数 ：：：："+replyNum);
                        System.out.println("会议相应人数 ：：：："+replyNum);
                        log.info("总参会人数 ：：：：："+attendeeNum);
                        System.out.println("总参会人数 ：：：：："+attendeeNum);
                        String meetingResponseRate = "0";
                        if(replyNum > 0){
                            Double meetingResponseRatee = (double)replyNum/attendeeNum*100;
                            String result = String .format("%.1f",meetingResponseRatee);
                            meetingResponseRate = result+"%";
                        }else{
                            meetingResponseRate = "0%";
                        }
                        log.info("会议相应率 ：：：："+meetingResponseRate);
                        System.out.println("会议相应率 ：：：："+meetingResponseRate);
                        datasnapshot.setMeetingResponseRate(meetingResponseRate);
                        //会议签到率
                        int signNum = 0;
                        if(endMeetingIds.size() >0) {
                            signNum = yuncmMeetingService.getUserSign(endMeetingIds);
                        }
                        String totalAttendanceRate = "0";
                        if(signNum >0){
                           Double totalAttendanceRatee  = (double)signNum/attendeeNum*100;
                            String resultt = String .format("%.1f",totalAttendanceRatee);
                            totalAttendanceRate = resultt+"%";
                        }else {
                            totalAttendanceRate = "0%";
                        }
                        datasnapshot.setTotalAttendanceRate(totalAttendanceRate);
                        //会议室使用率
                        ///////////////////////////////////////////////////////////
                        String conferenceUsageRate = "0";//会议室使用率
                        String staTime = sdf.format(saasTenant.getCreateTime());
                        String endTime = sdf.format(new Date());
                        //获取统计的天数
                        int days = TimeUtil.getTimeDifference(staTime,endTime);
                        //查询会议室可预订时间获取会议室预定时长
                        YuncmRoomReserveConf conf = meetingReserveService.selectYuncmRoomReserveConf();
                        long reserveTime = conf.getReserveTimeEnd().getTime() - conf.getReserveTimeStart().getTime();
                        //获取会议室总数量
                        List<YuncmMeetingRoom> rooms = this.yuncmMeetingService.selectTenantMeetingRoomCount();
                        //获取会议室预定总时长
                        long totalRoomTime = reserveTime * (days + 1) * rooms.size();
                        //获取会议总时长
                        long totalTime = getMeetingTotalTime(yunmeetingConferenceList);
                        //获取使用率保留一位小数
                        Double tota = (double) totalTime / totalRoomTime *100;
                        if(!tota.isNaN()){
                            String result = String .format("%.1f",tota);
                            conferenceUsageRate = result+"%";
                        }else{
                            conferenceUsageRate = "0%";
                        }
                        ///////////////////////////////////////////////////////////
                        datasnapshot.setConferenceUsageRate(conferenceUsageRate);
                        //已使用存储空间
                        String usedStorageSpace = fileUploadService.selectTenantuseSpace(tenantId);
                        datasnapshot.setUsedStorageSpace(usedStorageSpace);
                        //会议总时长
                        datasnapshot.setTotalConferenceLength(totalTime);
                        //租户主键id
                        datasnapshot.setTenantId(tenantId);
                        //租户解散时间
                        datasnapshot.setDissolutionTime(new Date());
                        List<InfoReleaseTerminal> infoReleaseTerminals = infoReleaseTerminalService.selectInfoReleaseTerminalByTenantId(tenantId);
                        int terminalNum = 0;
                        if(null != infoReleaseTerminals && infoReleaseTerminals.size() > 0){
                           terminalNum = infoReleaseTerminals.size();
                        }
                        datasnapshot.setTerminalTotalNum(terminalNum);//备份租户的终端数量
                        boolean b1 = saasTenantCoreService.saveDataSnapshot(datasnapshot);
                        if(!b1){
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"数据备份失败！");
                        }
                        if(null != infoReleaseTerminals && infoReleaseTerminals.size() > 0){
                            for (InfoReleaseTerminal infoReleaseTerminal:infoReleaseTerminals) {
                                if(null != infoReleaseTerminal){
                                    String hardwareId = infoReleaseTerminal.getHardwareId();
                                    if(StringUtils.isNotBlank(hardwareId)){
                                        RedisUtil.remove(hardwareId+ "_register_terminal");//删除终端绑定状态
                                    }
                                }
                            }
                        }
                        TerminalLog terminalLog = new TerminalLog();
                        terminalLog.setTenantId(tenantId);
                        terminalLogService.deleteTerminalLog(terminalLog);
                        //根据租户主键id删除终端版本升级日志
                        platformClientVersionUpgradeRecorderService.delPlatformClientVersionUpgradeRecorderByTenantId(tenantId);
                        //删除租户与终端的关联关系数据
                        platformTenantTerminalMiddleService.delPlatformTenantTerminalMiddlByTenantId(tenantId);
                        //删除redis中缓存的key值
                        List<String> keys = RedisUtil.keys(tenantId+"_*");
                        if(null != keys && keys.size() > 0){
                            for (String key:keys) {
                                if(StringUtils.isNotBlank(key)){
                                    RedisUtil.remove(key);
                                }
                            }
                        }
                        SaasTenant saasTenant1 = new SaasTenant();
                        saasTenant1.setId(tenantId);
                        saasTenant1.setStatus(2);//暂定2为解散状态
                        saasTenant1.setModifyTime(new Date());
                        saasTenantCoreService.updateSaasTenantService(saasTenant1);//SaasTenantById(tenantId);
                        //该处需要判断saasUserOuth表中是否存在tenantID的数据 如果有则修改没有则略过
                        int saasUserOuthNum = saasTenantCoreService.selectSaasUserOuthByTenantId(tenantId);
                        if(saasUserOuthNum >0) {
                            boolean b2 = saasTenantCoreService.updateSaasUserOauthTenantIdByTenantId(tenantId);
                            if (!b2) {
                                sysLogService.createLog(BusinessType.companyOp.toString(), EventType.company_dissolution.toString(), "企业" + saasTenant.getTenantName() + " 解散失败！", BusinessExceptionStatusEnum.Failure.getDescription(), Loglevel.info.toString());
                                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "企业解散失败！");
                            }
                        }
                        RedisUtil.set("yunmeeting_qiyejiesan_status_"+tenantId,"1");
                        map.put("success",true);
                        //添加微信推送功能
                        SysUser sysUser = new SysUser();
                        sysUser.setId(userInfo.getUserId());
                        sysUser.setUserName(userInfo.getUserName());
                        wxTemplateMsgService.dissolutionCompanyNotice(sysUser,saasTenant);
                        //刪除数据库功能(此处要确定好哪些数据需要备份后才可以执行该操作)
                        boolean b = loginRegisterCoreService.delDataBaseByName(dataBaseName);
                        if(b) {
                            //此处需要添加删除该租户定制节目数据信息的内容
                            platformProgramTenantMiddleService.delTenantCustomizedProgramByTenantId(tenantId);
                            sysLogService.createLog(BusinessType.companyOp.toString(), EventType.company_dissolution.toString(), "企业" + saasTenant.getTenantName() + " 解散成功！", BusinessExceptionStatusEnum.Success.getDescription(), Loglevel.info.toString());
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(), map, BusinessExceptionStatusEnum.Success.getCode());
                        }else {
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, "企业解散失败！");
                        }
                        } else {
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), BusinessExceptionStatusEnum.VerifyCodeError.getCode());
                    }
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.VerifyCodeError.getDescription(), BusinessExceptionStatusEnum.VerifyCodeError.getCode());
            }
            map.put("success",false);
            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Failure.getDescription(),map, BusinessExceptionStatusEnum.Failure.getCode());
        }
        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,BusinessExceptionStatusEnum.ParamErr.getDescription(),BusinessExceptionStatusEnum.ParamErr.getCode());
    }

    /**
     * 获取会议总时长
     * @param yunmeetingConferenceList
     * @return
     */
    public Long getMeetingTotalTime(List<YunmeetingConference> yunmeetingConferenceList){
        long totalTime = 0;
        if(null != yunmeetingConferenceList && yunmeetingConferenceList.size() > 0){
            for (YunmeetingConference yunmeetingConference:yunmeetingConferenceList) {
                totalTime += yunmeetingConference.getTakeEndDate().getTime() - yunmeetingConference.getTakeStartDate().getTime();
            }
        }
        return totalTime;
    }
}
