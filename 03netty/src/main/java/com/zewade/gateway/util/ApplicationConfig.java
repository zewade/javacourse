package com.zewade.gateway.util;


import org.springframework.util.StringUtils;

import java.util.ResourceBundle;

public class ApplicationConfig {
    /**
     * properties配置文件名
     */
    private final static String fileName = "application";

    public static String getValueByKey(String key) {
        try {
            String ve = System.getProperty(key);
            if (StringUtils.isEmpty(ve)) {
                ResourceBundle rb = ResourceBundle.getBundle(fileName);
                String value = rb.getString(key);
                if (value == null || value.equals("")) {
                    return "";
                } else {
                    return value;
                }
            } else {
                return ve;
            }

        } catch (Exception e) {
            return "";
        }
    }
}
