package com.huimin.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.huimin.session.SsoSession;
import com.huimin.util.LogUtil;

public class RedisSessionManager implements SessionManager, InitializingBean {

	private RedisTemplate<String, Object> redisTemplate;

	private Integer dbIndex;

	private int expires = 24 *60 * 60;
	
	private String prefix;
	public RedisSessionManager(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public Integer getDbIndex() {
		return dbIndex;
	}

	public void setDbIndex(Integer dbIndex) {
		this.dbIndex = dbIndex;
	}

	public int getExpires() {
		return expires;
	}

	public void setExpires(int expires) {
		this.expires = expires;
	}

	@Override
	public void set(String key, SsoSession value) {
		assert key == null : "key not support null";
		assert value == null : "value not support null";
		redisTemplate.opsForValue().set(getKeyPrefix() + key, value, expires, TimeUnit.SECONDS);
	}

	@Override
	public SsoSession get(String key) {
		try {
			return convert(redisTemplate.opsForValue().get(getKeyPrefix() + key));
		} catch (Exception e) {
			LogUtil.logger(this).error(e);
			return null;
		}
	}

	@Override
	public void delete(String key) {
		redisTemplate.delete(getKeyPrefix() + key);
	}

	@Override
	public Map<String, SsoSession> getAll() {
		Set<String> keys = redisTemplate.keys(getKeyPrefix() + "*");
		Map<String, SsoSession> allMap = new HashMap<>();
		if (keys != null && keys.size() > 0) {
			for (String k : keys) {
				allMap.put(k.substring(getKeyPrefix().length()), get(k));
			}
		}
		return allMap;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void afterPropertiesSet() throws Exception {
		redisTemplate.setKeySerializer( new StringRedisSerializer());
		if (dbIndex != null) {
			RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
			if (connectionFactory instanceof JedisConnectionFactory) {
				JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory) connectionFactory;
				jedisConnectionFactory.setDatabase(dbIndex);
			}else {
				LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory) connectionFactory;
				lettuceConnectionFactory.setDatabase(dbIndex);
			}
			redisTemplate.setConnectionFactory(connectionFactory);
		}
	}

	@Override
	public boolean exsit(String key) {
		if (key != null) {
			return redisTemplate.hasKey(getKeyPrefix() + key);
		}
		return false;
	}

	@Override
	public void clear() {
		List<String> keys = getAll().keySet().stream().map(key -> (getKeyPrefix() + key))
				.collect(Collectors.toList());
		if (!keys.isEmpty()) {
			redisTemplate.delete(keys);
		}
	}

	private SsoSession convert(Object obj) {
		if (obj instanceof SsoSession) {
			return (SsoSession) obj;
		}
		return null;
	}
	
	public void setClientPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	private String getKeyPrefix() {
		return SSO_CLIENT_PREFIX + prefix;
	}
}
