package com.huimin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.huimin.config.SSOServerPropertise.DataConfig;
import com.huimin.data.RedisSessionManager;
import com.huimin.data.SessionManager;

@Configuration
public class SessionManagerConfig {
	
	@Autowired
	private SSOServerPropertise serverPropertise;
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory){
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		return redisTemplate;
	}
	
	@Bean
	public SessionManager sessionManager(RedisTemplate<String, Object> redisTemplate){
		RedisSessionManager redisDataRepository = new RedisSessionManager(redisTemplate);
		DataConfig dataConfig = serverPropertise.getDataConfig();
		if (dataConfig != null) {
			redisDataRepository.setDbIndex(dataConfig.getDbIndex());
			redisDataRepository.setExpires(dataConfig.getExpires());
		}
		return redisDataRepository;
	}
	
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer() {
		RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
		return redisMessageListenerContainer;
	}
//	@Bean
//	public DataRepository<String, Map<String, Object>> dataRepository(){
//		DefaultDataRepository dataRepository = new DefaultDataRepository();
//		dataRepository.setExpires(10 * 60);
//        return dataRepository;
//	}
}
