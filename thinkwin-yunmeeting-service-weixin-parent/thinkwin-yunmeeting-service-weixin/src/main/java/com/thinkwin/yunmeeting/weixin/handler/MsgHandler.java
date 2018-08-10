package com.thinkwin.yunmeeting.weixin.handler;

import com.thinkwin.yunmeeting.weixin.service.WeixinService;
import me.chanjar.weixin.common.api.WxConsts;
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
public class MsgHandler extends AbstractHandler {

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
      Map<String, Object> context, WxMpService wxMpService,
            WxSessionManager sessionManager)    {

        WeixinService weixinService = (WeixinService) wxMpService;

        if (!wxMessage.getMsgType().equals(WxConsts.XmlMsgType.EVENT)) {
            //TODO 可以选择将消息保存到本地
        }

        //当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
       /* if (StringUtils.startsWithAny(wxMessage.getContent(), "你好", "客服")
            && weixinService.hasKefuOnline()) {
            return WxMpXmlOutMessage
                .TRANSFER_CUSTOMER_SERVICE().fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser()).build();
        }*/
        
      //TODO 组装回复消息
        String content=" ";
        //String content="您好，如您对企云会有任何疑问，可以通过<a href=\"http://www.yunmeetings.com\">企云会官网</a>，联系客服人员。";
       /* switch(wxMessage.getMsgType()){
        case WxConsts.KefuMsgType.TEXT:
          content = "回复信息内容:这是一条文本消息";
          break;
        case WxConsts.XmlMsgType.IMAGE:
          content = "回复信息内容:这是一张图片消息";
          break;
        case WxConsts.XmlMsgType.VOICE:
          content = "回复信息内容:这是一条语音消息";
          break;
        case WxConsts.XmlMsgType.VIDEO:
          content = "回复信息内容:这是一条视频消息";
          break;
        case WxConsts.XmlMsgType.SHORTVIDEO:
          content = "回复信息内容:这是一条小视频消息";
          break;
        case WxConsts.XmlMsgType.LOCATION:
          content = "回复信息内容:这是一条地理位置消息";
          break;
        case WxConsts.XmlMsgType.LINK:
          content = "回复信息内容:这是一条链接消息";
          break;
        case WxConsts.XmlMsgType.EVENT:
          content = "回复信息内容:这是一个事件";
          break;
        default:
          content = "回复信息内容:不是常用的消息";
        }*/

        //return new TextBuilder().build(content, wxMessage, weixinService);
        return null;

    }

}
