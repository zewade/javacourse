package cn.zewade.course.spring;

import cn.zewade.course.spring.beans1.Address;
import cn.zewade.course.spring.beans1.School;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ComposeBeansByAnnotationAutowired {

    @Bean
    public School school() {
        School school = new School(null);
        school.setName("浙江大学");
        return school;
    }

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContextByAnnotationAutowired.xml");

        ComposeBeansXmlManually.printBeans(context);
    }
}
