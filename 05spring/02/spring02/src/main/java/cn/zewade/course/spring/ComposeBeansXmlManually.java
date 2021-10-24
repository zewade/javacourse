package cn.zewade.course.spring;

import cn.zewade.course.spring.beans1.Address;
import cn.zewade.course.spring.beans1.School;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ComposeBeansXmlManually {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContextXmlManually.xml");

        printBeans(context);
    }

    public static void printBeans(ApplicationContext context) {
        Address address = (Address) context.getBean("address");
        System.out.println(address.toString());

        School school = (School) context.getBean("school");
        System.out.println(school.toString());
    }
}
