package com.thinkwin.yunmeeting.weixin.service.impl;

import com.thinkwin.yunmeeting.weixin.config.ThinkWinConfig;
import com.thinkwin.yunmeeting.weixin.service.WxOAuth2Service;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;

/*
 * 类说明：OAuth2相关接口
 * @author lining 2017/7/28
 * @version 1.0
 *
 */

@Service("wxOAuth2Service")
public class WxOAuth2ServiceImpl implements WxOAuth2Service {

    @Autowired
    private ThinkWinConfig twConfig;
    @Autowired
    protected WxMpService wxService;

    /**
     * 生成扫描二维码URL
     *
     * @param redirectURI 回调方法
     * @param scope       （snsapi_base静默，不弹出授权页面;snsapi_userinfo弹出授权页面）
     * @param state       状态，可随意填写
     * @return
     */
    @Override
    public String buildQrConnectUrl(String redirectURI, String scope, String state) {

        try {
            String httpPath = URLEncoder.encode(twConfig.getHttpServer() + redirectURI, "UTF-8");
            return wxService.buildQrConnectUrl(httpPath, scope, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 构建oauth2AuthorizationUrl
     *
     * @param redirectURI 回调方法
     * @param scope       （snsapi_base静默，不弹出授权页面;snsapi_userinfo弹出授权页面）
     * @param state       状态，可随意填写
     * @return URL
     */
    @Override
    public String oauth2buildAuthorizationUrl(String redirectURI, String scope, String state) {
        try {
            //String httpPath = URLEncoder.encode(twConfig.getHttpServer() + redirectURI, "UTF-8");
            String httpPath = twConfig.getHttpServer() + redirectURI;
            return wxService.oauth2buildAuthorizationUrl(httpPath, scope, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取WxMpOAuth2AccessToken
     *
     * @param code
     * @return
     */
    @Override
    public WxMpOAuth2AccessToken oauth2getAccessToken(String code) {
        try {
            return wxService.oauth2getAccessToken(code);
        } catch (WxErrorException e) {

        }
        return null;
    }

    /**
     * 刷新WxMpOAuth2AccessToken
     *
     * @param refreshToken
     * @return
     */
    @Override
    public WxMpOAuth2AccessToken oauth2refreshAccessToken(String refreshToken) {
        try {
            return wxService.oauth2refreshAccessToken(refreshToken);
        } catch (WxErrorException e) {

        }
        return null;
    }

    /**
     * 根据WxMpOAuth2AccessToken获取WxMpUser
     *
     * @param oAuth2AccessToken
     * @param lang
     * @return
     */
    @Override
    public WxMpUser oauth2getUserInfo(WxMpOAuth2AccessToken oAuth2AccessToken, String lang) {
        try {
            return wxService.oauth2getUserInfo(oAuth2AccessToken, lang);
        } catch (WxErrorException e) {

        }
        return null;
    }

    /**
     * 检查oAuth2AccessToken
     *
     * @param oAuth2AccessToken
     * @return
     */
    @Override
    public boolean oauth2validateAccessToken(WxMpOAuth2AccessToken oAuth2AccessToken) {

        return wxService.oauth2validateAccessToken(oAuth2AccessToken);
    }
}
