package org.example.broker.core;

import lombok.extern.slf4j.Slf4j;
import org.example.broker.cache.CommonCache;
import org.example.common.constant.BrokerConstant;
import org.example.broker.model.CommitLogMessageModel;


/**
 * @author qushutao
 * @since 2025/5/25-0:07
 */
@Slf4j
public class CommitLogAppendHandler {


    public void prepareMMapLoading(String topicName) {
        CommitLogMMapFileMode messageFileMode = new CommitLogMMapFileMode();
        messageFileMode.loadFileInMMap(topicName, 0, BrokerConstant.COMMIT_LOG_FILE_SIZE);
        CommonCache.getCommitLogMMapFileModeManager().putMessageFileMode(topicName, messageFileMode);
    }

    public void appendMessage(String topic, byte[] bytes) {
        log.info("topic name = {} ， ***** data = {}", topic, new String(bytes));
        CommitLogMMapFileMode messageFileMode = CommonCache.getCommitLogMMapFileModeManager().getMessageFileMode(topic);
        if (null == messageFileMode) {
            throw new RuntimeException("messageFileMode is null");
        }
        CommitLogMessageModel commitLogMessageModel = new CommitLogMessageModel();
        commitLogMessageModel.setContent(bytes);
        messageFileMode.writeContent(commitLogMessageModel, true);
    }

    public byte[] readMessage(String topic) {
        CommitLogMMapFileMode messageFileMode = CommonCache.getCommitLogMMapFileModeManager().getMessageFileMode(topic);
        if (null == messageFileMode) {
            throw new RuntimeException("messageFileMode is null");
        }
        return messageFileMode.readContent(0, 1000);
    }


}
