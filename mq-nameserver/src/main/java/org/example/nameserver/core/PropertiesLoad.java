package org.example.nameserver.core;


import org.example.common.constant.BrokerConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author qushutao
 * @since 2025/7/17 10:01
 **/
public class PropertiesLoad {

    private Properties properties = new Properties();

    public PropertiesLoad(){
        init();
    }

    private void init(){
        try {
            properties.load(new FileInputStream(BrokerConstant.MQ_HOME + "\\broker\\config"+"\\nameserver.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String get(String key){
        return properties.getProperty(key);
    }

    public static void main(String[] args) {
        PropertiesLoad propertiesLoad = new PropertiesLoad();
        try {
            propertiesLoad.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(propertiesLoad.get("username"));
    }
}
