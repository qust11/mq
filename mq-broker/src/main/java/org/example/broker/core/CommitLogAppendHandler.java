package org.example.broker.core;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.broker.cache.CommonCache;
import org.example.broker.constant.BrokerConstant;
import org.example.broker.model.CommitLogMessageModel;
import org.example.broker.model.EagleMqTopicModel;

import java.util.List;


/**
 * @author qushutao
 * @since 2025/5/25-0:07
 */
@Slf4j
public class CommitLogAppendHandler {

    private MMapFileModeManager messageFileModeManager = new MMapFileModeManager();

    public CommitLogAppendHandler() {
    }



    public void prepareMMapLoading(String topicName) {
        MMapFileMode messageFileMode = new MMapFileMode();
        messageFileMode.loadFileInMMap(topicName, 0, BrokerConstant.COMMIT_LOG_FILE_SIZE);
        messageFileModeManager.putMessageFileMode(topicName, messageFileMode);
    }

    public void appendMessage(String topic, byte[] bytes) {
        log.info("topic name = {} ， ***** data = {}", topic, new String(bytes));
        MMapFileMode messageFileMode = messageFileModeManager.getMessageFileMode(topic);
        if (null == messageFileMode) {
            throw new RuntimeException("messageFileMode is null");
        }
        CommitLogMessageModel commitLogMessageModel = new CommitLogMessageModel();
        commitLogMessageModel.setContent(bytes);
        messageFileMode.writeContent(commitLogMessageModel, true);
    }

    public byte[] readMessage(String topic) {
        MMapFileMode messageFileMode = messageFileModeManager.getMessageFileMode(topic);
        if (null == messageFileMode) {
            throw new RuntimeException("messageFileMode is null");
        }
        return messageFileMode.readContent(0, 1000);
    }


}
