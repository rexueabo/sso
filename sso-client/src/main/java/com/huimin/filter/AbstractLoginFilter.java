package com.huimin.filter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huimin.config.SSOClientConfig;
import com.huimin.data.DataRepository;
import com.huimin.data.DefaultDataRepository;
import com.huimin.data.RedisDataRepository;
import com.huimin.entity.HeartBeat;
import com.huimin.entity.Subject;
import com.huimin.session.SsoSession;
import com.huimin.util.Constant;
import com.huimin.util.EncryptUtil;
import com.huimin.util.HttpClientUtils;
import com.huimin.util.LogUtil;
import com.huimin.util.Response;
import com.huimin.util.SSOClientException;

public abstract class AbstractLoginFilter implements Filter {

	// 用来记录用户登录的凭证
	protected String ssoLoginUrl;
	protected String verifyTokenUrl;
	protected String redirectUrl;
	protected String logoutUrl;
	protected List<String> excludeUrls = new ArrayList<String>();
	protected DataRepository dataRepository;
	protected String path;
	protected boolean isHeartbeat = true;
	protected long heartbeatTime = 5 * 60;// 默认5分钟
	protected String heartbeatUrl;// 心跳url
	protected ExecutorService executorService;
	protected SSOClientConfig ssoClientConfig;
	protected LogUtil logger = LogUtil.logger(getClass());
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			if (isHeartbeat) {
				executorService = Executors.newSingleThreadExecutor();
			}
			String urls;
			if (ssoClientConfig != null) {
				ssoLoginUrl = ssoClientConfig.getSsoLoginUrl();
				verifyTokenUrl = ssoClientConfig.getVerifyTokenUrl();
				redirectUrl = ssoClientConfig.getRedirectUrl();
				urls = ssoClientConfig.getExcludeUrls();
				logoutUrl = ssoClientConfig.getLogoutUrl();
				Boolean heartbeat = ssoClientConfig.getIsHeartbeat();
				if (heartbeat != null) {
					isHeartbeat = heartbeat;
				}
				Long beatTime = ssoClientConfig.getHeartbeatTime();
				if (beatTime != null) {
					heartbeatTime = beatTime;
				}
				heartbeatUrl = ssoClientConfig.getHeartbeartUrl();

			} else {
				ssoLoginUrl = filterConfig.getInitParameter(Constant.SSO_LOGIN_URL);
				verifyTokenUrl = filterConfig.getInitParameter(Constant.VERIFY_TOKEN_URL);
				redirectUrl = filterConfig.getInitParameter(Constant.REDIRECTURL);
				logoutUrl = filterConfig.getInitParameter(Constant.LOGOUT_URL);
				urls = filterConfig.getInitParameter(Constant.EXCLUDE_URLS);
				String isbeart = filterConfig.getInitParameter(Constant.IS_HEARTBRAT);
				if (StringUtils.isNotEmpty(isbeart)) {
					isHeartbeat = Boolean.valueOf(isbeart);
				}
				String beatTime = filterConfig.getInitParameter(Constant.HEARTBRAT_TIME);
				if (beatTime != null) {
					heartbeatTime = Long.valueOf(beatTime);
				}
				heartbeatUrl = filterConfig.getInitParameter(Constant.HEARTBRAT_URL);
			}
			if (urls != null) {
				excludeUrls.addAll(Arrays.asList(urls.split(",")));
			}
			assert StringUtils.isEmpty(ssoLoginUrl) : "ssoLoginUrl is null";
			assert StringUtils.isEmpty(redirectUrl)
					&& StringUtils.isEmpty(logoutUrl) : "redirectUrl or logoutUrl is null";
			URI loginUrl = new URI(ssoLoginUrl);
			if (StringUtils.isEmpty(verifyTokenUrl)) {
				verifyTokenUrl = loginUrl.getScheme() + "://" + loginUrl.getAuthority() + "/verify";
			}
			if (StringUtils.isEmpty(heartbeatUrl)) {
				heartbeatUrl = loginUrl.getScheme() + "://" + loginUrl.getAuthority() + "/heartbeat";
			}
			if (StringUtils.isEmpty(logoutUrl)) {
				URI uri2 = new URI(redirectUrl);
				logoutUrl = uri2.getScheme() + "://" + uri2.getAuthority() + "/logout";
			}

