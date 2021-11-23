package cn.zewade.course.bank1.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BankAccount {
    private int id;
    private String accountId;
    private int accountType;
    private double balance;
    private Date createTime;
    private int isValidate;
    private Date updateTime;
}
