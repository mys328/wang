package com.thinkwin.yunmeeting.weixin.service;

import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/*
 * 类说明：
 * @author lining 2017/7/3
 * @version 1.0
 *
 */
public interface WeixinService{

    public WxMpXmlOutMessage route(WxMpXmlMessage message);

    public boolean hasKefuOnline();

    public WxMpConfigStorage getWxMpConfigStorage();

    public boolean checkSignature(String timestamp, String nonce, String signature);

}
