package com.thinkwin.SMSsender.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thinkwin.common.utils.redis.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * MQ消息接收类
 * 消息监听器
 *
 *  * 三种类型的消息监听器
 * 1、MessageListener的设计只是纯粹用来接收消息的，假如我们在使用MessageListener处理接收到的消息时
 * 我们需要发送一个消息通知对方我们已经收到这个消息了，那么这个时候我们就需要
 * 在代码里面去重新获取一个Connection或Session
 * 2、SessionAwareMessageListener的设计就是为了方便我们在接收到消息后发送一个回复的消息，
 * 它同样为我们提供了一个处理接收到的消息的onMessage方法，但是这个方法可以同时
 * 接收两个参数，一个是表示当前接收到的消息Message，另一个就是可以用来发送消息的Session对象
 * 3、SessionAwareMsgReceiver这个类继承了SessionAwareMessageListener，它是Spring为我们提供的，
 * 不是标准的JMS MessageListener。
 */
public class SMSMQConsumer implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(SMSMQConsumer.class);



    public void onMessage(Message arg0) {

         /*
        * 消息监听器在接收消息的时候接收到的就是一个Jms Message，
        * 如果我们要利用MessageConverter来把它转换成对应的Java对象的话，
        * 只能是我们往里面注入一个对应的MessageConverter，然后在里面手动的调用
        * */
         SMSMQMessageCoverter smsmqMessageCoverter = new SMSMQMessageCoverter();

        try {
            /*
            * 平台参数配置 redis读取
            * */
            String minute = RedisUtil.get("SMS.Consumer.minute");
            if(StringUtils.isBlank(minute)){
                minute = "10";
            }
            SMSMessageMQVo smsMessageMQVo  = (SMSMessageMQVo)smsmqMessageCoverter.fromMessage(arg0);
            if (smsMessageMQVo != null) {
                String sMSstr = smsMessageMQVo.getMessage();
                String phoneNumber = null;
                Integer sendTemplate = 1;
                String code = null;
                if (StringUtils.isNotBlank(sMSstr)) {
                    JSONObject jsonObj = JSON.parseObject(sMSstr);
                    sendTemplate = jsonObj.getInteger("sendTemplate");
                    phoneNumber = jsonObj.getString("phoneNumber");
                    if(sendTemplate.equals(1)){
                        code = jsonObj.getString("code");
                        TemplateSMSCode.verCode(phoneNumber, code, minute,sendTemplate);
                    } else{
                        TemplateSMSCode.noParams(phoneNumber, sendTemplate);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}