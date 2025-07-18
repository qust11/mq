package org.example.broker.config;

import io.micrometer.common.util.StringUtils;
import org.example.broker.cache.CommonCache;
import org.example.common.constant.BrokerConstant;

/**
 * @author qushutao
 * @since 2025-05-25
 */
public class GlobalPropertiesLoader {

    public GlobalPropertiesLoader() {
        loadProperties();
    }

    public void loadProperties() {
        GlobalProperties globalProperties = new GlobalProperties();
//        String maHome = System.getenv(BrokerConstant.MQ_HOME);
        String maHome = BrokerConstant.MQ_HOME;
        if (StringUtils.isBlank(maHome)){
            throw new IllegalStateException("MQ HOME is empty");
        }
        globalProperties = new GlobalProperties(maHome);
        CommonCache.setGlobalProperties(globalProperties);
    }
}
