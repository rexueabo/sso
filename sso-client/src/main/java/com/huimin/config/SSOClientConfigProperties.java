package com.huimin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sso.client")
public class SSOClientConfigProperties {
	private String redirectUrl;//重定向地址
	private String verifyTokenUrl;//sso-server 验证ticket地址
	private String systemName;//client 系统名称
	private String logoutUrl;//client 登出回调地址
	private String excludeUrls;//放行url
	private String ssoLoginUrl;//sso-server 登录地址

	private Boolean isHeartbeat;//是否开启心跳 
	private String heartbeartUrl;//心跳地址
	private Long heartbeatTime;//心跳间隔时间
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getVerifyTokenUrl() {
		return verifyTokenUrl;
	}

	public void setVerifyTokenUrl(String verifyTokenUrl) {
		this.verifyTokenUrl = verifyTokenUrl;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getExcludeUrls() {
		return excludeUrls;
	}

	public void setExcludeUrls(String excludeUrls) {
		this.excludeUrls = excludeUrls;
	}

	public String getSsoLoginUrl() {
		return ssoLoginUrl;
	}

	public void setSsoLoginUrl(String ssoLoginUrl) {
		this.ssoLoginUrl = ssoLoginUrl;
	}

	public Boolean getIsHeartbeat() {
		return isHeartbeat;
	}

	public void setIsHeartbeat(Boolean isHeartbeat) {
		this.isHeartbeat = isHeartbeat;
	}

	public String getHeartbeartUrl() {
		return heartbeartUrl;
	}

	public void setHeartbeartUrl(String heartbeartUrl) {
		this.heartbeartUrl = heartbeartUrl;
	}

	public Long getHeartbeatTime() {
		return heartbeatTime;
	}

	public void setHeartbeatTime(Long heartbeatTime) {
		this.heartbeatTime = heartbeatTime;
	}
	
}
