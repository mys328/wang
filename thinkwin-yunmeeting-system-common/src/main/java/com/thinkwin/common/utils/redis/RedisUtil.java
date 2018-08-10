package com.thinkwin.common.utils.redis;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Redis缓存工具
 * @author liusp
 *
 */
public class RedisUtil {
	
	//private static Logger log = Logger.getLogger(CacheUtil.class);
	private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);

    private static Jedis getJedis(){
        return JedisManager.getJedis();
    }

    private static void returnJedis(Jedis jedis){
        JedisManager.returnJedis(jedis);
    }
    
    /**
     * 判断是否存在
     * @param key
     * @return
     */
    public static boolean isExist(String key) {
    	Jedis  jedis =  getJedis();
    	try {
    		 return jedis.exists(key);    		
		} finally {
			returnJedis(jedis);
		}
    	
    }
    /**
     * 获取缓存数据
     * @param key
     * @param clz
     * @return
     */
    public   static <T> Object  getFromCache(String key,Class<T> clz){
    	Jedis  jedis =  getJedis();
    	Object result = null;
    	try {
//    		 byte[] bytes =jedis.get(key.getBytes());
//    		 if ( bytes != null && bytes.length > 0 ) { 
//	    		  byte[] uncompressStr = CompressUtil.decompressBytes(bytes);
//	    		 String json = new String(uncompressStr,0,uncompressStr.length );
//	    		 log.info("getFromCache()==:" + json);
//		    	 ObjectMapper mapper = new ObjectMapper();  
//		    	 result = mapper.readValue(json,clz);
//    		 }
    	
    		
    		String str = jedis.get(key);
//    		log.info(" from cache ======="	+ str);
    		if (StringUtils.isNotBlank(str)) {
    			String json = CompressUtil.uncompress(str);
//    			log.info("getFromCache()==:" + json);
    			ObjectMapper mapper = new ObjectMapper();
    			result = mapper.readValue(json,clz);
    		}
//    		log.info(" uncompress cache ======="	+ result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
    	return result;
    }
 
    
    
    /**
     * 获取缓存数据
     * @param key
     * @param clz
     * @return
     */
    public   static <T> Object  getFromCache(String key,Class<T> clz,boolean isCompress ){
    	Jedis  jedis =  getJedis();
    	Object result = null;
    	try {
    		
    		String str = jedis.get(key);
    		
    		log.info(" from cache ======="	+ str);
    		if ( isCompress) {
	    		if (StringUtils.isNotBlank(str)) {
	    			String json = CompressUtil.uncompress(str);
	    			ObjectMapper mapper = new ObjectMapper();
	    			result = mapper.readValue(json,clz);
	    		}
	 
    		} else {
    			if (StringUtils.isNotBlank(str)) {
	    			ObjectMapper mapper = new ObjectMapper();
	    			result = mapper.readValue(str,clz);
	    		}
    		}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
    	return result;
    }
 
    
    
    
    /**
     * 获取缓存数据
     * @param key
     * @return
     */
    public   static <T> Object  getFromCache(String key,boolean isCompress ){
    	Jedis  jedis =  getJedis();
    	Object result = null;
    	try {

    		String str = jedis.get(key);
    		if (StringUtils.isNotBlank(str)) {
    			if (isCompress ) {
    				result = CompressUtil.uncompress(str);
    			} else {
    				result = str;
    			}
//    			log.info("getFromCache()==:" + str);
    
    		}
		} finally {
			returnJedis(jedis);
		}
    	return result;
    }
    
    
    /**
     * 设置缓存数据过期时间
     * @param key
     * @param value
     */
    public   static void  putInCache(String key,String value, int seconds,  boolean isCompress){
    	Jedis  jedis =  getJedis();
    	try {	
			 if (isCompress ) {
				 jedis.setex(key, seconds, CompressUtil.compress(value));
			 } else{
				 jedis.setex(key, seconds, value);
			 }
		} finally {
			returnJedis(jedis);
		}
    }
    
    /**
     * 设置缓存数据
     * @param key
     * @param value
     */
    public   static void  putInCache(String key,String value,boolean isCompress){
    	Jedis  jedis =  getJedis();
    	try {	
//			 log.info("putInCache()==:   key=" + key + ", value=" + value);
			 if (isCompress ) {
				 jedis.set(key, CompressUtil.compress(value));			
			 } else{
				 jedis.set(key, value);
			 }
		} finally {
			returnJedis(jedis);
		}
    }
    
    
    
    /**
     * 设置缓存数据
     * @param key
     * @param value
     */
    public   static void  putInCache(String key,Object value, int seconds,  boolean isCompress){
    	Jedis  jedis =  getJedis();
    	try {
	    	ObjectMapper mapper = new ObjectMapper();  
			String json = mapper.writeValueAsString(value)  ;
			 jedis.setex(key,seconds, isCompress ? CompressUtil.compress(json) : json);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
    }
    
    /**
     * 设置缓存数据
     * @param key
     * @param value
     */
    public   static void  putInCache(String key,Object value, int seconds){
    	Jedis  jedis =  getJedis();
    	try {
	    	ObjectMapper mapper = new ObjectMapper();  
			String json = mapper.writeValueAsString(value)  ;
			 jedis.setex(key,seconds, CompressUtil.compress(json));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
    }
    
    /**
     * 设置缓存数据
     * @param key
     * @param value
     */
    public   static void  putInCache(String key,Object value){
    	Jedis  jedis =  getJedis();
    	try {
	    	ObjectMapper mapper = new ObjectMapper();  
			String json = mapper.writeValueAsString(value)  ;
//			 log.info("putInCache()==:   key=" + key + ", value=" + json);
			 jedis.set(key, CompressUtil.compress(json));
//			jedis.set(key.getBytes(),CompressUtil.compressBytes(json.getBytes("utf-8")) );
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			returnJedis(jedis);
		}
    }
    
    /**
     * 递增
     * @param key
     */
    public   static void  incr(String key){
    	Jedis  jedis =  getJedis();
    	try {	
//			 log.info("incr()==:" + key);
			
				 jedis.incr(key);
			
		} finally {
			returnJedis(jedis);
		}
    }
    
    
    /**
	 * 插入队列（左进）
	 * @param key
	 * @param value
	 */
	public static void push(String key, Object value, boolean isCompress){
		Jedis  jedis =  getJedis();
		try{
			if (StringUtils.isNotBlank(key)) {
					ObjectMapper mapper = new ObjectMapper();  
					String json = mapper.writeValueAsString(value)  ;
//					log.info("putInCache()==:   key=" + key + ", value=" + json);
					if (isCompress) {
						jedis.lpush(key, CompressUtil.compress(json));
					} else {
						jedis.lpush(key, json);
					}
			}
		}	 catch (IOException e) {
			e.printStackTrace();
		}finally{
			JedisManager.returnJedis(jedis);
		}
	}
	
	 /**
	 * 插入队列（左进) 适用String类型 (避免出现对String类型的处理,取出时出现字符串中有"")
	 * @param key
	 * @param value
	 */
	public static void pushString(String key, String value, boolean isCompress) {
		Jedis jedis = getJedis();
		try {
			if (StringUtils.isNotBlank(key)) {
				if (isCompress) {
					jedis.lpush(key, CompressUtil.compress(value));
				} else {
					jedis.lpush(key, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
	
	/**
	 * 取队列的值 （右出）
	 * @param key
	 * @return
	 */
	public static<T> Object pop(String key, boolean isCompress){
		Jedis  jedis =  getJedis();
		Object result = null;
		try{
			if (StringUtils.isNotBlank(key)) {					
					String value = jedis.rpop(key);	
					if(StringUtils.isNotBlank(value)){
						if (isCompress ) {
		    				result = CompressUtil.uncompress(value);
		    			} else {
		    				result = value;
		    			}
					}else{
		    			log.info("pop(),  value=" + value);
					}
					
//	    			log.info("pop()==:" + value);
			}
		}catch(Exception e){
			log.warn("pop() error,  e.message=" + e.getMessage());
		} finally{
			JedisManager.returnJedis(jedis);
		}
		return result;
	}
	
	
	
	/**
	 * 阻塞。取队列的值 （右出）
	 * @param key
	 * @return
	 */
	public static Object blockPop(String key, int timeouts, boolean isCompress){
		
		Jedis  jedis =  getJedis();
		Object result = null;
		List<String> bitsList = null;
		try{
			if (StringUtils.isNotBlank(key)) {					
					
					bitsList = jedis.brpop(timeouts, key);
					if(!CollectionUtils.isEmpty(bitsList)) {
						if ( bitsList.size() == 2) {
							if (isCompress ) {
								if(StringUtils.isNotBlank(bitsList.get(1))){
									result = CompressUtil.uncompress(bitsList.get(1));
								}else{
									log.info("blockPop 阻塞。取队列的值 （右出）取出的value值为空,key:"+key);
								}
			    			} else {
			    				result = bitsList.get(1);
			    			}
						}
					} 
//	    			log.info("pop()==:" + result);
			}
		}catch (Exception e){
			log.error("阻塞。取队列的值 （右出） key:"+key+";   bitsList.size():"+bitsList.size()+";  "+bitsList+";   异常:"+e);
			e.printStackTrace();
		}finally{
			JedisManager.returnJedis(jedis);
		}
		return result;
		
	}
    
    
    /**
     * 从缓存中移除数据
     * @param key
     */
    public   static void  remove(String key){
    	Jedis  jedis =  getJedis();
    	try {
			jedis.del(key.getBytes());
		}  finally {
			returnJedis(jedis);
		}
    }
    
    /**
     * 模糊取key
     * @param key
     * @return
     */
    public static List<String> keys(String key){
    	List<String> list = new ArrayList<String>();
    	Jedis  jedis =  getJedis();
    	try {
    		if(StringUtils.isNotBlank(key)){
    			Set<String> set = jedis.keys(key);
    			list.addAll(set);
    		}
			
		}  finally {
			returnJedis(jedis);
		}
    	
    	return list;
    }
    
    public static void rpush(String key, String value) {
    	Validate.notBlank(key);
    	Validate.notBlank(value);
		Jedis jedis = getJedis();
		try {
			jedis.rpush(key, value);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
    
    public static void lpush(String key, String value) {
    	Validate.notBlank(key);
    	Validate.notBlank(value);
		Jedis jedis = getJedis();
		try {
			jedis.lpush(key, value);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
    
    public static String lpop(String key) {
    	Validate.notBlank(key);
		Jedis jedis = getJedis();
		try {
			return jedis.lpop(key);
		} finally {
			JedisManager.returnJedis(jedis);
		}
    }
    
    public static String lindex(String key, long index) {
    	Validate.notBlank(key);
    	Validate.isTrue(index >= 0);
    	Jedis jedis = getJedis();
		try {
			if (index >= jedis.llen(key)) {
				return null;
			}
			return jedis.lindex(key, index);
		} finally {
			JedisManager.returnJedis(jedis);
		}
    }
    
    public static long lrem(String key, long count, String value) {
    	Validate.notBlank(key);
    	Jedis jedis = getJedis();
		try {
			return jedis.lrem(key, count, value);
		} finally {
			JedisManager.returnJedis(jedis);
		}
    }
    
    public static long llen(String key) {
    	Validate.notBlank(key);
    	Jedis jedis = getJedis();
		try {
			return jedis.llen(key);
		} finally {
			JedisManager.returnJedis(jedis);
		}
    }
    
    public static List<String> lrange(String key, long start, long end) {
    	Validate.notBlank(key);
    	Jedis jedis = getJedis();
		try {
			return jedis.lrange(key, start, end);
		} finally {
			JedisManager.returnJedis(jedis);
		}
    }
    
    public static void lset(String key, long index, String value) {
    	Validate.notBlank(key);
    	Validate.isTrue(index >= 0);
    	Validate.notBlank(value);
		Jedis jedis = getJedis();
		try {
			if (index >= jedis.llen(key)) {
				return;
			}
			jedis.lset(key, index, value);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
    
	public static void hmset(String key, Map<String, String> hash) {
		Validate.notBlank(key);
		Validate.notEmpty(hash);
		Jedis jedis = getJedis();
		try {
			jedis.hmset(key, hash);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}

	public static List<String> mget(String[] keys) {
		Validate.notEmpty(keys);
		Jedis jedis = getJedis();
		try {
			return jedis.mget(keys);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}

	public static String hget(String key, String field) {
		Validate.notBlank(key);
		Jedis jedis = getJedis();
		try {
			return jedis.hget(key, field);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}

	public static Map<String,String> hgetAll(String key) {
		Validate.notBlank(key);
		Jedis jedis = getJedis();
		try {
			return jedis.hgetAll(key);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}

	public static Map<String, String> hmget(String key, String...fields) {
		Validate.notBlank(key);
		Jedis jedis = getJedis();
		try {
			if (fields == null || fields.length == 0) {
				return jedis.hgetAll(key);
			}
			List<String> values = jedis.hmget(key, fields);
			Map<String, String> result = new HashMap<String, String>();
			for (int i = 0; i < fields.length; i++) {
				result.put(fields[i], values.get(i));
			}
			return result;
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
	
	public static long hdel(String key, String...fields) {
		Validate.notBlank(key);
		Validate.notEmpty(fields);
		Jedis jedis = getJedis();
		try {
			return jedis.hdel(key, fields);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
	
	public static long srem(String key, String...member) {
		Validate.notEmpty(key);
		Jedis jedis = getJedis();
		try {
			return jedis.srem(key, member);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
	
	public static long sadd(String key, String...member) {
		Validate.notEmpty(key);
		Jedis jedis = getJedis();
		try {
			return jedis.sadd(key, member);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
	
	public static long scard(String key) {
		Validate.notEmpty(key);
		Jedis jedis = getJedis();
		try {
			return jedis.scard(key);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
	
	public static Set<String> smembers(String key) {
		Validate.notEmpty(key);
		Jedis jedis = getJedis();
		try {
			return jedis.smembers(key);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
	
	public static Boolean sismember(String key, String member) {
		Validate.notBlank(key);
		Validate.notBlank(member);
		Jedis jedis = getJedis();
		try {
			return jedis.sismember(key, member);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
	
	public static long del(String...keys) {
		Validate.notEmpty(keys);
		Jedis jedis = getJedis();
		try {
			return jedis.del(keys);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
	
	public static boolean exists(String key) {
		Validate.notEmpty(key);
		Jedis jedis = getJedis();
		try {
			return jedis.exists(key);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
	
	public static void expire(String key, int seconds) {
		Validate.notEmpty(key);
		Validate.isTrue(seconds > 0);
		Jedis jedis = getJedis();
		try {
			jedis.expire(key, seconds);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
	
	public static String get(String key) {
		Validate.notEmpty(key);
		Jedis jedis = getJedis();
		try {
			return jedis.get(key);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}

	public static byte[] get(byte[] key) {
		Validate.notNull(key);
		Jedis jedis = getJedis();
		try {
			return jedis.get(key);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}

	public static String set(String key, String value) {
		Validate.notEmpty(key);
		Validate.notEmpty(value);
		Jedis jedis = getJedis();
		try {
			return jedis.set(key, value);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}

	public static String set(byte[] key, byte[] value) {
		Validate.notNull(key);
		Jedis jedis = getJedis();
		try {
			return jedis.set(key, value);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}

	public static String set(String key, String value, int seconds) {
		Validate.notEmpty(key);
		Validate.notEmpty(value);
		Jedis jedis = getJedis();
		try {
			return jedis.setex(key, seconds, value);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}

	public static String setnx(String key, String value, int seconds) {
		Validate.notEmpty(key);
		Validate.notEmpty(value);
		Jedis jedis = getJedis();
		try {
			return jedis.set(key, value, "NX", "EX", seconds);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}

	public static String set(byte[] key, byte[] value, int seconds) {
		Validate.notNull(key);
		Jedis jedis = getJedis();
		try {
			return jedis.setex(key, seconds, value);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}

	public static Long hset(String key, String field, String value) {
		Validate.notNull(key);
		Jedis jedis = getJedis();
		try {
			return jedis.hset(key, field, value);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}

	public static Long hsetnx(String key, String field, String value) {
		Validate.notNull(key);
		Jedis jedis = getJedis();
		try {
			return jedis.hsetnx(key, field, value);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}

	public static List<String> list(String pattern) {
		Validate.notEmpty(pattern);
		Jedis jedis = getJedis();
		List<String> result = new ArrayList<String>();
		try {
			Set<String> keys = jedis.keys(pattern);
			for (String key : keys) {
				result.add(jedis.get(key));
			}
			return result;
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
	
	public static Long publish(String channel, String message) {
		Validate.notEmpty(channel);
		Validate.notEmpty(message);
		Jedis jedis = getJedis();
		try {
			return jedis.publish(channel, message);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}
	
	public static void subscribe(JedisPubSub jedisPubSub, String...channel) {
		Validate.notEmpty(channel);
		Jedis jedis = getJedis();
		try {
			jedis.subscribe(jedisPubSub, channel);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}


	/**
	 * 批量删除模糊的keys
	 */
	public static void delRedisKeys(String keys){
		List<String> keyss = keys(keys);
		if(null != keyss && keyss.size() > 0){
			for (String key:keyss) {
				if(StringUtils.isNotBlank(key)){
					remove(key);
				}
			}
		}
	}

	public static Long ttl(String key) {
		Validate.notEmpty(key);
		Jedis jedis = getJedis();
		try {
			return jedis.ttl(key);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}

	public static Long pttl(String key) {
		Validate.notEmpty(key);
		Jedis jedis = getJedis();
		try {
			return jedis.pttl(key);
		} finally {
			JedisManager.returnJedis(jedis);
		}
	}

}
