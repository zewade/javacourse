package cn.zewade.abstractdatasource.controller;

import cn.zewade.abstractdatasource.ReadWriteTestService;
import cn.zewade.abstractdatasource.datasource.ReadWriteDataSource;
import cn.zewade.abstractdatasource.datasource.annotation.ReadDataSource;
import cn.zewade.abstractdatasource.datasource.annotation.WriteDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@RestController
public class ReadWriteTestController {

    @Resource
    ReadWriteTestService service;

    @GetMapping("/insertData")
    public String insertData() throws SQLException {
        return service.insertData();
    }

    @GetMapping("/queryData")
    public String queryData() throws SQLException {
        return service.queryData();
    }

}
