package org.example.nameserver.core;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.example.common.constant.NameServerConstant;
import org.example.nameserver.commmon.CommonCache;
import org.example.nameserver.store.ServiceInstance;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author qushutao
 * @since 2025/7/17 22:02
 **/
@Slf4j
public class InValidServiceRemoveTask implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(NameServerConstant.HEART_BEAT_TIME);
                Map<String, ServiceInstance> serviceInstanceMap = CommonCache.getServiceInstanceManager().getServiceInstanceMap();
                if (MapUtils.isEmpty(serviceInstanceMap)) {
                    continue;
                }
                Iterator<String> iterator = serviceInstanceMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String reqId = iterator.next();
                    ServiceInstance serviceInstance = serviceInstanceMap.get(reqId);

                    if (null == serviceInstance.getLastHeartBeatTime()) {
                        continue;
                    }
                    if (serviceInstance.getLastHeartBeatTime() + NameServerConstant.HEART_BEAT_TIME * 1000 < System.currentTimeMillis()) {
                        iterator.remove();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("线程被中断",e);
                throw new RuntimeException(e);
            }
        }

    }
}
