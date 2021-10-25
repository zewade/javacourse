package cn.zewade.course.homeworkspring10;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RestController("/")
public class CourseController {

    @Autowired
    DataSource dataSource;

    @GetMapping("/insertStudent")
    public String insertStudent(int id, String name) throws SQLException {
        Connection conn = dataSource.getConnection();

        // 3.拼写修改的SQL语句,参数采用?占位
        String sql = "insert student values (?,?)";
        // 4.调用数据库连接对象con的方法prepareStatement获取SQL语句的预编译对象
        PreparedStatement pst = conn.prepareStatement(sql);
        // 5.调用pst的方法setXXX设置?占位
        pst.setObject(1, id);
        pst.setObject(2, name);
        // 6.调用pst方法执行SQL语句
        pst.execute();
        // 7.关闭资源
        pst.close();
        conn.close();
        return "OK";
    }

    @GetMapping("/updateStudent")
    public String updateStudent(int id, String name) throws SQLException {
        Connection conn = dataSource.getConnection();

        // 3.拼写修改的SQL语句,参数采用?占位
        String sql = "update student set name=? where id = ?";
        // 4.调用数据库连接对象con的方法prepareStatement获取SQL语句的预编译对象
        PreparedStatement pst = conn.prepareStatement(sql);
        // 5.调用pst的方法setXXX设置?占位
        pst.setObject(1, name);
        pst.setObject(2, id);
        // 6.调用pst方法执行SQL语句
        pst.executeUpdate();
        // 7.关闭资源
        pst.close();
        conn.close();
        return "OK";
    }

    @GetMapping("/deleteStudent")
    public String deleteStudent(int id) throws SQLException {
        Connection conn = dataSource.getConnection();

        // 3.拼写修改的SQL语句,参数采用?占位
        String sql = "delete from student where id=? ";
        // 4.调用数据库连接对象con的方法prepareStatement获取SQL语句的预编译对象
        PreparedStatement pst = conn.prepareStatement(sql);
        // 5.调用pst的方法setXXX设置?占位
        pst.setObject(1, id);
        // 6.调用pst方法执行SQL语句
        pst.execute();
        // 7.关闭资源
        pst.close();
        conn.close();
        return "OK";
    }

    @GetMapping("/queryStudent")
    public String queryStudent() throws SQLException {
        Connection conn = dataSource.getConnection();

        // 3.拼写修改的SQL语句,参数采用?占位
        String sql = "select id, name from student";
        // 4.调用数据库连接对象con的方法prepareStatement获取SQL语句的预编译对象
        PreparedStatement pst = conn.prepareStatement(sql);
        // 5.调用pst方法执行SQL语句
        ResultSet rs = pst.executeQuery();
        StringBuilder stringBuilder = new StringBuilder();
        while(rs.next()){
            stringBuilder.append(rs.getInt(1) + "," + rs.getString(2) + "\r\n");
        }
        // 6.调用pst方法执行SQL语句
        pst.executeQuery();
        // 7.关闭资源
        pst.close();
        conn.close();
        return stringBuilder.toString();
    }
}
