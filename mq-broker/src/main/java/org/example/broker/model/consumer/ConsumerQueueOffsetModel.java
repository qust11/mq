package org.example.broker.model.consumer;

import lombok.Data;

import java.util.Map;

/**
 * @author qushutao
 * @since 2025/5/28-22:36
 */
@Data
public class ConsumerQueueOffsetModel {

    private OffsetTable offsetTable;

    @Data
    public static class OffsetTable {
        // Map<Topic名称，ConsumerGroupDetail>
        private Map<String, ConsumerGroupDetail> topicConsumerGroupDetail;
    }

    @Data
    public static class ConsumerGroupDetail {
        // Map<消费组名称，Map<文件，文件名称#已读取索引位置>>
        private Map<String, Map<String, String>> consumerGroupDetailMap;
    }
}
