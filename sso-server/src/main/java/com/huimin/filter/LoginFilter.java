package com.huimin.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huimin.config.SSOServerPropertise;
import com.huimin.data.DataRepository;
import com.huimin.entity.Ticket;
import com.huimin.session.SsoSession;
import com.huimin.session.SsoSessionUtil;
import com.huimin.util.Constant;
import com.huimin.util.TicketUtil;

@Component
@WebFilter
public class LoginFilter implements Filter {

	@Autowired
	private SSOServerPropertise serverPropertise;
	private List<String> excludeUrls = new ArrayList<>();
	private String loginPath;
	@Autowired
	private DataRepository dataRepository;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String urls = serverPropertise.getExcludeUrls();
		if (urls != null) {
			excludeUrls.addAll(Arrays.asList(urls.split(",")));
		}
		this.loginPath = serverPropertise.getLoginPath();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest re = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String requestURI = re.getRequestURI();
		if (excludeUrls.contains(requestURI)) {
			chain.doFilter(request, response);
			return;
		}
		if (requestURI.contains(".") && excludeUrls.contains(requestURI.substring(requestURI.lastIndexOf(".")))) {
			chain.doFilter(request, response);
			return;
		}
		SsoSession session = SsoSessionUtil.getSsoSession(re, res);
		if (isLogin(session)) {
			String returnUrl = re.getParameter(Constant.REDIRECTURL);
			if (StringUtils.isEmpty(returnUrl)) {
				chain.doFilter(request, response);
				return;
			}
			String ticket = TicketUtil.generateTicket(session.getId());
			@SuppressWarnings("unchecked")
			List<Ticket> tickets = session.getAttribute(Constant.TICKET_ALL, List.class);
			tickets.add(new Ticket(ticket));
			dataRepository.set(session);
			res.sendRedirect(returnUrl + "?ticket=" + ticket);
			return;
		}
		if (loginPath.equals(requestURI)) {
			chain.doFilter(request, response);
			return;
		}
		res.sendRedirect(loginPath);
		return;
	}

	private boolean isLogin(SsoSession ssoSession) {
		if (ssoSession == null) {
			return false;
		}
		Boolean isLogin = ssoSession.getAttribute(Constant.IS_LOGIN, Boolean.class);
		if (isLogin != null && isLogin) {
			return true;
		}
		return false;
	}
	@Override
	public void destroy() {

	}

}
