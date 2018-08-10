package com.thinkwin.common.utils.wechat;

import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 类名: WechatConfig </br>
 * 描述:</br>
 * 开发人员： weining </br>
 * 创建时间：  2017/11/6 </br>
 */
@Configuration
public class WechatConfig {

    private static String appId;
    private static String appSecret;
    private static String callBackUrl;
    private static String webSiteDomain;
    static  {
        Properties prop =  new  Properties();
        InputStream in = WechatConfig.class.getResourceAsStream("/wechat.properties");
        try  {
            prop.load(in);
            appId = prop.getProperty( "wechat.appid" ).trim();
            appSecret = prop.getProperty( "wechat.appsecret" ).trim();
            callBackUrl = prop.getProperty( "wechat.callbackurl" ).trim();
            webSiteDomain = prop.getProperty( "wechat.websitedomain" ).trim();
        }  catch  (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public String getWebSiteDomain() {
        return webSiteDomain;
    }

    public void setWebSiteDomain(String webSiteDomain) {
        this.webSiteDomain = webSiteDomain;
    }

    public static void main(String [] args){
        System.out.println(appId);
    }
}
