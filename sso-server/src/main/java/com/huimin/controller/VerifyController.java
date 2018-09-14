package com.huimin.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huimin.data.SessionManager;
import com.huimin.entity.Subject;
import com.huimin.entity.Ticket;
import com.huimin.session.SsoSession;
import com.huimin.util.Constant;
import com.huimin.util.LogUtil;
import com.huimin.util.Response;
import com.huimin.util.ResultEnum;
import com.huimin.util.TicketUtil;

@RestController
@RequestMapping("/verify")
public class VerifyController {
	@Autowired
	private SessionManager dataRepository;
	private LogUtil logger = LogUtil.logger(VerifyController.class);

	@PostMapping
	public synchronized Response  verifyTicket(@RequestParam String logoutUrl,
			@RequestParam String ticket,HttpServletRequest request) {
		logger.info("sso_server verify ticket: {} , logoutUrl : {}", ticket, logoutUrl);
		String userToken = TicketUtil.getUserToken(ticket);
		SsoSession session = dataRepository.get(userToken);
		if (session == null) {
			return Response.error(ResultEnum.NOT_LOGIN).addData(Constant.IS_LOGIN, false).build();
		}
		@SuppressWarnings("unchecked")
		List<Ticket> tickets = session.getAttribute(Constant.TICKET_ALL, List.class);
		if (tickets == null) {
			return Response.error(ResultEnum.NOT_LOGIN).addData(Constant.IS_LOGIN, false).build();
		}
		Subject subject = session.getAttribute(Constant.SUBJECT, Subject.class);
		for (Ticket tic : tickets) {
			if (ticket.equals(tic.getTicket())) {
				if (tic.isAuthentication()) {
					//说明该凭证已失效  ticket一次性 用过便无效  也有可能ticket被盗取 故注销登录
					logger.error("sso_server verify ticket : {} error,user : {} ticket compromised", ticket, subject.getUsername());
					dataRepository.delete(userToken);
					return Response.error(ResultEnum.NOT_LOGIN).addData(Constant.IS_LOGIN, false).build();
				}else {
					logger.info("sso_server verify ticket : {} , user : {} success", ticket, subject.getUsername());
					tic.setAuthentication(true);
					tic.setLogoutUrl(logoutUrl);
					dataRepository.addLogoutUrl(logoutUrl);
					dataRepository.set(userToken, session);
					return Response.ok().addData(Constant.IS_LOGIN, true).addData(Constant.SUBJECT, subject).build();
				}
			}
		}
	  return Response.error(ResultEnum.NOT_LOGIN).addData(Constant.IS_LOGIN, false).build();
	}
}
