package com.xuecheng.auth.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * redis操作类
 *
 * @author Walker_Don
 * @version V1.0
 * @ClassName RedisService
 * @date 2019年07月12日 上午 9:28
 */
@Component
public class RedisDao {
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 把key-value键值对存储到redis
	 *
	 * @param key
	 * @param value
	 * @param validSecond
	 * @return
	 */
	public boolean saveValue(String key, String value, long validSecond) {
		//1. 保存到redis
		stringRedisTemplate.boundValueOps(key).set(value, validSecond, TimeUnit.SECONDS);
		//2. 检查过期没有
		Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
		return expire > 0;
	}

	/**
	 * 从redis中获取authToken
	 *
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		//redis获取
		String value = stringRedisTemplate.opsForValue().get(key);
		return value;
	}

	/**
	 * 从redis中删除authToken
	 *
	 * @param key
	 */
	public void deleteEntry(String key) {
		//有可能redis中的令牌过期
		stringRedisTemplate.delete(key);
	}
}
