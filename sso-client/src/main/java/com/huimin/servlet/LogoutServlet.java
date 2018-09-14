package com.huimin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.huimin.data.SessionManager;
import com.huimin.util.Constant;
import com.huimin.util.Response;

/**
 * 当用户登出的时候  sso认证中心会通知各个单点登录系统注销登录
 * 此servlet的作用就是用于注销用户登录状态
 * @author zhuliang
 *
 * @Date 2018年3月14日下午7:16:57
 */
public class LogoutServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	private SessionManager dataRepository;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8");
			if (dataRepository != null) {
				String logoutAll = req.getParameter(Constant.LOGOUT_ALL);
				if (StringUtils.isNoneBlank(logoutAll) && Boolean.valueOf(logoutAll)) {
					dataRepository.clear();
				}else {
					String userToken = req.getParameter(Constant.USER_TOKEN);
					if (dataRepository.exsit(userToken)) {
						dataRepository.delete(userToken);
					}
				}
			}
			Response build = Response.ok().build();
			resp.getOutputStream().write(JSON.toJSONString(build).getBytes());
		} catch (Exception e) {
			Response build = Response.error().build();
			resp.getOutputStream().write(JSON.toJSONString(build).getBytes());
		}
	}

	public SessionManager getDataRepository() {
		return dataRepository;
	}

	public void setDataRepository(SessionManager dataRepository) {
		this.dataRepository = dataRepository;
	}
	
	
}
