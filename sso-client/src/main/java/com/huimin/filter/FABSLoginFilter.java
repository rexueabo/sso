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
 * 用于验证用户是否登录 sso单点登录客户端 此拦截器用于web前后端分离的项目
 * 
 * @author zhuliang
 *
 * @Date 2018年3月30日下午7:13:49
 */
public class FABSLoginFilter extends AbstractLoginFilter {

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
		// 前后端分离的web项目 需要早每次发送请求前 将用户token 和 唯一标识 保存在请求头中
		Cookie loginCookie = CookieUtil.getLoginCookie(re);
		String loginToken;
		if (loginCookie != null) {
			loginToken = loginCookie.getValue();
		} else {
			loginToken = re.getHeader(Constant.SSOCLIENTSESSIONID);
		}
		if (StringUtils.isBlank(loginToken)) {
			Response build = Response.error(ResultEnum.NOT_LOGIN)
					.addData(Constant.SSO_LOGIN_URL, jointParam(ssoLoginUrl, Constant.REDIRECTURL, redirectUrl))
					.build();
			res.getOutputStream().write(JSON.toJSONString(build).getBytes());
			return;
		}
		if (dataRepository.exsit(loginToken)) {
			// 说明用户已登录
			SsoSession session = dataRepository.get(loginToken);
			heartbeat(session);
			String username = session.getAttribute(Constant.SUBJECT, Subject.class).getUsername();
			logger.info("sso_client user : {} request", username);
			request.setAttribute(Constant.USERNAME, username);
			chain.doFilter(request, response);
			return;
		}

		String ticket = re.getParameter(Constant.TICKET);
		if (StringUtils.isNotEmpty(ticket)) {
			JSONObject jsonObject = verifyTicket(ticket);
			if (ResultEnum.OK.getCode() == RespondeParseUtil.getCode(jsonObject)) {
				JSONObject data = RespondeParseUtil.getData(jsonObject);
				Boolean isLogin = data.getBoolean(Constant.IS_LOGIN);
				if (isLogin != null && isLogin) {
					// 说明登录成功
					String userToken = EncryptUtil.getUserToken(ticket);
					JSONObject subJect = data.getJSONObject(Constant.SUBJECT);
					String username = subJect.getString(Constant.USERNAME);
					logger.info("sso_client user: {} login success; ticket : {}", username, ticket);
					SsoSession session = DefaultSsoSession.newInstence();
					session.addAttribute(Constant.SUBJECT, new Subject(username));
					session.addAttribute(Constant.TICKET, ticket);
					session.addAttribute(Constant.SSOCLIENTSESSIONID, loginToken);
					session.addAttribute(Constant.USER_TOKEN, userToken);
					dataRepository.set(userToken, session);
					dataRepository.set(loginToken, session);
					request.setAttribute(Constant.USERNAME, subJect.getString(Constant.USERNAME));
					chain.doFilter(request, response);
					return;
				}
			}
		}
		Response build = Response.error(ResultEnum.NOT_LOGIN)
				.addData(Constant.SSO_LOGIN_URL, jointParam(ssoLoginUrl, Constant.REDIRECTURL, redirectUrl)).build();
		res.getOutputStream().write(JSON.toJSONString(build).getBytes());
		return;

	}

}
