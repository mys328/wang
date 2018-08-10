package com.thinkwin.yunmeeting.weixin.localservice.impl;

import com.thinkwin.auth.service.LoginRegisterService;
import com.thinkwin.auth.service.SaasTenantInfoService;
import com.thinkwin.auth.service.SaasTenantService;
import com.thinkwin.auth.service.UserService;
import com.thinkwin.common.model.core.SaasTenantInfo;
import com.thinkwin.common.model.core.SaasUserOauth;
import com.thinkwin.common.model.core.SaasUserOauthInfo;
import com.thinkwin.common.model.db.SysUser;
import com.thinkwin.common.oauth.OauthEnum;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.core.service.SaasUserOauthInfoService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yunmeeting.weixin.config.ThinkWinConfig;
import com.thinkwin.yunmeeting.weixin.constant.WxMpConstant;
import com.thinkwin.yunmeeting.weixin.dto.ScanType;
import com.thinkwin.yunmeeting.weixin.localservice.LocalSaasUserOathService;
import com.thinkwin.yunmeeting.weixin.localservice.LocalWxScanService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/*
 * 类说明：
 * @author lining 2017/7/18
 * @version 1.0
 *
 */
@Service("wxLocalScanService")
public class LocalWxScanServiceImpl implements LocalWxScanService {


    private Logger log = LoggerFactory.getLogger(LocalWxScanServiceImpl.class);

    @Autowired
    private ThinkWinConfig twConfig;
    @Autowired
    private WxMpService wxService;
    @Resource
    private SaasTenantService saasTenantService;
    @Resource
    private LoginRegisterService loginRegisterService;
    @Resource
    private LocalSaasUserOathService localSaasUserOathService;
    @Resource
    private SaasTenantInfoService saasTenantInfoService;

    @Resource
    private SaasUserOauthInfoService saasUserOauthInfoService;
    @Resource(name="yunUserService")
    private UserService userService;


    /**
     * 处理关注业务逻辑
     *
     * @param wxMessage
     * @return
     */
    @Override
    public Boolean setScanSubscribe(WxMpXmlMessage wxMessage) {
        try {
            WxMpUser mpUser = wxService.getUserService().userInfo(wxMessage.getFromUser());
            TenantContext.setTenantId(WxMpConstant.DB_CORE); //切换CORE库
            SaasUserOauth temp=new SaasUserOauth();
            temp.setOauthUnionId(mpUser.getUnionId());
            temp.setOauthType(OauthEnum.WxService.getCode());
            List<SaasUserOauth> userOauth=this.loginRegisterService.selectOAuthLoginInfo(temp);
            if(null!=userOauth && userOauth.size()>0){
                TenantContext.setTenantId(userOauth.get(0).getTenantId());
                SysUser sysUser=this.userService.selectUserByUserId(userOauth.get(0).getUserId());
                sysUser.setIsSubscribe(WxMpConstant.SUBSCRIBE);
                sysUser.setOpenId(mpUser.getOpenId());
               return this.userService.updateUserByUserId(sysUser);
            }
        }catch (WxErrorException e){

        }
        return false;
    }

    /**
     * 处理取消关注业务逻辑
     *
     * @param wxMessage
     * @return
     */
    @Override
    public Boolean setScanUnsubscribe(WxMpXmlMessage wxMessage) {
        try {
            WxMpUser mpUser = wxService.getUserService().userInfo(wxMessage.getFromUser());
            TenantContext.setTenantId(WxMpConstant.DB_CORE); //切换CORE库

            //STEP 1
            //取消关注删除微信消息
            SaasUserOauthInfo oauthInfo=new SaasUserOauthInfo();
            oauthInfo.setOauthType(OauthEnum.WxService.getCode());
            oauthInfo.setOauthUnionId(mpUser.getUnionId());
            boolean f=this.saasUserOauthInfoService.delete(oauthInfo);

            //STEP 2
            SaasUserOauth temp=new SaasUserOauth();
            temp.setOauthUnionId(mpUser.getUnionId());
            temp.setOauthType(OauthEnum.WxService.getCode());
            List<SaasUserOauth> userOauth=this.loginRegisterService.selectOAuthLoginInfo(temp);
            if(null!=userOauth){
                TenantContext.setTenantId(userOauth.get(0).getTenantId());
                SysUser sysUser=this.userService.selectUserByUserId(userOauth.get(0).getUserId());
                sysUser.setIsSubscribe(WxMpConstant.UNSUBSCRIBE);
                this.userService.updateUserByUserId(sysUser);

            }
        }catch (WxErrorException e){
            return false;
        }
        return true;
    }


