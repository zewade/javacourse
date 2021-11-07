package cn.zewade.course.insertdatatest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InsertDataTestServiceTest {
    
    @Autowired
    InsertDataTestService service;
    
    @Test
    void genTestData() {
        service = new InsertDataTestService();
        System.out.println(service.genTestData(100_0000).size());
    }
    
    @Test
    void insertNormalCycle() {
        service = new InsertDataTestService();
        try {
            service.insertNormalCycle();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}