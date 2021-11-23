package cn.zewade.course.bank1.service;

public interface Bank1Service {

    boolean subtractAccountBalance(String accountId, double amount);

    boolean addAccountBalance(String accountId, double amount);
}
