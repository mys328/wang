package com.thinkwin.yunmeeting.weixin.service;

import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

/**
 *
 */
public interface WxOAuth2Service {


    /**
     * 生成扫描二维码URL
     * @param redirectURI 回调方法
     * @param scope （snsapi_base静默，不弹出授权页面;snsapi_userinfo弹出授权页面）
     * @param state 状态，可随意填写
     * @return
     */
    public String buildQrConnectUrl(String redirectURI, String scope, String state);

    /**
     * 构建oauth2AuthorizationUrl
     * @param redirectURI 回调方法
     * @param scope （snsapi_base静默，不弹出授权页面;snsapi_userinfo弹出授权页面）
     * @param state 状态，可随意填写
     * @return URL
     */
    public String oauth2buildAuthorizationUrl(String redirectURI, String scope, String state);

    /**
     * 获取WxMpOAuth2AccessToken
     * @param code
     * @return
     */
    public WxMpOAuth2AccessToken oauth2getAccessToken(String code);

    /**
     * 刷新WxMpOAuth2AccessToken
     * @param refreshToken
     * @return
     */
    public WxMpOAuth2AccessToken oauth2refreshAccessToken(String refreshToken);

    /**
     * 根据WxMpOAuth2AccessToken获取WxMpUser
     * @param oAuth2AccessToken
     * @param lang
     * @return
     */
    public WxMpUser oauth2getUserInfo(WxMpOAuth2AccessToken oAuth2AccessToken, String lang);

    /**
     * 检查oAuth2AccessToken
     * @param oAuth2AccessToken
     * @return
     */
    public boolean oauth2validateAccessToken(WxMpOAuth2AccessToken oAuth2AccessToken);




}
