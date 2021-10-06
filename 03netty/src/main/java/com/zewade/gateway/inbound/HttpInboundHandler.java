package com.zewade.gateway.inbound;

import com.zewade.gateway.filter.HttpRequestFilter;
import com.zewade.gateway.outbound.okhttp.OkhttpOutboundHandler;
import com.zewade.gateway.util.ApplicationConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private final OkhttpOutboundHandler handler;
    private HttpRequestFilter filter;

    public HttpInboundHandler() {
        this.handler = new OkhttpOutboundHandler();
        try {
            Class<?> clazz = Class.forName(ApplicationConfig.getValueByKey("gateway.request.filter"));
            this.filter = (HttpRequestFilter) clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            FullHttpRequest fullRequest = (FullHttpRequest) msg;
            handler.handle(fullRequest, ctx, filter);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

}
