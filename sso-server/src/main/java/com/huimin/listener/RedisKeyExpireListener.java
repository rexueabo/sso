package com.huimin.listener;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import com.huimin.data.DataRepository;
import com.huimin.data.RedisDataRepository;
import com.huimin.util.LogoutUtil;

@Service
public class RedisKeyExpireListener implements MessageListener, InitializingBean{
	 @Autowired  
	    RedisMessageListenerContainer listenerContainer;
	 
	 @Autowired
	 private DataRepository dataRepository;
	 private  RedisSerializer<?> keySerializer;
	 RedisDataRepository redisDataRepository;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (dataRepository instanceof RedisDataRepository) {
			redisDataRepository = (RedisDataRepository) dataRepository;
	     	RedisTemplate<String, Object> redisTemplate = redisDataRepository.getRedisTemplate();
		    keySerializer = redisTemplate.getKeySerializer();
	     	listenerContainer.addMessageListener(this,new PatternTopic("__keyevent@"+ redisDataRepository.getDbIndex() +"__:expired"));  
		    listenerContainer.setConnectionFactory(redisTemplate.getConnectionFactory());
		}
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		 String key = (String) keySerializer.deserialize(message.getBody());
		 if (key.startsWith(DataRepository.SSO_SERVER_PRIFIX)) {
			 LogoutUtil.logout(key.substring(DataRepository.SSO_SERVER_PRIFIX.length()), redisDataRepository.getLogoutUrls());
		}
	}

}
