package org.example.broker.core;

import org.example.broker.cache.CommonCache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qushutao
 * @since 2025/5/25-0:04
 */
public class CommitLogMMapFileModeManager {
    public CommitLogMMapFileModeManager() {
        CommonCache.setCommitLogMMapFileModeManager(this);
    }

    private static Map<String, CommitLogMMapFileMode> messageFileModeMap = new HashMap<>();


    public CommitLogMMapFileMode getMessageFileMode(String topic) {
        return messageFileModeMap.get(topic);
    }

    public void putMessageFileMode(String topic, CommitLogMMapFileMode messageFileMode) {
        messageFileModeMap.put(topic, messageFileMode);
    }

}
