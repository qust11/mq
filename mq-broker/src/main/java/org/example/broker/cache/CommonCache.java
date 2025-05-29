package org.example.broker.cache;

import org.example.broker.config.GlobalProperties;
import org.example.broker.core.CommitLogMMapFileModeManager;
import org.example.broker.core.consumequeue.ConsumeQueueMMapFileModeManager;
import org.example.broker.model.EagleMqTopicModel;
import org.example.broker.model.consume.ConsumeQueueOffsetModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qushutao
 * @since 2025-05-25
 */
public class CommonCache {

    public static GlobalProperties GLOBAL_PROPERTIES = new GlobalProperties();
    public static Map<String, EagleMqTopicModel> TOPIC_MODEL_MAP = new HashMap<>();
    public static ConsumeQueueOffsetModel CONSUME_QUEUE_OFFSET_MODEL = new ConsumeQueueOffsetModel();
    public static ConsumeQueueMMapFileModeManager CONSUME_QUEUE_MMAP_FILE_MODE_MANAGER = new ConsumeQueueMMapFileModeManager();
    public static CommitLogMMapFileModeManager COMMIT_LOG_MMAP_FILE_MODE_MANAGER = new CommitLogMMapFileModeManager();

    public static void setGlobalProperties(GlobalProperties globalProperties) {
        CommonCache.GLOBAL_PROPERTIES = globalProperties;
    }

    public static GlobalProperties getGlobalProperties() {
        return CommonCache.GLOBAL_PROPERTIES;

    }

//    public static void setTopicModelList(List<EagleMqTopicModel> eagleMqTopicModel) {
//        CommonCache.TOPIC_MODEL = eagleMqTopicModel;
//    }

    public static List<EagleMqTopicModel> getTopicModelList() {
        List<EagleMqTopicModel> eagleMqTopicModel = new ArrayList<>();
        CommonCache.TOPIC_MODEL_MAP.forEach((k, v) -> {
            eagleMqTopicModel.add(v);
        });
        return eagleMqTopicModel;
    }

    public static void setTopicModelMap(Map<String, EagleMqTopicModel> topicModelMap) {
        CommonCache.TOPIC_MODEL_MAP = topicModelMap;
    }

    public static EagleMqTopicModel getTopicModel(String topicName) {
        return CommonCache.TOPIC_MODEL_MAP.get(topicName);
    }

    public static ConsumeQueueOffsetModel getConsumeQueueOffsetModel() {
        return CommonCache.CONSUME_QUEUE_OFFSET_MODEL;
    }


    public static void setConsumeQueueOffsetModel(ConsumeQueueOffsetModel consumeQueueOffsetModel) {
        CommonCache.CONSUME_QUEUE_OFFSET_MODEL = consumeQueueOffsetModel;
    }

    public static ConsumeQueueMMapFileModeManager getConsumeQueueMMapFileModeManager() {
        return CommonCache.CONSUME_QUEUE_MMAP_FILE_MODE_MANAGER;
    }

    public static void setConsumeQueueMMapFileModeManager(ConsumeQueueMMapFileModeManager consumeQueueMMapFileModeManager) {
        CommonCache.CONSUME_QUEUE_MMAP_FILE_MODE_MANAGER = consumeQueueMMapFileModeManager;
    }

    public static CommitLogMMapFileModeManager getCommitLogMMapFileModeManager() {
        return CommonCache.COMMIT_LOG_MMAP_FILE_MODE_MANAGER;
    }

    public static void setCommitLogMMapFileModeManager(CommitLogMMapFileModeManager messageFileModeManager) {
        CommonCache.COMMIT_LOG_MMAP_FILE_MODE_MANAGER = messageFileModeManager;
    }
}
