package cn.zewade.course.insertdatatest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class InsertDataTestService {
    @Autowired
    DataSource dataSource;
    
    private List<Map<String, Object>> testDataMap = genTestData(1000);
    
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
    
    /**
     * 测试静态SQL 插入100W条记录约为20min
     * @throws SQLException
     */
    public void insertNormalCycle() throws SQLException {
        long start = System.nanoTime();
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            for (Map<String, Object> data : testDataMap) {
                String sql = String.format("insert buyer_order values (%d,%d,%d,\"%s\",%.2f,%.2f,%.2f,%d,\"%s\",\"%s\",%d,\"%s\",\"%s\",\"%s\",\"%s\")",
                        data.get(ORDER_ID), data.get(USER_ID), data.get(ADDRESS_ID), data.get(COUPON_ID), data.get(TOTAL_PRICE),
                        data.get(COUPON_PRICE), data.get(PAYABLE_PRICE), data.get(PAY_METHOD), data.get(INVOICE_TPL_ID), data.get(LEAVE_COMMENT),
                        data.get(ORDER_STATUS), data.get(CREATED_BY), data.get(CREATED_TIME), data.get(CREATED_BY), data.get(CREATED_TIME));
                st.executeUpdate(sql);
            }
        } finally {
            if (st != null)
                st.close();
            if (conn != null)
                conn.close();
        }
        long end = System.nanoTime();
        System.out.println("Cost:" + (end - start) / 1000_000.0);
    }

    /**
     * 预编译SQL 插入100W条记录约为20min
     * @throws SQLException
     */
    public void insertPrepareStatement() throws SQLException {
        long start = System.nanoTime();
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = dataSource.getConnection();
            String sql = "insert buyer_order values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(sql);
            for (Map<String, Object> data : testDataMap) {
                pst.setObject(1, data.get(ORDER_ID));
                pst.setObject(2, data.get(USER_ID));
                pst.setObject(3, data.get(ADDRESS_ID));
                pst.setObject(4, data.get(COUPON_ID));
                pst.setObject(5, data.get(TOTAL_PRICE));
                pst.setObject(6, data.get(COUPON_PRICE));
                pst.setObject(7, data.get(PAYABLE_PRICE));
                pst.setObject(8, data.get(PAY_METHOD));
                pst.setObject(9, data.get(INVOICE_TPL_ID));
                pst.setObject(10, data.get(LEAVE_COMMENT));
                pst.setObject(11, data.get(ORDER_STATUS));
                pst.setObject(12, data.get(CREATED_BY));
                pst.setObject(13, data.get(CREATED_TIME));
                pst.setObject(14, data.get(CREATED_BY));
                pst.setObject(15, data.get(CREATED_TIME));
                pst.execute();
            }
        } finally {
            if (pst != null)
                pst.close();
            if (conn != null)
                conn.close();
        }
        long end = System.nanoTime();
        System.out.println("Cost:" + (end - start) / 1000_000.0);
    }

    /**
     * 批量插入 插入100W条记录约为10s
     * @throws SQLException
     */
    public void insertBatch() throws SQLException {
        long start = System.nanoTime();
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            String sql = "insert buyer_order values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(sql);
            for (int i=0;i<1000;i++) {
                List<Map<String, Object>> testData = genTestData(10000);
                for (Map<String, Object> data : testData) {
                    pst.setObject(1, data.get(ORDER_ID));
                    pst.setObject(2, data.get(USER_ID));
                    pst.setObject(3, data.get(ADDRESS_ID));
                    pst.setObject(4, data.get(COUPON_ID));
                    pst.setObject(5, data.get(TOTAL_PRICE));
                    pst.setObject(6, data.get(COUPON_PRICE));
                    pst.setObject(7, data.get(PAYABLE_PRICE));
                    pst.setObject(8, data.get(PAY_METHOD));
                    pst.setObject(9, data.get(INVOICE_TPL_ID));
                    pst.setObject(10, data.get(LEAVE_COMMENT));
                    pst.setObject(11, data.get(ORDER_STATUS));
                    pst.setObject(12, data.get(CREATED_BY));
                    pst.setObject(13, data.get(CREATED_TIME));
                    pst.setObject(14, data.get(CREATED_BY));
                    pst.setObject(15, data.get(CREATED_TIME));
                    pst.addBatch();
                }
                pst.executeBatch();
                conn.commit();
                pst.clearBatch();
                System.out.println("Finished:" + (i+1));
            }
//            conn.commit();
        } finally {
            if (pst != null)
                pst.close();
            if (conn != null)
                conn.close();
        }
        long end = System.nanoTime();
        System.out.println("Cost:" + (end - start) / 1000_000.0);
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
