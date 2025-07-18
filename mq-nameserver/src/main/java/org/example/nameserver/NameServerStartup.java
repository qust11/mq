package org.example.nameserver;


import org.example.nameserver.commmon.CommonCache;
import org.example.nameserver.core.InValidServiceRemoveTask;
import org.example.nameserver.core.NameServerStarter;
import org.example.nameserver.core.PropertiesLoad;
import org.example.nameserver.store.ServiceInstanceManager;

/**
 * @author qushutao
 * @since 2025/7/14 9:33
 **/
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class NameServerStartup {
    public static NameServerStarter nameServerStarter;

    public static void main(String[] args) throws InterruptedException {
        nameServerStarter = new NameServerStarter(9876);
        // 加载配置信息
        PropertiesLoad propertiesLoad = new PropertiesLoad();
        CommonCache.setPropertiesLoad(propertiesLoad);

        // 实例管理器
        ServiceInstanceManager serviceInstanceManager = new ServiceInstanceManager();
        CommonCache.setServiceInstanceManager(serviceInstanceManager);

        // 启动定时任务 定时扫描心跳异常的broker
        InValidServiceRemoveTask inValidServiceRemoveTask = new InValidServiceRemoveTask();
        new Thread(inValidServiceRemoveTask).start();

        nameServerStarter.start();
    }
}