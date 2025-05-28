package org.example.broker.model;

import lombok.Data;
import org.example.broker.util.ByteConvertUtil;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author qushutao
 * @since 2025/5/25-23:48
 */
@Data
public class CommitLogMessageModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 8660912038548325633L;

    private byte[] content;

    public byte[] getBytes() {
        return content;

    }
}
