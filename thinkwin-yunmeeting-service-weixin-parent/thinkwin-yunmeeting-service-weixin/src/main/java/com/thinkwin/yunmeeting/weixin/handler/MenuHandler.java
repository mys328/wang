package com.thinkwin.yunmeeting.weixin.handler;

import com.thinkwin.yunmeeting.weixin.builder.AbstractBuilder;
import com.thinkwin.yunmeeting.weixin.service.WeixinService;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

/*
 * 类说明：
 * @author lining 2017/7/3
 * @version 1.0
 *
 */
@Component
public class MenuHandler extends AbstractHandler {

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {
        WeixinService weixinService = (WeixinService) wxMpService;

        String key = wxMessage.getEventKey();


        AbstractBuilder builder = null;
        String content=null;
        switch (key) {
            case "M21_BINDUSER":        //绑定人员
                content="M21_BINDUSER";
                break;
            default:
                break;
        }


        //其它情况处理
        if (builder != null) {
            try {
                return builder.build(content, wxMessage, weixinService);
            } catch (Exception e) {
                this.logger.error(e.getMessage(), e);
            }
        }

        return null;

    }

}
