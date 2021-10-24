package cn.zewade.course.spring;

import cn.zewade.course.spring.beans1.Address;
import cn.zewade.course.spring.beans1.School;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Configuration
public class ComposeBeansByAnnotationBean {

    @Bean
    public Address address() {
        Address address = new Address();
        address.setCity("杭州");
        address.setPhoneNo("0571-88888888");
        return address;
    }

    @Bean
    public School school(Address address) {
        School school = new School(null);
        school.setName("浙江大学");
        school.setAddress(address);
        return school;
    }

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContextByAnnotationBean.xml");

        ComposeBeansXmlManually.printBeans(context);
    }
}
