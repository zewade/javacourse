package cn.zewade.course.spring;

import cn.zewade.course.spring.beans.School;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
public class Spring08Application {

    public static void main(String[] args) {
        SpringApplication.run(Spring08Application.class, args);
    }

}
