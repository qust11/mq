package org.example.broker.core.consumerqueue;

import io.micrometer.common.util.StringUtils;
import org.apache.commons.collections4.MapUtils;
import org.example.broker.cache.CommonCache;
import org.example.broker.constant.BrokerConstant;
import org.example.broker.model.consumer.ConsumerQueueOffsetModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qushutao
 * @since 2025-05-29
 */
public class ConsumerQueueConsumerHandler {

    public byte[] consumer(String topicName, String consumeGroup, int queueId) {
        ConsumerQueueOffsetModel consumerQueueOffsetModel = getAndInitConsumerQueueOffsetModel(topicName, consumeGroup, queueId);
        ConsumerQueueOffsetModel.OffsetTable offsetTable = consumerQueueOffsetModel.getOffsetTable();
        ConsumerQueueOffsetModel.ConsumerGroupDetail consumerGroupDetail = offsetTable.getTopicConsumerGroupDetail().get(topicName);
        Map<String, Map<String, String>> consumerGroupDetailMap = consumerGroupDetail.getConsumerGroupDetailMap();
        Map<String, String> stringStringMap = consumerGroupDetailMap.get(consumeGroup);
        String str = stringStringMap.get(String.valueOf(queueId));
        // 格式 0000000#123  / 文件名#读取索引位置
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        String[] split = str.split("#");
        String fileName = split[0];
        int index = Integer.parseInt(split[1]);
        List<ConsumerQueueMMapFileMode> consumerQueueMMapFileModes = CommonCache.getConsumerQueueMMapFileModeManager().getMessageFileMode(topicName);
        ConsumerQueueMMapFileMode consumerQueueMMapFileMode = consumerQueueMMapFileModes.stream().filter(item -> item.getTopicName().equals(topicName) && item.getQueueId() == queueId).findFirst().orElse(null);
        if (null == consumerQueueMMapFileMode) {
            throw new RuntimeException("没有找到对应的消费队列");
        }

        byte[] bytes = consumerQueueMMapFileMode.readContent(index, );

        return bytes;
    }

    private String getConsumerDetail(String topicName, String consumeGroup, int queueId) {
        ConsumerQueueOffsetModel consumerQueueOffsetModel = getAndInitConsumerQueueOffsetModel(topicName, consumeGroup, queueId);
        ConsumerQueueOffsetModel.OffsetTable offsetTable = consumerQueueOffsetModel.getOffsetTable();
        Map<String, ConsumerQueueOffsetModel.ConsumerGroupDetail> topicConsumerGroupDetail = offsetTable.getTopicConsumerGroupDetail();
        ConsumerQueueOffsetModel.ConsumerGroupDetail consumerGroupDetail = topicConsumerGroupDetail.get(topicName);
        Map<String, Map<String, String>> consumerGroupDetailMap = consumerGroupDetail.getConsumerGroupDetailMap();
        Map<String, String> stringStringMap = consumerGroupDetailMap.get(consumeGroup);
        if (MapUtils.isEmpty(stringStringMap)) {
            stringStringMap = new HashMap<>();
            stringStringMap.put(String.valueOf(queueId), BrokerConstant.FIRST_LOG_FILE_NAME + "#" + 0);
        } else if (StringUtils.isNotBlank(stringStringMap.get(String.valueOf(queueId)))) {
            stringStringMap.put(String.valueOf(queueId), BrokerConstant.FIRST_LOG_FILE_NAME + "#" + 0);
        }

        return stringStringMap.get(String.valueOf(queueId));
    }


    private static Map<String, Map<String, String>> getInnerMap(String topicName, String consumeGroup, int queueId) {
        ConsumerQueueOffsetModel consumerQueueOffsetModel = CommonCache.getConsumerQueueOffsetModel();
        ConsumerQueueOffsetModel.OffsetTable offsetTable = consumerQueueOffsetModel.getOffsetTable();
        Map<String, ConsumerQueueOffsetModel.ConsumerGroupDetail> topicConsumerGroupDetail = offsetTable.getTopicConsumerGroupDetail();
        ConsumerQueueOffsetModel.ConsumerGroupDetail consumerGroupDetail = topicConsumerGroupDetail.get(topicName);
        if (null == consumerGroupDetail) {
            consumerGroupDetail = new ConsumerQueueOffsetModel.ConsumerGroupDetail();

        }
        return consumerGroupDetail.getConsumerGroupDetailMap();
    }
    private static ConsumerQueueOffsetModel getAndInitConsumerQueueOffsetModel(String topicName, String consumeGroup, int queueId) {
        ConsumerQueueOffsetModel consumerQueueOffsetModel = CommonCache.getConsumerQueueOffsetModel();
        if (null == consumerQueueOffsetModel) {
            consumerQueueOffsetModel = initConsumerQueueOffsetModel(topicName, consumeGroup, queueId);
        }
        return consumerQueueOffsetModel;
    }

    private static ConsumerQueueOffsetModel initConsumerQueueOffsetModel(String topicName, String consumeGroup, int queueId) {
        ConsumerQueueOffsetModel consumerQueueOffsetModel = new ConsumerQueueOffsetModel();
        ConsumerQueueOffsetModel.OffsetTable offsetTable = consumerQueueOffsetModel.getOffsetTable();
        Map<String, ConsumerQueueOffsetModel.ConsumerGroupDetail> topicConsumerGroupDetailMap = offsetTable.getTopicConsumerGroupDetail();
        ConsumerQueueOffsetModel.ConsumerGroupDetail consumerGroupDetail = new ConsumerQueueOffsetModel.ConsumerGroupDetail();
        Map<String, Map<String, String>> innerMap = new HashMap<>();
        innerMap.put(consumeGroup, new HashMap<>());
        consumerGroupDetail.setConsumerGroupDetailMap(innerMap);
        consumerGroupDetail.setConsumerGroupDetailMap(innerMap);
        topicConsumerGroupDetailMap.put(topicName, consumerGroupDetail);
        offsetTable.setTopicConsumerGroupDetail(topicConsumerGroupDetailMap);
        consumerQueueOffsetModel.setOffsetTable(offsetTable);
        CommonCache.setConsumerQueueOffsetModel(consumerQueueOffsetModel);
        return consumerQueueOffsetModel;
    }

    public void ack() {

    }
}
