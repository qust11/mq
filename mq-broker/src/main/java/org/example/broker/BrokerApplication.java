package org.example.broker;

import lombok.extern.slf4j.Slf4j;
import org.example.broker.cache.CommonCache;
import org.example.broker.config.ConsumerQueueLoader;
import org.example.broker.config.EagleMqTopicLoader;
import org.example.broker.config.GlobalPropertiesLoader;
import org.example.broker.core.CommitLogAppendHandler;
import org.example.broker.core.consumerqueue.ConsumerQueueAppendHandler;
import org.example.broker.core.consumerqueue.ConsumerQueueMMapFileModeManager;
import org.example.broker.model.EagleMqTopicModel;
import org.example.broker.model.consumer.ConsumerQueueOffsetModel;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    private static ConsumerQueueLoader consumerQueueLoader;
    private static ConsumerQueueAppendHandler consumerQueueAppendHandler;
    private static ConsumerQueueMMapFileModeManager consumerQueueMMapFileModeManager;

    private static void init() {
        // 加载全局配置信息
        globalPropertiesLoader = new GlobalPropertiesLoader();

        // 加载消息队列
        eagleMqTopicLoader = new EagleMqTopicLoader();

        // 初始化消息队列文件 并进行文件映射
        commitLogAppendHandler = new CommitLogAppendHandler();

        consumerQueueAppendHandler = new ConsumerQueueAppendHandler();

        consumerQueueLoader = new ConsumerQueueLoader();

        consumerQueueMMapFileModeManager = new ConsumerQueueMMapFileModeManager();


        List<EagleMqTopicModel> eagleMqTopicModelList = CommonCache.getTopicModelList();
        for (EagleMqTopicModel eagleMqTopicModel : eagleMqTopicModelList) {
            String topicName = eagleMqTopicModel.getTopic();
            commitLogAppendHandler.prepareMMapLoading(topicName);
//            commitLogAppendHandler.appendMessage(topicName, "hello world this is good".getBytes());
//            byte[] bytes = commitLogAppendHandler.readMessage(topicName);
//            log.info("topicName: {}, bytes: {}", topicName, new String(bytes));
            consumerQueueAppendHandler.prepareConsumerQueueMMapLoading(topicName);
        }


    }

    public static void main(String[] args) throws InterruptedException {
//        SpringApplication.run(BrokerApplication.class, args);
        init();
        EagleMqTopicModel topicModel = CommonCache.getTopicModel("order_cancel_topic");
//        List<Thread> threads = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            int finalI = i;
//            Thread thread = new Thread(() -> commitLogAppendHandler.appendMessage(topicModel.getTopic(), ("t-content" + finalI).getBytes()));
//            threads.add(thread);
//        }
//        threads.forEach(Thread::start);
//        threads.forEach(thread -> {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        byte[] bytes = commitLogAppendHandler.readMessage(topicModel.getTopic());
//        for(int i = 0;i<= 50000;i++){
//            commitLogAppendHandler.appendMessage("order_cancel_topic", ("t-content" + i).getBytes());
//        }
//        byte[] bytes = commitLogAppendHandler.readMessage("order_cancel_topic");
//        log.info("bytes: {}", new String(bytes));
    }
}