    /**
     * 扫一扫业务入口
     * @param wxMessage 扫码信息
     * @return
     */
    @Override
    public WxMpXmlOutNewsMessage scanQRcode(WxMpXmlMessage wxMessage) {
        WxMpXmlOutNewsMessage m = new WxMpXmlOutNewsMessage();
        WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
        String company = WxMpConstant.YUNMEETING;
        ScanType scanType = ScanType.YUNMEETING_NULL; //默认
        SaasTenantInfo tenantInfo = null;
        SaasUserOauth userOauth = null;
        String localUserId = null;
        String localTenantId = null;
        boolean wxService = false; //公众号未绑定
        boolean wxOpen = false; //开放平台未绑定


        //扫码参数
        String event = wxMessage.getEvent(); //事件类型
        String eventKey = wxMessage.getEventKey(); //事件值
        if (eventKey.indexOf(WxMpConstant.PrefixQrType.QRSCENE) != -1) {
            eventKey = eventKey.replaceFirst(WxMpConstant.PrefixQrType.QRSCENE, "");
        }
        String ticket = wxMessage.getTicket();

        //分别查一下eventKey和ticket 判断当前扫码是来自于加入租户还是个人绑定业务
        TenantContext.setTenantId(WxMpConstant.DB_CORE); //切换CORE库
        tenantInfo = this.saasTenantInfoService.selectSaasTenantInfo(eventKey);

        //用户二维码
        if (StringUtils.isNotBlank(ticket)) {
            SaasUserOauth tempUserOauth = new SaasUserOauth();
            tempUserOauth.setTicket(ticket);
            List<SaasUserOauth> temp = this.loginRegisterService.selectOAuthLoginInfo(tempUserOauth);
            if (null != temp && temp.size() > 0) {
                userOauth = temp.get(0);
            }
        }

        //********************************************************
        //判断企业是否已经解散
        if(null!=tenantInfo){
            String tenant= RedisUtil.get(WxMpConstant.YUNMEETING_DISSOLUTION_COMPANY_PREFIX+tenantInfo.getTenantId());
            if(StringUtils.isNotBlank(tenant)){
                return dissolution(m,item,wxMessage,null);
            }
        }

        if(null!=userOauth){
            String tenant= RedisUtil.get(WxMpConstant.YUNMEETING_DISSOLUTION_COMPANY_PREFIX+userOauth.getTenantId());
            if(StringUtils.isNotBlank(tenant)){
                return dissolution(m,item,wxMessage,null);
            }
        }
        //********************************************************


        //1. 查询是否关联
        //根据UnionId查询SaasUserOauth
        List<SaasUserOauth> userOauths = this.localSaasUserOathService.getUserOauths(wxMessage);

        if (WxConsts.EventType.SUBSCRIBE.equals(event)) {//关注事件

            //判断是否已经关联IF关联ELSE未关联
            if (null != userOauths && userOauths.size() > 0) {

                //********************************************************
                //判断企业是否已经解散
                    String tenant= RedisUtil.get(WxMpConstant.YUNMEETING_DISSOLUTION_COMPANY_PREFIX+userOauths.get(0).getTenantId());
                    if(StringUtils.isNotBlank(tenant)){
                        return dissolution(m,item,wxMessage,ScanType.TENANT_UNSETTLED);
                    }
                //********************************************************

                for (SaasUserOauth uo : userOauths) {

                    if (uo.getOauthType().equals(OauthEnum.WxService.getCode())) {
                        wxService = true;
                        localUserId = uo.getUserId();
                        localTenantId = uo.getTenantId();
                        company = this.saasTenantInfoService.selectSaasTenantInfo(uo.getTenantId()).getTenantName();
                    } else if (uo.getOauthType().equals(OauthEnum.WxOpen.getCode())) {
                        wxOpen = true;
                        localUserId = uo.getUserId();
                        localTenantId = uo.getTenantId();
                        company = this.saasTenantInfoService.selectSaasTenantInfo(uo.getTenantId()).getTenantName();
                    }
                }

                if (wxOpen) { //未绑定服务号
                    scanType = ScanType.ACCOUNT_UNSETTLED;
                } else if (wxService) { //已绑定服务号
                    scanType = ScanType.SETTLED;
                }

                if (wxService && wxOpen) {
                    scanType = ScanType.SETTLED;
                }

            }else if(null!=userOauth) { //租户二维码
                company = this.saasTenantInfoService.selectSaasTenantInfo(userOauth.getTenantId()).getTenantName();
                scanType =ScanType.ACCOUNT_UNSETTLED;

            }else { //未关联处理
                scanType = ScanType.YUNMEETING;
            }

        } else if (WxConsts.EventType.SCAN.equals(event)) { //扫码事件

            //判断是否租户二维码
            if (null != tenantInfo) { //租户二维码
                //二维码租户名称
                company = (null != tenantInfo) ? tenantInfo.getTenantName() : company;

                //判断是否已经关联IF关联ELSE未关联
                if (null != userOauths && userOauths.size() > 0) {
                    for (SaasUserOauth uo : userOauths) {

                        if (uo.getOauthType().equals(OauthEnum.WxService.getCode())) {

                            localUserId = uo.getUserId();
                            localTenantId = uo.getTenantId();
                            company = this.saasTenantInfoService.selectSaasTenantInfo(uo.getTenantId()).getTenantName();

                            if (uo.getTenantId().equals(tenantInfo.getTenantId())) {
                                scanType = ScanType.SETTLED;
                            } else {
                                scanType = ScanType.TENANT_SETTLED_OTHER;
                            }
                        } else if (uo.getOauthType().equals(OauthEnum.WxOpen.getCode())) {
                            localUserId = uo.getUserId();
                            localTenantId = uo.getTenantId();
                            company = this.saasTenantInfoService.selectSaasTenantInfo(uo.getTenantId()).getTenantName();

                            if (uo.getTenantId().equals(tenantInfo.getTenantId())) {
                                scanType = ScanType.ACCOUNT_UNSETTLED;
                            } else {
                                scanType = ScanType.TENANT_SETTLED_OTHER;
                            }
                        }
                    }
                } else {
                    scanType = ScanType.TENANT_UNSETTLED;
                }

            } else if (null != userOauth) { //用户二维码

                //判断是否已经关联IF关联ELSE未关联
                if (null != userOauths && userOauths.size() > 0) {
                    for (SaasUserOauth uo : userOauths) {

                        if (uo.getOauthType().equals(OauthEnum.WxService.getCode())) {

                            localUserId = uo.getUserId();
                            localTenantId = uo.getTenantId();
                            company = this.saasTenantInfoService.selectSaasTenantInfo(uo.getTenantId()).getTenantName();
                            if (uo.getUserId().equals(userOauth.getUserId()) && userOauth.getIsBind().equals(WxMpConstant.WX_USER_BOUND)) {
                                scanType = ScanType.SETTLED;
                            } else {
                                scanType = ScanType.TENANT_SETTLED_OTHER;
                            }

                        } else if (uo.getOauthType().equals(OauthEnum.WxOpen.getCode())) {
                            localUserId = uo.getUserId();
                            localTenantId = uo.getTenantId();
                            company = this.saasTenantInfoService.selectSaasTenantInfo(uo.getTenantId()).getTenantName();
                            if (uo.getUserId().equals(userOauth.getUserId()) && uo.getIsBind().equals(WxMpConstant.WX_USER_BOUND)) {
                                scanType = ScanType.ACCOUNT_UNSETTLED;
                            } else {
                                scanType = ScanType.TENANT_SETTLED_OTHER;
                            }
                        }
                    }
                } else {
                    company = this.saasTenantInfoService.selectSaasTenantInfo(userOauth.getTenantId()).getTenantName();
                    if(userOauth.getIsBind().equals(WxMpConstant.WX_USER_BOUND)){
                        scanType = ScanType.ACCOUNT_REMOVE;
                    }else{
                        scanType = ScanType.ACCOUNT_UNSETTLED;
                    }
                }
            }

        }

        //2.只绑定开放平台
        if (null!=userOauths && userOauths.size() > 0) {
            if (null == userOauth) {  //若已经绑定开放平台，则添加一条公众平台绑定信息
                userOauth = new SaasUserOauth();
                userOauth.setId(CreateUUIdUtil.Uuid());
                userOauth.setUserId(localUserId);
                userOauth.setTenantId(localTenantId);
                userOauth.setOauthType(OauthEnum.WxService.getCode());
                userOauth.setIsBind(WxMpConstant.WX_USER_BOUND);
                userOauth.setStatus(WxMpConstant.UserOauthStatus.ENABLE);
                item = this.getQRcodeInfo(item, scanType, company, wxMessage, userOauth, tenantInfo, WxMpConstant.SAVE);
            }
            item = this.getQRcodeInfo(item, scanType, company, wxMessage, userOauth, tenantInfo, WxMpConstant.UPDATE);
        }else {
            item = this.getQRcodeInfo(item, scanType, company, wxMessage, userOauth, tenantInfo, WxMpConstant.UPDATE);
        }


        //判断是否有返回信息，若无则返回NULL
        if(null!=item.getDescription() && item.getDescription().length()>0){
            m = WxMpXmlOutNewsMessage.NEWS()
                    .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
                    .addArticle(item)
                    .build();
            return m;
        }
        return null;
    }


