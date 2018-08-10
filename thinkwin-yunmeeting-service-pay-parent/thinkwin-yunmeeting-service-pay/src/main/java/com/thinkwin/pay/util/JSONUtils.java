package com.thinkwin.pay.util;

import com.thinkwin.pay.service.impl.ZfbPayService;
import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class JSONUtils {

	private static Logger logger = LoggerFactory.getLogger(ZfbPayService.class);

	public static Map<String, String> JsonToMap(String jsonStr) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, String> params = objectMapper.readValue(jsonStr,Map.class);
			return params;
		} catch (Exception e) {
			logger.error("JsonToMap Error " + jsonStr, e);
			return null;
		}
	}

	public static Map<String, Object> readJson2Map(String jsonStr) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> params = objectMapper.readValue(jsonStr,Map.class);
			return params;
		} catch (Exception e) {
			logger.error("JsonToMap Error " + jsonStr, e);
			return null;
		}
	}

	public static <T> T readJson2Bean(String jsonStr,Class<T> clazz) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			T t = objectMapper.readValue(jsonStr,clazz);
			return t;
		} catch (Exception e) {
			logger.error("readJson2Bean Error " + jsonStr, e);
			return null;
		}
	}

	public static List<HashMap<String, Object>> JsonToMapList(String jsonStr) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			List<HashMap<String, Object>> params = objectMapper.readValue(jsonStr,List.class);
			for (int i = 0; i < params.size(); i++) {
				Map<String, Object> map = params.get(i);
				Set<String> set = map.keySet();
				for (Iterator<String> it = set.iterator(); it.hasNext();) {
					String key = it.next();
				}
			}
			return params;
		} catch (Exception e) {
			logger.error("JsonToMap Error " + jsonStr, e);
			return null;
		}
	}

	public static String WriteToJson(Object... params) throws IOException {
		if(ArrayUtils.isEmpty(params)){
			return "";
		}
		Map<String,Object> paramMap = new HashMap<String, Object>();
		for(int i=0;i<params.length;i+=2){
			paramMap.put((String) params[i], params[i+1]);
		}
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(paramMap);
	}

	public static String toJson(Object target){
		String result = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			result = objectMapper.writeValueAsString(target);
		} catch (Exception e) {
			logger.error("convert to Json Str Fail !");
			e.printStackTrace();
		}
		return result;
	}
}