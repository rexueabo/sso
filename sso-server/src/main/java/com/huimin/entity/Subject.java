package com.huimin.entity;

import java.io.Serializable;

public class Subject implements Serializable{

	private static final long serialVersionUID = 1L;

	private String username;

	public Subject() {
	}
	public Subject(String username) {
		this.username = username; 
	}
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
