package cn.zewade.course.spring.beans1;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Data
public class School {
    private String name;
    @Autowired
    private Address address;

    public School(Address address) {
        this.address = address;
    }
}
