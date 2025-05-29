package org.example.broker.config;

import com.alibaba.fastjson2.JSONObject;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.example.broker.cache.CommonCache;
import org.example.broker.constant.BrokerConstant;
import org.example.broker.model.consumer.ConsumerQueueOffsetModel;
import org.example.broker.util.FileContentReaderUtil;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author qushutao
 * @since 2025/5/28-22:49
 */
@Slf4j
public class ConsumerQueueLoader {

    private String filePath;

    public ConsumerQueueLoader() {
        loadProperties();
    }

    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String mqHome = globalProperties.getMqHome();
        if (StringUtils.isBlank(mqHome)) {
            throw new IllegalStateException("MQ HOME is empty");
        }
        filePath = mqHome + BrokerConstant.CONSUMER_QUEUE_OFFSET_MODEL_PATH;
        String content = FileContentReaderUtil.readContent(filePath);
        // 读取消费信息配置
        ConsumerQueueOffsetModel consumerQueueOffsetModel = JSONObject.parseObject(content, ConsumerQueueOffsetModel.class);
        CommonCache.setConsumerQueueOffsetModel(consumerQueueOffsetModel);

//        refreshConsumer();
    }

    private void refreshConsumer() {
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(() -> {
                    ConsumerQueueOffsetModel consumerQueueOffsetModel = CommonCache.getConsumerQueueOffsetModel();
                    if (consumerQueueOffsetModel == null){
                        return;
                    }
//                    ConsumerQueueOffsetModel consumerQueueOffsetModel = getConsumerQueueOffsetModel();
                    String jsonString = JSONObject.toJSONString(consumerQueueOffsetModel);
                    log.info("refreshConsumer ........ json = {}", jsonString);
                    FileContentReaderUtil.writeContent(filePath, jsonString);
                }, 5, 5, TimeUnit.SECONDS);
    }

    public static ConsumerQueueOffsetModel getConsumerQueueOffsetModel() {
        ConsumerQueueOffsetModel consumerQueueOffsetModel = new ConsumerQueueOffsetModel();

        ConsumerQueueOffsetModel.OffsetTable offsetTable = new ConsumerQueueOffsetModel.OffsetTable();

        ConsumerQueueOffsetModel.ConsumerGroupDetail consumerGroupDetail = new ConsumerQueueOffsetModel.ConsumerGroupDetail();
        Map<String, Map<String, String>> consumerGroupDetailMap = new HashMap<>();
        Map<String, String> innerMap = new HashMap<>();
        innerMap.put("test", "test");
        consumerGroupDetailMap.put("test" + LocalTime.now().toString(), innerMap);
        consumerGroupDetail.setConsumerGroupDetailMap(consumerGroupDetailMap);

        Map<String, ConsumerQueueOffsetModel.ConsumerGroupDetail> topicConsumerGroupDetail = new HashMap<>();
        topicConsumerGroupDetail.put("test" + LocalTime.now().toString(), consumerGroupDetail);
        offsetTable.setTopicConsumerGroupDetail(topicConsumerGroupDetail);
        consumerQueueOffsetModel.setOffsetTable(offsetTable);
        return consumerQueueOffsetModel;
    }

    public static void main(String[] args) {
        ConsumerQueueOffsetModel consumerQueueLoader = getConsumerQueueOffsetModel();
        System.out.println(JSONObject.toJSONString(consumerQueueLoader));
    }
}
