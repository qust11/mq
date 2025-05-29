package org.example.broker.cache;

import org.example.broker.config.GlobalProperties;
import org.example.broker.core.consumerqueue.ConsumerQueueMMapFileModeManager;
import org.example.broker.model.EagleMqTopicModel;
import org.example.broker.model.consumer.ConsumerQueueOffsetModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qushutao
 * @since 2025-05-25
 */
public class CommonCache {

    public static GlobalProperties GLOBAL_PROPERTIES = new GlobalProperties();
    public static Map<String, EagleMqTopicModel> TOPIC_MODEL_MAP = new HashMap<>();
    public static ConsumerQueueOffsetModel CONSUMER_QUEUE_OFFSET_MODEL = new ConsumerQueueOffsetModel();
    public static ConsumerQueueMMapFileModeManager CONSUMER_QUEUE_MMAP_FILE_MODE_MANAGER = new ConsumerQueueMMapFileModeManager();

    public static void setGlobalProperties(GlobalProperties globalProperties) {
        CommonCache.GLOBAL_PROPERTIES = globalProperties;
    }

    public static GlobalProperties getGlobalProperties() {
        return CommonCache.GLOBAL_PROPERTIES;

    }

//    public static void setTopicModelList(List<EagleMqTopicModel> eagleMqTopicModel) {
//        CommonCache.TOPIC_MODEL = eagleMqTopicModel;
//    }

    public static List<EagleMqTopicModel> getTopicModelList() {
        List<EagleMqTopicModel> eagleMqTopicModel = new ArrayList<>();
        CommonCache.TOPIC_MODEL_MAP.forEach((k, v) -> {
            eagleMqTopicModel.add(v);
        });
        return eagleMqTopicModel;
    }

    public static void setTopicModelMap(Map<String, EagleMqTopicModel> topicModelMap) {
        CommonCache.TOPIC_MODEL_MAP = topicModelMap;
    }

    public static EagleMqTopicModel getTopicModel(String topicName) {
        return CommonCache.TOPIC_MODEL_MAP.get(topicName);
    }

    public static ConsumerQueueOffsetModel getConsumerQueueOffsetModel() {
        return CommonCache.CONSUMER_QUEUE_OFFSET_MODEL;
    }


    public static void setConsumerQueueOffsetModel(ConsumerQueueOffsetModel consumerQueueOffsetModel) {
        CommonCache.CONSUMER_QUEUE_OFFSET_MODEL = consumerQueueOffsetModel;
    }

    public static ConsumerQueueMMapFileModeManager getConsumerQueueMMapFileModeManager() {
        return CommonCache.CONSUMER_QUEUE_MMAP_FILE_MODE_MANAGER;
    }

    public static void setConsumerQueueMMapFileModeManager(ConsumerQueueMMapFileModeManager consumerQueueMMapFileModeManager) {
        CommonCache.CONSUMER_QUEUE_MMAP_FILE_MODE_MANAGER = consumerQueueMMapFileModeManager;
    }
}
