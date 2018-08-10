package com.thinkwin.common.messageGrouping;

import com.alibaba.fastjson.JSONObject;
import com.thinkwin.common.dto.publish.CommandMessage;
import com.thinkwin.common.mqMessage.MessageMQVo;
import com.thinkwin.common.mqMessage.MqMessageSenderUtil;
import com.thinkwin.common.mqMessage.MqMessageVoInterface;
import com.thinkwin.common.utils.redis.RedisUtil;

import java.util.*;

public class MessageGrouping implements IMessageGrouping {

	@Override
	public List<MqMessageVoInterface> processMessageGrouping(CommandMessage commandMessage) {
		
		List<MqMessageVoInterface> mqMessageVoList = new ArrayList<MqMessageVoInterface>();

		List<String> searchKeyList =new ArrayList<String>();

		//生成业务标识
		if(commandMessage !=null ){
			String tenantId = null;
			String terminalId=null;
			String searchKey = null;
			String [] searchArray = null;
			List<String> terminalIdList =commandMessage.getTerminals();
			if(terminalIdList != null && terminalIdList.size() >0) {
				for (int i = 0; i < terminalIdList.size(); i++) {
					tenantId = commandMessage.getTenantId();
					terminalId = terminalIdList.get(i);
					searchKey = tenantId + "_publish_" + terminalId;
					searchKeyList.add(searchKey);
				}

				String[] strings = new String[searchKeyList.size()];
				searchArray = (String[]) searchKeyList.toArray(strings);
                List<String> queueList = null;
                Map<String,String> queueMap=null;
				if (searchArray.length>0) {
					queueMap = new HashMap<String,String>();
					/**获取到业务中涉及到的队列**/
					queueList = RedisUtil.mget(searchArray);
					//业务id与队列对应关系
					if(queueList !=null && queueList.size() >0){

						String[] queueSize = new String[queueList.size()];
						String[]  queueArr = (String [])queueList.toArray(queueSize);

						for (int i = 0; i < searchArray.length; i++) {
							if(!queueMap.containsKey(searchArray[i])){
								queueMap.put(searchArray[i],queueArr[i]);
							}
						}
						queueList = new ArrayList<String>(new HashSet<String>(queueList));
						for (String queueKey :queueList) {
							if(null == queueKey){
								System.out.println("信发设备对应的队列为空");
								continue;
							}
							List<String> groupTerminalIdList = new ArrayList<String>();
							//构建新的CommandMessage对象
							List<String>  terminals = commandMessage.getTerminals();
							for (int i = 0; i <terminals.size() ; i++) {
								String termId = (String)terminals.get(i);
								String tenId =(String) commandMessage.getTenantId();
								String tempKey = tenId +"_publish_"+termId;
								if(queueMap.containsKey(tempKey)){
									String queueTmp = queueMap.get(tempKey);
									if(queueKey.equals(queueTmp)){
										groupTerminalIdList.add(termId);
									}
								}

							}
							//分组后的数据
							CommandMessage groupCommandMessage = new CommandMessage();
							groupCommandMessage.setCmd(commandMessage.getCmd());
							groupCommandMessage.setData(commandMessage.getData());
							groupCommandMessage.setRequestId(commandMessage.getRequestId());
							groupCommandMessage.setTenantId(commandMessage.getTenantId());
							groupCommandMessage.setTimestamp(commandMessage.getTimestamp());
							groupCommandMessage.setTerminals(groupTerminalIdList);
							//转换成消息队列格式

							MqMessageVoInterface mqMessageVo= new MessageMQVo();
							((MessageMQVo) mqMessageVo).setTenantId(tenantId);
							((MessageMQVo) mqMessageVo).setMessage((String) JSONObject.toJSONString(groupCommandMessage));
							((MessageMQVo) mqMessageVo).setQueueFlag(queueKey);

							mqMessageVoList.add(mqMessageVo);
							MqMessageSenderUtil.sendMessageToMQ(mqMessageVo);
						}
					}else{
						System.out.println("redis中获取到的数据集为空!!!");
					}
				}
			}
		}
		
		return mqMessageVoList;
	}

}
