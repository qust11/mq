package org.example.broker.config;

import io.micrometer.common.util.StringUtils;
import org.example.broker.cache.CommonCache;
import org.example.broker.model.TopicModel;
import org.example.broker.util.FileContentReaderUtil;

import java.util.List;

/**
 * @author qushutao
 * @since 2025-05-25
 */
public class EagleMqTopicLoader {
    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String mqHome = globalProperties.getMqHome();
        if (StringUtils.isBlank(mqHome)) {
            throw new IllegalStateException("MQ HOME is empty");
        }
        String jsonPaht = mqHome + "/broker/config/mq_topic.json";

        List<TopicModel> topicModel = FileContentReaderUtil.readTopicModel(jsonPaht);

        CommonCache.setTopicModelList(topicModel);

    }
}
