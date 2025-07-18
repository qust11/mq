package org.example.nameserver.commmon;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.nameserver.core.PropertiesLoad;
import org.example.nameserver.store.ServiceInstanceManager;

/**
 * @author qushutao
 * @since 2025/7/17 10:09
 **/
public class CommonCache {

    @Setter
    @Getter
    private static PropertiesLoad propertiesLoad;

    @Setter
    @Getter
    private static ServiceInstanceManager serviceInstanceManager = new ServiceInstanceManager();


}
