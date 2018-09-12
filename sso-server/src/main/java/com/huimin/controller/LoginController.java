package com.huimin.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huimin.authentication.Authentication;
import com.huimin.data.DataRepository;
import com.huimin.entity.Subject;
import com.huimin.entity.Ticket;
import com.huimin.session.SsoSession;
import com.huimin.session.SsoSessionUtil;
import com.huimin.util.Constant;
import com.huimin.util.LogUtil;
import com.huimin.util.Response;
import com.huimin.util.TicketUtil;
import com.huimin.util.ValidateCode;

@Controller
public class LoginController {

	private LogUtil logger = LogUtil.logger(LoginController.class);

	@Autowired
	private DataRepository dataRepository;
	@Autowired
	private Authentication authentication;

	@GetMapping("/login")
	public String test01(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String returnUrl = request.getParameter(Constant.REDIRECTURL);
		SsoSession session = SsoSessionUtil.getSsoSession(request, response);
		Boolean isLogin = session.getAttribute(Constant.IS_LOGIN, Boolean.class);
		if (isLogin != null && isLogin) {
			if (StringUtils.isEmpty(returnUrl)) {
				return "index";
			}
		}
		model.addAttribute(Constant.REDIRECTURL, returnUrl);
		return "login";
	}

	@PostMapping("/dologin")
	@ResponseBody
	public Response login(@RequestParam String username, @RequestParam String password,
			 @RequestParam String code, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		try {
			SsoSession ssoSession = SsoSessionUtil.getSsoSession(request, response);
			Object exsitCode = ssoSession.getAttribute(Constant.VALIDATE_CODE);
			if (exsitCode == null || !code.equalsIgnoreCase(exsitCode.toString())) {
				return Response.error().setMessage("验证码错误").build();
			}
			boolean authenticate = authentication.authenticate(username, password);
			if (authenticate) {
				ssoSession.removeAttribute(Constant.VALIDATE_CODE);
				logger.info("{} :login success", username);
				Boolean isLogin = ssoSession.getAttribute(Constant.IS_LOGIN, Boolean.class);
				String id = ssoSession.getId();
				if (isLogin != null && isLogin) {
					// 说明用户已登录过 重复登录
					String ticket = TicketUtil.generateTicket(id);
					return Response.ok().addData(Constant.TICKET, ticket).build();
				} else {
					ssoSession.addAttribute(Constant.SUBJECT, new Subject(username));
					String ticket = TicketUtil.generateTicket(id);
					List<Ticket> tickets = new ArrayList<>();
					tickets.add(new Ticket(ticket));
					ssoSession.addAttribute(Constant.TICKET_ALL, tickets);
					ssoSession.addAttribute(Constant.IS_LOGIN, true);
					dataRepository.set(ssoSession);
					return Response.ok().addData(Constant.TICKET, ticket).build();
				}
			}
			return Response.error().setMessage("用户名或密码错误").build();
		} catch (Exception e) {
			logger.error(e);
			return Response.error().setMessage("认证失败").build();
		}
	}

	@GetMapping("/validateCode")
	public void validateCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ValidateCode validateCode = new ValidateCode(120, 40, 4, 100);
		String code = validateCode.getCode();
		// 设置响应的类型格式为图片格式
		response.setContentType("image/jpeg");
		// 禁止图像缓存。
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		SsoSession ssoSession = SsoSessionUtil.getSsoSession(request, response);
		ssoSession.addAttribute(Constant.VALIDATE_CODE, code);
		dataRepository.set(ssoSession);
		validateCode.write(response.getOutputStream());
	}

}
