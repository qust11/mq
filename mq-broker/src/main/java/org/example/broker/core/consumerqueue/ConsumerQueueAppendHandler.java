package org.example.broker.core.consumerqueue;

import lombok.extern.slf4j.Slf4j;
import org.example.broker.cache.CommonCache;
import org.example.broker.constant.BrokerConstant;
import org.example.broker.core.MMapFileMode;
import org.example.broker.core.MMapFileModeManager;
import org.example.broker.model.CommitLogMessageModel;
import org.example.broker.model.EagleMqQueueModel;
import org.example.broker.model.EagleMqTopicModel;

import java.util.ArrayList;
import java.util.List;


/**
 * @author qushutao
 * @since 2025/5/25-0:07
 */
@Slf4j
public class ConsumerQueueAppendHandler {

    private ConsumerQueueMMapFileModeManager consumerQueueMMapFileModeManager = new ConsumerQueueMMapFileModeManager();

    public void prepareConsumerQueueMMapLoading(String topicName) {
        EagleMqTopicModel eagleMqTopicModel = CommonCache.getTopicModel(topicName);
        if (eagleMqTopicModel == null){
            log.error("topic not exists topicName ={}", topicName);
            return;
        }
        List<ConsumerQueueMMapFileMode> consumerQueueMMapFileModes = new ArrayList<>();
        List<EagleMqQueueModel> queueInfo = eagleMqTopicModel.getQueueInfo();
        for (EagleMqQueueModel queueModel : queueInfo) {
            ConsumerQueueMMapFileMode consumerQueueMMapFileMode = new ConsumerQueueMMapFileMode();
            consumerQueueMMapFileMode.loadFileInMMap(topicName,
                    queueModel.getId(),
                    queueModel.getFileName(),
                    queueModel.getLatestOffset(),
                    queueModel.getOffsetLimit());
            consumerQueueMMapFileModes.add(consumerQueueMMapFileMode);
        }
        consumerQueueMMapFileModeManager.putMessageFileMode(topicName, consumerQueueMMapFileModes);
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
