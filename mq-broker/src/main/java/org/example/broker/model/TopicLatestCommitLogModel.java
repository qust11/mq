package org.example.broker.model;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author qushutao
 * @since 2025/5/25-20:45
 */
@Data
public class TopicLatestCommitLogModel {

    // 文件名
    private String fileName;

    // 文件最大偏移量
    private long offsetLimit;

    // 偏移量最新写入文件的位置
    private AtomicLong offset;

}
