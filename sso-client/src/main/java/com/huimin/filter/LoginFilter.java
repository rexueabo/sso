package com.huimin.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huimin.entity.Subject;
import com.huimin.session.DefaultSsoSession;
import com.huimin.session.SsoSession;
import com.huimin.util.Constant;
import com.huimin.util.CookieUtil;
import com.huimin.util.EncryptUtil;
import com.huimin.util.RespondeParseUtil;
import com.huimin.util.Response;
import com.huimin.util.ResultEnum;

/**
 * 用于验证用户是否登录 sso单点登录客户端
 * 
 * @author zhuliang
 *
 * @Date 2018年3月14日下午7:13:49
 */
public class LoginFilter extends AbstractLoginFilter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest re = (HttpServletRequest) request;
		String requestURI = re.getRequestURI();
		if (excludeUrls.contains(requestURI)) {
			chain.doFilter(request, response);
			return;
		}

		if (path.equals(requestURI)) {
			doLogout(re, res);
			return;
		}

		String ticket = re.getParameter(Constant.TICKET);
		Cookie loginCookie = CookieUtil.getLoginCookie(re);
		if (loginCookie == null && StringUtils.isEmpty(ticket)) {
			if (isAjax(re)) {
				Response build = Response.error(ResultEnum.NOT_LOGIN)
						.addData(Constant.SSO_LOGIN_URL, jointParam(ssoLoginUrl, Constant.REDIRECTURL, redirectUrl))
						.build();
				res.getWriter().print(JSON.toJSONString(build));
				return;
			}
			res.sendRedirect(jointParam(ssoLoginUrl, Constant.REDIRECTURL, redirectUrl));
			return;
		}
		if (loginCookie != null) {
			String loginToken = loginCookie.getValue();
			String userToken = EncryptUtil.getUserToken(loginToken);
			if (dataRepository.exsit(userToken)) {
				SsoSession session = dataRepository.get(userToken);
				if (loginToken.equals(session.getAttribute(Constant.SSOCLIENTSESSIONID))) {
					// 说明用户已登录
					heartbeat(session);
					String username = session.getAttribute(Constant.SUBJECT, Subject.class).getUsername();
					logger.info("sso_client user : {} request", username);
					request.setAttribute(Constant.USERNAME, username);
					chain.doFilter(request, response);
					return;
				}
			}
		}
		if (StringUtils.isNotEmpty(ticket)) {
			JSONObject jsonObject = verifyTicket(ticket);
			if (ResultEnum.OK.getCode() == RespondeParseUtil.getCode(jsonObject)) {
				JSONObject data = RespondeParseUtil.getData(jsonObject);
				Boolean isLogin = data.getBoolean(Constant.IS_LOGIN);
				if (isLogin != null && isLogin) {
					// 说明登录成功
					String loginToken = EncryptUtil.generateLoginToken(ticket);
					JSONObject subJect = data.getJSONObject(Constant.SUBJECT);
					String username = subJect.getString(Constant.USERNAME);
					logger.info("sso_client user: {} login success; ticket : {}", username, ticket);
					SsoSession session = DefaultSsoSession.newInstence();
					session.addAttribute(Constant.SUBJECT, new Subject(username));
					session.addAttribute(Constant.TICKET, ticket);
					session.addAttribute(Constant.SSOCLIENTSESSIONID, loginToken);
					String userToken = EncryptUtil.getUserToken(loginToken);
					session.addAttribute(Constant.USER_TOKEN, userToken);
					dataRepository.set(userToken, session);
					CookieUtil.addLoginCookie(loginToken, res);
					request.setAttribute(Constant.USERNAME, subJect.getString(Constant.USERNAME));
					chain.doFilter(request, response);
					return;
				}
			}
		}
		if (isAjax(re)) {
			Response build = Response.error(ResultEnum.NOT_LOGIN)
					.addData(Constant.SSO_LOGIN_URL, jointParam(ssoLoginUrl, Constant.REDIRECTURL, redirectUrl))
					.build();
			res.getWriter().print(JSON.toJSONString(build));
			return;
		}
		res.sendRedirect(jointParam(ssoLoginUrl, Constant.REDIRECTURL, redirectUrl));
		return;
	}

}
