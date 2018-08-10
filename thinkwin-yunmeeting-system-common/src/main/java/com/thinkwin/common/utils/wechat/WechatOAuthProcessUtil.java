package com.thinkwin.common.utils.wechat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thinkwin.common.utils.StringUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.WechatAccessTokenVo;
import com.thinkwin.common.vo.WechatSNSUserInfoVo;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 类名: WXOAuthProcess </br>
 * 描述: 微信第三方登录授权流程工具类</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/5/5 </br>
 */
public class WechatOAuthProcessUtil {
    private static Logger log = LoggerFactory.getLogger(WechatOAuthProcessUtil.class);
    //开发平台应用唯一标识
    private static String appId;
    //开放平台应用密钥
    private static String appSecret;
    //回调地址
    private static String backUrl;
    //域名
    private static String webSiteDomain;
    static {
        WechatConfig wechatConfig = new WechatConfig();
        appId = wechatConfig.getAppId();
        appSecret = wechatConfig.getAppSecret();
        backUrl = wechatConfig.getCallBackUrl();
        webSiteDomain = wechatConfig.getWebSiteDomain();
    }
    /**
     * 1.获取授权code
     * @param req
     * @param resp
     */
    public static void getOAuthCode(HttpServletRequest req, HttpServletResponse resp){
        String url="https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_login&state=STATE#wechat_redirect";
        url = url.replace("APPID",appId);
        url = url.replace("REDIRECT_URI",URLEncoder.encode(backUrl));
        try {
            resp.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 2.获取授权调用token
     * @param code        授权临时票据 根据code来换取accessToken
     */
    public static WechatAccessTokenVo getOauthAccessToken(String code){
        WechatAccessTokenVo  wechatAccessTokenVo = null;
        //拼接微信获取accessToken请求的链接
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        url = url.replace("APPID",appId);
        url = url.replace("SECRET",appSecret);
        url = url.replace("CODE",code);
        // 获取网页授权凭证 发送https请求
        JSONObject jsonObject = (WechatCommonUtil.httpsRequest(url, "GET", null));
        log.info("************************获取授权凭证成功************************ujsonObject="+jsonObject);
        if (null != jsonObject) {
            try {
                wechatAccessTokenVo = new WechatAccessTokenVo();
                wechatAccessTokenVo.setAccessToken(jsonObject.getString("access_token"));
                wechatAccessTokenVo.setExpiresIn(jsonObject.getInteger("expires_in"));
                wechatAccessTokenVo.setRefreshToken(jsonObject.getString("refresh_token"));
                wechatAccessTokenVo.setOpenId(jsonObject.getString("openid"));
                wechatAccessTokenVo.setScope(jsonObject.getString("scope"));
                wechatAccessTokenVo.setUnionid(jsonObject.getString("unionid"));
            } catch (Exception e) {
                wechatAccessTokenVo = null;
                int errorCode = jsonObject.getInteger("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("获取网页授权凭证失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return wechatAccessTokenVo;
    }

    /**
     * 3.通过网页授权获取用户信息
     *
     * @param accessToken 网页授权接口调用凭证
     * @param openId 用户标识
     * @return SNSUserInfo
     */
    @SuppressWarnings( { "deprecation", "unchecked" })
    public static WechatSNSUserInfoVo getSNSUserInfo(String accessToken, String openId) {
        WechatSNSUserInfoVo snsUserInfo = null;
        // 拼接请求地址 发送https请求
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
        url = url.replace("ACCESS_TOKEN", accessToken);
        url = url.replace("OPENID", openId);

        // 通过网页授权获取用户信息
        JSONObject jsonObject = WechatCommonUtil.httpsRequest(url, "GET", null);

        if (null != jsonObject) {
            try {
                snsUserInfo = new WechatSNSUserInfoVo();
                // 用户的标识
                snsUserInfo.setOpenId(jsonObject.getString("openid"));
                // 昵称
                String nickname = jsonObject.getString("nickname");
                snsUserInfo.setNickname(nickname);
                // 性别（1是男性，2是女性，0是未知）
                snsUserInfo.setSex(jsonObject.getInteger("sex"));
                // 用户所在国家
                snsUserInfo.setCountry(jsonObject.getString("country"));
                // 用户所在省份
                snsUserInfo.setProvince(jsonObject.getString("province"));
                // 用户所在城市
                snsUserInfo.setCity(jsonObject.getString("city"));
                // 用户头像
                snsUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
                //用户unionId
                String unionId = jsonObject.getString("unionid");
                if(StringUtils.isBlank(unionId)){
                    unionId = jsonObject.getString("openid");
                }
                snsUserInfo.setUnionid(unionId);
                // 用户特权信息
                snsUserInfo.setPrivilegeList(JSONArray.toJavaObject(jsonObject.getJSONArray("privilege"), List.class));

            } catch (Exception e) {
                snsUserInfo = null;
                int errorCode = jsonObject.getInteger("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("获取用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return snsUserInfo;
    }

    /**
     * 刷新授权调用token
     * @param refreshToken    通过access_token获取到的refresh_token参数
     */
    public static WechatAccessTokenVo refreshAccessToken(String refreshToken){
        WechatAccessTokenVo  wechatAccessTokenVo = null;
        //拼接微信刷新accessToken请求的链接
        String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
        url = url.replace("APPID",appId);
        url = url.replace("REFRESH_TOKEN",refreshToken);
        // 获取网页授权凭证 发送https请求
        JSONObject jsonObject = WechatCommonUtil.httpsRequest(url, "GET", null);
        if (null != jsonObject) {
            try {
                wechatAccessTokenVo = new WechatAccessTokenVo();
                wechatAccessTokenVo.setAccessToken(jsonObject.getString("access_token"));
                wechatAccessTokenVo.setExpiresIn(jsonObject.getInteger("expires_in"));
                wechatAccessTokenVo.setRefreshToken(jsonObject.getString("refresh_token"));
                wechatAccessTokenVo.setOpenId(jsonObject.getString("openid"));
                wechatAccessTokenVo.setScope(jsonObject.getString("scope"));
            } catch (Exception e) {
                wechatAccessTokenVo = null;
                int errorCode = jsonObject.getInteger("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("获取网页授权凭证失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return wechatAccessTokenVo;
    }
}
