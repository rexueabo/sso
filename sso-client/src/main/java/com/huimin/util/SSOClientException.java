package com.huimin.util;

public class SSOClientException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public SSOClientException() {
	}
	public SSOClientException(String message) {
		super(message);
	}
	public SSOClientException(String message, Exception e) {
		super(message, e);
	}
	public SSOClientException(Exception e) {
		super(e);
	}
}
