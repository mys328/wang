package com.thinkwin.yunmeeting.weixin.localservice;

import com.thinkwin.common.model.core.SaasUserOauth;
import com.thinkwin.common.model.db.SysUser;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

import java.util.List;

public interface LocalSaasUserOathService {

    /**
     * 用户扫码绑定微信号
     * @param userOauth OAuth
     * @param wxMessage
     * @return
     */
    public boolean BindWxUser(SaasUserOauth userOauth,WxMpXmlMessage wxMessage,Integer type);

    /**
     * 根据wxMessage 获取用户的UserOauth信息
     * @param wxMessage
     * @return
     */
    public SaasUserOauth getUserOauth(WxMpXmlMessage wxMessage);

    /**
     * 根据wxMessage 获取用户SaasUserOauth
     * @param wxMessage
     * @return
     */
    public List<SaasUserOauth> getUserOauths(WxMpXmlMessage wxMessage);

    /**
     * 根据wxMessage 得到用户的UserOauth信息,从而获取用户的SysUser信息
     * @param wxMessage
     * @return
     */
    public SysUser getSysUser(WxMpXmlMessage wxMessage);

    /**
     * 保存关注公众信息
     * @param wxMpUser
     * @return
     */
    public Boolean savaSaasOauthInfo(WxMpUser wxMpUser);



}
