package org.example.broker.model;

import lombok.Data;

/**
 * @author qushutao
 * @since 2025-05-25
 */
@Data
public class EagleMqQueueModel {

    private Integer id;

    private String fileName;

    private int offsetLimit;

    private int latestOffset;

    private int lastOffset;
}
