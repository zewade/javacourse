package com.zewade.myhttpclient.client.impl;

import com.zewade.myhttpclient.client.MyClient;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Wade
 * @date 2021-09-26
 * @description
 */
public class HttpClientImpl implements MyClient {
	
	private CloseableHttpClient httpclient = HttpClients.createDefault();
	
	@Override
	public String doGetRequest(String url, Map<String, Object> params) throws IOException {
		HttpGet httpGet = new HttpGet(url + getParamsByMap(params));
		System.out.println(httpGet.getURI().toString());
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpGet);
			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity, "UTF-8");
		} finally {
			response.close();
		}
	}
	
	public static String getParamsByMap(Map<String, Object> map) {
		if (map == null || map.keySet().size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (sb.length() > 0) {
				sb.append("&");
			}
			sb.append(entry.getKey() + "=" + entry.getValue());
		}
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {
		String url = "http://localhost:8801";
		Map<String, Object> params = new HashMap<>();
		params.put("userId", "admin");
		MyClient myClient = new HttpClientImpl();
		String text = myClient.doGetRequest(url, params);
		System.out.println("url: " + url + "\nresponse: \n" + text);
	}
}
