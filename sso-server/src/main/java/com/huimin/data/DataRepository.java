package com.huimin.data;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.huimin.session.SsoSession;
import com.huimin.util.TicketUtil;

/**
 * 用于保存用户登录信息的接口
 * 
 * @author zhuliang
 *
 * @Date 2018年3月19日下午6:40:34
 */
public interface DataRepository {
	String SSO_SERVER_PRIFIX = "sso-server-";

	public void set(String key, SsoSession value);

	public default void set(SsoSession value) {
		assert value == null : "value not support null";
		String id = value.getId();
		assert StringUtils.isEmpty(id) : "sessonId not support null";
		set(TicketUtil.md5(id), value);
	};

	public SsoSession get(String key);

	public void delete(String key);

	public Map<String, SsoSession> getAll();

	public boolean exsit(String key);

	public void clear();

	public void addLogoutUrl(String logoutUrl);

	public Set<String> getLogoutUrls();

	public void expire(String key);
}
