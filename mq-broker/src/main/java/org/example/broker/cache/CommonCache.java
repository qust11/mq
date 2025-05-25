package org.example.broker.cache;

import org.example.broker.config.GlobalProperties;
import org.example.broker.model.TopicModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qushutao
 * @since 2025-05-25
 */
public class CommonCache {

    public static GlobalProperties GLOBAL_PROPERTIES = new GlobalProperties();

    public static List<TopicModel> TOPIC_MODEL = new ArrayList<>();

    public static void setGlobalProperties(GlobalProperties globalProperties) {
        CommonCache.GLOBAL_PROPERTIES = globalProperties;
    }

    public static GlobalProperties getGlobalProperties() {
        return CommonCache.GLOBAL_PROPERTIES;

    }
    public static void setTopicModelList(List<TopicModel> topicModel) {
        CommonCache.TOPIC_MODEL = topicModel;
    }

    public static List<TopicModel> getTopicModelList() {
        return CommonCache.TOPIC_MODEL;

    }
}
