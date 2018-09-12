package com.huimin.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;

import com.huimin.session.SsoSession;
import com.huimin.util.LogUtil;
import com.huimin.util.LogoutUtil;

public class DefaultDataRepository implements DataRepository, InitializingBean {

	private Map<String, SsoSession> data = new ConcurrentHashMap<>();
	private  final String EXPIRE = "sso-server-expires";
	private static LogUtil logger = LogUtil.logger(DefaultDataRepository.class);
	private Set<String> logoutUrls = new HashSet<>();

	@Override
	public void set(String key, SsoSession session) {
		assert key == null : "key not support null";
		assert session == null : "value not support null";
		session.addAttribute(EXPIRE, System.currentTimeMillis() + expires * 1000);
		data.put(SSO_SERVER_PRIFIX + key, session);
	}

	private Integer expires = 30 * 60;

	public Integer getExpires() {
		return expires;
	}

	public void setExpires(Integer expires) {
		this.expires = expires;
	}

	@Override
	public SsoSession get(String key) {
		SsoSession session = data.get(SSO_SERVER_PRIFIX +key);
		if (!isValid(session)) {
			delete(key);
			return null;
		}
		return session;
	}

	@Override
	public void delete(String key) {
		LogoutUtil.logout(key, data.get(SSO_SERVER_PRIFIX + key));
		data.remove(SSO_SERVER_PRIFIX + key);
	}

	@Override
	public Map<String, SsoSession> getAll() {
		Map<String, SsoSession> map = new ConcurrentHashMap<>();
		for (String key : data.keySet()) {
			SsoSession result = get(key);
			if (result != null) {
				map.put(key.substring(SSO_SERVER_PRIFIX.length()), result);
			}
		}
		return map;
	}

	@Override
	public boolean exsit(String key) {
		return get(SSO_SERVER_PRIFIX + key) != null;
	}

	@Override
	public void clear() {
		data.clear();
		LogoutUtil.logoutAll(logoutUrls);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (expires != null) {
			new Thread(() -> {
				try {
					TimeUnit.MINUTES.sleep(1);
					Iterator<Entry<String, SsoSession>> iterator = data.entrySet().iterator();
					while (iterator.hasNext()) {
						Entry<String, SsoSession> next = iterator.next();
						if (!isValid(next.getValue())) {
							LogoutUtil.logout(next.getKey(), next.getValue());
							iterator.remove();
						}
					}
				} catch (Exception e) {
					logger.error("用户登出异常", e);
				}
			}).start();
		}
	}

	public Set<String> getLogoutUrls() {
		return logoutUrls;
	}

	private boolean isValid(SsoSession session) {
		if (session == null) {
			return false;
		}
		Long expire = session.getAttribute(EXPIRE, Long.class);
		if (expire != null && System.currentTimeMillis() - expire > 0) {
			// 说明该key已经过期
			return false;
		}
		return true;
	}

	@Override
	public void addLogoutUrl(String logoutUrl) {
		logoutUrls.add(logoutUrl);
	}

	@Override
	public void expire(String key) {
		SsoSession ssoSession = get(key);
		if (ssoSession != null) {
			ssoSession.addAttribute(EXPIRE, System.currentTimeMillis() + expires * 1000);
		}
	}
}
