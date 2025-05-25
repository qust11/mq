package org.example.broker.model;

import lombok.Data;

import java.util.List;

/**
 * @author qushutao
 * @since 2025-05-25
 */
@Data
public class TopicModel {

    private String topic;

    private List<MqQueueModel> queueInfo;

    private long createAt;

    private long updateAt;
}
