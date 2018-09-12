package com.huimin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "sso-server")
@Component
public class SSOServerPropertise {

	private String excludeUrls;//登录过滤器放行url 用逗号隔开
	private String loginPath;
	private DataConfig dataConfig;
	
	public String getExcludeUrls() {
		return excludeUrls;
	}
	public String getLoginPath() {
		return loginPath;
	}

	public void setLoginPath(String loginPath) {
		this.loginPath = loginPath;
	}

	public void setExcludeUrls(String excludeUrls) {
		this.excludeUrls = excludeUrls;
	}


	public DataConfig getDataConfig() {
		return dataConfig;
	}


	public void setDataConfig(DataConfig dataConfig) {
		this.dataConfig = dataConfig;
	}

	public static class DataConfig {
		
		private Long expires;//凭证过过期时间
		
		private Integer dbIndex; //redis数据库

		public Long getExpires() {
			return expires;
		}

		public void setExpires(Long expires) {
			this.expires = expires;
		}

		public Integer getDbIndex() {
			return dbIndex;
		}

		public void setDbIndex(Integer dbIndex) {
			this.dbIndex = dbIndex;
		}
	}
}
