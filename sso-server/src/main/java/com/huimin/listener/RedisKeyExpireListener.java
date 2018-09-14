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

import com.huimin.data.SessionManager;
import com.huimin.data.RedisSessionManager;
import com.huimin.util.LogoutUtil;

@Service
public class RedisKeyExpireListener implements MessageListener, InitializingBean{
	 @Autowired  
	    RedisMessageListenerContainer listenerContainer;
	 
	 @Autowired
	 private SessionManager dataRepository;
	 private  RedisSerializer<?> keySerializer;
	 RedisSessionManager redisDataRepository;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (dataRepository instanceof RedisSessionManager) {
			redisDataRepository = (RedisSessionManager) dataRepository;
	     	RedisTemplate<String, Object> redisTemplate = redisDataRepository.getRedisTemplate();
		    keySerializer = redisTemplate.getKeySerializer();
	     	listenerContainer.addMessageListener(this,new PatternTopic("__keyevent@"+ redisDataRepository.getDbIndex() +"__:expired"));  
		    listenerContainer.setConnectionFactory(redisTemplate.getConnectionFactory());
		}
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		 String key = (String) keySerializer.deserialize(message.getBody());
		 if (key.startsWith(SessionManager.SSO_SERVER_PRIFIX)) {
			 LogoutUtil.logout(key.substring(SessionManager.SSO_SERVER_PRIFIX.length()), redisDataRepository.getLogoutUrls());
		}
	}

}
