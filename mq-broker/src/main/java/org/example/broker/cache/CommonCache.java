package org.example.broker.cache;

import org.example.broker.config.GlobalProperties;
import org.example.broker.model.EagleMqTopicModel;

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

    public static List<EagleMqTopicModel> TOPIC_MODEL = new ArrayList<>();

    public static Map<String, EagleMqTopicModel> TOPIC_MODEL_MAP = new HashMap<>();

    public static void setGlobalProperties(GlobalProperties globalProperties) {
        CommonCache.GLOBAL_PROPERTIES = globalProperties;
    }

    public static GlobalProperties getGlobalProperties() {
        return CommonCache.GLOBAL_PROPERTIES;

    }

    public static void setTopicModelList(List<EagleMqTopicModel> eagleMqTopicModel) {
        CommonCache.TOPIC_MODEL = eagleMqTopicModel;
    }

    public static List<EagleMqTopicModel> getTopicModelList() {
        return CommonCache.TOPIC_MODEL;
    }

    public static void setTopicModelMap(Map<String, EagleMqTopicModel> topicModelMap) {
        CommonCache.TOPIC_MODEL_MAP = topicModelMap;
    }

    public static EagleMqTopicModel getTopicModel(String topicName) {
        return CommonCache.TOPIC_MODEL_MAP.get(topicName);
    }
}
