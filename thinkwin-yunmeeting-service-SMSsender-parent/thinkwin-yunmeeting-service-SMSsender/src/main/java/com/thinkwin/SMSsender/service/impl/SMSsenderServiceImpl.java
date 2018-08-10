package com.thinkwin.SMSsender.service.impl;

import com.alibaba.fastjson.JSON;
import com.thinkwin.SMSsender.service.SMSsenderService;
import com.thinkwin.SMSsender.util.MqMessageSenderUtil;
import com.thinkwin.SMSsender.util.SMSCodeUtil;
import com.thinkwin.SMSsender.util.SMSMessageMQVo;
import com.thinkwin.common.utils.SMSCode;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("sMSsenderService")
public class SMSsenderServiceImpl implements SMSsenderService {

    @Override
    public String SMSsender(String phoneNumber, Integer sendTemplate) {

        //组装参数
        Map<String, Object> sendParamMap = new HashMap<String, Object>();
        //如果是发送验证码则生成验证码
        String code = null;
        String jsonString = "";
        sendParamMap.put("phoneNumber", phoneNumber);
        sendParamMap.put("sendTemplate", sendTemplate);

//      if(SMSCode.TEMPLATEID_YUMEETING.equals("179767")){
        if(sendTemplate.equals(1)){
            code = SMSCodeUtil.generateRandomSMSCode();
            sendParamMap.put("code",code);
        }
        jsonString = JSON.toJSONString(sendParamMap);

        //将消息传递给MQ,完成异步过程
        SMSMessageMQVo messageMQVo = new SMSMessageMQVo();
        messageMQVo.setFd_uuid("thinkwin");
        messageMQVo.setMessage(jsonString);

        MqMessageSenderUtil.sendMessageToMQ(messageMQVo, "thinkwin");
        return code;
    }
}
