package com.thinkwin.yunmeeting.weixin.localservice;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;

/*
 * 类说明：
 * @author lining 2017/7/18
 * @version 1.0
 *
 */
public interface LocalWxScanService {

    /**
     * 处理关注业务逻辑
     * @param wxMessage
     * @return
     */
    public Boolean setScanSubscribe(WxMpXmlMessage wxMessage);

    /**
     * 处理取消关注业务逻辑
     * @param wxMessage
     * @return
     */
    public Boolean setScanUnsubscribe(WxMpXmlMessage wxMessage);



    /**
     * 公众号扫一扫处理
     * @param wxMessage 扫码信息
     * @return 返回处理结果
     */
    public WxMpXmlOutNewsMessage scanQRcode(WxMpXmlMessage wxMessage);
}
