package org.example.broker.util;

import org.example.broker.cache.CommonCache;
import org.example.broker.constant.BrokerConstant;

/**
 * @author qushutao
 * @since 2025/5/25-23:13
 */
public class CommitLogFileNameUtil {

    public static String buildFirstLogFileName() {
        return BrokerConstant.FIRST_LOG_FILE_NAME;
    }

    public static String incCommitLogFileName(String oldName) {
        if (oldName.length() != 8) {
            throw new IllegalStateException("file name is invalid,fileName is not digits");
        }
        int fileName = Integer.parseInt(oldName);
        return completionFileName(fileName + 1);
    }

    public static String buildCommitLogFileName(String topicName, String newFileName) {
        return CommonCache.getGlobalProperties().getMqHome() +
                BrokerConstant.BASIC_STORE_PATH +
                topicName + "\\" + newFileName;
    }

    public static String completionFileName(int fileName) {
        return String.format("%08d", fileName);
    }


}
