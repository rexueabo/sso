package com.huimin.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.huimin.data.SessionManager;
import com.huimin.util.CookieUtil;
import com.huimin.util.TicketUtil;

public class SsoSessionUtil {

	private SsoSessionUtil() {
	}

	private static SessionManager dataRepository;

	public static SsoSession getSsoSession(HttpServletRequest request, HttpServletResponse response) {
		Cookie loginCookie = CookieUtil.getLoginCookie(request);
		if (loginCookie != null) {
			SsoSession ssoSession = dataRepository(request).get(TicketUtil.md5(loginCookie.getValue()));
			if (ssoSession != null) {
				return ssoSession;
			}
		}
		return generateSsoSession(request, response);
	}

	public static void deleteSsoSession(HttpServletRequest request) {
		Cookie loginCookie = CookieUtil.getLoginCookie(request);
		if (loginCookie != null) {
			dataRepository(request).delete(TicketUtil.md5(loginCookie.getValue()));
		}
	}
	private static SsoSession generateSsoSession(HttpServletRequest request, HttpServletResponse response) {
		SsoSession session = DefaultSsoSession.newInstence();
		CookieUtil.addLoginCookie(session.getId(), response);
		dataRepository(request).set(session);
		return session;
	}

	private static SessionManager dataRepository(HttpServletRequest request) {
		if (dataRepository == null) {
			ApplicationContext applicationContext = WebApplicationContextUtils
					.getWebApplicationContext(request.getServletContext());
			dataRepository = applicationContext.getBean(SessionManager.class);
		}
		return dataRepository;
	}
}
