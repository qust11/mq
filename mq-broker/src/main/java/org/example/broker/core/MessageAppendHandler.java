package com.example.mqbroker.core;

/**
 * @author qushutao
 * @since 2025/5/25-0:07
 */
public class MessageAppendHandler {

    private MessageFileModeManager messageFileModeManager = new MessageFileModeManager();

    private static final String TOPIC_NAME = "op";

    public MessageAppendHandler() {
        MessageFileMode messageFileMode = new MessageFileMode();
        messageFileMode.loadFileInMMap("mq-broker/src/main/java/com/example/mqbroker/store/order/000001", 0, 1024*1024);
        messageFileModeManager.putMessageFileMode(TOPIC_NAME, messageFileMode);
    }

    public void appendMessage(String topic,byte[] bytes) {
        MessageFileMode messageFileMode = messageFileModeManager.getMessageFileMode(topic);
        if (null == messageFileMode){
            throw new RuntimeException("messageFileMode is null");
        }
        messageFileMode.writeContent(bytes, true);
    }

    public byte[] readMessage(String topic, int offset, int size) {
        MessageFileMode messageFileMode = messageFileModeManager.getMessageFileMode(topic);
        if (null == messageFileMode){
            throw new RuntimeException("messageFileMode is null");
        }
        return messageFileMode.readContent(offset, size);
    }

    public static void main(String[] args) {
            MessageAppendHandler  messageAppendHandler = new MessageAppendHandler();
            messageAppendHandler.appendMessage(TOPIC_NAME, "hello world this is good".getBytes());
            byte[] bytes = messageAppendHandler.readMessage(TOPIC_NAME, 0, 10);
            System.out.println(new String(bytes));
    }
}
