package org.example.broker.util;

import org.example.broker.cache.CommonCache;
import org.example.broker.constant.BrokerConstant;

/**
 * @author qushutao
 * @since 2025/5/25-23:13
 */
public class FileNameUtil {

    public static String buildFirstLogFileName() {
        return BrokerConstant.FIRST_LOG_FILE_NAME;
    }

    public static String incConsumeQueueFileName(String oldName) {
        return incCommitLogFileName(oldName);
    }
    public static String incCommitLogFileName(String oldName) {
        if (oldName.length() != 8) {
            throw new IllegalStateException("file name is invalid,fileName is not digits");
        }
        int fileName = Integer.parseInt(oldName);
        return completionFileName(fileName + 1);
    }

    public static String buildCommitLogFileName(String topicName, String fileName) {
        return CommonCache.getGlobalProperties().getMqHome() +
                BrokerConstant.BASIC_COMMITLOG_PATH +
                topicName + "\\" + fileName;
    }

    public static String buildConsumeQueueName(String topicName,Integer queueId, String fileName) {
        String storePath = BrokerConstant.BASIC_CONSUMEQUEUE_PATH + topicName + "\\" + queueId + "\\";
        return CommonCache.getGlobalProperties().getMqHome() +
                storePath  + fileName;
    }


    public static String completionFileName(int fileName) {
        return String.format("%08d", fileName);
    }


}
