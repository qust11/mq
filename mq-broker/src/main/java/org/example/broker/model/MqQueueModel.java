package org.example.broker.model;

import lombok.Data;

/**
 * @author qushutao
 * @since 2025-05-25
 */
@Data
public class MqQueueModel {

    private Integer id;

    private Long minOffset;

    private Long maxOffset;

    private Long currentOffset;
}
