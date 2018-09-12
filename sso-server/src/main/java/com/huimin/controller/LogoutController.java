package com.huimin.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.huimin.session.SsoSessionUtil;
import com.huimin.util.Constant;

@Controller
public class LogoutController {
	
	@GetMapping("/logout")
	public String testLogout(Model model,HttpServletRequest request, HttpServletResponse response) {
		SsoSessionUtil.deleteSsoSession(request);
		model.addAttribute(Constant.REDIRECTURL, request.getParameter(Constant.REDIRECTURL));
		return "login";
	}
}
