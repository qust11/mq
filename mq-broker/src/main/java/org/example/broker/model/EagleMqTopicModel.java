package org.example.broker.model;

import lombok.Data;

import java.util.List;

/**
 * @author qushutao
 * @since 2025-05-25
 */
@Data
public class EagleMqTopicModel {

    // topicName
    private String topic;

    // 当前主题的最新的文件信息
    private TopicLatestCommitLogModel latestCommitLog;

    // topic的队列信息
    private List<EagleMqQueueModel> queueInfo;

    // 创建时间
    private long createAt;

    // 最后更新时间
    private long updateAt;
}
