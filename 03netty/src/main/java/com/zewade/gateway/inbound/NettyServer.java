package com.zewade.gateway.inbound;

import com.zewade.gateway.outbound.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Wade
 * @date 2021-10-02
 * @description
 */
@Component
@Slf4j
public class NettyServer {
	
	private ServerBootstrap bootstrap;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private final ExecutorService serverStartService = Executors
			.newSingleThreadExecutor(new NamedThreadFactory(
					"netty-gateway"));
	
	@Value("${gateway.host:}")
	private String host;
	
	@Value("${gateway.port}")
	private int port;
	
	@PostConstruct
	public void start() {
		serverStartService.execute(() -> {
			init();
			String inetHost = StringUtils.isEmpty(host) ? InetAddressUtil.getLocalIP() : host;
			try {
				ChannelFuture f = bootstrap.bind(inetHost, port).sync();
				log.info("Netty Api Gateway started, host is {} , port is {}.",
						inetHost, port);
				f.channel().closeFuture().sync();
				log.info("Netty Api Gateway closed, host is {} , port is {}.",
						inetHost, port);
			} catch (InterruptedException e) {
				log.error("Netty Api Gateway start failed", e);
			} finally {
				destroy();
			}
		});
		
	}
	
	private void init() {
		bootstrap = new ServerBootstrap();
		bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
		workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
		bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.DEBUG))
				.childHandler(new HttpInboundInitializer())
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
	}
	
	@PreDestroy
	public void destroy() {
		if (workerGroup != null) {
			workerGroup.shutdownGracefully();
		}
		if (bossGroup != null) {
			bossGroup.shutdownGracefully();
		}
		serverStartService.shutdown();
	}
}
