package org.example.broker.model;

import lombok.Data;

/**
 * @author qushutao
 * @since 2025-05-27
 */
@Data
public class ConsumerQueueDetailModel {

    // 文件名称
    private int commitLogFileName;

    // 消息起始位置
    private long msgIndex;

    // 消息长度
    private long msgLength;
}
