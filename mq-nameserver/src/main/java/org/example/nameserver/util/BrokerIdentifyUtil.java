package org.example.nameserver.util;


/**
 * @author qushutao
 * @since 2025/7/17 16:29
 **/
public class BrokerIdentifyUtil {


    public static String getBrokerIdentify(String brokerIp, int brokerPort) {
        return brokerIp + ":" + brokerPort;
    }

    public static String[] getBrokerIdentify(String brokerIdentify) {
        return brokerIdentify.split(":");
    }

}
