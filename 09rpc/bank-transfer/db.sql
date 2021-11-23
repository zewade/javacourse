DROP TABLE IF EXISTS `t_bank_account`;
CREATE TABLE `t_bank_account`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT COMMENT '主键 自增列',
    `account_id`   varchar(20) NOT NULL COMMENT '账户编号',
    `account_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '账户类型：1 人民币账户，2 美元账户',
    `balance`      double NOT NULL COMMENT '客户余额',
    `create_time`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户注册时间',
    `is_validate`  tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据是否有效标识：1有效数据，2 无效数据',
    `update_time`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `t_try_log`;
CREATE TABLE `t_try_log`
(
    `tx_no`       varchar(64) NOT NULL COMMENT '事务id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`tx_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `t_confirm_log`;
CREATE TABLE `t_confirm_log`
(
    `tx_no`       varchar(64) NOT NULL COMMENT '事务id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`tx_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `t_cancel_log`;
CREATE TABLE `t_cancel_log`
(
    `tx_no`       varchar(64) NOT NULL COMMENT '事务id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`tx_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;