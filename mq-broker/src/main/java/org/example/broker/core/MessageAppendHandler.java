package org.example.broker.core;

/**
 * @author qushutao
 * @since 2025/5/25-0:07
 */
public class MessageAppendHandler {

    private MMapFileModeManager messageFileModeManager = new MMapFileModeManager();


    public MessageAppendHandler() {

    }

    public void preparMMapLoading(String path ,String topicName){
        MMapFileMode messageFileMode = new MMapFileMode();
        messageFileMode.loadFileInMMap(path, 0, 1024*1024);
        messageFileModeManager.putMessageFileMode(topicName, messageFileMode);
    }

    public void appendMessage(String topic, byte[] bytes) {
        MMapFileMode messageFileMode = messageFileModeManager.getMessageFileMode(topic);
        if (null == messageFileMode){
            throw new RuntimeException("messageFileMode is null");
        }
        messageFileMode.writeContent(bytes, true);
    }

    public byte[] readMessage(String topic) {
        MMapFileMode messageFileMode = messageFileModeManager.getMessageFileMode(topic);
        if (null == messageFileMode){
            throw new RuntimeException("messageFileMode is null");
        }
        return messageFileMode.readContent(0, 10);
    }


}
