package com.thinkwin.common.mqMessage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.ArrayList;
import java.util.List;


/**
 * MQ消息接收类
 * 
 * @author yangyiqian
 */
public abstract class MQConsumer implements MessageListener {
	
	private static final Logger log = LoggerFactory.getLogger(MQConsumer.class);

	public void onMessage(Message arg0) {

		ActiveMQObjectMessage msg = (ActiveMQObjectMessage) arg0;
		try{
			if(msg != null){
				String messageStr =msg.getStringProperty("message");
				if(StringUtils.isNotBlank(messageStr)){//不为空则处理消息
					JSONObject jsonObj = JSON.parseObject(messageStr);

					String tenantId=null;
					JSONArray bizJsonArray =null;
					//此处抽象为业务ID列表
					List<String> bizIdList = null;

					if(jsonObj.containsKey("tenantId")){
						tenantId = jsonObj.getString("tenantId");
					}

					if(jsonObj.containsKey("terminals")){
						bizJsonArray = jsonObj.getJSONArray("terminals");
						if(bizJsonArray !=null && bizJsonArray.size()>0){
							bizIdList = new ArrayList<String>();
							for (int i = 0; i <bizJsonArray.size() ; i++) {
								String tempStr = (String) bizJsonArray.get(i);
								bizIdList.add(tempStr);
							}
						}

					}
					processBiz(tenantId,bizIdList,messageStr);
				}
			}
		}catch(Exception e){
			log.error(e.getMessage());
		}
	}

	/**
	 * 功能：处理消息队列信息，不同的业务类型通用这个消息处理方法，具体实现需要子类来实现
	 * @param tenantId
	 * @param bizIdList
	 * @param messageStr
	 * @return
	 */
	public abstract Boolean processBiz(String tenantId,List<String> bizIdList,String messageStr);
	
	

}