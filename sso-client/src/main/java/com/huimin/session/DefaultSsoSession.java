package com.huimin.session;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSsoSession implements SsoSession, Serializable{

	private static final long serialVersionUID = 1L;

	private long creationTime;
	private Map<String, Object> map = new ConcurrentHashMap<>();
	public static SsoSession newInstence() {
		return new DefaultSsoSession();
	}
	private DefaultSsoSession() {
		creationTime = System.currentTimeMillis();
	}
	private String id;
	@Override
	public void addAttribute(String name, Object value) {
		assert name == null : "name not support null";
		assert value == null : "value not support null";
		map.put(name, value);
	}

	@Override
	public void removeAttribute(String name) {
        map.remove(name);		
	}

	@Override
	public Object getAttribute(String name) {
		return map.get(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttribute(String name, Class<T> clazz) {
		Object attribute = getAttribute(name);
		if (clazz == null || attribute == null) {
			return null;
		}
		if (clazz.isAssignableFrom(attribute.getClass())) {
			return (T) attribute;
		}
		throw new ClassCastException();
	}

	@Override
	public long getCreationTime() {
		return creationTime;
	}

	@Override
	public Set<String> getAttributeNames() {
		return map.keySet();
	}

	@Override
	public String getId() {
		return id;
	}

}
