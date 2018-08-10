package com.thinkwin.config;

import com.thinkwin.common.utils.redis.JedisManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableCaching
@PropertySource("classpath:/redis.properties")
public class CacheConfig extends CachingConfigurerSupport {

	private @Value("${redis.ip}")
	String redisHost;

	private @Value("${redis.port}")
	int redisPort;

	private @Value("${redis.password}")
	String redisPassword;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory factory = new JedisConnectionFactory();
		factory.setHostName(redisHost);
		factory.setPort(redisPort);
		if(StringUtils.isNotBlank(redisPassword)){
			factory.setPassword(redisPassword);
		}
		factory.setUsePool(true);
		factory.setPoolConfig(JedisManager.getConfig());
		return factory;
	}

	@Bean
	public RedisTemplate<Object, Object> redisTemplate() {
		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory());
		return redisTemplate;
	}

	@Bean
	public CacheManager cacheManager() {
		RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate());
		cacheManager.setDefaultExpiration(300);

		return cacheManager;
	}
}
