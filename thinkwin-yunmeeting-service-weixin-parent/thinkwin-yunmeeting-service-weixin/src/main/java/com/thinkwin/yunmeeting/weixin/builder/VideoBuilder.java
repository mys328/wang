package com.thinkwin.yunmeeting.weixin.builder;

import com.thinkwin.yunmeeting.weixin.service.WeixinService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutVideoMessage;

/*
 * 类说明：
 * @author lining 2017/7/3
 * @version 1.0
 *
 */
public class VideoBuilder extends AbstractBuilder {
    @Override
    public WxMpXmlOutVideoMessage build(String content, WxMpXmlMessage wxMessage, WeixinService service) {
        return null;
    }
}
