package com.huimin.data;

import java.util.Map;

import com.huimin.session.SsoSession;

/**
 * 用于保存用户登录信息的接口
 * @author zhuliang
 *
 * @Date 2018年3月19日下午6:40:34
 */
public interface DataRepository{

	static final String SSO_CLIENT_PREFIX = "sso-client-";
	public void set(String key, SsoSession value);
	
	public SsoSession get(String key);
	
	public void delete(String key);
	
	public Map<String, SsoSession> getAll();
	
	public boolean exsit(String key);
	
	public void clear();
	
}
