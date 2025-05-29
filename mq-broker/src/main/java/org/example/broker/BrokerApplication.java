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

import java.util.List;

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

        for (int i = 0;i < 100;i++){
            commitLogAppendHandler.appendMessage(topicId, ("t-content" + i).getBytes());
//            consumeQueueConsumeHandler.consume(topicId, consumeGroup, 0);
//            consumeQueueConsumeHandler.ack(topicId, consumeGroup, 0);
        }
//        EagleMqTopicModel topicModel = CommonCache.getTopicModel();
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
