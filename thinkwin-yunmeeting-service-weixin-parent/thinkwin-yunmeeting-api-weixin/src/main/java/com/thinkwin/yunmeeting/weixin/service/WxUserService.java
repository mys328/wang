package com.thinkwin.yunmeeting.weixin.service;

import me.chanjar.weixin.mp.bean.result.WxMpUser;

public interface WxUserService {

    /**
     * 获取微信用户基本信息
     * @param openId
     * @return
     */
    public WxMpUser getWxUserInfo(String openId);


}
