package cn.zewade.course.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ComposeBeansXmlAutowiredByType {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContextAutowiredByType.xml");

        ComposeBeansXmlManually.printBeans(context);
    }
}
