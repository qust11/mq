package com.example.mqbroker.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qushutao
 * @since 2025/5/25-0:04
 */
public class MessageFileModeManager {

    private static Map<String, MessageFileMode> messageFileModeMap = new HashMap<>();


    public MessageFileMode getMessageFileMode(String topic) {
        return messageFileModeMap.get(topic);
    }

    public void putMessageFileMode(String topic, MessageFileMode messageFileMode) {
        messageFileModeMap.put(topic, messageFileMode);
    }

}
