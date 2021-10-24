package cn.zewade.course.spring;

import cn.zewade.course.spring.beans.School;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(School.class)
public class SchoolAutoConfiguration {

    @Autowired
    private School school;

    @Bean
    public School printSchool() {
        System.out.println(school.toString());
        return school;
    }
}
