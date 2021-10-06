package com.zewade.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class NettyServerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(NettyServerApplication.class, args);
    }
}
