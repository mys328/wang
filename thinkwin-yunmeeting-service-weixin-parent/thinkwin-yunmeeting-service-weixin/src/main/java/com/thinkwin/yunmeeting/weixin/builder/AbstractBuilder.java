package com.thinkwin.yunmeeting.weixin.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkwin.yunmeeting.weixin.service.WeixinService;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/*
 * 类说明：
 * @author lining 2017/7/3
 * @version 1.0
 *
 */
public abstract class AbstractBuilder {
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  public abstract WxMpXmlOutMessage build(String content,
      WxMpXmlMessage wxMessage, WeixinService service) ;
}
