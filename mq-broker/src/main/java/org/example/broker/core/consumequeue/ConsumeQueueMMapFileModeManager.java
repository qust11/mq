package org.example.broker.core.consumequeue;

import org.example.broker.cache.CommonCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qushutao
 * @since 2025/5/25-0:04
 */
public class ConsumeQueueMMapFileModeManager {

    public ConsumeQueueMMapFileModeManager() {
        CommonCache.setConsumeQueueMMapFileModeManager(this);
    }

    private static Map<String, List<ConsumeQueueMMapFileMode>> consumeQueueMMapFileModeMap = new HashMap<>();


    public List<ConsumeQueueMMapFileMode> getMessageFileMode(String topic) {
        return consumeQueueMMapFileModeMap.get(topic);
    }

    public void putMessageFileMode(String topic, List<ConsumeQueueMMapFileMode> consumeQueueMMapFileModes) {
        consumeQueueMMapFileModeMap.put(topic, consumeQueueMMapFileModes);
    }

}
