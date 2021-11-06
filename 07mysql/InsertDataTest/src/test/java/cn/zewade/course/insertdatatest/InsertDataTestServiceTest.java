package cn.zewade.course.insertdatatest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InsertDataTestServiceTest {
    @Test
    void genTestData() {
        InsertDataTestService service = new InsertDataTestService();
        System.out.println(service.genTestData(100_0000).size());
    }
}