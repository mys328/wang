package com.thinkwin.yunmeeting.weixin.localservice.impl;

import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.model.core.SaasUserOauth;
import com.thinkwin.common.model.core.SaasUserOauthInfo;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.oauth.OauthEnum;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.SHA1Util;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yunmeeting.weixin.constant.WxMpConstant;
import com.thinkwin.yunmeeting.weixin.localservice.LocalSaasUserOathService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*
 * 类说明：
 * @author lining 2017/8/4
 * @version 1.0
 *
 */
@Service("localSaasUserOathService")
public class LocalSaasUserOathServiceImpl implements LocalSaasUserOathService {

    @Autowired
    private WxMpService wxService;
    @Resource(name="yunUserService")
    private UserService userService;
    @Resource
    private LoginRegisterService loginRegisterService;
    @Resource
    SaasTenantService saasTenantCoreService;

    /**
     * 用户扫码绑定微信号
     *
     * @param userOauth OAuth
     * @param wxMessage
     * @return
     */
    @Override
    public boolean BindWxUser(SaasUserOauth userOauth, WxMpXmlMessage wxMessage,Integer type) {
        try {

            //更新Oauth 登陆表
            WxMpUser wxMpUser = this.wxService.getUserService().userInfo(wxMessage.getFromUser());
            userOauth.setOauthOpenId(wxMessage.getFromUser());
            userOauth.setOauthUnionId(wxMpUser.getUnionId());
            userOauth.setOauthPhoto(wxMpUser.getHeadImgUrl());
            userOauth.setPassword(SHA1Util.SHA1(wxMpUser.getUnionId()));
            userOauth.setOauthUserName(wxMpUser.getNickname());
            userOauth.setIsBind(WxMpConstant.WX_USER_BOUND);
            userOauth.setOauthType(OauthEnum.WxService.getCode());
            userOauth.setUpdateTime(new Date());



            //保存
            //切换数据源
            boolean f1=false;
            TenantContext.setTenantId(WxMpConstant.DB_CORE);
            if(WxMpConstant.WX_USER_UNBOUND.equals(type)){  //开放平台绑定，公众号未绑定
                userOauth.setUpdateTime(new Date());
                f1=this.loginRegisterService.saveOAuthLoginInfo(userOauth);
            }else{
                f1=this.loginRegisterService.updateOAuthLoginInfo(userOauth);
            }



            //更新租户下用户表
            TenantContext.setTenantId(userOauth.getTenantId());
            String userId = userOauth.getUserId();
            SysUser sysUser=this.userService.selectUserByUserId(userId);
            sysUser.setWechat(wxMpUser.getNickname());
            sysUser.setOpenId(wxMpUser.getOpenId());
            sysUser.setIsSubscribe(WxMpConstant.SUBSCRIBE);
            boolean f2=this.userService.updateUserByUserId(sysUser);

            //删除解散企业中的用户信息
            saasTenantCoreService.deleteDissolutionUserInfoByUserId(userId);

            if(f1 && f2){
                return true;
            }
                return false;
        }catch(WxErrorException e){
                e.printStackTrace();
        }catch(Exception e){
                e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据wxMessage 获取用户的UserOauth信息
     *
     * @param wxMessage
     * @return
     */
    @Override
    public SaasUserOauth getUserOauth(WxMpXmlMessage wxMessage) {
        SaasUserOauth userOauth = new SaasUserOauth();
        List<SaasUserOauth> userOauths=null;
        try {
            userOauth.setOauthType(OauthEnum.WxService.getCode());
            WxMpUser wxMpUser = this.wxService.getUserService().userInfo(wxMessage.getFromUser());
            userOauth.setOauthUnionId(wxMpUser.getUnionId());
            userOauth.setIsBind(WxMpConstant.WX_USER_BOUND);
            TenantContext.setTenantId(WxMpConstant.DB_CORE);
            userOauths = this.loginRegisterService.selectOAuthLoginInfo(userOauth);

        }catch(WxErrorException e){
            e.printStackTrace();
            }
        return (userOauths!=null && userOauths.size()>0)?userOauths.get(0):null;
    }

    /**
     * 根据wxMessage 获取用户SaasUserOauth
     *
     * @param wxMessage
     * @return
     */
    @Override
    public List<SaasUserOauth> getUserOauths(WxMpXmlMessage wxMessage) {
        SaasUserOauth userOauth = new SaasUserOauth();
        List<SaasUserOauth> userOauths=null;
        try {
            WxMpUser wxMpUser = this.wxService.getUserService().userInfo(wxMessage.getFromUser());
            userOauth.setOauthUnionId(wxMpUser.getUnionId());
            userOauth.setIsBind(WxMpConstant.WX_USER_BOUND);
            TenantContext.setTenantId(WxMpConstant.DB_CORE);
            userOauths = this.loginRegisterService.selectOAuthLoginInfo(userOauth);

        }catch(WxErrorException e){
            e.printStackTrace();
        }
        return userOauths;
    }

    /**
     * 根据wxMessage 得到用户的UserOauth信息,从而获取用户的SysUser信息
     *
     * @param wxMessage
     * @return
     */
    @Override
    public SysUser getSysUser(WxMpXmlMessage wxMessage) {

        SysUser sysUser=new SysUser();

        SaasUserOauth userOauth =getUserOauth(wxMessage);
        if(userOauth!=null){
            TenantContext.setTenantId(userOauth.getTenantId());
            sysUser = this.userService.selectUserByUserId(userOauth.getUserId());
        }
        return sysUser;
    }

    @Override
    public Boolean savaSaasOauthInfo(WxMpUser wxMpUser){

        boolean f=false;
        try {

            //STPE 1 添加微信信息
            SaasUserOauthInfo userOauthInfo=new SaasUserOauthInfo();
            //OAuthInfo详情
            String userOauthInfoId = CreateUUIdUtil.Uuid();
            userOauthInfo.setId(userOauthInfoId);
            userOauthInfo.setOauthUnionId(wxMpUser.getUnionId());
            userOauthInfo.setOauthOpenId(wxMpUser.getOpenId());
            //userOauthInfo.setUserOauthId(userOauth.getId());
            userOauthInfo.setNickName(wxMpUser.getNickname());
            userOauthInfo.setOauthType(OauthEnum.WxService.getCode());
            int sex = (WxMpConstant.MAN_ZH.equals(wxMpUser.getSex()) ? WxMpConstant.MAN : WxMpConstant.WOMAN);
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

            f=this.loginRegisterService.saveOAuthUserInfo(userOauthInfo);

            //STEP 2 判断是否是已经绑定的用户，更新关注状态
            SaasUserOauth temp=new SaasUserOauth();
            temp.setOauthUnionId(wxMpUser.getUnionId());
            temp.setOauthType(OauthEnum.WxService.getCode());
            temp.setIsBind(1);
            List<SaasUserOauth> userOauth=this.loginRegisterService.selectOAuthLoginInfo(temp);
            if(null!=userOauth){
                TenantContext.setTenantId(userOauth.get(0).getTenantId());
                SysUser sysUser=this.userService.selectUserByUserId(userOauth.get(0).getUserId());
                sysUser.setIsSubscribe(WxMpConstant.SUBSCRIBE);
                this.userService.updateUserByUserId(sysUser);

            }
        }catch(Exception e){
            e.printStackTrace();
            }
        return f;
    }
}
