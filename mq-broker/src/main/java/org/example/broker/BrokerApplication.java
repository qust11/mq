package org.example.broker;

import org.example.broker.cache.CommonCache;
import org.example.broker.config.EagleMqTopicLoader;
import org.example.broker.config.GlobalProperties;
import org.example.broker.config.GlobalPropertiesLoader;
import org.example.broker.constant.BrokerConstant;
import org.example.broker.core.MessageAppendHandler;
import org.example.broker.model.MqQueueModel;
import org.example.broker.model.TopicModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

/**
 * @author qushutao
 * @since 2025-05-25
 */
@SpringBootApplication
public class BrokerApplication {

    private static GlobalPropertiesLoader globalPropertiesLoader;
    private static EagleMqTopicLoader eagleMqTopicLoader;
    private static MessageAppendHandler messageAppendHandler;

    private static void init() {
        globalPropertiesLoader = new GlobalPropertiesLoader();
        globalPropertiesLoader.loadProperties();

        eagleMqTopicLoader = new EagleMqTopicLoader();
        eagleMqTopicLoader.loadProperties();

         messageAppendHandler = new MessageAppendHandler();

        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String mqHome = globalProperties.getMqHome();

        List<TopicModel> topicModelList = CommonCache.getTopicModelList();

        for (TopicModel topicModel : topicModelList) {
            String topic = topicModel.getTopic();
            List<MqQueueModel> queueInfo = topicModel.getQueueInfo();
            String path = mqHome + BrokerConstant.BASIC_STORE_PATH  + topic + "\\" + "000001";
            messageAppendHandler.preparMMapLoading(path, topic);
            messageAppendHandler.appendMessage(topic, "hello world this is good".getBytes());
            byte[] bytes = messageAppendHandler.readMessage(topic);
            System.out.println(new String(bytes));
        }


    }

    public static void main(String[] args) {
//        SpringApplication.run(BrokerApplication.class, args);
        init();
    }
}
