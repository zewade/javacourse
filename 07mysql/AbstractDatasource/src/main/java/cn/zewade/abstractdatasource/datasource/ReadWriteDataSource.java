package cn.zewade.abstractdatasource.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/***
 * 读写分离路由规则
 */
@Slf4j
@Component
public class ReadWriteDataSource extends AbstractRoutingDataSource {

    @Autowired
    @Qualifier("writeDataSource")
    private DataSource writeDataSource;

    @Autowired
    @Qualifier("readDataSource")
    private DataSource readDataSource;

    @Override
    protected Object determineCurrentLookupKey() {
        //读写分离逻辑，返回setTargetDataSources中对应的key
        String typeKey = DataSourceContextHolder.getReadOrWrite();
        log.info("使用"+typeKey+"数据库.............");
        return typeKey;
    }

    @Override
    public void afterPropertiesSet() {
        //初始化bean的时候执行，可以针对某个具体的bean进行配置
        //afterPropertiesSet 早于init-method
        //将datasource注入到targetDataSources中，可以为后续路由用到的key
        this.setDefaultTargetDataSource(writeDataSource);
        Map<Object,Object> targetDataSources=new HashMap<Object,Object>();
        targetDataSources.put( DataSourceType.write.name(), writeDataSource);
        targetDataSources.put( DataSourceType.read.name(),  readDataSource);
        this.setTargetDataSources(targetDataSources);
        //执行原有afterPropertiesSet逻辑，
        //即将targetDataSources中的DataSource加载到resolvedDataSources
        super.afterPropertiesSet();
    }
}
