package com.huimin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sso.client")
public class SSOClientConfig {
	private String redirectUrl;
	private String verifyTokenUrl;
	private String systemName;
	private String logoutUrl;
	private String excludeUrls;
	private String ssoLoginUrl;

	private Boolean isHeartbeat;
	private String heartbeartUrl;
	private Long heartbeatTime;
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
