package com.huimin.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.huimin.session.SsoSession;


public class DefaultSessionManager implements SessionManager{

	private Map<String, SsoSession> data = new ConcurrentHashMap<>();
	@Override
	public void set(String key, SsoSession value) {
		assert key == null : "key not support null";
		assert value == null : "value not support null";
		data.put(SSO_CLIENT_PREFIX + key, value);
	}

	@Override
	public SsoSession get(String key) {
		return data.get(SSO_CLIENT_PREFIX + key);
	}

	@Override
	public void delete(String key) {
       data.remove(SSO_CLIENT_PREFIX + key);		
	}

	@Override
	public Map<String, SsoSession> getAll() {
		return data;
	}

	@Override
	public boolean exsit(String key) {
		return data.containsKey(SSO_CLIENT_PREFIX + key);
	}

	@Override
	public void clear() {
		data.clear();
	}


}
