package com.thinkwin.yunmeeting.weixin.service.impl;

import com.thinkwin.yunmeeting.weixin.service.WxQrcodeService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * 类说明：二维码Service
 * @author lining 2017/7/5
 * @version 1.0
 *
 */
@Service("wxQrcodeService")
public class WxQrcodeServiceImpl  implements WxQrcodeService{

    @Autowired
    protected WxMpService wxService;
    /**
     *
     *
     * @param sceneId （整型：1—100000）
     * @return 返回二维码URL
     */
    @Override
    public String getPermanentQrcode(Integer sceneId) {
        try{
            WxMpQrCodeTicket ticket = this.wxService.getQrcodeService().qrCodeCreateLastTicket(sceneId);
            String url = this.wxService.getQrcodeService().qrCodePictureUrl(ticket.getTicket());
            return  url;
        }catch(WxErrorException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成微信带参数的永久二维码
     *
     * @param sceneStr （1-64位字符串）
     * @return 返回二维码URL
     */
    @Override
    public String getPermanentQrcode(String sceneStr)  {
        try{
            WxMpQrCodeTicket ticket = this.wxService.getQrcodeService().qrCodeCreateLastTicket(sceneStr);
            String url = this.wxService.getQrcodeService().qrCodePictureUrl(ticket.getTicket());
            return  url;
        }catch(WxErrorException e){
           e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成微信带参数的临时Ticket
     *
     * @param sceneId       （1-32位非0整型）
     * @param expireSeconds （时长秒，最大2592000即30天，如果不填写默认为30秒）
     * @return 返回二维码URL
     */
    @Override
    public WxMpQrCodeTicket getTemporaryQrcodeTicket(Long sceneId, Integer expireSeconds)  {
        WxMpQrCodeTicket ticket=null;
        try{
            ticket = this.wxService.getQrcodeService().qrCodeCreateTmpTicket(sceneId+"", expireSeconds);
        }catch(WxErrorException e){
            e.printStackTrace();;
        }
        return ticket;
    }

    /**
     * 通过ticket换取二维码
     *
     * @param ticket
     * @return 返回二维码URL
     */
    @Override
    public String getQrCodePictureUrl(String ticket) {
        try {

            String url = this.wxService.getQrcodeService().qrCodePictureUrl(ticket);
            return  url;
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 生成微信带参数的临时二维码
     *
     * @param sceneId （1-32位非0整型）
     * @param expireSeconds （时长秒，最大2592000即30天，如果不填写默认为30秒）
     * @return 返回二维码URL
     */
    @Override
    public String getTemporaryQrcode(Long sceneId, Integer expireSeconds)  {
        try{
            WxMpQrCodeTicket ticket = this.wxService.getQrcodeService().qrCodeCreateTmpTicket(sceneId+"", 2592000);
            String url = this.wxService.getQrcodeService().qrCodePictureUrl(ticket.getTicket());
            return url;
        }catch(WxErrorException e){
            e.printStackTrace();
        }
       return  null;
    }

    /**
     * 长链接转短链接接口
     *
     * @param url
     * @return
     */
    @Override
    public String getShortUrl(String url) {
        try {
            String shortUrl = this.wxService.shortUrl(url);

            return  shortUrl;
        }catch(WxErrorException e){
            e.printStackTrace();
        }
        return null;
    }
}
