package cn.zewade.course.insertdatatest;

import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InsertDataTestService {
    @Autowired
    DataSource dataSource;

    public List<Map<String, Object>> genTestData(int size) {
        return IntStream.rangeClosed(1, size).mapToObj(i -> {
            Map<String, Object> testData = new HashMap<>();
            testData.put("order_id", SnowflakeId.getId());
            testData.put("user_id", SnowflakeId.getId());
            testData.put("address_id", SnowflakeId.getId());
            testData.put("coupon_id", SnowflakeId.getId());
            testData.put("total_price", Math.random() * 1000);
            testData.put("coupon_price", 10);
            testData.put("payable_price", Double.parseDouble(testData.get("total_price").toString()) - 10);
            testData.put("pay_method", i % 10);
            testData.put("invoice_tpl_id", SnowflakeId.getId());
            testData.put("leave_comment", UUID.randomUUID().toString());
            testData.put("order_status", i % 5);
            Calendar calendar = Calendar.getInstance();
            calendar.set(2021, 1, 1);
            calendar.add(Calendar.DAY_OF_MONTH, (int) (Math.random() * 365));
            testData.put("create_time", calendar.getTime());
            testData.put("create_by", SnowflakeId.getId());
            return testData;
        }).collect(Collectors.toList());
    }
}
