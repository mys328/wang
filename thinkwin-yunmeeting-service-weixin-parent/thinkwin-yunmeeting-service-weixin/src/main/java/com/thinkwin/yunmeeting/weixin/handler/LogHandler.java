package com.thinkwin.yunmeeting.weixin.handler;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
public class LogHandler extends AbstractHandler {
  private static final ObjectMapper JSON = new ObjectMapper();
  static {
    JSON.setSerializationInclusion(Include.NON_NULL);
    JSON.configure(SerializationFeature.INDENT_OUTPUT, Boolean.TRUE);
  }

  @Override
  public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
      Map<String, Object> context, WxMpService wxMpService,
      WxSessionManager sessionManager) {
    try {
      this.logger.info("\n接收到请求消息，内容：{}", JSON.writeValueAsString(wxMessage));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return null;
  }

}
