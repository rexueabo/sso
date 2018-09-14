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
public interface SessionManager {
	String SSO_SERVER_PRIFIX = "sso-server-";

	public void set(String sessionId, SsoSession value);

	/**
	 * 保存会话
	 * @param value
	 */
	public default void set(SsoSession value) {
		assert value == null : "value not support null";
		String id = value.getId();
		assert StringUtils.isEmpty(id) : "sessonId not support null";
		set(TicketUtil.md5(id), value);
	};

	/**
	 * 获取会话
	 * @param sessionId
	 * @return
	 */
	public SsoSession get(String sessionId);

	/**
	 * 删除会话
	 * @param sessionId
	 */
	public void delete(String sessionId);

	/**
	 * 获取所有会话
	 * @return
	 */
	public Map<String, SsoSession> getAll();

	/**
	 * 会话是否处存在
	 * @param sessionId
	 * @return
	 */
	public boolean exsit(String sessionId);

	/**
	 * 清除所有会话
	 */
	public void clear();

	public void addLogoutUrl(String logoutUrl);

	public Set<String> getLogoutUrls();

	/**
	 * 设置过去时间
	 * @param sessionId
	 */
	public void expire(String sessionId);
}
