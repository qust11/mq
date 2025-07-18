package org.example.nameserver.store;


import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qushutao
 * @since 2025/7/17 15:01
 **/
@Data
public class ServiceInstance {

    private String brokerIp;

    private int brokerPort;

    private Long lastHeartBeatTime;

    private Long firstRegisterTime;

    private Map<String,String> attrs = new HashMap<>();
}
