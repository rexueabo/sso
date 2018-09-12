package com.huimin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class RespondeParseUtil {

	private RespondeParseUtil() {};
	
	public static int getCode(String response) {
		return parseResponse(response).getIntValue(Constant.CODE);
	}
	public static int getCode(JSONObject response) {
		return response.getIntValue(Constant.CODE);
	}
	public static String getMessage(String response) {
		return parseResponse(response).getString(Constant.MESSAGE);
	}
	public static String getMessage(JSONObject response) {
		return response.getString(Constant.MESSAGE);
	}
	
	public static JSONObject getData(String response) {
		return parseResponse(response).getJSONObject(Constant.DATA);
	}
	public static JSONObject getData(JSONObject response) {
		return response.getJSONObject(Constant.DATA);
	}
	
	
	public static JSONObject parseResponse(String response) {
		return JSON.parseObject(response);
	}
}
