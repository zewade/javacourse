package io.kimmking.rpcfx.demo.provider;

import io.kimmking.rpcfx.api.RpcfxResolver;
import io.kimmking.rpcfx.demo.api.OrderService;
import io.kimmking.rpcfx.demo.api.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

public class DemoResolver implements RpcfxResolver, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Map<String, String> servicesConfig;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.servicesConfig = new HashMap<>();
        this.servicesConfig.put(OrderService.class.getName(), OrderServiceImpl.class.getName());
        this.servicesConfig.put(UserService.class.getName(), UserServiceImpl.class.getName());

    }

    @Override
    public Object resolve(String serviceClass) {
        try {
            Class<?> clazz = Class.forName(servicesConfig.get(serviceClass));
            Object object = clazz.newInstance();
            return object;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
