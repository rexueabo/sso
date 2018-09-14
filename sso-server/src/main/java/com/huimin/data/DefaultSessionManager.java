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

public class DefaultSessionManager implements SessionManager, InitializingBean {

	private Map<String, SsoSession> data = new ConcurrentHashMap<>();
	private  final String EXPIRE = "sso-server-expires";
	private static LogUtil logger = LogUtil.logger(DefaultSessionManager.class);
	private Set<String> logoutUrls = new HashSet<>();

	@Override
	public void set(String sessionId, SsoSession session) {
		assert sessionId == null : "sessionId not support null";
		assert session == null : "value not support null";
		session.addAttribute(EXPIRE, System.currentTimeMillis() + expires * 1000);
		data.put(SSO_SERVER_PRIFIX + sessionId, session);
	}

	private Integer expires = 30 * 60;

	public Integer getExpires() {
		return expires;
	}

	public void setExpires(Integer expires) {
		this.expires = expires;
	}

	@Override
	public SsoSession get(String sessionId) {
		SsoSession session = data.get(SSO_SERVER_PRIFIX +sessionId);
		if (!isValid(session)) {
			delete(sessionId);
			return null;
		}
		return session;
	}

	@Override
	public void delete(String sessionId) {
		LogoutUtil.logout(sessionId, data.get(SSO_SERVER_PRIFIX + sessionId));
		data.remove(SSO_SERVER_PRIFIX + sessionId);
	}

	@Override
	public Map<String, SsoSession> getAll() {
		Map<String, SsoSession> map = new ConcurrentHashMap<>();
		for (String sessionId : data.keySet()) {
			SsoSession result = get(sessionId);
			if (result != null) {
				map.put(sessionId.substring(SSO_SERVER_PRIFIX.length()), result);
			}
		}
		return map;
	}

	@Override
	public boolean exsit(String sessionId) {
		return get(SSO_SERVER_PRIFIX + sessionId) != null;
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
			// 说明该sessionId已经过期
			return false;
		}
		return true;
	}

	@Override
	public void addLogoutUrl(String logoutUrl) {
		logoutUrls.add(logoutUrl);
	}

	@Override
	public void expire(String sessionId) {
		SsoSession ssoSession = get(sessionId);
		if (ssoSession != null) {
			ssoSession.addAttribute(EXPIRE, System.currentTimeMillis() + expires * 1000);
		}
	}
}
