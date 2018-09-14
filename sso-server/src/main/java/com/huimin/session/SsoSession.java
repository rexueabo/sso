package com.huimin.session;

import java.util.Set;

public interface SsoSession {

	/**
	 * 添加属性
	 * @param name
	 * @param value
	 */
	void addAttribute(String name, Object value);
	
	/**
	 * 移除属性
	 * @param name
	 */
	void removeAttribute(String name);
	
	/**
	 * 获取属性值
	 * @param name
	 * @return
	 */
	Object getAttribute(String name);
	
	/**
	 * 获取属性对应的类型值
	 * @param name
	 * @param clazz
	 * @return
	 */
	<T> T getAttribute(String name, Class<T> clazz);
	
	/**
	 * 会话创建时间
	 * @return
	 */
    long getCreationTime();
    
    /**
     * 获取所有的属性名
     * @return
     */
    Set<String> getAttributeNames();
    
    /**
     * 获取会话id
     * @return
     */
    String getId();
    
 }
