package com.huimin.data;

import java.util.Map;

import com.huimin.session.SsoSession;

/**
 * 用于保存用户登录信息的接口
 * @author zhuliang
 *
 * @Date 2018年3月19日下午6:40:34
 */
public interface SessionManager{

	static final String SSO_CLIENT_PREFIX = "sso-client-";
	/**
	 * 添加会话
	 * @param key
	 * @param value
	 */
	public void set(String key, SsoSession value);
	
	/**
	 * 获取会话
	 * @param key
	 * @return
	 */
	public SsoSession get(String key);
	/**
	 * 删除会话
	 * @param key
	 */
	public void delete(String key);
	
	/**
	 * 获取所有会话
	 * @return
	 */
	public Map<String, SsoSession> getAll();
	
	/**
	 * 会话是否存在
	 * @param key
	 * @return
	 */
	public boolean exsit(String key);
	
	/**
	 * 清除所有会话
	 */
	public void clear();
	
}
