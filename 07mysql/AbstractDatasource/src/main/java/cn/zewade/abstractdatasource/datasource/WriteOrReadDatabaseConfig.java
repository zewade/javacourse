package cn.zewade.abstractdatasource.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class WriteOrReadDatabaseConfig {
    // 定义读库，写库的 DataSource
    @Primary
    @Bean(name = "writeDataSource", destroyMethod = "close")
    @ConfigurationProperties(prefix = "course.write")
    public DataSource writeDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "readDataSource", destroyMethod = "close")
    @ConfigurationProperties(prefix = "course.read")
    public DataSource readDataSource() {
        return new DruidDataSource();
    }

    // 事务管理
    @Bean(name = "writeOrReadTransactionManager")
    public DataSourceTransactionManager transactionManager(ReadWriteDataSource readWriteDataSource) {
        //Spring 的jdbc事务管理器
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(readWriteDataSource);
        return transactionManager;
    }
}
