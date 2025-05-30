package org.example.broker.config;

import com.alibaba.fastjson2.JSONObject;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.example.broker.cache.CommonCache;
import org.example.broker.constant.BrokerConstant;
import org.example.broker.model.consume.ConsumeQueueOffsetModel;
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
public class ConsumeQueueLoader {

    private String filePath;

    public ConsumeQueueLoader() {
        loadProperties();
    }

    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String mqHome = globalProperties.getMqHome();
        if (StringUtils.isBlank(mqHome)) {
            throw new IllegalStateException("MQ HOME is empty");
        }
        filePath = mqHome + BrokerConstant.CONSUME_QUEUE_OFFSET_MODEL_PATH;
        String content = FileContentReaderUtil.readContent(filePath);
        // 读取消费信息配置
        ConsumeQueueOffsetModel consumeQueueOffsetModel = JSONObject.parseObject(content, ConsumeQueueOffsetModel.class);
        CommonCache.setConsumeQueueOffsetModel(consumeQueueOffsetModel);
        refreshConsume();
    }

    private void refreshConsume() {
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(() -> {
                    ConsumeQueueOffsetModel consumeQueueOffsetModel = CommonCache.getConsumeQueueOffsetModel();
                    if (consumeQueueOffsetModel == null){
                        return;
                    }
//                    ConsumeQueueOffsetModel consumeQueueOffsetModel = getConsumeQueueOffsetModel();
                    String jsonString = JSONObject.toJSONString(consumeQueueOffsetModel);
                    log.info("refreshConsume ........ json = {}", jsonString);
                    FileContentReaderUtil.writeContent(filePath, jsonString);
                }, 3, 3, TimeUnit.SECONDS);
    }

    public static ConsumeQueueOffsetModel getConsumeQueueOffsetModel() {
        ConsumeQueueOffsetModel consumeQueueOffsetModel = new ConsumeQueueOffsetModel();

        ConsumeQueueOffsetModel.OffsetTable offsetTable = new ConsumeQueueOffsetModel.OffsetTable();

        ConsumeQueueOffsetModel.ConsumeGroupDetail consumeGroupDetail = new ConsumeQueueOffsetModel.ConsumeGroupDetail();
        Map<String, Map<String, String>> consumeGroupDetailMap = new HashMap<>();
        Map<String, String> innerMap = new HashMap<>();
        innerMap.put("test", "test");
        consumeGroupDetailMap.put("test" + LocalTime.now().toString(), innerMap);
        consumeGroupDetail.setConsumeGroupDetailMap(consumeGroupDetailMap);

        Map<String, ConsumeQueueOffsetModel.ConsumeGroupDetail> topicConsumeGroupDetail = new HashMap<>();
        topicConsumeGroupDetail.put("test" + LocalTime.now().toString(), consumeGroupDetail);
        offsetTable.setTopicConsumeGroupDetail(topicConsumeGroupDetail);
        consumeQueueOffsetModel.setOffsetTable(offsetTable);
        return consumeQueueOffsetModel;
    }

    public static void main(String[] args) {
        ConsumeQueueOffsetModel consumeQueueLoader = getConsumeQueueOffsetModel();
        System.out.println(JSONObject.toJSONString(consumeQueueLoader));
    }
}