    //返回扫码后的消息
    public WxMpXmlOutNewsMessage.Item getQRcodeInfo(WxMpXmlOutNewsMessage.Item item,ScanType scanType,String company,WxMpXmlMessage wxMessage,SaasUserOauth userOauth,SaasTenantInfo tenantInfo,Integer type) {

        //返回扫码后结果
        switch (scanType) {
            case SETTLED://已绑定当前用户
                item.setDescription("您已经是【" + company + "】成员，您可以进行会议的预订和查询。");
                break;
            case TENANT_SETTLED_OTHER://租户邀请码已绑定其它用户
                item.setDescription("您已经是【" + company + "】成员，不允许再加入其它公司");
                break;
            case TENANT_UNSETTLED://租户邀请码未绑定
                item.setDescription("欢迎您使用【" + company + "】企云会会议管理平台，请先点击【阅读全文】完成认证才可使用，如有任何问题可点击下方小键盘与我们联系。");
                item.setUrl(twConfig.getHttpServer() + "/wechat/wxRegister/inviteCodePage?openId=" + wxMessage.getFromUser() + "&tenantId=" + tenantInfo.getTenantId());
                break;
            case ACCOUNT_SETTLED_OTHER: //账户二维码已绑定其它账户
                item.setDescription("您已经绑定【" + company + "】企云会会议管理平台");
                break;
            case ACCOUNT_UNSETTLED:
                //绑定微信号业务 start
                boolean flag = localSaasUserOathService.BindWxUser(userOauth, wxMessage,type);
                //绑定微信号业务 end
                if (flag) {
                    item.setDescription("您的微信成功绑定【" + company + "】企云会会议管理平台。");
                } else {
                    item.setDescription("您的微信绑定【" + company + "】企云会会议管理平台失败，请重新绑定。");
                }
                break;
            case ACCOUNT_REMOVE:
                item.setDescription("该二维码已经被其他用户绑定，请先解除绑定");
                break;
            case YUNMEETING:
                StringBuffer content = new StringBuffer();
                content.append("欢迎使用【企云会】会议管理系统，您可以");
                content.append("联系企业管理员扫码加入企业，");
                content.append("或在浏览器登录www.yunmeetings.com开通企业账户，如有任何问题可点击下方小键盘与我们联系。");
                item.setDescription(content.toString());
                item.setUrl("http://www.yunmeetings.com");
                break;
            case YUNMEETING_NULL:
                item.setDescription("该二维码已失效");
                break;
            default:
                break;
        }
        return item;
    }

    /*解散企业
     *
     */
    public WxMpXmlOutNewsMessage dissolution(WxMpXmlOutNewsMessage m,WxMpXmlOutNewsMessage.Item item,WxMpXmlMessage wxMessage,ScanType scanType){
        ScanType type=(null==scanType)?ScanType.YUNMEETING_NULL:ScanType.YUNMEETING;
        item = this.getQRcodeInfo(item, type, null,null,null,null,null);
        //判断是否有返回信息，若无则返回NULL
        if(null!=item.getDescription() && item.getDescription().length()>0){
            m = WxMpXmlOutNewsMessage.NEWS()
                    .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
                    .addArticle(item)
                    .build();
            return m;
        }
        return null;
    }




}
