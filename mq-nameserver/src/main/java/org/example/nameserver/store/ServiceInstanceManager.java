package org.example.nameserver.store;


import lombok.Data;
import org.example.nameserver.util.BrokerIdentifyUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qushutao
 * @since 2025/7/17 15:01
 **/
@Data
public class ServiceInstanceManager {

    Map<String, ServiceInstance> serviceInstanceMap = new HashMap<>();

    public void addServiceInstance(ServiceInstance serviceInstance) {
        serviceInstanceMap.put(BrokerIdentifyUtil.getBrokerIdentify(serviceInstance.getBrokerIp(), serviceInstance.getBrokerPort()), serviceInstance);
    }

    public void addIfPresent(ServiceInstance serviceInstance) {
        ServiceInstance instance = getServiceInstance(serviceInstance.getBrokerIp(), serviceInstance.getBrokerPort());
        if (null != instance && null != instance.getFirstRegisterTime()) {
            serviceInstance.setFirstRegisterTime(instance.getFirstRegisterTime());
        }
        serviceInstanceMap.put(BrokerIdentifyUtil.getBrokerIdentify(serviceInstance.getBrokerIp(), serviceInstance.getBrokerPort()), serviceInstance);
    }

    public ServiceInstance getServiceInstance(String brokerIp, int brokerPort) {
        return serviceInstanceMap.get(BrokerIdentifyUtil.getBrokerIdentify(brokerIp, brokerPort));
    }


    public void removerInstance(String key) {
        serviceInstanceMap.remove(key);
    }
}
