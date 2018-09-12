package com.huimin.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.huimin.service.PersonService;

@Controller
@RequestMapping("/test")
public class TestController {
    public static final Map<String, HttpSession> loginMap = new ConcurrentHashMap<>();
	@Autowired
	private PersonService personService;
	@GetMapping
	public String test01(Model model,HttpServletRequest request,HttpServletResponse response) {
		model.addAttribute("redirectUrl", request.getParameter("redirectUrl"));
		return "index";
	}
	
	@GetMapping("/setCookie")
	public void testSetCookie(String t,String userToken, HttpServletResponse response) throws IOException {
		response.sendRedirect(t + "?userToken=" + userToken);
	}
	
	@GetMapping("/cookie")
	public String test01(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			System.out.println(cookie.getName() + "---> " + cookie.getValue());
		}
		HttpSession session = request.getSession();
		System.out.println(session);
		return "OK";
	}
	@GetMapping("/login")
	public void login(String username, String password,String redirectUrl,HttpServletRequest request, HttpServletResponse response) {
		boolean authenticate = personService.authenticate(username, password);
		if (authenticate) {
			System.out.println("OK");
			String cookieToken = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("cookieToken", cookieToken);
			String userToken = UUID.randomUUID().toString();
			HttpSession session = request.getSession();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userToken", userToken);
			List<String> loginSysUrls = new ArrayList<String>();
			loginSysUrls.add(redirectUrl);
			map.put("loginSysUrls", loginSysUrls);
			session.setAttribute(cookieToken, map);
			cookie.setDomain("test.com");
			cookie.setPath("/");
			loginMap.put(cookieToken, session);
			response.addCookie(cookie);
		}
		try {
			response.sendRedirect(redirectUrl);
		} catch (IOException e) {
			try {
				request.getRequestDispatcher("/").forward(request, response);
			} catch (ServletException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
