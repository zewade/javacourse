package cn.zewade.course.transcation;

import java.io.IOException;
import java.sql.SQLException;

//@SpringBootApplication
public class TranscationApplication {

    public static void main(String[] args) throws SQLException, IOException {
        XAOrderService orderService = new XAOrderService("/META-INF/sharding-databases-tables.yaml");
        orderService.init();
        orderService.insert();
        orderService.cleanup();
    }

}
