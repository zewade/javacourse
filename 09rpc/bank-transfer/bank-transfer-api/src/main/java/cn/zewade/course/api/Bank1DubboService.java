package cn.zewade.course.api;

public interface Bank1DubboService {

    boolean subtractAccountBalance(String accountId, double amount);

    boolean addAccountBalance(String accountId, double amount);
}
