package cn.zewade.shardingjdbcreadwritesplit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@RestController
public class ReadWriteTestController {

    @Resource
    DataSource dataSource;

    @GetMapping("/insertData")
    public String insertData() throws SQLException {
        Connection conn = dataSource.getConnection();

        // 3.拼写修改的SQL语句,参数采用?占位
        String sql = "insert ids(id) values (?)";
        // 4.调用数据库连接对象con的方法prepareStatement获取SQL语句的预编译对象
        PreparedStatement pst = conn.prepareStatement(sql);
        // 5.调用pst的方法setXXX设置?占位
        pst.setObject(1, UUID.randomUUID().toString());
        // 6.调用pst方法执行SQL语句
        pst.execute();
        // 7.关闭资源
        pst.close();
        conn.close();
        return "OK";
    }

    @GetMapping("/queryData")
    public String queryData() throws SQLException {
        Connection conn = dataSource.getConnection();

        // 3.拼写修改的SQL语句,参数采用?占位
        String sql = "select * from ids";
        // 4.调用数据库连接对象con的方法prepareStatement获取SQL语句的预编译对象
        PreparedStatement pst = conn.prepareStatement(sql);
        // 6.调用pst方法执行SQL语句
        ResultSet rs = pst.executeQuery();
        StringBuilder stringBuilder = new StringBuilder();
        while(rs.next()){
            stringBuilder.append(rs.getString(1)  + "<br>");
        }
        // 7.关闭资源
        rs.close();
        pst.close();
        conn.close();
        return stringBuilder.toString();
    }
}
