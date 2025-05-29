package org.example.broker.model.consume;

import lombok.Data;

import java.util.Map;

/**
 * @author qushutao
 * @since 2025/5/28-22:36
 */
@Data
public class ConsumeQueueOffsetModel {

    private OffsetTable offsetTable;

    @Data
    public static class OffsetTable {
        // Map<Topic名称，ConsumeGroupDetail>
        private Map<String, ConsumeGroupDetail> topicConsumeGroupDetail;
    }

    @Data
    public static class ConsumeGroupDetail {
        // Map<消费组名称，Map<文件，文件名称#已读取索引位置>>
        private Map<String, Map<String, String>> consumeGroupDetailMap;
    }
}
