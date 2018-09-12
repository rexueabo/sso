package com.huimin.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.huimin.entity.Subject;
import com.huimin.entity.Ticket;
import com.huimin.session.SsoSession;

public class LogoutUtil {

	private LogoutUtil() {
	}

	private static LogUtil logger = LogUtil.logger(LogoutUtil.class);

	public static void logout(String userToken, SsoSession session) {
		try {
			if (session != null) {
				Subject subject = session.getAttribute(Constant.SUBJECT, Subject.class);
				if (subject != null) {
					logger.info("user：{} logout ", subject.getUsername());
					Set<String> logoutUrls = new HashSet<>();
					@SuppressWarnings("unchecked")
					List<Ticket> tickets = session.getAttribute(Constant.TICKET_ALL, List.class);
					for (Ticket ticket : tickets) {
						logoutUrls.add(ticket.getLogoutUrl());
					}
					logout(userToken, logoutUrls);
				}
			}
		} catch (Exception e) {
			logger.error("user logout error", e);
		}
	}

	public static void logout(String userToken, Set<String> logoutUrls) {
		if (logoutUrls != null) {
			Map<String, Object> params = new HashMap<>();
			params.put(Constant.USER_TOKEN, userToken);
			for (String logoutUrl : logoutUrls) {
				try {
					HttpClientUtils.doPost(logoutUrl, params);
				} catch (Exception e) {
					logger.error("user logout error, logoutUrl : {}, param ,{}, Exception: {}", logoutUrl, params, e);
				}
			}
			logger.info("logout user success");
		}
	}

	public static void logoutAll(Set<String> logoutUrls) {
		if (logoutUrls != null) {
			logger.info("logout all user start");
			Map<String, Object> params = new HashMap<>();
			params.put(Constant.LOGOUT_ALL, true);
			for (String logoutUrl : logoutUrls) {
				try {
					HttpClientUtils.doPost(logoutUrl, params);
				} catch (Exception e) {
					logger.error("user logout error, logoutUrl : {}, param ,{}, Exception: {}", logoutUrl, params, e);
				}
			}
			logger.info("logout all user end");
		}
	}
}
