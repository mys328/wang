package com.thinkwin.common.utils;

import org.springframework.security.access.ConfigAttribute;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 资源和权限的对应关系map  
 * key-资源url  
 * value-权限 
 * @author liyang
 *
 */
public class ResourceMap {
	/**
	 * 构建单例map
	 */
	private static Map<String,Collection<ConfigAttribute>> resourceMap = 
			new HashMap<String,Collection<ConfigAttribute>>();
	
	/**
	 * 通过key获取value
	 * @param permissionUrl
	 * @return
	 */
	public static Collection<ConfigAttribute> get( String permissionUrl ){
		return resourceMap.get(permissionUrl);
	}
	
	/**
	 * 存值
	 * @param permissionUrl
	 * @param configAttributes
	 */
	public static void put( String permissionUrl,Collection<ConfigAttribute> configAttributes ){
		resourceMap.put(permissionUrl, configAttributes);
	}
	
	/**
	 * 获取key的迭代器
	 * @return
	 */
	public static Iterator<String> ketSetIterator(){
		return resourceMap.keySet().iterator();
	}
	
	/**
	 * 判断是否有key
	 * @param keyUrl
	 * @return
	 */
	public static boolean containsKey(String keyUrl){
		return resourceMap.containsKey(keyUrl);
	}
	
	/**
	 * 清空map，加载最新资源
	 */
	public static void clean(){
			resourceMap = new HashMap<String,Collection<ConfigAttribute>>();
	}
}
