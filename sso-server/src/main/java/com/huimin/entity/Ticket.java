package com.huimin.entity;

import java.io.Serializable;
/**
 * 票据实体类
 * @author zhuliang
 *
 * @Date 2018年4月4日下午5:08:30
 */
public class Ticket implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private boolean authentication;//该票据是否被认证过
	
	private String logoutUrl; //该票据对应的系统
	
	private String ticket;

	private String heartbeatToken; //该票据对应的登录系统
	public Ticket() {
	}
	public Ticket(String ticket) {
		this.ticket = ticket;
	}
	public boolean isAuthentication() {
		return authentication;
	}

	public void setAuthentication(boolean authentication) {
		this.authentication = authentication;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public String getHeartbeatToken() {
		return heartbeatToken;
	}
	public void setHeartbeatToken(String heartbeatToken) {
		this.heartbeatToken = heartbeatToken;
	}
	@Override
	public String toString() {
		return "Ticket [authentication=" + authentication + ", logoutUrl=" + logoutUrl + ", ticket=" + ticket
				+ ", heartbeatToken=" + heartbeatToken + "]";
	}
	
}
