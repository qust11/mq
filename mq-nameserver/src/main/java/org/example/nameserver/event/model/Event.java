package org.example.nameserver.event.model;


import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * @author qushutao
 * @since 2025/7/16 11:32
 **/
@Data
public class Event {

    private long timestamp;

    private ChannelHandlerContext ctx;

}
