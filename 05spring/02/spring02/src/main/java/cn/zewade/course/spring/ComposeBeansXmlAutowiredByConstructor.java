package cn.zewade.course.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ComposeBeansXmlAutowiredByConstructor {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContextAutowiredByConstructor.xml");

        ComposeBeansXmlManually.printBeans(context);
    }
}
