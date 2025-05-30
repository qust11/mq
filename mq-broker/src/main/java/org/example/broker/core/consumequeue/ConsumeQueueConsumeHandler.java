package org.example.broker.core.consumequeue;

import io.micrometer.common.util.StringUtils;
import org.example.broker.cache.CommonCache;
import org.example.broker.constant.BrokerConstant;
import org.example.broker.core.CommitLogMMapFileMode;
import org.example.broker.model.EagleMqQueueModel;
import org.example.broker.model.consume.ConsumeQueueDetailModel;
import org.example.broker.model.consume.ConsumeQueueOffsetModel;
import org.example.broker.util.FileNameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qushutao
 * @since 2025-05-29
 */
public class ConsumeQueueConsumeHandler {

    private static final Logger log = LoggerFactory.getLogger(ConsumeQueueConsumeHandler.class);

    public byte[] consume(String topicName, String consumeGroup, int queueId) {
        ConsumeQueueOffsetModel consumeQueueOffsetModel = getAndInitConsumeQueueOffsetModel(topicName, consumeGroup, queueId);
        ConsumeQueueOffsetModel.OffsetTable offsetTable = consumeQueueOffsetModel.getOffsetTable();
        ConsumeQueueOffsetModel.ConsumeGroupDetail consumeGroupDetail = offsetTable.getTopicConsumeGroupDetail().get(topicName);
        Map<String, Map<String, String>> consumeGroupDetailMap = consumeGroupDetail.getConsumeGroupDetailMap();
        Map<String, String> stringStringMap = consumeGroupDetailMap.get(consumeGroup);
        String str = stringStringMap.get(String.valueOf(queueId));
        // 格式 0000000#123  / 文件名#读取索引位置
        String[] split = str.split("#");
        String fileName = split[0];
        int index = Integer.parseInt(split[1]);

        List<ConsumeQueueMMapFileMode> consumeQueueMMapFileModes = CommonCache.getConsumeQueueMMapFileModeManager().getMessageFileMode(topicName);
        ConsumeQueueMMapFileMode consumeQueueMMapFileMode = consumeQueueMMapFileModes.stream().filter(item -> item.getTopicName().equals(topicName) && item.getQueueId() == queueId).findFirst().orElse(null);
        if (null == consumeQueueMMapFileMode) {
            throw new RuntimeException("没有找到对应的消费队列");
        }
        boolean readable = isReadable(index, topicName, queueId);
        if (!readable){
            return new byte[0];
        }
        byte[] bytes = consumeQueueMMapFileMode.readContent(index);
        ConsumeQueueDetailModel consumeQueueDetailModel = ConsumeQueueDetailModel.convertFromBytes(bytes);
        log.info("消费队列数据：topicName = {},consumeGroup = {}, queueId = {}, 信息内容={}", topicName, consumeGroup, queueId, consumeQueueDetailModel);

        CommitLogMMapFileMode messageFileMode = CommonCache.getCommitLogMMapFileModeManager().getMessageFileMode(topicName);
        if (null == messageFileMode) {
            log.error("没有找到对应的消费队列 topic = {}", topicName);
            throw new RuntimeException("没有找到对应的消费队列");

        }
        return messageFileMode.readContent(consumeQueueDetailModel.getMsgIndex(), consumeQueueDetailModel.getMsgLength());
    }

    private boolean isReadable( int index,  String topicName, int queueId) {
        EagleMqQueueModel queueModel = CommonCache.getTopicModel(topicName).getQueueInfo().stream().filter(c -> c.getId().equals(queueId)).findFirst().orElse(null);
        if (null == queueModel){
            log.error("消费消息 没有找到对应的消费队列 topic = {}", topicName);
            throw new RuntimeException("没有找到对应的消费队列");
        }
        // 如果当前消息所在位置大于文件存储的位置 则停止读取
        if (index >= queueModel.getLatestOffset().get()) {
            return false;
        }
        return true;
    }


    public boolean ack(String topicName, String consumeGroup, int queueId) {
        ConsumeQueueOffsetModel consumeQueueOffsetModel = CommonCache.getConsumeQueueOffsetModel();
        ConsumeQueueOffsetModel.OffsetTable offsetTable = consumeQueueOffsetModel.getOffsetTable();
        ConsumeQueueOffsetModel.ConsumeGroupDetail consumeGroupDetail = offsetTable.getTopicConsumeGroupDetail().get(topicName);
        Map<String, Map<String, String>> consumeGroupDetailMap = consumeGroupDetail.getConsumeGroupDetailMap();
        Map<String, String> stringStringMap = consumeGroupDetailMap.get(consumeGroup);
        String str = stringStringMap.get(String.valueOf(queueId));
        String[] split = str.split("#");
        String fileName = split[0];
        int index = Integer.parseInt(split[1]);
        boolean readable = isReadable(index, topicName, queueId);
        if (!readable){
            return true;
        }
        stringStringMap.put(String.valueOf(queueId), fileName + "#" + (index + 12));
        return true;
    }

    private static ConsumeQueueOffsetModel getAndInitConsumeQueueOffsetModel(String topicName, String consumeGroup, int queueId) {
        ConsumeQueueOffsetModel consumeQueueOffsetModel = CommonCache.getConsumeQueueOffsetModel();
        if (null == consumeQueueOffsetModel || null == consumeQueueOffsetModel.getOffsetTable()) {
            consumeQueueOffsetModel = initConsumeQueueOffsetModel(topicName, consumeGroup, queueId);
        }
        return consumeQueueOffsetModel;
    }

    private static ConsumeQueueOffsetModel initConsumeQueueOffsetModel(String topicName, String consumeGroup, int queueId) {
        ConsumeQueueOffsetModel consumeQueueOffsetModel = new ConsumeQueueOffsetModel();
        ConsumeQueueOffsetModel.OffsetTable offsetTable = new ConsumeQueueOffsetModel.OffsetTable();
        Map<String, ConsumeQueueOffsetModel.ConsumeGroupDetail> topicConsumeGroupDetailMap = new HashMap<>();
        ConsumeQueueOffsetModel.ConsumeGroupDetail consumeGroupDetail = new ConsumeQueueOffsetModel.ConsumeGroupDetail();
        Map<String, Map<String, String>> innerMap = new HashMap<>();
        HashMap<String, String> finalMap = new HashMap<>();
        finalMap.put(String.valueOf(queueId), BrokerConstant.FIRST_LOG_FILE_NAME + "#0");
        innerMap.put(consumeGroup, finalMap);
        consumeGroupDetail.setConsumeGroupDetailMap(innerMap);
        topicConsumeGroupDetailMap.put(topicName, consumeGroupDetail);
        offsetTable.setTopicConsumeGroupDetail(topicConsumeGroupDetailMap);
        consumeQueueOffsetModel.setOffsetTable(offsetTable);
        CommonCache.setConsumeQueueOffsetModel(consumeQueueOffsetModel);
        return consumeQueueOffsetModel;
    }

}
