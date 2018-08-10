package com.thinkwin.yunmeeting.weixin.service.impl;

import com.thinkwin.yunmeeting.weixin.service.WxUserService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * 类说明：
 * @author lining 2017/7/5
 * @version 1.0
 *
 */
@Service("wxUserService")
public class WxUserServiceImpl implements WxUserService {

    @Autowired
    private WxMpService wxService;

    /**
     * 获取微信用户基本信息
     *
     * @param openId
     * @return
     */
    @Override
    public WxMpUser getWxUserInfo(String openId){
        try{

            WxMpUser user = this.wxService.getUserService().userInfo(openId, null);
            return user;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
