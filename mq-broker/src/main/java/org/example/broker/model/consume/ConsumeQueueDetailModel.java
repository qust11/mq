package org.example.broker.model.consume;

import lombok.Data;
import org.example.broker.util.ByteConvertUtil;

/**
 * @author qushutao
 * @since 2025-05-27
 */
@Data
public class ConsumeQueueDetailModel {

    // 文件名称
    private int fileName;

    // 消息起始位置
    private int msgIndex;

    // 消息长度
    private int msgLength;

    public byte[] convertToBytes(){
        byte[] fileNameBytes = ByteConvertUtil.intToByteArray(fileName);
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

    public static ConsumeQueueDetailModel convertFromBytes(byte[] bytes){
        ConsumeQueueDetailModel consumeQueueDetailModel = new ConsumeQueueDetailModel();

        byte [] fileNameBytes = ByteConvertUtil.getByteArray(bytes, 0, 4);
        byte [] msgIndexBytes = ByteConvertUtil.getByteArray(bytes, 4, 4);
        byte [] msgLengthBytes = ByteConvertUtil.getByteArray(bytes, 8, 4);

        consumeQueueDetailModel.setFileName(ByteConvertUtil.byteArrayToInt(fileNameBytes));
        consumeQueueDetailModel.setMsgIndex(ByteConvertUtil.byteArrayToInt(msgIndexBytes));
        consumeQueueDetailModel.setMsgLength(ByteConvertUtil.byteArrayToInt(msgLengthBytes));
        return consumeQueueDetailModel;
    }

    public static void main(String[] args) {
        ConsumeQueueDetailModel consumeQueueDetailModel = new ConsumeQueueDetailModel();
        consumeQueueDetailModel.setFileName(1);
        consumeQueueDetailModel.setMsgIndex(2);
        consumeQueueDetailModel.setMsgLength(3);
        byte[] bytes = consumeQueueDetailModel.convertToBytes();
        ConsumeQueueDetailModel consumeQueueDetailModel1 = ConsumeQueueDetailModel.convertFromBytes(bytes);
        System.out.println(consumeQueueDetailModel1);
    }
}
