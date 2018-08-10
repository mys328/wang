package com.thinkwin.yunmeeting.weixin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/*
 * 类说明：
 * @author lining 2017/7/17
 * @version 1.0
 *
 */
@Configuration
public class ThinkWinConfig {

    //微信服务器URL
    @Value("#{twProperties.httpServer}")
    private String httpServer;

    public String getHttpServer() {
        return this.httpServer;
    }
}
