package org.example.broker;

import lombok.extern.slf4j.Slf4j;
import org.example.broker.cache.CommonCache;
import org.example.broker.config.EagleMqTopicLoader;
import org.example.broker.config.GlobalPropertiesLoader;
import org.example.broker.core.CommitLogAppendHandler;
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

    private static void init() {
        globalPropertiesLoader = new GlobalPropertiesLoader();
        globalPropertiesLoader.loadProperties();

        eagleMqTopicLoader = new EagleMqTopicLoader();
        eagleMqTopicLoader.loadProperties();

        commitLogAppendHandler = new CommitLogAppendHandler();


        List<EagleMqTopicModel> eagleMqTopicModelList = CommonCache.getTopicModelList();

        for (EagleMqTopicModel eagleMqTopicModel : eagleMqTopicModelList) {
            String topicName = eagleMqTopicModel.getTopic();
            commitLogAppendHandler.prepareMMapLoading(topicName);
            commitLogAppendHandler.appendMessage(topicName, "hello world this is good".getBytes());
            byte[] bytes = commitLogAppendHandler.readMessage(topicName);
            log.info("topicName: {}, bytes: {}", topicName, new String(bytes));
        }


    }

    public static void main(String[] args) {
//        SpringApplication.run(BrokerApplication.class, args);
        init();
    }
}
