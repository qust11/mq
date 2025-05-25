package org.example.broker.util;

import org.example.broker.constant.BrokerConstant;

/**
 * @author qushutao
 * @since 2025/5/25-23:13
 */
public class CommonLogFileNameUtil {

    public static String buildFirstLogFileName() {
        return BrokerConstant.FIRST_LOG_FILE_NAME;
    }

    public static String incCommitLogFileName(String oldName) {
        if (oldName.length() != 8 ){
            throw new IllegalStateException("file name is invalid,fileName is not digits");
        }
        int fileName = Integer.parseInt(oldName);
        return String.format("%08d", fileName + 1);
    }
}
