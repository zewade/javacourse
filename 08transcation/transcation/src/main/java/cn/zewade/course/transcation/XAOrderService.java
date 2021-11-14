/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.zewade.course.transcation;

import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Order service.
 *
 */
class XAOrderService {
    
    private final DataSource dataSource;

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
    
    XAOrderService(final String yamlConfigFile) throws IOException, SQLException {
        dataSource = YamlShardingSphereDataSourceFactory.createDataSource(getFile(yamlConfigFile));
    }
    
    private File getFile(final String fileName) {
        return new File(XAOrderService.class.getResource(fileName).getFile());
    }
    
    /**
     * Init.
     */
    void init() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS buyer_order");
            statement.execute("CREATE TABLE BUYER_ORDER\n" +
                    "(\n" +
                    "    ORDER_ID       BIGINT NOT NULL COMMENT '订单ID',\n" +
                    "    USER_ID        BIGINT NOT NULL COMMENT '用户ID',\n" +
                    "    ADDRESS_ID     BIGINT COMMENT '地址ID',\n" +
                    "    COUPON_ID      VARCHAR(50) COMMENT '优惠券ID',\n" +
                    "    TOTAL_PRICE    DECIMAL(24, 6) COMMENT '总金额',\n" +
                    "    COUPON_PRICE   DECIMAL(24, 6) COMMENT '优惠金额',\n" +
                    "    PAYABLE_PRICE  DECIMAL(24, 6) COMMENT '应付金额',\n" +
                    "    PAY_METHOD     VARCHAR(32) COMMENT '支付方式',\n" +
                    "    INVOICE_TPL_ID VARCHAR(50) COMMENT '开票设置ID',\n" +
                    "    LEAVE_COMMENT  VARCHAR(1000) COMMENT '订单留言备注',\n" +
                    "    ORDER_STATUS   VARCHAR(32) COMMENT '订单状态',\n" +
                    "    CREATED_BY     VARCHAR(50) COMMENT '创建人',\n" +
                    "    CREATED_TIME   DATETIME COMMENT '创建时间',\n" +
                    "    UPDATED_BY     VARCHAR(50) COMMENT '更新人',\n" +
                    "    UPDATED_TIME   DATETIME COMMENT '更新时间',\n" +
                    "    PRIMARY KEY (ORDER_ID)\n" +
                    ") COMMENT = '订单';");
        }
    }
    
    /**
     * Clean up.
     */
    void cleanup() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS buyer_order");
        }
    }
    
    /**
     * Execute XA.
     *
     * @throws SQLException SQL exception
     */
    void insert() throws SQLException {
        TransactionTypeHolder.set(TransactionType.XA);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("insert into buyer_order values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            doInsert(preparedStatement);
            connection.commit();
        } finally {
            TransactionTypeHolder.clear();
        }
    }
    
    /**
     * Execute XA with exception.
     *
     * @throws SQLException SQL exception
     */
    void insertFailed() throws SQLException {
        TransactionTypeHolder.set(TransactionType.XA);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("insert into buyer_order values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            doInsert(preparedStatement);
            connection.rollback();
        } finally {
            TransactionTypeHolder.clear();
        }
    }
    
    private void doInsert(final PreparedStatement preparedStatement) throws SQLException {
        List<Map<String, Object>> testData = genTestData(10);
        for (Map<String, Object> data : testData) {
            preparedStatement.setObject(1, data.get(ORDER_ID));
            preparedStatement.setObject(2, data.get(USER_ID));
            preparedStatement.setObject(3, data.get(ADDRESS_ID));
            preparedStatement.setObject(4, data.get(COUPON_ID));
            preparedStatement.setObject(5, data.get(TOTAL_PRICE));
            preparedStatement.setObject(6, data.get(COUPON_PRICE));
            preparedStatement.setObject(7, data.get(PAYABLE_PRICE));
            preparedStatement.setObject(8, data.get(PAY_METHOD));
            preparedStatement.setObject(9, data.get(INVOICE_TPL_ID));
            preparedStatement.setObject(10, data.get(LEAVE_COMMENT));
            preparedStatement.setObject(11, data.get(ORDER_STATUS));
            preparedStatement.setObject(12, data.get(CREATED_BY));
            preparedStatement.setObject(13, data.get(CREATED_TIME));
            preparedStatement.setObject(14, data.get(CREATED_BY));
            preparedStatement.setObject(15, data.get(CREATED_TIME));
            preparedStatement.executeUpdate();
        }
    }
    
    /**
     * Select all.
     *
     * @return record count
     */
    int selectAll() throws SQLException {
        int result = 0;
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeQuery("SELECT COUNT(1) AS count FROM buyer_order");
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        }
        return result;
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
