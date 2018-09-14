package com.huimin.config;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.huimin.data.SessionManager;
import com.huimin.data.RedisSessionManager;
import com.huimin.filter.LoginFilter;

@Configuration
@Import(value = SSOClientConfigProperties.class)
public class SSOClientConfig implements WebMvcConfigurer {

	@Autowired
	private SSOClientConfigProperties ssoClientConfig;

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(source);
	}

	@Bean
	public FilterRegistrationBean<Filter> logoutfilterRegistration() {
		FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
		LoginFilter loginFilter = new LoginFilter();
		// FABSLoginFilter loginFilter = new FABSLoginFilter();
		loginFilter.setSsoClientConfig(ssoClientConfig);
		//loginFilter.setDataRepository(dataRepository());
		registrationBean.setFilter(loginFilter);
		registrationBean.setOrder(2);
		registrationBean.addUrlPatterns("*");
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<Filter> corsfilterRegistration() {
		FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(corsFilter());
		registrationBean.setOrder(1);
		return registrationBean;
	}
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory){
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		return redisTemplate;
	}
	@Bean
	public SessionManager dataRepository(RedisTemplate<String, Object> redisTemplate) {
		//DefaultDataRepository dataRepository = new DefaultDataRepository();
		return new RedisSessionManager(redisTemplate);
	}
}
