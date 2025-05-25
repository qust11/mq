package org.example.broker.config;

import io.micrometer.common.util.StringUtils;
import org.example.broker.cache.CommonCache;
import org.example.broker.constant.BrokerConstant;
import org.example.broker.model.EagleMqTopicModel;
import org.example.broker.util.FileContentReaderUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        String jsonPath = mqHome + BrokerConstant.CONFIG_TOPIC_JSON_PATH;

        List<EagleMqTopicModel> eagleMqTopicModel = FileContentReaderUtil.readTopicModel(jsonPath);
        CommonCache.setTopicModelList(eagleMqTopicModel);

        Map<String, EagleMqTopicModel> collect = eagleMqTopicModel.stream().collect(Collectors.toMap(EagleMqTopicModel::getTopic, Function.identity()));
        CommonCache.setTopicModelMap(collect);
    }
}
