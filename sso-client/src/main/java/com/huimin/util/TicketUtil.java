package com.huimin.util;

import java.util.UUID;

public class TicketUtil {

	public static String generateLoginToken(String ticket) {
		ticket = ticket.substring(0, 32);
		return ticket.concat(EncryptUtil.md516(UUID.randomUUID().toString()));
	}
}
