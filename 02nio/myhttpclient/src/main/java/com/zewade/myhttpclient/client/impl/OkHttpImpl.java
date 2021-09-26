package com.zewade.myhttpclient.client.impl;

import com.zewade.myhttpclient.client.MyClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

/**
 * @author Wade
 * @date 2021-09-26
 * @description
 */
public class OkHttpImpl implements MyClient {
	
	public OkHttpClient client = new OkHttpClient();
	
	@Override
	public String doGetRequest(String url, Map<String, Object> params) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}
	
	public static void main(String[] args) throws Exception {
		String url = "http://localhost:8801";
		MyClient myClient = new OkHttpImpl();
		String text = myClient.doGetRequest(url, null);
		System.out.println("url: " + url + "\nresponse: \n" + text);
	}
}
