package com.huimin.session;

import java.util.Set;

public interface SsoSession {

	void addAttribute(String name, Object value);
	
	void removeAttribute(String name);
	
	Object getAttribute(String name);
	
	<T> T getAttribute(String name, Class<T> clazz);
	
    long getCreationTime();
    
    Set<String> getAttributeNames();
    
    String getId();
    
 }
