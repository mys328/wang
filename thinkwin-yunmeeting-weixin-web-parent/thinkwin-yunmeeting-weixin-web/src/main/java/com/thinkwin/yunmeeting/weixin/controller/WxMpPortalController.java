package com.thinkwin.yunmeeting.weixin.controller;

import com.thinkwin.yunmeeting.weixin.service.WeixinService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author LiNing
 * 微信入口
 */

@RestController
@RequestMapping("/wechat/portal")
public class WxMpPortalController {
    @Resource
    private WeixinService weixinService;

    @ResponseBody()
    @RequestMapping(method = RequestMethod.GET)
    public String authGet(@RequestParam(name = "signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "echostr", required = false) String echostr) {

        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }

        if (this.getWxService().checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }

        return "非法请求";
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
     public String post(@RequestBody String requestBody, @RequestParam("signature") String signature,
                        @RequestParam(name = "encrypt_type", required = false) String encType,
                        @RequestParam(name = "msg_signature", required = false) String msgSignature,
                        @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce) {

         if (!this.weixinService.checkSignature(timestamp, nonce, signature)) {
             throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
         }

         String out = null;
         if (encType == null) {
             // 明文传输的消息
             WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
             WxMpXmlOutMessage outMessage = this.getWxService().route(inMessage);
             if (outMessage == null) {
                 return "";
             }

             out = outMessage.toXml();
         } else if ("aes".equals(encType)) {
             // aes加密的消息
             WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(requestBody,
                     this.getWxService().getWxMpConfigStorage(), timestamp, nonce, msgSignature);

             WxMpXmlOutMessage outMessage = this.getWxService().route(inMessage);
             if (outMessage == null) {
                 return "";
             }

             out = outMessage.toEncryptedXml(this.getWxService().getWxMpConfigStorage());
         }


         return out;
     }

    protected WeixinService getWxService() {
        return this.weixinService;
    }

}

