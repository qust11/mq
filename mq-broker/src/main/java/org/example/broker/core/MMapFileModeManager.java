package org.example.broker.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qushutao
 * @since 2025/5/25-0:04
 */
public class MMapFileModeManager {

    private static Map<String, MMapFileMode> messageFileModeMap = new HashMap<>();


    public MMapFileMode getMessageFileMode(String topic) {
        return messageFileModeMap.get(topic);
    }

    public void putMessageFileMode(String topic, MMapFileMode messageFileMode) {
        messageFileModeMap.put(topic, messageFileMode);
    }

}
