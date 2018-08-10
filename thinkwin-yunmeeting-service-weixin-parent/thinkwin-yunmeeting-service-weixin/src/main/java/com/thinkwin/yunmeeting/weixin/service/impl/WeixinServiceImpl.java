package com.thinkwin.yunmeeting.weixin.service.impl;

import com.thinkwin.yunmeeting.weixin.config.WxMpConfig;
import com.thinkwin.yunmeeting.weixin.handler.*;
import com.thinkwin.yunmeeting.weixin.service.WeixinService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.util.crypto.SHA1;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.impl.WxMpServiceApacheHttpClientImpl;
import me.chanjar.weixin.mp.bean.kefu.result.WxMpKfOnlineList;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.constant.WxMpEventConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/*
 * 类说明：
 * @author lining 2017/7/3
 * @version 1.0
 *
 */
@Service("weixinService")
public class WeixinServiceImpl  extends WxMpServiceApacheHttpClientImpl implements WeixinService{

    @Autowired
    protected LogHandler logHandler;

    @Autowired
    protected NullHandler nullHandler;

    @Autowired
    protected KfSessionHandler kfSessionHandler;

    @Autowired
    protected StoreCheckNotifyHandler storeCheckNotifyHandler;

    @Autowired
    private WxMpConfig wxConfig;

    @Autowired
    private LocationHandler locationHandler;

    @Autowired
    private MenuHandler menuHandler;

    @Autowired
    private MsgHandler msgHandler;

    @Autowired
    private UnsubscribeHandler unsubscribeHandler;

    @Autowired
    private SubscribeHandler subscribeHandler;

    @Autowired
    private ScanHandler scanHandler;

    private WxMpMessageRouter router;

    @PostConstruct
    public void init() {
        //final LocalWxMpConfigStorage config = new LocalWxMpConfigStorage();
        //JedisPool jedisPool= JedisManager.getPool();
        //final WxMpInRedisConfigStorage config = new WxMpInRedisConfigStorage(jedisPool);
        final WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
        config.setAppId(this.wxConfig.getAppid());// 设置微信公众号的appid
        config.setSecret(this.wxConfig.getAppsecret());// 设置微信公众号的app corpSecret
        config.setToken(this.wxConfig.getToken());// 设置微信公众号的token
        config.setAesKey(this.wxConfig.getAesKey());// 设置消息加解密密钥
        super.setWxMpConfigStorage(config);

        this.refreshRouter();
    }

    private void refreshRouter() {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(this);

        // 记录所有事件的日志
        newRouter.rule().handler(this.logHandler).next();

        // 接收客服会话管理事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxMpEventConstants.CustomerService.KF_CREATE_SESSION)
                .handler(this.kfSessionHandler).end();
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxMpEventConstants.CustomerService.KF_CLOSE_SESSION)
                .handler(this.kfSessionHandler).end();
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxMpEventConstants.CustomerService.KF_SWITCH_SESSION)
                .handler(this.kfSessionHandler).end();

        // 门店审核事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxMpEventConstants.POI_CHECK_NOTIFY)
                .handler(this.storeCheckNotifyHandler)
                .end();

        // 自定义菜单事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.MenuButtonType.CLICK).handler(this.getMenuHandler()).end();

        // 点击菜单连接事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.MenuButtonType.VIEW).handler(this.nullHandler).end();

        // 关注事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.SUBSCRIBE).handler(this.getSubscribeHandler())
                .end();

        // 取消关注事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.UNSUBSCRIBE).handler(this.getUnsubscribeHandler())
                .end();

        // 上报地理位置事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.LOCATION).handler(this.getLocationHandler()).end();

        // 接收地理位置消息
        newRouter.rule().async(false).msgType(WxConsts.EventType.LOCATION)
                .handler(this.getLocationHandler()).end();

        // 扫码事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.SCAN).handler(this.getScanHandler()).end();

        // 默认
        newRouter.rule().async(false).handler(this.getMsgHandler()).end();

        this.router = newRouter;
    }

    @Override
    public WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            //路由-菜单
            //this.router.rule().msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.EVT_CLICK).eventKey("M21_BINDUSER").handler(menuHandler).end();
            //this.router.rule().msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.EVT_SCAN).handler(scanHandler).end();
            //this.router.rule().msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.EVT_SCANCODE_PUSH).eventKey("M1_SCAN").handler(scanHandler).end();
            return this.router.route(message);
        } catch (Exception e) {
        }

        return null;
    }

    @Override
    public boolean hasKefuOnline() {
        try {
            WxMpKfOnlineList kfOnlineList = this.getKefuService().kfOnlineList();
            return kfOnlineList != null && kfOnlineList.getKfOnlineList().size() > 0;
        } catch (Exception e) {
        }

        return false;
    }

    @Override
    public boolean checkSignature(String timestamp, String nonce, String signature) {
        try {
            return SHA1.gen(new String[]{this.getWxMpConfigStorage().getToken(), timestamp, nonce}).equals(signature);
        } catch (Exception var5) {
            this.log.error("Checking signature failed, and the reason is :" + var5.getMessage());
            return false;
        }
    }


    protected MenuHandler getMenuHandler() {
        return this.menuHandler;
    }

    protected SubscribeHandler getSubscribeHandler() {
        return this.subscribeHandler;
    }

    protected UnsubscribeHandler getUnsubscribeHandler() {
        return this.unsubscribeHandler;
    }

    protected AbstractHandler getLocationHandler() {
        return this.locationHandler;
    }

    protected MsgHandler getMsgHandler() {
        return this.msgHandler;
    }

    protected ScanHandler getScanHandler() {
        return this.scanHandler;
    }

}
