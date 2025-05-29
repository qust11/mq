package org.example.broker.model;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qushutao
 * @since 2025-05-25
 */
@Data
public class EagleMqQueueModel {


    private Integer id;

    private String fileName;

    private int offsetLimit;

    private AtomicInteger latestOffset;

    private int lastOffset;
}
