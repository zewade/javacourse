package cn.zewade.course.bank1.dubbo;

import cn.zewade.course.api.Bank1DubboService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "1.0.0")
public class Bank1DubboServiceImpl implements Bank1DubboService {
    @Override
    public boolean subtractAccountBalance(String accountId, double amount) {
        return false;
    }

    @Override
    public boolean addAccountBalance(String accountId, double amount) {
        return false;
    }
}
