package com.zewade.myhttpclient.client;

import java.io.IOException;
import java.util.Map;

/**
 * @author Wade
 * @date 2021-09-26
 * @description
 */
public interface MyClient {
	String doGetRequest(String url, Map<String, Object> params) throws IOException;
}
