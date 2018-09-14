package com.huimin.listener;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import com.huimin.data.SessionManager;
import com.huimin.util.DateUtil;
import com.huimin.util.LogUtil;

@Component
public class ContextClosedListener implements ApplicationListener<ApplicationEvent>{

	private LogUtil logger = LogUtil.logger(ContextClosedListener.class);
	@Autowired
	private SessionManager dataRepository;
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
         if (event instanceof ContextClosedEvent) {
        	//系统关闭的时候注销所有的登录
        	logger.info("content closed date :" + DateUtil.format(new Date()));
			dataRepository.clear();
		}		
	}

}
