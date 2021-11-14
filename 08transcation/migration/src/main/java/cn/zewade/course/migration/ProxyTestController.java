package cn.zewade.course.migration;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
public class ProxyTestController {

    @Resource
    private DataSource dataSource;

    private static final String ORDER_ID = "ORDER_ID";
    private static final String USER_ID = "USER_ID";
    private static final String ADDRESS_ID = "ADDRESS_ID";
    private static final String COUPON_ID = "COUPON_ID";
    private static final String TOTAL_PRICE = "TOTAL_PRICE";
    private static final String COUPON_PRICE = "COUPON_PRICE";
    private static final String PAYABLE_PRICE = "PAYABLE_PRICE";
    private static final String PAY_METHOD = "PAY_METHOD";
    private static final String INVOICE_TPL_ID = "INVOICE_TPL_ID";
    private static final String LEAVE_COMMENT = "LEAVE_COMMENT";
    private static final String ORDER_STATUS = "ORDER_STATUS";
    private static final String CREATED_BY = "CREATED_BY";
    private static final String CREATED_TIME = "CREATED_TIME";
    private static final String UPDATED_BY = "UPDATED_BY";
    private static final String UPDATED_TIME = "UPDATED_TIME";

    @GetMapping("/insert")
    public String insert() {
        long start = System.nanoTime();
        String sql = "insert buyer_order values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        for (Map<String, Object> data : genTestData(10)) {
            jdbcTemplate.update(sql, data.get(ORDER_ID), data.get(USER_ID),
                    data.get(ADDRESS_ID), data.get(COUPON_ID), data.get(TOTAL_PRICE),
                    data.get(COUPON_PRICE), data.get(PAYABLE_PRICE), data.get(PAY_METHOD),
                    data.get(INVOICE_TPL_ID), data.get(LEAVE_COMMENT), data.get(ORDER_STATUS),
                    data.get(CREATED_BY), data.get(CREATED_TIME), data.get(CREATED_BY), data.get(CREATED_TIME));
        }
        long end = System.nanoTime();
        System.out.println("Cost:" + (end - start) / 1000_000.0);
        return "Success";
    }

    @GetMapping("/update")
    public String update(@RequestParam Long orderId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "update buyer_order set updated_time ='" + simpleDateFormat.format(new Date()) + "' where" +
                " order_id = " +orderId;
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int result = jdbcTemplate.update(sql);
        return "Result:" + result;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam Long orderId) {
        String sql = "delete from buyer_order where order_id =" + orderId;
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int result = jdbcTemplate.update(sql);
        return "Result:" + result;
    }

    @GetMapping("/query")
    public String query() {
        String sql = "select * from buyer_order order by order_id desc limit 1";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Map<String, Object> result = jdbcTemplate.queryForMap(sql);
        if (result.isEmpty()) {
            return "No Data!";
        } else {
            return result.toString();
        }
    }

    public List<Map<String, Object>> genTestData(int size) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return IntStream.rangeClosed(1, size).mapToObj(i -> {
            Map<String, Object> testData = new HashMap<>();
            testData.put("ORDER_ID", SnowflakeId.getId());
            testData.put("USER_ID", SnowflakeId.getId());
            testData.put("ADDRESS_ID", SnowflakeId.getId());
            testData.put("COUPON_ID", UUID.randomUUID().toString());
            testData.put("TOTAL_PRICE", ((Double)Math.random()).floatValue() * 1000);
            testData.put("COUPON_PRICE", 10f);
            testData.put("PAYABLE_PRICE", Double.parseDouble(testData.get("TOTAL_PRICE").toString()) - 10);
            testData.put("PAY_METHOD", i % 10);
            testData.put("INVOICE_TPL_ID", UUID.randomUUID().toString());
            testData.put("LEAVE_COMMENT", UUID.randomUUID().toString());
            testData.put("ORDER_STATUS", i % 5);
            Calendar calendar = Calendar.getInstance();
            calendar.set(2021, 1, 1);
            calendar.add(Calendar.DAY_OF_MONTH, (int) (Math.random() * 365));
            testData.put("CREATED_TIME", sf.format(calendar.getTime()));
            testData.put("CREATED_BY", UUID.randomUUID().toString());
            return testData;
        }).collect(Collectors.toList());
    }
}
