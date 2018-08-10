package com.thinkwin.web.terminalController;

import com.alibaba.fastjson.JSON;
import com.thinkwin.TenantUserVo;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.core.SaasTenantInfo;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.SysUserVo;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.InfoReleaseTerminalService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * User: yinchunlei
 * Date: 2018/5/2
 * Company: thinkwin
 */
@Controller
public class AppController {

    private Logger logger = Logger.getLogger(AppController.class);

    @Resource
    InfoReleaseTerminalService infoReleaseTerminalService;

    @Resource
    private UserService userService;
    @Resource
    private SaasTenantService saasTenantCoreService;

    /**
     * app登录功能接口
     * @return
     */
    @RequestMapping(value = "/getUserInfo")
    @ResponseBody
    public Object getUserInfo (){
        logger.info("app登录获取用户信息");
        System.out.println("app登录获取用户信息");
        Map map = new HashMap();
        String userId = TenantContext.getUserInfo().getUserId();
        String tenantId = TenantContext.getTenantId();
        if (StringUtils.isNotBlank(userId)) {
            SysUser sysUser = null;
            String s1 = RedisUtil.get(tenantId+"_yunmeeting_SysUserInfo_" + userId);
            SysUserVo sysUserVo = new SysUserVo();
            if(StringUtils.isNotBlank(s1)){
                sysUserVo = JSON.parseObject(s1, SysUserVo.class);
            }else {
                sysUser = userService.selectUserByUserId(userId);
                if (null != sysUser) {
                    BeanUtils.copyProperties(sysUser,sysUserVo);
                    sysUserVo.setOpenId(sysUser.getOpenId());
                    String photo = sysUser.getPhoto();
                    if (StringUtils.isNotBlank(photo)) {
                        Map<String, String> photos = userService.getUploadInfo(photo);
                        if(null != photos) {
                            sysUserVo.setPhoto(photos.get("primary"));
                            sysUserVo.setBigPicture(photos.get("big"));
                            sysUserVo.setInPicture(photos.get("in"));
                            sysUserVo.setSmallPicture(photos.get("small"));
                        }
                    }
                    boolean isRole = getUserRoleIdsByUserId();
                    sysUserVo.setRole(isRole);
                    String s = JSON.toJSONString(sysUserVo);
                    //把字符串存redis里面
                    RedisUtil.set(tenantId+"_yunmeeting_SysUserInfo_" + userId, s);
                    RedisUtil.expire(tenantId+"_yunmeeting_SysUserInfo_" + userId, 1200);
                }
            }
            map.put("userName", sysUserVo.getUserName());
            //  }
            if (StringUtils.isNotBlank(tenantId)) {
                TenantContext.setTenantId(tenantId);
                List<String> roles = userService.selectUserRole(userId);
                map.put("roles",roles);
                if(null == roles && roles.size() <= 0){
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"您没有权限");
                }
                SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(tenantId);
                if (null != saasTenant) {
                    String tenantType = saasTenant.getTenantType();
                    if(StringUtils.isBlank(tenantType)){
                        tenantType = "0";
                    }
                    if("0".equals(tenantType)){
                        //return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(15,"您的企业当前为免费版，请登录网页端控制台-订单管理进行专业版升级后使用");//提示语现在定为前端自己显示（2018.6.4）
                        map.put("isExpired", 0);//0为免费用户
                        return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
                    }
                    Date basePackageExpir = saasTenant.getBasePackageExpir();
                    if(null != basePackageExpir){
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        String format1 = df.format(basePackageExpir);
                        String format = df.format(date);
                        int i = compare_date(format1, format);
                        if(i==1){
                            map.put("isExpired", 1);//购买期限内
                        }else if(i == -1){
                            map.put("isExpired", 2);//服务过期
                        }else {
                            map.put("isExpired", 2);//服务过期
                        }
                    }else {
                        map.put("isExpired", 0);//免费用户
                    }
                    map.put("companyName", saasTenant.getTenantName());
                    map.put("companyId", saasTenant.getId());
                    SaasTenantInfo saasTenantInfo = saasTenantCoreService.selectSaasTenantInfoByTenandId(tenantId);
                    String in = null;
                    if (null != saasTenantInfo) {
                        String companyLogo = saasTenantInfo.getCompanyLogo();
                        Map<String, String> logos = userService.getUploadInfo(companyLogo);
                        if(null != logos) {
                            in = logos.get("in");
                        }
                    }
                    map.put("companyLogo", in);
                    String terminalManagerPasswd = saasTenant.getTerminalManagerPasswd();
                    if (StringUtils.isNotBlank(terminalManagerPasswd)) {
                        if(roles.contains("1") ||roles.contains("5")){
                        }else {
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"您没有权限");
                        }
                        map.put("passwordStatus", true);
                    } else {
                        if(roles.contains("1")){
                        }else {
                            return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"您没有权限");
                        }
                        map.put("passwordStatus", false);
                    }
                }else {
                    return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0,"您没有权限");
                }
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),map);
            }
        }
        return null;
    }



    /**
     * 获取properties文件
     * @return
     */
    public Properties getProperties(){
        Properties properties = new Properties();
        try {
            InputStream is = AppController.class.getClassLoader().getResourceAsStream("service.properties");
            properties.load(is);
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
        return properties;
    }


    public static int compare_date(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }



    public boolean getUserRoleIdsByUserId(){
        TenantUserVo userInfo = TenantContext.getUserInfo();
        if(null != userInfo) {
            String userId = userInfo.getUserId();
            if(StringUtils.isNotBlank(userId)) {
                List<String> list = userService.selectUserRole(userId);
                if(null != list && list.size() > 0){
                    for (String roleId:list) {
                        if("1".equals(roleId)||"2".equals(roleId)||"3".equals(roleId)||"5".equals(roleId)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