			if (dataRepository == null) {
				ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
				dataRepository = ac.getBean(DataRepository.class);
				if (dataRepository == null) {
					dataRepository = new DefaultDataRepository();
				}
				if (dataRepository instanceof RedisDataRepository) {
					((RedisDataRepository) dataRepository).setClientPrefix(EncryptUtil.md516(logoutUrl) + "-");
				}
			}
			URI uri = new URI(logoutUrl);
			path = uri.getPath();
			logger.info("sso_client init success");
		} catch (URISyntaxException e) {
			logger.error("sso_client init error", e);
			throw new SSOClientException("sso_client init error", e);
		}
	}

	public void setDataRepository(DataRepository dataRepository) {
		this.dataRepository = dataRepository;
	};

	public void setSsoClientConfig(SSOClientConfig ssoClientConfig) {
		this.ssoClientConfig = ssoClientConfig;
	};

	@Override
	public void destroy() {
		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdown();
		}
		logger.info("sso_clent shutdown success");
	}

	protected void doLogout(HttpServletRequest re, HttpServletResponse res) {
		try {
			res.setCharacterEncoding("UTF-8");
			if (dataRepository != null) {
				String logoutAll = re.getParameter(Constant.LOGOUT_ALL);
				if (StringUtils.isNotBlank(logoutAll) && Boolean.valueOf(logoutAll)) {
					logger.info("sso_clent logout all users");
					dataRepository.clear();
				} else {
					String userToken = re.getParameter(Constant.USER_TOKEN);
					if (dataRepository != null && dataRepository.exsit(userToken)) {
						SsoSession session = dataRepository.get(userToken);
						logger.info("sso_client logout user {} success, userToken : {}", session.getAttribute(Constant.SUBJECT, Subject.class).getUsername(), userToken);
						dataRepository.delete(userToken);
						String loginToken = session.getAttribute(Constant.SSOCLIENTSESSIONID, String.class);
						if (StringUtils.isNotEmpty(loginToken) && !loginToken.startsWith(userToken)) {
							dataRepository.delete(loginToken);
						}
					}
				}
			}
			Response build = Response.ok().build();
			res.getOutputStream().write(JSON.toJSONString(build).getBytes());
			return;
		} catch (Exception e) {
			logger.error( "sso_clent logout error :", e);
			Response build = Response.error().build();
			try {
				res.getOutputStream().write(JSON.toJSONString(build).getBytes());
				return;
			} catch (IOException e1) {
				logger.error( "sso_clent logout error:", e1);
			}
		}

	}

	protected boolean isAjax(HttpServletRequest request) {
		String requestedWith = request.getHeader("x-requested-with");
		return requestedWith != null ? "XMLHttpRequest".equalsIgnoreCase(requestedWith) : false;
	}

	protected void heartbeat(SsoSession session) {
		if (isHeartbeat) {
			try {
				session.addAttribute(Constant.HEARTBRAT_TIME, heartbeatTime * 1000);
				Future<Integer> submit = executorService.submit(new HeartBeat(heartbeatUrl, session));
				Integer result = submit.get();
				String userToken = session.getAttribute(Constant.USER_TOKEN, String.class);
				String loginToken = session.getAttribute(Constant.SSOCLIENTSESSIONID, String.class);
				if (result == 0) {
					session.addAttribute(Constant.REFRESH_TIME, System.currentTimeMillis());
					dataRepository.set(userToken, session);
					if (!loginToken.startsWith(userToken)) {
						dataRepository.set(loginToken, session);
					}
				}else if (result == 1) {
					dataRepository.delete(userToken);
					if (StringUtils.isNotEmpty(loginToken) && !loginToken.startsWith(userToken)) {
						dataRepository.delete(loginToken);
					}
				}
			} catch (Exception e) {
				logger.error( "sso_clent heartbeat error :", e);
			}
		}
	}
	
	protected JSONObject verifyTicket(String ticket) {
		Map<String, Object> params = new HashMap<>();
		params.put(Constant.LOGOUT_URL, logoutUrl);
		params.put(Constant.TICKET, ticket);
		String result = HttpClientUtils.doPost(verifyTokenUrl, params);
		return JSON.parseObject(result);
	}
	
	protected String jointParam(String url,String paramName, String paramValue) {
		if (url.indexOf("?") == -1) {
			return url.concat("?").concat(paramName + "=" + paramValue);
		}else {
			return url.concat("&").concat(paramName + "=" + paramValue);
		}
	}
}
