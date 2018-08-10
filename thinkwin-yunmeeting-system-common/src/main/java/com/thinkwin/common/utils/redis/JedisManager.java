package com.thinkwin.common.utils.redis;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


/**
 * redis管理类
 * @author liusp
 *
 */
public class JedisManager {
	private static Logger log = LoggerFactory.getLogger(JedisManager.class);
	
	private static JedisPool jedisPool = null;
	private static JedisPoolConfig config = null;

	/** 
	 * 获取redis配置 
	 * @return
	 */
	public static JedisPoolConfig getConfig(){
		if(config == null){
			config = new JedisPoolConfig();
			int maxActive = RedisPropertiesUtil.getInt("redis.pool.maxActive");
			int maxIdle =  RedisPropertiesUtil.getInt("redis.pool.maxIdle");
			int maxWait =  RedisPropertiesUtil.getInt("redis.pool.maxWait");
			String onBorrow  = RedisPropertiesUtil.getString("redis.pool.testOnBorrow");
			String onReturn  = RedisPropertiesUtil.getString("redis.pool.testOnReturn");
			boolean testOnBorrow = onBorrow == null? true : Boolean.parseBoolean(onBorrow); 
			boolean testOnReturn =  onReturn == null? true : Boolean.parseBoolean(onReturn); 
			config.setMaxTotal(maxActive);
			config.setMaxIdle(maxIdle);
			config.setMaxWaitMillis(maxWait);
			config.setTestOnBorrow(testOnBorrow);
			config.setTestOnReturn(testOnReturn);
			
			//设置最小空闲数
			config.setMinIdle(5);
			//Idle时进行连接扫描
			config.setTestWhileIdle(true);
			//表示idle object evitor两次扫描之间要sleep的毫秒数
			config.setTimeBetweenEvictionRunsMillis(30000);
			//表示idle object evitor每次扫描的最多的对象数
			config.setNumTestsPerEvictionRun(10);
			//表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
			config.setMinEvictableIdleTimeMillis(60000);
		}
		return config;
	}
	
	/**
	 * 根据配置获取连接池
	 * @return
	 */
	public static JedisPool getPool() {
		if(jedisPool == null){
		
			String ip = RedisPropertiesUtil.getString("redis.ip");
			if(StringUtils.isBlank(ip)) {
				log.error("failed to get the value of redis.ip!");
				return null;
			}
		    int port = RedisPropertiesUtil.getInt("redis.port");
		    if (port <= 0 ){//TODO 
		    	log.error("failed to get the value of redis.port!");
				return null;
		    }
			String password = RedisPropertiesUtil.getString("redis.password");
		    int timeout = RedisPropertiesUtil.getInt("redis.pool.timeout");
			
			JedisPoolConfig config = JedisManager.getConfig();
			
			if(StringUtils.isNotBlank(password)){
				password = password.trim();
				jedisPool = new JedisPool(config, ip, port, timeout, password);
			}else{
				jedisPool = new JedisPool(config, ip, port, timeout);
			}
		}
		
		return jedisPool;		
	}

	/**
	 * 从jedis连接池中获取获取jedis对象
	 */
	public static Jedis getJedis() {
		jedisPool = JedisManager.getPool();
		if(null == jedisPool) {
			log.error("failed to get jedis pool from properties config file!");
			return null;
		}
		return jedisPool.getResource();
	}


	/**
	 * 
	 * 回收jedis
	 */
	public static void returnJedis(Jedis jedis) {
		if (jedis != null && jedisPool != null)
			try {
				jedisPool.returnResource(jedis);
			}catch (Exception e){}
	}
	
}
