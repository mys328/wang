package com.thinkwin.SMSsender.service;

/**
 * 短信服务接口
 */
public interface SMSsenderService {

    /*
    * 1、电话号码
    * 2、发送模板
    * */
    public String SMSsender(String phoneNumber,Integer sendTemplate);
}
