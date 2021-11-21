package io.kimmking.rpcfx.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import io.kimmking.rpcfx.api.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import lombok.Data;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class Rpcfx {

    private static Bootstrap bootstrap;
    private static ClientHandler clientHandler;

    static {
        ParserConfig.getGlobalInstance().addAccept("io.kimmking");

        // 初始化Netty
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        clientHandler = new ClientHandler();
        bootstrap.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
//                        socketChannel.pipeline().addLast(new HttpResponseDecoder());
//                        socketChannel.pipeline().addLast(new HttpRequestEncoder());
                        socketChannel.pipeline().addLast(new HttpClientCodec());
                        socketChannel.pipeline().addLast(new HttpObjectAggregator(1024 * 10 * 1024));
                        socketChannel.pipeline().addLast(clientHandler);
                    }
                });
    }

    public static <T, filters> T createFromRegistry(final Class<T> serviceClass, final String zkUrl, Router router, LoadBalancer loadBalance, Filter filter) {

        // 加filte之一

        // curator Provider list from zk
        List<String> invokers = new ArrayList<>();
        // 1. 简单：从zk拿到服务提供的列表
        // 2. 挑战：监听zk的临时节点，根据事件更新这个list（注意，需要做个全局map保持每个服务的提供者List）

        List<String> urls = router.route(invokers);

        String url = loadBalance.select(urls); // router, loadbalance

        return (T) create(serviceClass, url, filter);

    }

    public static <T> T create(final Class<T> serviceClass, final String url, Filter... filters) {

        // 0. 替换动态代理 -> 字节码生成
//        return (T) Proxy.newProxyInstance(Rpcfx.class.getClassLoader(), new Class[]{serviceClass}, new RpcfxInvocationHandler(serviceClass, url, filters));
        // 改为AOP实现
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(serviceClass);
        enhancer.setCallback(new AopMethodInterceptor(url));
        return (T) enhancer.create();
    }

    /**
     *  AOP处理类
     */
    public static class AopMethodInterceptor implements MethodInterceptor {

        public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

        private final String url;
        private final Filter[] filters;

        public <T> AopMethodInterceptor(String url, Filter... filters) {
            this.url = url;
            this.filters = filters;
        }

        private RpcfxResponse post(RpcfxRequest req, String url) throws IOException {
            String reqJson = JSON.toJSONString(req);
            System.out.println("req json: "+reqJson);

//            OkHttpClient client = new OkHttpClient();
//            final Request request = new Request.Builder()
//                    .url(url)
//                    .post(RequestBody.create(JSONTYPE, reqJson))
//                    .build();
//            String respJson = client.newCall(request).execute().body().string();
//            System.out.println("resp json: " + respJson);
//            return JSON.parseObject(respJson, RpcfxResponse.class);
            try {
                URI uri = new URI(url);
                ChannelFuture cf = bootstrap.connect(uri.getHost(), uri.getPort()).sync();

                DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.POST, uri.toASCIIString(),
                        Unpooled.wrappedBuffer(reqJson.getBytes(StandardCharsets.UTF_8)));

                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                request.headers().set(HttpHeaderNames.HOST, uri.getHost());
                request.headers().set(HttpHeaderNames.CONTENT_TYPE, JSONTYPE);

                cf.channel().writeAndFlush(request);
                cf.channel().closeFuture().sync();
                String respJson = clientHandler.getRespStr();
                System.out.println("resp json: " + respJson);
                return JSON.parseObject(respJson, RpcfxResponse.class);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            System.out.println("AOP实现处理类");

            RpcfxRequest request = new RpcfxRequest();
            request.setServiceClass(o.getClass().getInterfaces()[0].getName());
            request.setMethod(method.getName());
            request.setParams(objects);

            if (null!=filters) {
                for (Filter filter : filters) {
                    if (!filter.filter(request)) {
                        return null;
                    }
                }
            }

            RpcfxResponse response = post(request, url);
            return JSON.parse(response.getResult().toString());
        }
    }

    @Data
    public static class ClientHandler extends ChannelInboundHandlerAdapter {

        private String respStr;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//            try {
//                ByteBuf bb = (ByteBuf)msg;
//                byte[] respByte = new byte[bb.readableBytes()];
//                bb.readBytes(respByte);
//                setRespStr(new String(respByte));
//                System.out.println("client received：" + respStr);
//            } finally{
//                ReferenceCountUtil.release(msg);
//            }
            if (msg instanceof HttpResponse) {
                HttpResponse response = (HttpResponse) msg;
                setRespStr(response.decoderResult().toString());
            }
            if (msg instanceof HttpContent) {
                HttpContent content = (HttpContent) msg;
                ByteBuf buf = content.content();
                buf.release();
            }
        }
    }


    public static class RpcfxInvocationHandler implements InvocationHandler {

        public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

        private final Class<?> serviceClass;
        private final String url;
        private final Filter[] filters;

        public <T> RpcfxInvocationHandler(Class<T> serviceClass, String url, Filter... filters) {
            this.serviceClass = serviceClass;
            this.url = url;
            this.filters = filters;
        }

        // 可以尝试，自己去写对象序列化，二进制还是文本的，，，rpcfx是xml自定义序列化、反序列化，json: code.google.com/p/rpcfx
        // int byte char float double long bool
        // [], data class

        @Override
        public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {

            // 加filter地方之二
            // mock == true, new Student("hubao");

            RpcfxRequest request = new RpcfxRequest();
            request.setServiceClass(this.serviceClass.getName());
            request.setMethod(method.getName());
            request.setParams(params);

            if (null!=filters) {
                for (Filter filter : filters) {
                    if (!filter.filter(request)) {
                        return null;
                    }
                }
            }

            RpcfxResponse response = post(request, url);

            // 加filter地方之三
            // Student.setTeacher("cuijing");

            // 这里判断response.status，处理异常
            // 考虑封装一个全局的RpcfxException

            return JSON.parse(response.getResult().toString());
        }

        private RpcfxResponse post(RpcfxRequest req, String url) throws IOException {
            String reqJson = JSON.toJSONString(req);
            System.out.println("req json: "+reqJson);

            // 1.可以复用client
            // 2.尝试使用httpclient或者netty client
            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(JSONTYPE, reqJson))
                    .build();
            String respJson = client.newCall(request).execute().body().string();
            System.out.println("resp json: "+respJson);
            return JSON.parseObject(respJson, RpcfxResponse.class);
        }
    }
}
