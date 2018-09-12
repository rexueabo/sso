package com.huimin.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huimin.util.Constant;
import com.huimin.util.Response;

/**
 * 用来测试单点登录的客户端
 * @author zhuliang
 *
 * @Date 2018年3月15日上午9:42:58
 */
@Controller
@RequestMapping("/test")
public class TestController {

	@PostMapping()
	//@GetMapping()
	@ResponseBody
	public Response test() {
		return Response.ok().build();
	}
	
	@GetMapping("/")
	public String index() {
		return "logout";
	}
	@GetMapping("/logout")
	public String testLogout() {
		return "logout";
	}
	@GetMapping("/setCookie")
	public void testSetCookie(HttpServletResponse response, String userToken) {
        response.addCookie(new Cookie(Constant.USER_TOKEN, userToken));
	}
	
}
