package com.huimin.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.huimin.session.SsoSession;
import com.huimin.util.LogUtil;
import com.huimin.util.LogoutUtil;

public class RedisDataRepository implements DataRepository, InitializingBean {

	private LogUtil logger = LogUtil.logger(RedisDataRepository.class);
	private RedisTemplate<String, Object> redisTemplate;

	private final String lOGOUT_URLS = "lOGOUT_URLS";
	private Integer dbIndex;

	private Long expires = 30 * 60L;
	public RedisDataRepository(RedisTemplate<String, Object> redisTemplate) {
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

	public Long getExpires() {
		return expires;
	}

	public void setExpires(Long expires) {
		this.expires = expires;
	}

	@Override
	public void set(String key, SsoSession session) {
		assert key == null : "key not support null";
		assert session == null : "session not support null";
		if (expires == null) {
			redisTemplate.opsForValue().set(SSO_SERVER_PRIFIX + key, session);
		} else {
			redisTemplate.opsForValue().set(SSO_SERVER_PRIFIX + key, session, expires, TimeUnit.SECONDS);
		}
	}

	@Override
	public SsoSession get(String key) {
		try {
			return convert(redisTemplate.opsForValue().get(SSO_SERVER_PRIFIX + key));
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public void delete(String key) {
		LogoutUtil.logout(key, get(key));
		redisTemplate.delete(SSO_SERVER_PRIFIX + key);
	}

	@Override
	public Map<String, SsoSession> getAll() {
		Set<String> keys = redisTemplate.keys(SSO_SERVER_PRIFIX + "*");
		Map<String, SsoSession> allMap = new HashMap<>();
		if (keys != null && keys.size() > 0) {
			for (String k : keys) {
				if (!k.contains(lOGOUT_URLS)) {
					allMap.put(k.substring(SSO_SERVER_PRIFIX.length()), get(k));
				}
			}
		}
		return allMap;
	}

	public void addLogoutUrl(String logoutUrl) {
		redisTemplate.opsForSet().add(SSO_SERVER_PRIFIX + lOGOUT_URLS, logoutUrl);
	}

	public Set<String> getLogoutUrls() {
		Set<String> logoutUrls = new HashSet<>();
		Set<Object> members = redisTemplate.opsForSet().members(SSO_SERVER_PRIFIX + lOGOUT_URLS);
		if (members != null) {
			for (Object obj : members) {
				logoutUrls.add(obj.toString());
			}
		}
		return logoutUrls;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
		if (connectionFactory instanceof JedisConnectionFactory) {
			JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory) connectionFactory;
			if (dbIndex == null) {
				dbIndex = jedisConnectionFactory.getDatabase();
			} else {
				RedisSentinelConfiguration sentinelConfiguration = jedisConnectionFactory.getSentinelConfiguration();
				if (sentinelConfiguration != null) {
					sentinelConfiguration.setDatabase(dbIndex);
				} else {
					RedisStandaloneConfiguration standaloneConfiguration = jedisConnectionFactory
							.getStandaloneConfiguration();
					if (standaloneConfiguration != null) {
						standaloneConfiguration.setDatabase(dbIndex);
					}
				}
			}
		} else {
			LettuceConnectionFactory lettuceConnectionFactory = (LettuceConnectionFactory) connectionFactory;
			if (dbIndex == null) {
				dbIndex = lettuceConnectionFactory.getDatabase();
			} else {
				lettuceConnectionFactory.setDatabase(dbIndex);
			}
		}
		redisTemplate.setConnectionFactory(connectionFactory);
	}

	@Override
	public boolean exsit(String key) {
		if (key != null) {
			return redisTemplate.hasKey(SSO_SERVER_PRIFIX + key);
		}
		return false;
	}

	@Override
	public void clear() {
		List<String> keys = getAll().keySet().stream().map(key -> (SSO_SERVER_PRIFIX + key))
				.collect(Collectors.toList());
		if (!keys.isEmpty()) {
			redisTemplate.delete(keys);
			LogoutUtil.logoutAll(getLogoutUrls());
		}
	}

	@Override
	public void expire(String key) {
		if (exsit(key)) {
			redisTemplate.expire(SSO_SERVER_PRIFIX + key, expires, TimeUnit.SECONDS);
		}
	}

	private SsoSession convert(Object obj) {
		if (obj instanceof SsoSession) {
			return (SsoSession) obj;
		}
		return null;
	}
}
