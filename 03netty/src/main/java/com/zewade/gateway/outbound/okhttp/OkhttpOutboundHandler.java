package com.zewade.gateway.outbound.okhttp;

import com.zewade.gateway.filter.HttpRequestFilter;
import com.zewade.gateway.filter.HttpResponseFilter;
import com.zewade.gateway.router.BackendsRouter;
import com.zewade.gateway.util.ApplicationConfig;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
public class OkhttpOutboundHandler {
	
	private final List<String> backends;
	private BackendsRouter router;
	private HttpResponseFilter responseFilter;
	private final OkHttpClient client;
	
	public OkhttpOutboundHandler() {
		this.backends = Arrays.asList(ApplicationConfig.getValueByKey("gateway.backends").split(","));
		String responseFilterName = ApplicationConfig.getValueByKey("gateway.response.filter");
		String backendsRouterName = ApplicationConfig.getValueByKey("gateway.backends.router");
		this.client = new OkHttpClient();
		try {
			Class<?> clazz = Class.forName(responseFilterName);
			this.responseFilter = (HttpResponseFilter) clazz.newInstance();
			clazz = Class.forName(backendsRouterName);
			this.router = (BackendsRouter) clazz.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void handle(FullHttpRequest fullRequest, ChannelHandlerContext ctx, HttpRequestFilter requestFilter) {
		String backendUrl = router.route(this.backends);
		final String url = backendUrl + fullRequest.uri();
		requestFilter.filter(fullRequest, ctx);
		fetchGet(fullRequest, ctx, url);
	}
	
	private void fetchGet(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final String url) {
		Request request = new Request.Builder()
				.url(url)
				.build();
		try (Response response = client.newCall(request).execute()) {
			handleResponse(fullRequest, ctx, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final Response backendResponse) throws Exception {
		FullHttpResponse response = null;
		try {
			response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(Objects.requireNonNull(backendResponse.body()).bytes()));
			
			response.headers().set("Content-Type", "application/json");
			response.headers().setInt("Content-Length", Integer.parseInt(Objects.requireNonNull(backendResponse.header("Content-Length"))));
			
			responseFilter.filter(response);
			
		} catch (Exception e) {
			e.printStackTrace();
			response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
			ctx.close();
		} finally {
			if (fullRequest != null) {
				if (!HttpUtil.isKeepAlive(fullRequest)) {
					ctx.write(response).addListener(ChannelFutureListener.CLOSE);
				} else {
					ctx.write(response);
				}
			}
			ctx.flush();
		}
	}
}
