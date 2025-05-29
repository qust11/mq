package org.example.broker.core.consumerqueue;

import org.example.broker.cache.CommonCache;
import org.example.broker.core.MMapFileMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qushutao
 * @since 2025/5/25-0:04
 */
public class ConsumerQueueMMapFileModeManager {

    public ConsumerQueueMMapFileModeManager() {
        CommonCache.setConsumerQueueMMapFileModeManager(this);
    }

    private static Map<String, List<ConsumerQueueMMapFileMode>> consumerQueueMMapFileModeMap = new HashMap<>();


    public List<ConsumerQueueMMapFileMode> getMessageFileMode(String topic) {
        return consumerQueueMMapFileModeMap.get(topic);
    }

    public void putMessageFileMode(String topic, List<ConsumerQueueMMapFileMode> consumerQueueMMapFileModes) {
        consumerQueueMMapFileModeMap.put(topic, consumerQueueMMapFileModes);
    }

}
