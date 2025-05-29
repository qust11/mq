package org.example.broker.model.consumer;

import lombok.Data;
import org.example.broker.util.ByteConvertUtil;

/**
 * @author qushutao
 * @since 2025-05-27
 */
@Data
public class ConsumerQueueDetailModel {

    // 文件名称
    private int commitLogFileName;

    // 消息起始位置
    private int msgIndex;

    // 消息长度
    private int msgLength;

    public byte[] convertToBytes(){
        byte[] fileNameBytes = ByteConvertUtil.intToByteArray(commitLogFileName);
        byte[] msgIndexBytes = ByteConvertUtil.intToByteArray(msgIndex);
        byte[] msgLengthBytes = ByteConvertUtil.intToByteArray(msgLength);
        byte[] result = new byte[12];
        int index = 0;
        for (int i = 0; i < 4; i++) {
            result[index++] = fileNameBytes[i];
        }
        for (int i = 0; i < 4; i++) {
            result[index++] = msgIndexBytes[i];
        }
        for (int i = 0; i < 4; i++) {
            result[index++] = msgLengthBytes[i];
        }
        return result;
    }
}
