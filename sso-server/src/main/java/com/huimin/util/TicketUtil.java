package com.huimin.util;

import java.util.UUID;

public class TicketUtil {

	public static String generateTicket(String userToken) {
		userToken = md5(userToken);
		return userToken.concat(EncryptUtil.md516(UUID.randomUUID().toString()));
	}
	
	public static String md5(String userToken) {
		return EncryptUtil.md5(userToken.replaceAll("-", ""));
	}
	
	public static String getUserToken(String token) {
		return token.substring(0, 32);
	}
}
