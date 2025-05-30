package org.example.broker;

import lombok.extern.slf4j.Slf4j;
import org.example.broker.cache.CommonCache;
import org.example.broker.config.ConsumeQueueLoader;
import org.example.broker.config.EagleMqTopicLoader;
import org.example.broker.config.GlobalPropertiesLoader;
import org.example.broker.core.CommitLogAppendHandler;
import org.example.broker.core.CommitLogMMapFileModeManager;
import org.example.broker.core.consumequeue.ConsumeQueueAppendHandler;
import org.example.broker.core.consumequeue.ConsumeQueueConsumeHandler;
import org.example.broker.core.consumequeue.ConsumeQueueMMapFileModeManager;
import org.example.broker.model.EagleMqTopicModel;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * @author qushutao
 * @since 2025-05-25
 */
@Slf4j
@SpringBootApplication
public class BrokerApplication {

    private static GlobalPropertiesLoader globalPropertiesLoader;
    private static EagleMqTopicLoader eagleMqTopicLoader;
    private static CommitLogAppendHandler commitLogAppendHandler;
    private static ConsumeQueueLoader consumeQueueLoader;
    private static ConsumeQueueAppendHandler consumeQueueAppendHandler;
    private static ConsumeQueueMMapFileModeManager consumeQueueMMapFileModeManager;
    private static ConsumeQueueConsumeHandler consumeQueueConsumeHandler;
    private static CommitLogMMapFileModeManager commitLogMMapFileModeManager;

    private static void init() {
        // 加载全局配置信息
        globalPropertiesLoader = new GlobalPropertiesLoader();
        // 加载消息队列
        eagleMqTopicLoader = new EagleMqTopicLoader();
        // 初始化消息队列文件 并进行文件映射
        commitLogAppendHandler = new CommitLogAppendHandler();
        consumeQueueAppendHandler = new ConsumeQueueAppendHandler();
        // 加载消费配置
        consumeQueueLoader = new ConsumeQueueLoader();
        consumeQueueMMapFileModeManager = new ConsumeQueueMMapFileModeManager();
        consumeQueueConsumeHandler = new ConsumeQueueConsumeHandler();
        commitLogMMapFileModeManager = new CommitLogMMapFileModeManager();


        List<EagleMqTopicModel> eagleMqTopicModelList = CommonCache.getTopicModelList();
        for (EagleMqTopicModel eagleMqTopicModel : eagleMqTopicModelList) {
            String topicName = eagleMqTopicModel.getTopic();
            commitLogAppendHandler.prepareMMapLoading(topicName);
            consumeQueueAppendHandler.prepareConsumeQueueMMapLoading(topicName);
        }
    }

    public static void main(String[] args) throws InterruptedException {
//        SpringApplication.run(BrokerApplication.class, args);
        init();
        String topicId = "order_cancel_topic";
        String consumeGroup = "order_cancel_consume_group";

        // 一个写多个读取 看看会不会有并发问题
//       new Thread(()->{
//            for (int i = 0; i < 10000; i++) {
//                commitLogAppendHandler.appendMessage(topicId, ("this is test content" + i).getBytes());
//            }
//        }).start();

       List<Thread> consumerList = new ArrayList<>();
       for (int i = 0; i < 2; i++) {
           Thread thread = new Thread(() -> {
               while (true){
                   byte[] bytes = consumeQueueConsumeHandler.consume(topicId, consumeGroup, 0);
                   log.info("********************consume******************** ,Thread name = {}, content: {}",Thread.currentThread().getName(), new String(bytes));
                   consumeQueueConsumeHandler.ack(topicId, consumeGroup, 0);
                   try {
                       Thread.sleep(100);
                   } catch (InterruptedException e) {
                       throw new RuntimeException(e);
                   }
               }
           });
           consumerList.add(thread);
       }
       consumerList.forEach(Thread::start);
       consumerList.forEach(thread -> {
           try {
               thread.join();
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
       });

    }
}
