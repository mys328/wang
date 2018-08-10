package com.thinkwin.yunmeeting.weixin.handler;

import com.thinkwin.yunmeeting.weixin.localservice.LocalWxScanService;
import com.thinkwin.yunmeeting.weixin.service.WeixinService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/*
 * 类说明：扫码处理
 * @author lining 2017/7/3
 * @version 1.0
 * 1.扫企业邀请码，已绑定和未绑定
 * 2.人员绑定，已绑定和未绑定
 * return:返回图文信息
 *
 */
@Component
public class ScanHandler extends AbstractHandler {

    @Autowired
    private LocalWxScanService localWxScanService;

    @Override
    public WxMpXmlOutNewsMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) throws WxErrorException {

        try {
            WeixinService weixinService = (WeixinService) wxMpService;

            //处理扫一扫具体业务
            WxMpXmlOutNewsMessage m=this.localWxScanService.scanQRcode(wxMessage);

            return m;
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }
        return null;
    }
}
