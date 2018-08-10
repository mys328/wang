package com.thinkwin.yunmeeting.weixin.h5.controller;

import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.SaasTenantService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.core.SaasUserOauth;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.model.db.SysUserRole;
import com.thinkwin.common.oauth.OauthEnum;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.PropertyUtil;
import com.thinkwin.fileupload.service.FileUploadService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yunmeeting.weixin.service.WxOAuth2Service;
import com.thinkwin.yunmeeting.weixin.service.WxUserService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 类说明：
 * @author lining 2017/8/24
 * @version 1.0
 *
 */
@Controller
@RequestMapping("/wechat/wxMeeting")
public class WxMeetingController {

    private final Logger log = LoggerFactory.getLogger(WxMeetingController.class);


    @Resource
    private WxOAuth2Service wxOAuth2Service;
    @Resource
    private WxUserService wxUserService;
    @Resource
    private LoginRegisterService loginRegisterService;
    @Resource
    private UserService userService;
    @Resource
    private FileUploadService fileUploadService;
    @Resource
    private SaasTenantService saasTenantService;

    /**
     * 公众号上预订会议、查询会议请求
     * @param request
     * @param response
     */
    @RequestMapping("/yunmeeting")
    @ResponseBody
    public void meetingPage(HttpServletRequest request, HttpServletResponse response) {
        String h5Server= PropertyUtil.getH5Server();
        try {
            //向微信发请求step1
            String state="thinkwin";
            String url=wxOAuth2Service.oauth2buildAuthorizationUrl(h5Server,"snsapi_base",state);
            log.info("##############Url="+url);
            response.sendRedirect(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 公众号上预订会议、查询会议请求
     * @param request
     * @param response
     */
    @RequestMapping("/qyh")
    @ResponseBody
    public void qyh(HttpServletRequest request, HttpServletResponse response,String type) {
        String menu="";
        try {
            if("1".equals(type)){
                menu=PropertyUtil.getH5Reservation();
            }else{
                menu=PropertyUtil.getH5Query();
            }
            //向微信发请求step1
            String state=type;
            String url=wxOAuth2Service.oauth2buildAuthorizationUrl(menu,"snsapi_base",state);
            log.info("##############Url="+url);
            response.sendRedirect(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取认证信息
     * @param request
     * @param response
     */
    @RequestMapping("/wxMeetingOauth")
    @ResponseBody
    public ResponseResult wxMeetingOauth(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        ResponseResult responseResult=new ResponseResult();
        Map<String, Object> map=new HashMap<>();

        // 用户同意授权后，能获取到code
        String code = request.getParameter("code");
        String state = request.getParameter("state");

        WxMpUser wxMpUser=null;
        try {

            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxOAuth2Service.oauth2getAccessToken(code);

            List<SaasUserOauth> userOauth=null;
            SaasTenant tenant=null;
            if(null!=wxMpOAuth2AccessToken) {
                wxMpUser = this.wxUserService.getWxUserInfo(wxMpOAuth2AccessToken.getOpenId());
                log.info("###1###"+wxMpUser.getOpenId()+"---"+wxMpUser.getNickname());
                SaasUserOauth tempOauth=new SaasUserOauth();
                tempOauth.setOauthOpenId(wxMpUser.getOpenId());
                tempOauth.setOauthType(OauthEnum.WxService.getCode());
                tempOauth.setIsBind(1);
                //切换数据源
                TenantContext.setTenantId("0");
                userOauth=this.loginRegisterService.selectOAuthLoginInfo(tempOauth);
                if(userOauth!=null && userOauth.size()>0) {
                    tenant = saasTenantService.selectSaasTenantServcie(userOauth.get(0).getTenantId());
                }

            }
            //视图名
            if(null!=userOauth && (null!=tenant && tenant.getStatus()==1)) {
                log.info("###2###"+userOauth.get(0).getUserId()+"---"+userOauth.get(0).getOauthUserName());
                TenantContext.setTenantId(userOauth.get(0).getTenantId());
                SysUser sysUser=this.userService.findByOpenId(userOauth.get(0).getOauthOpenId(),null);
                String re=(sysUser!=null)?"User is no NUll":"User is NUll";
                log.info("###3###"+re);
                if(null!=sysUser){
                    String isAdminRoom="0";
                    List<SysUserRole> userRoles=this.userService.getCurrentUserRoleIds(sysUser.getId());
                    for(SysUserRole r: userRoles){
                       if(r.getRoleId().equals("3")){
                           isAdminRoom="1";
                           break;
                       }
                    }
                    String photo = sysUser.getPhoto();
                    if (StringUtils.isNotBlank(photo)) {
                        Map<String, String> picMap = userService.getUploadInfo(photo);
                        if(null != picMap){
                            sysUser.setPhoto(picMap.get("big"));
                            sysUser.setCentralgraph(picMap.get("in"));
                            sysUser.setThumbnails(picMap.get("small"));
                        }
                    }
                    log.info("###4###"+sysUser.getId()+"---"+sysUser.getUserName());
                    responseResult.setIfSuc(1);
                    map.put("publish",tenant.getTenantType());
                    map.put("openId",userOauth.get(0).getOauthOpenId());
                    map.put("tenantId",userOauth.get(0).getTenantId());
                    map.put("sysUser",sysUser);
                    map.put("isAdminRoom",isAdminRoom);
                    responseResult.setData(map);
                    responseResult.setMsg("认证成功");
                }else{
                    responseResult.setIfSuc(-1);
                    responseResult.setMsg("用户认证失败");
                }
            }else{
                responseResult.setIfSuc(-1);
                responseResult.setMsg("用户认证失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("###5### Result is seccess.");
        int ifSuc = responseResult.getIfSuc();
        log.info("###5### Result is seccess." + ifSuc);
        return responseResult;
    }

}
