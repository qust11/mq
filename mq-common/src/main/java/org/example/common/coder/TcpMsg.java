package org.example.common.coder;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qushutao
 * @since 2025/7/14 19:21
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TcpMsg {

    private short magic = 12;

    private int code;

    private int len;

    private byte[] body;


    public TcpMsg(int code, byte[] body) {
        this.code = code;
        this.body = body;
        this.len = body.length;
    }
}
