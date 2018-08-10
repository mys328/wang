package com.thinkwin.yunmeeting.weixin.handler;

import com.thinkwin.service.TenantContext;
import com.thinkwin.yunmeeting.weixin.localservice.LocalSaasUserOathService;
import com.thinkwin.yunmeeting.weixin.localservice.LocalWxScanService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/*
 * 类说明：
 * @author lining 2017/7/3
 * @version 1.0
 *
 */
@Component
public class SubscribeHandler extends AbstractHandler {

    @Autowired
    private LocalWxScanService localWxScanService;
    @Autowired
    private WxMpService wxService;
    @Autowired
    private LocalSaasUserOathService localSaasUserOathService;

  @Override
  public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
      WxSessionManager sessionManager) throws WxErrorException {

    this.logger.info("新关注用户 OPENID: " + wxMessage.getFromUser());



    // 获取微信用户基本信息
    WxMpUser userWxInfo = this.wxService.getUserService().userInfo(wxMessage.getFromUser());
      if (userWxInfo != null) {
          // TODO 可以添加关注用户到本地
          TenantContext.setTenantId("0");
          localSaasUserOathService.savaSaasOauthInfo(userWxInfo);
        }

    WxMpXmlOutMessage responseResult = null;
    try {
      responseResult = handleSpecial(wxMessage);
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
    }

    if (responseResult != null) {
      return responseResult;
    }

    try {

        //处理扫一扫具体业务
        WxMpXmlOutNewsMessage m=this.localWxScanService.scanQRcode(wxMessage);

        return m;

    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
    }

    return null;
  }

  /**
   * 处理特殊请求，比如如果是扫码进来的，可以做相应处理
   */
  protected WxMpXmlOutMessage handleSpecial(WxMpXmlMessage wxMessage) throws Exception {
    //TODO
    return null;
  }

}
