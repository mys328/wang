package com.thinkwin.yunmeeting.weixin.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import me.chanjar.weixin.mp.api.WxMpMessageHandler;

/*
 * 类说明：
 * @author lining 2017/7/3
 * @version 1.0
 *
 */
public abstract class AbstractHandler implements WxMpMessageHandler {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected final Gson gson = new Gson();

}
