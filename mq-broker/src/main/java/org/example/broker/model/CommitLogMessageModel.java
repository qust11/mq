package org.example.broker.model;

import lombok.Data;

/**
 * @author qushutao
 * @since 2025/5/25-23:48
 */
@Data
public class CommitLogMessageModel {

    private int size;

    private byte[] content;
}
