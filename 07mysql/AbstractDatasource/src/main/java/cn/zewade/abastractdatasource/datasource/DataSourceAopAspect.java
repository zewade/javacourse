package cn.zewade.abastractdatasource.datasource;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.PriorityOrdered;

@Aspect
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
public class DataSourceAopAspect implements PriorityOrdered {

    @Before("execution(* cn.zewade.abastractdatasource.datasource.service.*.*(..)) "
            + " && @annotation(cn.zewade.abastractdatasource.datasource.annotation.ReadDataSource) ")
    public void setReadDataSourceType() {
        //如果已经开启写事务了，那之后的所有读都从写库读
        DataSourceContextHolder.setRead();
    }
    @Before("execution(* cn.zewade.abastractdatasource.datasource.service.*.*(..)) "
            + " && @annotation(cn.zewade.abastractdatasource.datasource.annotation.WriteDataSource) ")
    public void setWriteDataSourceType() {
        DataSourceContextHolder.setWrite();
    }
    @Override
    public int getOrder() {
        /**
         * 值越小，越优先执行 要优于事务的执行
         * 在启动类中加上了@EnableTransactionManagement(order = 10)
         */
        return 1;
    }
}
