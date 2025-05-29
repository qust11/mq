package org.example.broker.core.consumequeue;

import lombok.extern.slf4j.Slf4j;
import org.example.broker.cache.CommonCache;
import org.example.broker.model.EagleMqQueueModel;
import org.example.broker.model.EagleMqTopicModel;

import java.util.ArrayList;
import java.util.List;


/**
 * @author qushutao
 * @since 2025/5/25-0:07
 */
@Slf4j
public class ConsumeQueueAppendHandler {


    public void prepareConsumeQueueMMapLoading(String topicName) {

        EagleMqTopicModel eagleMqTopicModel = CommonCache.getTopicModel(topicName);
        if (eagleMqTopicModel == null) {
            log.error("topic not exists topicName ={}", topicName);
            return;
        }
        List<ConsumeQueueMMapFileMode> consumeQueueMMapFileModes = new ArrayList<>();
        List<EagleMqQueueModel> queueInfo = eagleMqTopicModel.getQueueInfo();
        for (EagleMqQueueModel queueModel : queueInfo) {
            ConsumeQueueMMapFileMode consumeQueueMMapFileMode = new ConsumeQueueMMapFileMode();
            consumeQueueMMapFileMode.loadFileInMMap(topicName,
                    queueModel.getId(),
                    queueModel.getFileName(),
                    queueModel.getLastOffset(),
                    queueModel.getLatestOffset().get(),
                    queueModel.getOffsetLimit());
            consumeQueueMMapFileModes.add(consumeQueueMMapFileMode);
        }
        CommonCache.getConsumeQueueMMapFileModeManager().putMessageFileMode(topicName, consumeQueueMMapFileModes);
    }

//
//    public void appendMessage(String topic, byte[] bytes) {
//        log.info("topic name = {} ， ***** data = {}", topic, new String(bytes));
//        MMapFileMode messageFileMode = messageFileModeManager.getMessageFileMode(topic);
//        if (null == messageFileMode) {
//            throw new RuntimeException("messageFileMode is null");
//        }
//        CommitLogMessageModel commitLogMessageModel = new CommitLogMessageModel();
//        commitLogMessageModel.setContent(bytes);
//        messageFileMode.writeContent(commitLogMessageModel, true);
//    }
//
//    public byte[] readMessage(String topic) {
//        MMapFileMode messageFileMode = messageFileModeManager.getMessageFileMode(topic);
//        if (null == messageFileMode) {
//            throw new RuntimeException("messageFileMode is null");
//        }
//        return messageFileMode.readContent(0, 1000);
//    }


}
