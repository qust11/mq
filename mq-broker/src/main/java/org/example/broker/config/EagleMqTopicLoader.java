package org.example.broker.config;

import com.alibaba.fastjson2.JSON;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.example.broker.cache.CommonCache;
import org.example.broker.model.EagleMqTopicModel;
import org.example.broker.util.FileContentReaderUtil;
import org.example.common.constant.BrokerConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author qushutao
 * @since 2025-05-25
 */
@Slf4j
public class EagleMqTopicLoader {
    private String fileJsonPath;

    public EagleMqTopicLoader() {
        loadProperties();
    }

    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String mqHome = globalProperties.getMqHome();
        if (StringUtils.isBlank(mqHome)) {
            throw new IllegalStateException("MQ HOME is empty");
        }
        fileJsonPath = mqHome + BrokerConstant.CONFIG_TOPIC_JSON_PATH;
        // 读取topic配置文件
        List<EagleMqTopicModel> eagleMqTopicModel = FileContentReaderUtil.readTopicModel(fileJsonPath);
        Map<String, EagleMqTopicModel> collect = eagleMqTopicModel.stream().collect(Collectors.toMap(EagleMqTopicModel::getTopic, Function.identity()));
        CommonCache.setTopicModelMap(collect);

        // 刷新
        refreshTopicLatestCommitLog();
    }

    // 开启刷盘 刷新最新的offset和topic对应的filename

    public void refreshTopicLatestCommitLog() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            List<EagleMqTopicModel> topicModelList = new ArrayList<>();
            CommonCache.TOPIC_MODEL_MAP.forEach((topicName, eagleMqTopicModel) -> topicModelList.add(eagleMqTopicModel));
            String content = JSON.toJSONString(topicModelList);
            FileContentReaderUtil.writeContent(fileJsonPath, content);
            log.info("refreshTopicLatestCommitLog ........ json = {}", JSON.toJSONString(topicModelList));
        }, 3, 3, TimeUnit.SECONDS);
    }
}
