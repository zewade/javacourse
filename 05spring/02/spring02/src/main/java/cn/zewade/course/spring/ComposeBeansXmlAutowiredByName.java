package cn.zewade.course.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ComposeBeansXmlAutowiredByName {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContextXmlAutowiredByName.xml");

        ComposeBeansXmlManually.printBeans(context);
    }
}
