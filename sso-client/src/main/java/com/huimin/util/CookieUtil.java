package com.huimin.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

	private CookieUtil() {};
	
	public static Cookie getLoginCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (Constant.SSOCLIENTSESSIONID.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}

	public static void addLoginCookie(String loginToken, HttpServletResponse response) {
		Cookie cookie = new Cookie(Constant.SSOCLIENTSESSIONID, loginToken);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);		
	}
}
