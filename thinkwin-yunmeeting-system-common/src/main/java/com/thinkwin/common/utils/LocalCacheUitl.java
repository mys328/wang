package com.thinkwin.common.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例模式MemCacheUitl(双重检查加锁)
 * 内存cache
 * 
 * @author 杨嶷岍
 * 
 */
public class LocalCacheUitl {

	private volatile static LocalCacheUitl uniqueInstance = null;
	private Map<String,Object> cacheMap = null;
	private LocalCacheUitl() {
		cacheMap = new ConcurrentHashMap<String,Object>();
	}
	public static LocalCacheUitl getInstance() {
		if (uniqueInstance == null) {
			synchronized (LocalCacheUitl.class) {
				uniqueInstance = new LocalCacheUitl();
			}
		}
		return uniqueInstance;
	}

	private Map<String,Object> getCache() {
		return cacheMap;
	}
	
	public static void  put(String key,Object obj) {
		 getInstance().getCache().put(key, obj);
	}
	public static Boolean  containsKey (String key) {
		return getInstance().getCache().containsKey(key);
	}
	public static Object  get(String key) {
		Map tMap = getInstance().getCache();
		return tMap.get(key);
	}
	public static Integer  del(String key) {
		Map<String,Object> m=  getInstance().getCache();
		if(m.containsKey(key)){
			m.remove(key);
			return 1;
		}else{
			return -1;
		}
	}
	public static String remove(Object value) {
		Map<String,Object> m=  getInstance().getCache();
		String key = "";
		for (Map.Entry entry : m.entrySet()) {
			if (entry.getValue() == value) {
				key = entry.getKey().toString();
				m.remove(key);
			}
		}
		return key;
	}
}
