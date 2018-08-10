package com.thinkwin.yunmeeting.weixin.service;

import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

/**
 * 生成带参数二维码
 */
public interface WxQrcodeService {

    /**
     * 生成微信带参数的永久二维码
     * @param sceneId （整型：1—100000）
     * @return 返回二维码URL
     */
    public String getPermanentQrcode(Integer sceneId);

    /**
     * 生成微信带参数的永久二维码
     * @param sceneStr  （1-64位字符串）
     * @return 返回二维码URL
     */
    public String getPermanentQrcode(String sceneStr);

    /**
     * 生成微信带参数的临时二维码
     * @param sceneId  （1-32位非0整型）
     * @param expireSeconds   （时长秒，最大2592000即30天，如果不填写默认为30秒）
     * @return 返回二维码URL
     */
    public String getTemporaryQrcode(Long sceneId ,Integer expireSeconds);

    /**
     * 生成微信带参数的临时Ticket
     * @param sceneId  （1-32位非0整型）
     * @param expireSeconds   （时长秒，最大2592000即30天，如果不填写默认为30秒）
     * @return 返回二维码URL
     */
    public WxMpQrCodeTicket getTemporaryQrcodeTicket(Long sceneId,Integer expireSeconds);

    /**
     * 通过ticket换取二维码
     * @param ticket
     * @return 返回二维码URL
     */
    public String getQrCodePictureUrl(String ticket);

    /**
     * 长链接转短链接接口
     * @param url
     * @return
     */
    public String getShortUrl(String url);
}
