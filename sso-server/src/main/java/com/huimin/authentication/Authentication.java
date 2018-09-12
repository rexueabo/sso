package com.huimin.authentication;

/**
 * 验证用户是否合法
 * @author zhuliang
 *
 * @Date 2018年4月11日上午11:44:15
 */
public interface Authentication {

	/**
	 * 
	 * @param userName  账号 
	 * @param passWord  密码
	 * @return
	 */
	 boolean authenticate(String userName, String passWord);
}
