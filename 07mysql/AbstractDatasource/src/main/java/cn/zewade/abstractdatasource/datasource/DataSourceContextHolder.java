package cn.zewade.abstractdatasource.datasource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataSourceContextHolder {
    //线程本地环境
    private static final ThreadLocal<String> local = new ThreadLocal<String>();

    public static void setRead() {
        local.set(DataSourceType.read.name());
        log.info("数据库切换到读库...");
    }

    public static void setWrite() {
        local.set(DataSourceType.write.name());
        log.info("数据库切换到写库...");
    }

    public static String getReadOrWrite() {
        return local.get();
    }
}
