package com.huimin.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huimin.session.SsoSession;
import com.huimin.util.Constant;
import com.huimin.util.HttpClientUtils;
import com.huimin.util.RespondeParseUtil;
import com.huimin.util.ResultEnum;

public class HeartBeat implements Callable<Integer>{

	private SsoSession session;
	private String heartbeatUrl;
	public HeartBeat(String heartbeatUrl,SsoSession session) {
		this.heartbeatUrl = heartbeatUrl;
		this.session = session;
	}
	

	@Override
	public Integer call() throws Exception {
		if (session != null) {
			long currentTimeMillis = System.currentTimeMillis();
			Long refreshTime = session.getAttribute(Constant.REFRESH_TIME, Long.class);
			if (refreshTime == null) {
				refreshTime = session.getCreationTime();
			}
			long heartbeatTime = session.getAttribute(Constant.HEARTBRAT_TIME, Long.class);
			if (currentTimeMillis - refreshTime >= heartbeatTime) {
				Map<String, Object> params = new HashMap<>(2);
				params.put(Constant.TICKET, session.getAttribute(Constant.TICKET));
				String result = HttpClientUtils.doPost(heartbeatUrl, params);
				JSONObject jsonObject = JSON.parseObject(result);
				int code = RespondeParseUtil.getCode(jsonObject);
				if (ResultEnum.OK.getCode() == code) {
					return 0;
				}
				if (ResultEnum.TICKET_INVALID.getCode() == code) {
					//说明服务端一删除该凭证 应该注销登录
					return 1;
				}
			}
		}
		return 2;
	}

}
