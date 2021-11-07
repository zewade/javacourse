package cn.zewade.course.insertdatatest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.SQLException;

/**
 * @author Wade
 * @date 2021-11-07
 * @description
 */
@RestController
public class TestController {
	
	@Resource
	InsertDataTestService service;
	
	@GetMapping("/insertNormalCycle")
	public void insertNormalCycle() throws SQLException {
		service.insertNormalCycle();
	}

	@GetMapping("/insertPrepareStatement")
	public void insertPrepareStatement() throws SQLException {
		service.insertPrepareStatement();
	}

	@GetMapping("/insertBatch")
	public void insertBatch() throws SQLException {
		service.insertBatch();
	}
}
