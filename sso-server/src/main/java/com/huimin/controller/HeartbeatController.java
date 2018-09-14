package com.huimin.controller;

import java.util.List;

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
@RequestMapping("/heartbeat")
public class HeartbeatController {

	@Autowired
	private SessionManager dataRepository;
	private LogUtil logger = LogUtil.logger(VerifyController.class);

	@PostMapping
	public Response heartbeat(@RequestParam String ticket) {
		String userToken = TicketUtil.getUserToken(ticket);
		SsoSession session = dataRepository.get(userToken);
		logger.info("sso_server heartbeat ticket : {}", ticket);
		if (session != null) {
			@SuppressWarnings("unchecked")
			List<Ticket> tickets = session.getAttribute(Constant.TICKET_ALL, List.class);
			for (Ticket tic : tickets) {
				if (ticket.equals(tic.getTicket())) {
					Subject subject = session.getAttribute(Constant.SUBJECT, Subject.class);
					logger.info("sso_server heartbeat user : {}, ticket : {}", subject.getUsername(), ticket);
					dataRepository.expire(userToken);
					return Response.ok().build();
				}
			}
		}
		return Response.error(ResultEnum.TICKET_INVALID).build();
	}
}
