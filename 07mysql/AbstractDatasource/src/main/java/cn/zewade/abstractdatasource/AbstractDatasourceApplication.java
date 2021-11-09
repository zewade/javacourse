package cn.zewade.abstractdatasource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@PropertySource(encoding="UTF-8", value = { "classpath:application.properties" })
@EnableTransactionManagement(order = 10) //保证读写分离的切面正确
public class AbstractDatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AbstractDatasourceApplication.class, args);
    }

}
