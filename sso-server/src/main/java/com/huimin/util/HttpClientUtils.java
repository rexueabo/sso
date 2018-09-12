package com.huimin.util;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author zhuliang
 *
 * @Date 2018年1月31日下午3:29:21
 */
public class HttpClientUtils {
	private static String defaultCharset = "UTF-8";
	// private static String sha256 = "~*DaxW*$ETSCwPqTWmKe9j*u(BV4it5N";
	private static String jsonContenType = "application/json";

	private static LogUtil logUtil = LogUtil.logger(HttpClientUtils.class);
	public static String doPost(String url) {
		return doPost(url, null);
	}

	public static String doPostJson(String url, Map<String, Object> params) {
		try {
			HttpPost httpPost = new HttpPost(url);
			String signString = null;
			if (params != null && !params.isEmpty()) {
				signString = JSON.toJSONString(params);
				httpPost.setEntity(new StringEntity(signString, defaultCharset));
			}
			httpPost.setHeaders(headers(jsonContenType, signString));
			logUtil.info("start execute request: url : " + url + ";params : " + params);
			return doHttpRequest(httpPost, defaultCharset);
		} catch (Exception e) {
			logUtil.error("doPostJson  error", e);
			logUtil.error("url:" + url + ";params :" + params);
			throw new RuntimeException(e.getMessage());
		}
	}

	public static String doPostSha256(String url, Map<String, Object> params) {
		return doPost(url, getSignSha256(params), defaultCharset, null);
	}

	public static String doPost(String url, Map<String, Object> params) {
		return doPost(url, params, defaultCharset);
	}
	public static String doPostJsonType(String url, Map<String, Object> params) {
		try {
			return doPost(url, params, defaultCharset, headers(jsonContenType, EntityUtils.toString(handleParams(params, defaultCharset))));
		} catch (Exception e) {
			logUtil.error("doPost error", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public static String doPost(String url, Map<String, Object> params, String charset) {
		try {
			return doPost(url, params, charset, headers(null, EntityUtils.toString(handleParams(params, charset))));
		} catch (Exception e) {
			logUtil.error("doPost error", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数列表
	 * @param charset
	 *            编码
	 * @param headers
	 *            请求头
	 * @return
	 */
	public static String doPost(String url, Map<String, Object> params, String charset, Header[] headers) {
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(handleParams(params, charset));
			if (headers != null) {
				httpPost.setHeaders(headers);
			}
			logUtil.info("start execute request: url : " + url + ";params : " + params);
			return doHttpRequest(httpPost, charset);
		} catch (Exception e) {
			logUtil.error("httpclient  error", e);
			logUtil.error("url:" + url + ";params :" + params);
			throw new RuntimeException(e.getMessage());
		}
	}

	public static String doGet(String url) {
		return doGet(url, null);
	}

	public static String doGet(String url, Map<String, Object> params) {
		return doGet(url, params, defaultCharset);
	}

	public static String doGetSha256(String url, Map<String, Object> params) {
		return doGet(url, getSignSha256(params), defaultCharset);
	}

	/**
	 * 执行get请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数
	 * @param charset
	 *            编码
	 * @return
	 */
	public static String doGet(String url, Map<String, Object> params, String charset) {
		try {
			HttpEntity httpEntity = handleParams(params, charset);
			if (httpEntity != null) {
				url = url + "?" + EntityUtils.toString(httpEntity);
			}
			URL urlPath = new URL(url);
			String signString = urlPath.getPath() + "?" + (urlPath.getQuery() == null ? "" : urlPath.getQuery());
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeaders(headers(null, URLDecoder.decode(signString, charset)));
			LogUtil.logger(HttpClientUtils.class).info("start execute request: url : " + url + ";params : " + params);
			return doHttpRequest(httpGet, charset);
		} catch (Exception e) {
			logUtil.error("httpclient  error", e);
			logUtil.error("url:" + url + ";params :" + params);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * 执行请求的核心方法
	 * 
	 * @param url
	 * @param params
	 * @param charset
	 * @param method
	 * @return
	 * @throws Exception
	 */
	private static String doHttpRequest(HttpRequestBase method, String charset) throws Exception {
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(2000).setConnectTimeout(2000)
				.setSocketTimeout(2000).build();
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			method.setConfig(requestConfig);
			HttpResponse response = httpClient.execute(method);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = entity == null ? null : EntityUtils.toString(entity, charset);
				logUtil.info("httpclient response : {} ",result);
				return result;
			}
			String message = entity == null ? "" : EntityUtils.toString(entity);
			logUtil.error("httpclient  errorMessage : " + message);
			throw new RuntimeException(message);
		}
	}

	/**
	 * 定义请求头
	 * 
	 * @param contentType
	 * @param qureyString
	 * @return
	 */
	private static Header[] headers(String contentType, String queryString) {
		Header[] headers = new BasicHeader[2];
		if (StringUtils.isNotEmpty(contentType)) {
			headers[0] = new BasicHeader("Content-Type", contentType);
		} else {
			headers[0] = new BasicHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		}
//		String timestamp = String.valueOf(System.currentTimeMillis());
//		String nonce = String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
		headers[1] = new BasicHeader("Accept", "application/json;charset=utf-8");
//		headers[2] = new BasicHeader("X_Timestamp", timestamp);
//		headers[3] = new BasicHeader("X_Nonce", nonce);
//		String sign = EncryptUtil.md5(nonce + String.valueOf(timestamp) + queryString + "{cn.huimin100.hmsp}");
//		headers[4] = new BasicHeader("X_Sign", sign);
		return headers;
	}

	/**
	 * 处理参数
	 * 
	 * @param params
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	private static UrlEncodedFormEntity handleParams(Map<String, Object> params, String charset) throws Exception {
		if (params == null || params.isEmpty()) {
			return null;
		}
		// 对map需要进行处理
		if (!(params instanceof JSONObject)) {
			params = JSON.parseObject(JSON.toJSONString(params));
		}
		List<NameValuePair> nameValuePairs = new ArrayList<>();
		for (Entry<String, Object> entry : params.entrySet()) {
			Object value = entry.getValue();
			nameValuePairs.add(new BasicNameValuePair(entry.getKey(), value == null ? "" : String.valueOf(value)));
		}
		return new UrlEncodedFormEntity(nameValuePairs, charset);
	}

	private static Map<String, Object> getSignSha256(Map<String, Object> params) {
		if (!(params instanceof JSONObject)) {
			params = JSON.parseObject(JSON.toJSONString(params));
		}
		Map<String, String> map = new HashMap<>();
		if (params != null) {
			params.forEach((key, value) -> {
				if (value != null) {
					map.put(key, String.valueOf(value));
				}
			});
		}
		// TODO //do sha256签名
		// map = SHA256Sign.sign(map, sha256);
		return new HashMap<>(map);
	}
}
