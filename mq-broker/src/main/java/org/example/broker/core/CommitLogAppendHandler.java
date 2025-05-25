package org.example.broker.core;

import lombok.NoArgsConstructor;
import org.example.broker.model.CommitLogMessageModel;

/**
 * @author qushutao
 * @since 2025/5/25-0:07
 */
@NoArgsConstructor
public class CommitLogAppendHandler {

    private MMapFileModeManager messageFileModeManager = new MMapFileModeManager();


    public void prepareMMapLoading(String topicName) {
        MMapFileMode messageFileMode = new MMapFileMode();
        messageFileMode.loadFileInMMap(topicName, 0, 1024 * 1024);
        messageFileModeManager.putMessageFileMode(topicName, messageFileMode);
    }

    public void appendMessage(String topic, byte[] bytes) {
        MMapFileMode messageFileMode = messageFileModeManager.getMessageFileMode(topic);
        if (null == messageFileMode) {
            throw new RuntimeException("messageFileMode is null");
        }
        CommitLogMessageModel commitLogMessageModel = new CommitLogMessageModel();
        commitLogMessageModel.setContent(bytes);
        commitLogMessageModel.setSize(bytes.length);
        messageFileMode.writeContent(commitLogMessageModel, true);
    }

    public byte[] readMessage(String topic) {
        MMapFileMode messageFileMode = messageFileModeManager.getMessageFileMode(topic);
        if (null == messageFileMode) {
            throw new RuntimeException("messageFileMode is null");
        }
        return messageFileMode.readContent(0, 28);
    }


}
